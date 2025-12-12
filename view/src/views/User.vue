<template>
  <div class="user-page">
    <!-- 背景特效层：渐变+音符，不遮挡内容 -->
    <div class="bg-effects" aria-hidden="true">
      <div class="note note-1">♪</div>
      <div class="note note-2">♫</div>
      <div class="note note-3">♪</div>
      <div class="note note-4">♫</div>
    </div>

    <!-- 顶部标题与操作 -->
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <!-- <el-button type="primary" class="glass-btn" @click="handleAddUser">
        <el-icon><Plus /></el-icon>
        添加用户
      </el-button> -->
    </div>

    <!-- 概览卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div>
          <p class="stat-label">总用户数</p>
          <p class="stat-value">{{ total || 0 }}</p>
        </div>
        <div class="stat-icon blue">
          <el-icon><UserFilled /></el-icon>
        </div>
      </div>
      <div class="stat-card">
        <div>
          <p class="stat-label">活跃用户</p>
          <p class="stat-value">{{ activeCount }}</p>
        </div>
        <div class="stat-icon green">
          <el-icon><CircleCheckFilled /></el-icon>
        </div>
      </div>
      <div class="stat-card">
        <div>
          <p class="stat-label">管理员</p>
          <p class="stat-value">{{ adminCount }}</p>
        </div>
        <div class="stat-icon orange">
          <el-icon><StarFilled /></el-icon>
        </div>
      </div>
      <div class="stat-card">
        <div>
          <p class="stat-label">新增用户</p>
          <p class="stat-value">{{ newUsersHint }}</p>
        </div>
        <div class="stat-icon purple">
          <el-icon><Plus /></el-icon>
        </div>
      </div>
    </div>

    <!-- 搜索与筛选 -->
    <div class="toolbar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名、昵称..."
        clearable
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <div class="filters">
        <el-select v-model="statusFilter" placeholder="全部状态" clearable>
          <el-option label="活跃" value="active" />
          <el-option label="禁用" value="inactive" />
        </el-select>
        <el-select v-model="roleFilter" placeholder="全部角色" clearable>
          <el-option label="管理员" value="admin" />
          <el-option label="普通用户" value="user" />
        </el-select>
      </div>
    </div>

    <!-- 用户卡片列表 -->
    <div class="cards-wrapper">
      <div v-if="filteredUsers.length === 0" class="empty-tip">暂无用户数据</div>
      <div v-else class="cards-grid">
        <div v-for="item in filteredUsers" :key="item.id" class="user-card glass">
          <div class="card-header">
            <div class="avatar" :style="getAvatarStyle(item.username)">
              {{ getAvatarText(item.username) }}
              <span class="status-dot" :class="item.status === 'active' ? 'on' : 'off'"></span>
            </div>
            <div class="user-info">
              <div class="name-line">
                <span class="username">{{ item.username }}</span>
                <el-tag size="small" :type="item.role === 'admin' ? 'danger' : 'info'">
                  {{ item.role === 'admin' ? '管理员' : '普通用户' }}
                </el-tag>
              </div>
              <p class="nickname">{{ item.nickname || '-' }}</p>
            </div>
            <div class="status-tag" :class="item.status === 'active' ? 'active' : 'inactive'">
              {{ item.status === 'active' ? '活跃' : '禁用' }}
            </div>
          </div>

          <div class="card-meta">
            <div class="meta-item">
              <span>注册时间</span>
              <span>{{ item.createdAt || '-' }}</span>
            </div>
          </div>

          <div class="card-actions">
            <el-button size="small" type="primary" plain @click="handleEdit(item)">编辑</el-button>
            <el-button
              size="small"
              :type="item.status === 'active' ? 'danger' : 'success'"
              plain
              @click="handleToggleStatus(item)"
            >
              {{ item.status === 'active' ? '停用' : '启用' }}
            </el-button>
            <el-button size="small" type="info" plain @click="handleResetPassword(item)">重置密码</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑用户" width="500px">
      <el-form ref="editFormRef" :model="editForm" label-width="100px">
        <el-form-item label="角色">
          <el-select v-model="editForm.role" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="活跃" value="active" />
            <el-option label="禁用" value="inactive" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="重置密码" width="500px">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPasswordSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus, UserFilled, CircleCheckFilled, StarFilled } from '@element-plus/icons-vue'
import { userApi } from '@/api'

const loading = ref(false)
const userList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const statusFilter = ref('')
const roleFilter = ref('')
const editDialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const editFormRef = ref(null)
const passwordFormRef = ref(null)

const editForm = reactive({
  id: null,
  role: '',
  status: ''
})

const passwordForm = reactive({
  id: null,
  newPassword: ''
})

const getAvatarText = (name = '') => (name ? name.slice(0, 1).toUpperCase() : 'U')
const getAvatarStyle = (name = '') => {
  const colors = [
    ['#6366f1', '#8b5cf6'],
    ['#0ea5e9', '#22d3ee'],
    ['#f97316', '#f59e0b'],
    ['#10b981', '#34d399']
  ]
  const idx = name ? name.charCodeAt(0) % colors.length : 0
  const [c1, c2] = colors[idx]
  return {
    background: `linear-gradient(135deg, ${c1}, ${c2})`
  }
}

const passwordRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const loadUserList = async () => {
  loading.value = true
  try {
    const res = await userApi.list({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    })
    if (res.code === 200) {
      userList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadUserList()
}

const handleEdit = (row) => {
  Object.assign(editForm, {
    id: row.id,
    role: row.role,
    status: row.status
  })
  editDialogVisible.value = true
}

const handleUpdate = async () => {
  try {
    const res = await userApi.update(editForm.id, {
      role: editForm.role,
      status: editForm.status
    })
    if (res.code === 200) {
      ElMessage.success('更新成功')
      editDialogVisible.value = false
      loadUserList()
    }
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const handleResetPassword = (row) => {
  passwordForm.id = row.id
  passwordForm.newPassword = ''
  passwordDialogVisible.value = true
}

const handleResetPasswordSubmit = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await userApi.resetPassword(passwordForm.id, passwordForm.newPassword)
        if (res.code === 200) {
          ElMessage.success('重置密码成功')
          passwordDialogVisible.value = false
        }
      } catch (error) {
        ElMessage.error('重置密码失败')
      }
    }
  })
}

const handlePageChange = () => {
  loadUserList()
}

const handleSizeChange = () => {
  currentPage.value = 1
  loadUserList()
}

const handleToggleStatus = async (row) => {
  try {
    const targetStatus = row.status === 'active' ? 'inactive' : 'active'
    const res = await userApi.update(row.id, { status: targetStatus })
    if (res.code === 200) {
      ElMessage.success(targetStatus === 'active' ? '已启用' : '已停用')
      loadUserList()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleAddUser = () => {
  ElMessage.info('请通过后台添加用户')
}

onMounted(() => {
  loadUserList()
})

const activeCount = computed(() => (userList.value || []).filter(u => u.status === 'active').length)
const adminCount = computed(() => (userList.value || []).filter(u => u.role === 'admin').length)
const newUsersHint = computed(() => Math.max((userList.value || []).length - 0, 0))
const filteredUsers = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  return (userList.value || []).filter(item => {
    const matchKeyword =
      !keyword ||
      item.username?.toLowerCase().includes(keyword) ||
      item.nickname?.toLowerCase().includes(keyword)
    const matchStatus = !statusFilter.value || item.status === statusFilter.value
    const matchRole = !roleFilter.value || item.role === roleFilter.value
    return matchKeyword && matchStatus && matchRole
  })
})
</script>

<style scoped>
.user-page {
  padding: 20px;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(circle at 22% 18%, rgba(99, 102, 241, 0.12), transparent 40%),
    radial-gradient(circle at 78% 16%, rgba(14, 165, 233, 0.12), transparent 36%),
    radial-gradient(circle at 30% 78%, rgba(236, 72, 153, 0.10), transparent 34%),
    linear-gradient(135deg, #0b1220 0%, #0f1b2e 42%, #0f1f33 100%);
}

.bg-effects {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.note {
  position: absolute;
  font-size: 46px;
  color: rgba(255, 255, 255, 0.12);
  animation: float-user 12s linear infinite;
  filter: blur(0.3px);
}

.note-1 { top: 72%; left: 18%; animation-duration: 12s; animation-delay: 0s; color: rgba(99, 102, 241, 0.18); }
.note-2 { top: 16%; left: 82%; animation-duration: 14s; animation-delay: 1s; color: rgba(14, 165, 233, 0.20); }
.note-3 { top: 34%; left: 46%; animation-duration: 11s; animation-delay: 0.6s; color: rgba(236, 72, 153, 0.18); }
.note-4 { top: 10%; left: 26%; animation-duration: 15s; animation-delay: 1.8s; color: rgba(79, 70, 229, 0.22); font-size: 42px; }

@keyframes float-user {
  0% { transform: translateY(0) translateX(0) scale(1); opacity: 0.7; }
  40% { opacity: 0.95; }
  60% { transform: translateY(-16px) translateX(5px) scale(1.05); opacity: 0.85; }
  100% { transform: translateY(-36px) translateX(-4px) scale(1.08); opacity: 0; }
}

.page-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #ffffff;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 14px;
  margin-bottom: 16px;
  position: relative;
  z-index: 1;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  padding: 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  backdrop-filter: blur(12px);
  box-shadow: 0 14px 30px rgba(0, 0, 0, 0.35);
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
}

.stat-label {
  color: #d2dcea;
  font-size: 13px;
}

.stat-value {
  color: #e9ecf2;
  font-size: 24px;
  font-weight: 700;
  margin-top: 6px;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.stat-icon.blue { background: linear-gradient(135deg, #6366f1, #8b5cf6); }
.stat-icon.green { background: linear-gradient(135deg, #10b981, #34d399); }
.stat-icon.orange { background: linear-gradient(135deg, #f59e0b, #f97316); }
.stat-icon.purple { background: linear-gradient(135deg, #a855f7, #8b5cf6); }

.toolbar {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  padding: 18px;
  display: flex;
  gap: 12px;
  align-items: center;
  backdrop-filter: blur(10px);
  margin-top: 50px;
  margin-bottom: 16px;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.toolbar:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 35px rgba(0, 0, 0, 0.4);
}

.search-input {
  max-width: 360px;
}

.filters {
  display: flex;
  gap: 10px;
  align-items: center;
}

.cards-wrapper {
  margin-top: 12px;
  position: relative;
  z-index: 1;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
}

.user-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(12px);
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.user-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 24px 50px rgba(0, 0, 0, 0.45);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-weight: 700;
  color: #fff;
  position: relative;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
}

.status-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  border: 2px solid #0b1220;
}
.status-dot.on { background: #10b981; }
.status-dot.off { background: #ef4444; }

.user-info {
  flex: 1;
}
.name-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 2px;
}
.username {
  color: #e5e7eb;
  font-weight: 700;
}
.nickname {
  color: #94a3b8;
  font-size: 13px;
}

.status-tag {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}
.status-tag.active {
  background: rgba(16, 185, 129, 0.15);
  color: #34d399;
}
.status-tag.inactive {
  background: rgba(239, 68, 68, 0.15);
  color: #f87171;
}

.card-meta {
  margin-top: 10px;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.04);
}
.meta-item {
  display: flex;
  justify-content: space-between;
  color: #cbd5e1;
  font-size: 13px;
}
.meta-item span:first-child {
  color: #94a3b8;
}

.card-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  position: relative;
  z-index: 1;
}

.glass-btn {
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.9), rgba(139, 92, 246, 0.9));
  box-shadow: 0 12px 30px rgba(99, 102, 241, 0.25);
}

/* 用深度选择器（::v-deep）穿透：用于修改element-plus的样式 */
::v-deep .el-select__wrapper {
  background-color: rgb(40, 46, 62); 
}

::v-deep .pagination .el-pager li {
  /* 替换成你想要的背景色，比如深蓝色 */
  background: #2d3748 !important; /* 若默认样式权重极高，可加!important强制覆盖 */
  /* 可选：同时修改文字颜色（避免和背景冲突） */
  color: #ffffff;
}
::v-deep .pagination .el-pager li {
  /* 替换成你想要的背景色，比如深蓝色 */
  background: #2d3748 !important; /* 若默认样式权重极高，可加!important强制覆盖 */
  /* 可选：同时修改文字颜色（避免和背景冲突） */
  color: #ffffff;
}

/* 统一修改分页的“上一页/下一页”按钮背景（包含.btn-prev和.btn-next） */
::v-deep .pagination .el-pagination .btn-prev,
::v-deep .pagination .el-pagination .btn-next {
  /* 替换成你想要的背景色（比如和之前分页按钮一致的深色） */
  background: #2d3748 !important; 
  color: #ffffff; /* 文字颜色（避免和背景冲突） */
}

/* 可选：鼠标 hover 时的样式 */
::v-deep .pagination .el-pagination .btn-prev:hover,
::v-deep .pagination .el-pagination .btn-next:hover,
::v-deep .pagination .el-pager li:hover{
  background: #4b50eb !important; /* hover时的背景色 */
}
</style>

