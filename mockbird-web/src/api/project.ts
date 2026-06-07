import http from './index'

export interface Project {
  id: number
  name: string
  description: string
  upstreamUrl: string
  createdAt: string
  updatedAt: string
}

export interface ProjectCreateData {
  name: string
  description: string
  upstreamUrl: string
}

export interface ProjectUpdateData {
  name: string
  description: string
  upstreamUrl: string
}

export function getProjectList(): Promise<Project[]> {
  return http.get('/api/projects').then((res) => res.data.data)
}

export function createProject(data: ProjectCreateData): Promise<Project> {
  return http.post('/api/projects', data).then((res) => res.data.data)
}

export function updateProject(id: number, data: ProjectUpdateData): Promise<Project> {
  return http.put(`/api/projects/${id}`, data).then((res) => res.data.data)
}

export function deleteProject(id: number): Promise<void> {
  return http.delete(`/api/projects/${id}`)
}
