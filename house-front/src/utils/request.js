import axios from 'axios'
import { useInfoStore } from '@/stores/userInfo'
import router from '@/router/index'
import { ElMessage, ElMessageBox } from 'element-plus'
const store = useInfoStore()

// 1.创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_BASE_API,
  timeout: 60000,
})

// 2.请求拦截
service.interceptors.request.use(
  (config) => {
    if (store.token) {
      config.headers.Authorization = 'Bearer ' + store.token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 3.响应拦截
service.interceptors.response.use(
  (response) => {
    const { data } = response
    if (data.code === 666) {
      ElMessageBox.alert('登录已过期', '过期', {
        confirmButtonText: '去登录',
        callback: () => {
          store.removeToken()
          store.removeUser()
          router.push({ name: 'Login' })
        },
      })
    }
    return data
  },
  (error) => {
    if (error.status === 403) {
      ElMessageBox.alert('登录已过期', '过期', {
        confirmButtonText: '去登录',
        callback: () => {
          store.removeToken()
          store.removeUser()
          router.push({ name: 'Login' })
        },
      })
    } else {
      ElMessage({
        message: error.message,
        type: 'error',
        duration: 5000,
      })
    }
    return Promise.reject(error)
  },
)
export default service
