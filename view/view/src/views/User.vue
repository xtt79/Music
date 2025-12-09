<template>
  <div class="user-page">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名..."
        style="width: 300px"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchUpdate">
        批量修改状态
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="userList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="nickname" label="昵称" width="150" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : ''">
            {{ row.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
            {{ row.status === 'active' ? '活跃' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" @click="handleResetPassword(row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>

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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { userApi } from '@/api'

const loading = ref(false)
const userList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const selectedIds = ref([])
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

const handleBatchUpdate = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请选择新状态', '批量修改状态', {
      inputType: 'select',
      inputOptions: {
        active: '活跃',
        inactive: '禁用'
      }
    })
    const res = await userApi.batchUpdateStatus(selectedIds.value, value)
    if (res.code === 200) {
      ElMessage.success('批量更新成功')
      selectedIds.value = []
      loadUserList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量更新失败')
    }
  }
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handlePageChange = () => {
  loadUserList()
}

const handleSizeChange = () => {
  currentPage.value = 1
  loadUserList()
}

onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.user-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #ffffff;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

