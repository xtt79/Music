// src/utils/auth.js

const USER_KEY = 'yolomusic_user'

/**
 * 获取用户信息
 */
export function getUser() {
  try {
    const userStr = localStorage.getItem(USER_KEY)
    if (!userStr) {
      console.log('🔍 getUser(): 本地未找到用户信息')
      return null
    }

    const user = JSON.parse(userStr)
    console.log('🔍 getUser(): 找到用户', user.username)
    return user
  } catch (error) {
    console.error('❌ 解析用户信息失败:', error)
    localStorage.removeItem(USER_KEY)
    return null
  }
}

/**
 * 设置用户信息
 */
export function setUser(user) {
  try {
    console.log('💾 setUser(): 存储用户信息', user)

    // 确保密码等敏感信息被移除
    const userToStore = { ...user }
    if (userToStore.password) {
      delete userToStore.password
    }

    localStorage.setItem(USER_KEY, JSON.stringify(userToStore))

    // 验证存储是否成功
    const storedUser = getUser()
    console.log('✅ setUser(): 存储验证', storedUser ? '成功' : '失败')

    return true
  } catch (error) {
    console.error('❌ 存储用户信息失败:', error)
    return false
  }
}

/**
 * 移除用户信息
 */
export function removeUser() {
  console.log('🗑️ removeUser(): 清除用户信息')
  localStorage.removeItem(USER_KEY)
}

/**
 * 检查是否已登录
 */
export function isLoggedIn() {
  const user = getUser()
  const loggedIn = !!user
  console.log('🔐 isLoggedIn():', loggedIn ? `已登录 (${user.username})` : '未登录')
  return loggedIn
}

// 兼容性函数
export function getToken() {
  return null
}

export function setToken(token) {
  console.log('setToken(): Session模式忽略token')
}

export function removeToken() {
  removeUser()
}