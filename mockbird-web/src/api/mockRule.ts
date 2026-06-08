import http from './index'

export interface MockRule {
  id: number
  apiInterfaceId: number
  interfaceName?: string
  name: string
  matchType: string
  matchRule: string
  responseStatusCode: number
  responseBody: string
  responseHeaders: string
  delayMs: number
  enabled: number
  createdAt: string
  updatedAt: string
}

export interface MockRulePageParams {
  page?: number
  size?: number
  name?: string
  apiInterfaceId?: number
}

export interface MockRulePageResult {
  list: MockRule[]
  total: number
  page: number
  size: number
}

export interface MockRuleCreateData {
  apiInterfaceId: number | null
  name: string
  matchType: string
  matchRule: string
  responseBody?: string
  responseStatusCode?: number
  responseHeaders?: string
  delayMs?: number
  enabled?: number
}

export interface MockRuleUpdateData {
  apiInterfaceId: number
  name: string
  matchType: string
  matchRule: string
  responseBody?: string
  responseStatusCode?: number
  responseHeaders?: string
  delayMs?: number
  enabled?: number
}

export function getMockRuleList(params: MockRulePageParams): Promise<MockRulePageResult> {
  return http.get('/api/mock-rules', { params }).then((res) => res.data.data)
}

export function createMockRule(data: MockRuleCreateData): Promise<MockRule> {
  return http.post('/api/mock-rules', data).then((res) => res.data.data)
}

export function updateMockRule(id: number, data: MockRuleUpdateData): Promise<MockRule> {
  return http.put(`/api/mock-rules/${id}`, data).then((res) => res.data.data)
}

export function deleteMockRule(id: number): Promise<void> {
  return http.delete(`/api/mock-rules/${id}`)
}
