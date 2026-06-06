import client from './client'
import type { CredentialSummary, CredentialDetail, CredentialRequest } from '../types'

export async function listCredentials(): Promise<CredentialSummary[]> {
  const { data } = await client.get<CredentialSummary[]>('/vault')
  return data
}

// Llamamos al detalle solo cuando el usuario selecciona una credencial.
// Así la contraseña descifrada no vive en memoria más tiempo del necesario.
export async function getCredentialDetail(id: number): Promise<CredentialDetail> {
  const { data } = await client.get<CredentialDetail>(`/vault/${id}`)
  return data
}

export async function createCredential(req: CredentialRequest): Promise<CredentialDetail> {
  const { data } = await client.post<CredentialDetail>('/vault', req)
  return data
}

export async function updateCredential(id: number, req: CredentialRequest): Promise<CredentialDetail> {
  const { data } = await client.put<CredentialDetail>(`/vault/${id}`, req)
  return data
}

export async function deleteCredential(id: number): Promise<void> {
  await client.delete(`/vault/${id}`)
}
