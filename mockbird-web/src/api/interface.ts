import http from './index'

export interface ApiInterface {
  id: number
  projectId: number
  projectName: string
  name: string
  path: string
  method: string
  upstreamUrl: string
  requestParams: string
  responseExample: string
  createdAt: string
  updatedAt: string
}

export interface InterfacePageParams {
  page?: number
  size?: number
  name?: string
  method?: string
  projectId?: number
}

export interface InterfacePageResult {
  list: ApiInterface[]
  total: number
  page: number
  size: number
}

export interface InterfaceCreateData {
  projectId: number
  name: string
  path: string
  method: string
  upstreamUrl?: string
  requestParams?: string
  responseExample?: string
}

export interface InterfaceUpdateData {
  name: string
  path: string
  method: string
  upstreamUrl?: string
  requestParams?: string
  responseExample?: string
}

export function getInterfaceList(params: InterfacePageParams): Promise<InterfacePageResult> {
  return http.get('/api/interfaces', { params }).then((res) => res.data.data)
}

export function createInterface(data: InterfaceCreateData): Promise<ApiInterface> {
  return http.post('/api/interfaces', data).then((res) => res.data.data)
}

export function updateInterface(id: number, data: InterfaceUpdateData): Promise<ApiInterface> {
  return http.put(`/api/interfaces/${id}`, data).then((res) => res.data.data)
}

export function deleteInterface(id: number): Promise<void> {
  return http.delete(`/api/interfaces/${id}`)
}
