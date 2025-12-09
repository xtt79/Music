import { createRouter, createWebHistory } from 'vue-router'
import { getUser } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/Index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: 'music',
        name: 'Music',
        component: () => import('@/views/Music.vue'),
        meta: { title: '音乐管理', icon: 'Headset' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/User.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'playlist',
        name: 'Playlist',
        component: () => import('@/views/Playlist.vue'),
        meta: { title: '歌单管理', icon: 'List' }
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/Log.vue'),
        meta: { title: '操作日志', icon: 'Document' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const user = getUser()  // 改为检查用户信息而不是token

  if (to.meta.requiresAuth && !user) {
    next('/login')
  } else if (to.path === '/login' && user) {
    next('/')
  } else {
    next()
  }
})

export default router
