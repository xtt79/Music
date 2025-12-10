<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
      <div class="logo">
        <el-icon v-if="!isCollapse" class="logo-icon"><Headset /></el-icon>
        <span v-if="!isCollapse" class="logo-text">音乐后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        router
        background-color="rgba(15, 23, 42, 0.8)"
        text-color="#94a3b8"
        active-text-color="#6366f1"
      >
        <el-menu-item v-if="isAdmin" index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        <el-menu-item index="/music">
          <el-icon><Headset /></el-icon>
          <template #title>音乐管理</template>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/user">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/playlist">
          <el-icon><List /></el-icon>
          <template #title>歌单管理</template>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/log">
          <el-icon><Document /></el-icon>
          <template #title>操作日志</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="toggleCollapse">
            <Expand v-if="isCollapse" />
            <Fold v-else />
          </el-icon>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar" />
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>

    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUser, removeToken } from '@/utils/auth'
import { logout } from '@/api'

const route = useRoute()
const router = useRouter()

const defaultAvatar =
  'data:image/svg+xml;utf8,' +
  encodeURIComponent(`
<svg xmlns="http://www.w3.org/2000/svg" width="160" height="160" viewBox="0 0 160 160">
  <defs>
    <linearGradient id="g" x1="32" y1="24" x2="128" y2="140" gradientUnits="userSpaceOnUse">
      <stop stop-color="#4F46E5"/>
      <stop offset="1" stop-color="#0EA5E9"/>
    </linearGradient>
  </defs>
  <circle cx="80" cy="80" r="78" fill="#0B1220"/>
  <circle cx="80" cy="80" r="70" fill="url(#g)"/>
  <path d="M94 44v52c0 8.6-7 15.6-15.6 15.6S62.8 104.6 62.8 96c0-8.7 7-15.7 15.6-15.7 3.4 0 6.6 1.1 9.2 2.9V44H94Z" fill="#E5E7EB"/>
  <circle cx="78.4" cy="99.5" r="7.5" fill="#E5E7EB"/>
  <circle cx="108" cy="56" r="5.5" fill="#E5E7EB"/>
</svg>
`)

const isCollapse = ref(false)
const user = ref(null)
const isAdmin = computed(() => user.value?.role === 'admin')

const activeMenu = computed(() => route.path)
const username = computed(() => user.value?.username || '管理员')
const userAvatar = computed(() => user.value?.avatar || defaultAvatar)

// 切换侧边栏折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 处理下拉菜单命令
const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await logout()
      removeToken()
      ElMessage.success('退出成功')
      router.push('/login')
    } catch (error) {
      removeToken()
      router.push('/login')
    }
  }
}

onMounted(() => {
  user.value = getUser()
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(10px);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  font-size: 24px;
  margin-right: 8px;
  color: #6366f1;
}

.logo-text {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header {
  background: rgba(30, 41, 59, 0.5);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  color: #94a3b8;
  transition: color 0.3s;
}

.collapse-icon:hover {
  color: #ffffff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #ffffff;
}

.username {
  font-size: 14px;
}

.main-content {
  background: rgba(30, 41, 59, 0.3);
  backdrop-filter: blur(10px);
  padding: 20px;
  overflow-y: auto;
}
</style>

