import request from '@/utils/request'

/**
 * 登录API
 */
export function login(data) {
  // 使用URL编码格式，而不是FormData
  // 因为您的LoginServlet使用request.getParameter()获取参数
  const params = new URLSearchParams()
  params.append('username', data.username)
  params.append('password', data.password)

  return request({
    url: '/api/login',
    method: 'post',
    data: params.toString(),
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 退出登录API
 */
export function logout() {
  return request({
    url: '/api/login',
    method: 'delete'
  })
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request({
    url: '/api/login',
    method: 'get'
  })
}

/**
 * 音乐管理API
 */
export const musicApi = {
  // 获取音乐列表
  list(params) {
    return request({
      url: '/api/music',
      method: 'get',
      params
    })
  },
  // 获取音乐详情
  getById(id) {
    return request({
      url: `/api/music/${id}`,
      method: 'get'
    })
  },
  // 添加音乐
  create(data) {
    return request({
      url: '/api/music',
      method: 'post',
      data
    })
  },
  // 更新音乐
  update(id, data) {
    return request({
      url: `/api/music/${id}`,
      method: 'put',
      data
    })
  },
  // 删除音乐
  delete(id) {
    return request({
      url: `/api/music/${id}`,
      method: 'delete'
    })
  },
  // 批量删除音乐
  batchDelete(ids) {
    return request({
      url: `/api/music/batch/${ids.join(',')}`,
      method: 'delete'
    })
  },
  // 获取所有流派
  getGenres() {
    return request({
      url: '/api/music/genres',
      method: 'get'
    })
  },
  // 播放音乐（增加播放次数）
  play(id) {
    return request({
      url: `/api/music/${id}/play`,
      method: 'post'
    })
  }
}

/**
 * 用户管理API
 */
export const userApi = {
  // 获取用户列表
  list(params) {
    return request({
      url: '/api/user',
      method: 'get',
      params
    })
  },
  // 获取用户详情
  getById(id) {
    return request({
      url: `/api/user/${id}`,
      method: 'get'
    })
  },
  // 更新用户
  update(id, data) {
    return request({
      url: `/api/user/${id}`,
      method: 'put',
      data
    })
  },
  // 重置密码
  resetPassword(id, newPassword) {
    return request({
      url: `/api/user/${id}/reset-password`,
      method: 'put',
      data: { newPassword }
    })
  },
  // 批量更新状态
  batchUpdateStatus(ids, status) {
    return request({
      url: '/api/user/batch-status',
      method: 'put',
      data: { ids, status }
    })
  },
  // 创建用户
  create(data) {
    return request({
      url: '/api/user',
      method: 'post',
      data
    })
  }
}

/**
 * 歌单管理API
 */
export const playlistApi = {
  // 获取歌单列表
  list(params) {
    return request({
      url: '/api/playlist',
      method: 'get',
      params
    })
  },
  // 获取歌单详情
  getById(id) {
    return request({
      url: `/api/playlist/${id}`,
      method: 'get'
    })
  },
  // 创建歌单
  create(data) {
    return request({
      url: '/api/playlist',
      method: 'post',
      data
    })
  },
  // 更新歌单
  update(id, data) {
    return request({
      url: `/api/playlist/${id}`,
      method: 'put',
      data
    })
  },
  // 删除歌单
  delete(id) {
    return request({
      url: `/api/playlist/${id}`,
      method: 'delete'
    })
  },
  // 添加音乐到歌单
  addMusic(playlistId, musicId) {
    return request({
      url: '/api/playlist',
      method: 'post',
      params: {
        action: 'addMusic',
        playlistId,
        musicId
      }
    })
  },
  // 从歌单移除音乐
  removeMusic(playlistId, musicId) {
    return request({
      url: '/api/playlist',
      method: 'delete',
      params: {
        action: 'removeMusic',
        playlistId,
        musicId
      }
    })
  },
  // 获取歌单中的音乐列表
  getMusic(playlistId) {
    return request({
      url: `/api/playlist/music/${playlistId}`,
      method: 'get'
    })
  },
  // 更新歌单封面
  updateCover(playlistId, file) {
    const formData = new FormData()
    formData.append('file', file)

    return request({
      url: `/api/playlist/${playlistId}/cover`,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

/**
 * 仪表盘API
 */
export const dashboardApi = {
  // 获取统计数据
  getStatistics() {
    return request({
      url: '/api/stats',
      method: 'get'
    })
  },
  // 获取流派分布
  getGenreDistribution() {
    return request({
      url: '/api/dashboard/genre-distribution',
      method: 'get'
    })
  },
  // 获取用户增长趋势
  getUserGrowthTrend() {
    return request({
      url: '/api/dashboard/user-growth',
      method: 'get'
    })
  },
  // 获取热门歌曲
  getHotMusic() {
    return request({
      url: '/api/dashboard/hot-music',
      method: 'get'
    })
  },
  // 获取系统状态
  getSystemStatus() {
    return request({
      url: '/api/dashboard/system-status',
      method: 'get'
    })
  }
}

/**
 * 操作日志API
 */
export const logApi = {
  // 获取日志列表
  list(params) {
    return request({
      url: '/api/log',
      method: 'get',
      params
    })
  },
  // 清空日志
  clear() {
    return request({
      url: '/api/log',
      method: 'delete'
    })
  },
  // 导出日志
  export(params) {
    return request({
      url: '/api/log/export',
      method: 'get',
      params,
      responseType: 'blob'
    })
  }
}

/**
 * 文件上传API
 */
export function uploadFile(file, type) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', type)

  return request({
    url: '/api/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 搜索API
 */
export const searchApi = {
  // 搜索音乐
  music(keyword) {
    return request({
      url: '/api/search/music',
      method: 'get',
      params: { keyword }
    })
  },
  // 搜索用户
  user(keyword) {
    return request({
      url: '/api/search/user',
      method: 'get',
      params: { keyword }
    })
  },
  // 搜索歌单
  playlist(keyword) {
    return request({
      url: '/api/search/playlist',
      method: 'get',
      params: { keyword }
    })
  }
}

/**
 * 系统设置API
 */
export const systemApi = {
  // 获取系统配置
  getConfig() {
    return request({
      url: '/api/system/config',
      method: 'get'
    })
  },
  // 更新系统配置
  updateConfig(data) {
    return request({
      url: '/api/system/config',
      method: 'put',
      data
    })
  },
  // 备份数据库
  backup() {
    return request({
      url: '/api/system/backup',
      method: 'post',
      responseType: 'blob'
    })
  },
  // 清理缓存
  clearCache() {
    return request({
      url: '/api/system/clear-cache',
      method: 'post'
    })
  }
}

/**
 * 分类API
 */
export const categoryApi = {
  // 获取所有分类
  list() {
    return request({
      url: '/api/category',
      method: 'get'
    })
  },
  // 创建分类
  create(data) {
    return request({
      url: '/api/category',
      method: 'post',
      data
    })
  },
  // 更新分类
  update(id, data) {
    return request({
      url: `/api/category/${id}`,
      method: 'put',
      data
    })
  },
  // 删除分类
  delete(id) {
    return request({
      url: `/api/category/${id}`,
      method: 'delete'
    })
  }
}

/**
 * 播放历史API
 */
export const historyApi = {
  // 获取播放历史
  list(params) {
    return request({
      url: '/api/history',
      method: 'get',
      params
    })
  },
  // 清空播放历史
  clear() {
    return request({
      url: '/api/history',
      method: 'delete'
    })
  },
  // 添加播放记录
  add(musicId) {
    return request({
      url: `/api/history/${musicId}`,
      method: 'post'
    })
  }
}

export default {
  login,
  logout,
  getCurrentUser,
  musicApi,
  userApi,
  playlistApi,
  dashboardApi,
  logApi,
  uploadFile,
  searchApi,
  systemApi,
  categoryApi,
  historyApi
}