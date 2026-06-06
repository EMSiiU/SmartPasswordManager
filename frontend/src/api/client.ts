import axios from 'axios'
import { getToken, clearToken } from '../utils/token'

// El baseURL '/api' se reescribe a 'http://localhost:8080' mediante el proxy
// de Vite (vite.config.ts). Así el navegador nunca hace una petición
// cross-origin y evitamos tener que configurar CORS en el backend durante dev.
const client = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// INTERCEPTOR DE PETICIÓN — añade el JWT a todas las llamadas.
// Esto es mejor que pasar el header a mano en cada función de la API.
client.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// INTERCEPTOR DE RESPUESTA — si el backend dice 401 (token expirado o inválido),
// limpiamos el token y redirigimos al login.
// Usamos window.location en vez de useNavigate porque este módulo está fuera
// de React y no tiene acceso a los hooks del router.
client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearToken()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default client
