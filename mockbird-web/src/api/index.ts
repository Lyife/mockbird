import axios from 'axios'
import { ElMessage } from 'element-plus'

const http = axios.create({
  baseURL: '',
  timeout: 15000,
})

http.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body.code && body.code !== 200) {
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message))
    }
    return response
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  },
)

export default http
