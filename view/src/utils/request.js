import axios from 'axios'
import { ElMessage } from 'element-plus'
import { setUser, removeUser, getUser } from './auth'
import router from '@/router'

// 创建axios实例
const service = axios.create({
    baseURL: '',
    timeout: 30000,
    withCredentials: true
})

// 请求拦截器
service.interceptors.request.use(
    config => {
        console.log(`🌐 发送请求: ${config.method.toUpperCase()} ${config.url}`)

        // 确保URL以/开头
        if (!config.url.startsWith('/')) {
            config.url = '/' + config.url
        }

        // 对于登录请求，使用URL编码格式
        if (config.url === '/api/login') {
            config.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8'

            // 如果是对象数据，转换为URL编码字符串
            if (config.data && typeof config.data === 'object') {
                const params = new URLSearchParams()
                Object.keys(config.data).forEach(key => {
                    params.append(key, config.data[key])
                })
                config.data = params.toString()
            }
        } else {
            // 其他请求使用JSON格式
            config.headers['Content-Type'] = 'application/json;charset=UTF-8'
        }

        return config
    },
    error => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        console.log(`✅ 请求成功: ${response.status} ${response.config.url}`)
        console.log('📥 响应数据:', response.data)

        const res = response.data

        // ========== 登录成功特殊处理 ==========
        if (response.config.url.includes('/login') && res.code === 200) {
            console.log('🎯 登录成功！开始存储用户信息')

            // 存储用户信息
            if (res.data) {
                setUser(res.data)
                console.log('💾 用户信息已存储:', {
                    id: res.data.id,
                    username: res.data.username
                })
            }

            // 显示成功消息
            ElMessage.success('登录成功')

            // 直接返回，不进行后续的错误检查
            return res
        }

        // ========== 其他请求的业务错误处理 ==========
        if (res.code !== 200) {
            console.log('❌ 业务错误:', res.code, res.message)
            const url = response.config.url || ''

            // 登录接口的错误单独处理，不做全局登出跳转
            if (url.includes('/login')) {
                const msg =
                    res.message ||
                    (res.code === 401 ? '账号存在风险，已禁用！' : '登录失败')
                ElMessage.error(msg)
                return res
            }

            // 401未授权，清除用户信息并跳转到登录页
            if (res.code === 401) {
                console.log('🔐 401未授权，清除用户信息')
                ElMessage.error('登录已过期，请重新登录')
                removeUser()

                // 延迟跳转，避免影响当前操作
                setTimeout(() => {
                    if (router.currentRoute.value.path !== '/login') {
                        router.push('/login')
                    }
                }, 1500)
            } else if (res.code === 404) {
                ElMessage.error('接口不存在: ' + (res.message || ''))
            } else {
                ElMessage.error(res.message || '请求失败')
            }

            // 🔥 关键修改：返回错误响应而不是reject
            // 这样上层代码可以通过 res.code 判断是否成功
            return res
        } else {
            return res
        }
    },
    error => {
        console.error('❌ 网络错误:', error)

        // 这里只处理真正的网络错误（HTTP状态码非200）
        if (error.response) {
            console.error('HTTP错误状态:', error.response.status)
            const status = error.response.status

            switch (status) {
                case 401:
                    ElMessage.error('未授权，请重新登录')
                    removeUser()
                    setTimeout(() => {
                        if (router.currentRoute.value.path !== '/login') {
                            router.push('/login')
                        }
                    }, 1500)
                    break
                case 404:
                    ElMessage.error('接口不存在')
                    break
                case 500:
                    ElMessage.error('服务器错误')
                    break
                case 502:
                case 503:
                    ElMessage.error('服务暂时不可用')
                    break
                default:
                    ElMessage.error(error.message || '网络错误')
            }
        } else if (error.request) {
            // 请求已发出，但没有收到响应
            ElMessage.error('网络连接超时，请检查网络')
        } else {
            // 其他错误
            ElMessage.error(error.message || '网络错误')
        }

        return Promise.reject(error)
    }
)

export default service