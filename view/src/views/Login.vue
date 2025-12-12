<template>
  <div class="login-container">
    <!-- 背景跳动音符 -->
    <div class="floating-notes">
      <span class="note note-1">♪</span>
      <span class="note note-2">♫</span>
      <span class="note note-3">♬</span>
      <span class="note note-4">♩</span>
    </div>

    <div class="login-box">
      <div class="logo-section">
        <el-icon class="logo-icon"><Headset /></el-icon>
        <h1 class="title gradient-text">YOLOMusic后台管理系统</h1>
        <p class="subtitle">You Only Live Once</p>
      </div>

      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              size="large"
              :prefix-icon="User"
              clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
          />
        </el-form-item>

        <!-- <el-form-item>
          <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
        </el-form-item> -->

        <el-form-item>
          <el-button
              type="primary"
              size="large"
              class="login-button"
              :loading="loading"
              @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录系统' }}
          </el-button>
        </el-form-item>
      </el-form>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Headset } from '@element-plus/icons-vue'
import { login } from '@/api'
import { setUser } from '@/utils/auth'

const router = useRouter()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        if (res.code === 200) {
          // 不再需要设置token，使用Session认证
          setUser(res.data)
          ElMessage.success('登录成功')
          router.push('/')
        }
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at 20% 20%, rgba(99, 102, 241, 0.15), transparent 30%),
              radial-gradient(circle at 80% 30%, rgba(14, 165, 233, 0.18), transparent 32%),
              radial-gradient(circle at 50% 80%, rgba(124, 58, 237, 0.16), transparent 30%),
              linear-gradient(135deg, #0b1022 0%, #0f172a 50%, #111827 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.1) 0%, transparent 70%);
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.login-box {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 16px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.38), inset 0 0 0 1px rgba(255,255,255,0.02);
  position: relative;
  z-index: 1;
}

.logo-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  font-size: 64px;
  color: #6366f1;
  margin-bottom: 16px;
  animation: float 3s ease-in-out infinite;
}

.title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.subtitle {
  color: #94a3b8;
  font-size: 14px;
}

.login-form {
  margin-top: 30px;
}

.login-button {
  width: 100%;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 0 10px 30px rgba(99, 102, 241, 0.35);
}

.login-button:hover {
  background: linear-gradient(135deg, #5856eb 0%, #7c3aed 100%);
}

.floating-notes {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.note {
  position: absolute;
  color: rgba(255,255,255,0.15);
  font-size: 48px;
  animation: float 4s ease-in-out infinite;
}

.note-1 { top: 18%; left: 18%; color: rgba(99,102,241,0.22); animation-delay: 0s; }
.note-2 { top: 35%; right: 16%; color: rgba(14,165,233,0.22); animation-delay: 0.8s; }
.note-3 { bottom: 28%; left: 20%; color: rgba(124,58,237,0.22); animation-delay: 1.2s; }
.note-4 { bottom: 18%; right: 18%; color: rgba(236,72,153,0.22); animation-delay: 0.4s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12px); }
}

.demo-info {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
}

.demo-text {
  color: #94a3b8;
  font-size: 12px;
  margin-bottom: 8px;
}

.demo-account {
  color: #6366f1;
  font-size: 13px;
}
</style>
