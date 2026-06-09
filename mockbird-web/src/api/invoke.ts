import http from './index'

export interface InvokeRequest {
  queryParams?: Record<string, string>
  headers?: Record<string, string>
  body?: string
}

export interface InvokeResponse {
  statusCode: number
  headers: Record<string, string[]>
  body: string
  durationMs: number
}

export function invokeInterface(id: number, data: InvokeRequest): Promise<InvokeResponse> {
  return http.post(`/api/interfaces/${id}/invoke`, data).then((res) => res.data.data)
}
