<template>
  <div class="playlist-page">
    <div class="page-header">
      <h2 class="page-title">歌单管理</h2>
      <el-button type="primary" @click="handleAdd">创建歌单</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索歌单..."
        style="width: 300px"
        clearable
        @keyup.enter="handleSearch"
      />
    </div>

    <el-table v-loading="loading" :data="playlistList">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="封面" width="100">
        <template #default="{ row }">
          <el-image
            :src="row.coverImage || '/default-cover.png'"
            fit="cover"
            style="width: 60px; height: 60px; border-radius: 8px"
          />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="歌单名称" min-width="150" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="musicCount" label="歌曲数" width="100" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" @click="handleManageMusic(row)">管理音乐</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="歌单名称" required>
          <el-input v-model="formData.name" placeholder="请输入歌单名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            :action="uploadUrl"
            :data="{ type: 'playlist' }"
            :on-success="handleCoverUploadSuccess"
            :show-file-list="false"
          >
            <el-button>上传封面</el-button>
          </el-upload>
          <el-image
            v-if="formData.coverImage"
            :src="formData.coverImage"
            style="width: 100px; height: 100px; margin-top: 10px; border-radius: 8px"
            fit="cover"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { playlistApi } from '@/api'

const loading = ref(false)
const playlistList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('创建歌单')
const formRef = ref(null)
const uploadUrl = '/api/upload'

const formData = reactive({
  id: null,
  name: '',
  description: '',
  coverImage: ''
})

const loadPlaylistList = async () => {
  loading.value = true
  try {
    const res = await playlistApi.list({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    })
    if (res.code === 200) {
      playlistList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载歌单列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadPlaylistList()
}

const handleAdd = () => {
  dialogTitle.value = '创建歌单'
  Object.assign(formData, {
    id: null,
    name: '',
    description: '',
    coverImage: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑歌单'
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个歌单吗？', '提示', {
      type: 'warning'
    })
    const res = await playlistApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadPlaylistList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleManageMusic = (row) => {
  ElMessage.info('管理音乐功能开发中...')
}

const handleSubmit = async () => {
  if (!formData.name) {
    ElMessage.warning('请输入歌单名称')
    return
  }
  
  try {
    let res
    if (formData.id) {
      res = await playlistApi.update(formData.id, formData)
    } else {
      res = await playlistApi.create(formData)
    }
    if (res.code === 200) {
      ElMessage.success(formData.id ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadPlaylistList()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleCoverUploadSuccess = (response) => {
  if (response.code === 200) {
    formData.coverImage = response.data.fileUrl
    ElMessage.success('封面上传成功')
  }
}

const handlePageChange = () => {
  loadPlaylistList()
}

const handleSizeChange = () => {
  currentPage.value = 1
  loadPlaylistList()
}

onMounted(() => {
  loadPlaylistList()
})
</script>

<style scoped>
.playlist-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #ffffff;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

