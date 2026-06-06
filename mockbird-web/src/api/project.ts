import http from './index'

export interface Project {
  id: number
  name: string
  description: string
  upstreamUrl: string
  createdAt: string
  updatedAt: string
}

export function getProjectList(): Promise<Project[]> {
  return http.get('/api/projects').then((res) => res.data.data)
}
