import axios from 'axios'
import { getToken, saveToken, clearToken } from '../utils/token'

const client = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// ─── Estado del mecanismo de refresh ─────────────────────────────────────────
// Si varias peticiones fallan con 401 simultáneamente, solo llamamos a
// /auth/refresh UNA vez y las demás esperan encoladas.

let isRefreshing = false

type Subscriber = {
  onToken: (token: string) => void
  onFail: (err: unknown) => void
}
let refreshSubscribers: Subscriber[] = []

function notifySuccess(newToken: string) {
  refreshSubscribers.forEach(s => s.onToken(newToken))
  refreshSubscribers = []
}

function notifyFailure(err: unknown) {
  refreshSubscribers.forEach(s => s.onFail(err))
  refreshSubscribers = []
}

// ─── Interceptor de petición ──────────────────────────────────────────────────
client.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// ─── Interceptor de respuesta ─────────────────────────────────────────────────
client.interceptors.response.use(
  (response) => response,

  async (error) => {
    const originalRequest = error.config

    // El 401 vino del propio refresh → el refresh token también es inválido.
    // No intentamos de nuevo: cerramos sesión.
    if (error.response?.status === 401 &&
        originalRequest?.url === '/auth/refresh') {
      clearToken()
      window.location.href = '/login'
      return Promise.reject(error)
    }

    // 401 en cualquier otro endpoint, primer intento (sin _retry):
    if (error.response?.status === 401 && !originalRequest?._retry) {
      originalRequest._retry = true

      if (isRefreshing) {
        // Otro refresh ya está en curso: ponemos esta petición en cola.
        return new Promise((resolve, reject) => {
          refreshSubscribers.push({
            onToken: (newToken) => {
              originalRequest.headers.Authorization = `Bearer ${newToken}`
              resolve(client(originalRequest))
            },
            onFail: (err) => reject(err),
          })
        })
      }

      isRefreshing = true

      try {
        // Cookie HttpOnly enviada automáticamente por el browser (no la tocamos desde JS).
        const { data } = await client.post<{ accessToken: string }>('/auth/refresh')
        const newToken = data.accessToken

        saveToken(newToken)
        notifySuccess(newToken)

        originalRequest.headers.Authorization = `Bearer ${newToken}`
        return client(originalRequest)

      } catch (refreshError) {
        notifyFailure(refreshError)
        clearToken()
        window.location.href = '/login'
        return Promise.reject(refreshError)

      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(error)
  }
)

export default client
