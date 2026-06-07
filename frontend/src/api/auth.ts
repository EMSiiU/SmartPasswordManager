import client from './client'
import type { AuthResponse } from '../types'

export async function register(nombre: string, correo: string, password: string): Promise<AuthResponse> {
  const { data } = await client.post<AuthResponse>('/auth/register', { nombre, correo, password })
  return data
}

export async function login(correo: string, password: string): Promise<AuthResponse> {
  const { data } = await client.post<AuthResponse>('/auth/login', { correo, password })
  return data
}

/**
 * Cierra la sesión en el servidor: revoca el refresh token y borra la cookie.
 * La cookie es HttpOnly, así que el servidor es quien la limpia; JS no puede
 * borrarla directamente.
 */
export async function callLogout(): Promise<void> {
  await client.post('/auth/logout')
}
