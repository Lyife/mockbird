import http from './index'

export interface RequestLog {
  id: number
  projectId: number
  apiInterfaceId: number
  mockRuleId: number | null
  requestMethod: string
  requestPath: string
  requestHeaders: string
  requestBody: string
  responseStatusCode: number
  responseBody: string
  durationMs: number
  createdAt: string
}

export interface RequestLogPageParams {
  page?: number
  size?: number
  projectId?: number
}

export interface RequestLogPageResult {
  list: RequestLog[]
  total: number
  page: number
  size: number
}

export function getRequestLogList(params: RequestLogPageParams): Promise<RequestLogPageResult> {
  return http.get('/api/request-logs', { params }).then((res) => res.data.data)
}

export function getRequestLogDetail(id: number): Promise<RequestLog> {
  return http.get(`/api/request-logs/${id}`).then((res) => res.data.data)
}
