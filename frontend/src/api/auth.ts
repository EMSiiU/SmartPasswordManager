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
