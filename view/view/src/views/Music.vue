<template>
  <div class="music-page">
    <div class="page-header">
      <h2 class="page-title">音乐管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加音乐
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索音乐、歌手、专辑..."
        style="width: 300px"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="filterGenre" placeholder="选择流派" clearable style="width: 150px" @change="handleSearch">
        <el-option
          v-for="genre in genres"
          :key="genre"
          :label="genre"
          :value="genre"
        />
      </el-select>
      <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
        批量删除
      </el-button>
    </div>

    <!-- 音乐列表 -->
    <el-table
      v-loading="loading"
      :data="musicList"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <el-image
            :src="row.coverImage || '/default-cover.png'"
            :preview-src-list="[row.coverImage]"
            fit="cover"
            style="width: 60px; height: 60px; border-radius: 8px"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="歌曲标题" min-width="150" />
      <el-table-column prop="artist" label="歌手" width="120" />
      <el-table-column prop="album" label="专辑" width="120" />
      <el-table-column prop="genre" label="流派" width="100" />
      <el-table-column prop="playCount" label="播放量" width="100">
        <template #default="{ row }">
          {{ row.playCount?.toLocaleString() || 0 }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="歌曲标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入歌曲标题" />
        </el-form-item>
        <el-form-item label="歌手" prop="artist">
          <el-input v-model="formData.artist" placeholder="请输入歌手名称" />
        </el-form-item>
        <el-form-item label="专辑">
          <el-input v-model="formData.album" placeholder="请输入专辑名称" />
        </el-form-item>
        <el-form-item label="流派">
          <el-select v-model="formData.genre" placeholder="请选择流派" style="width: 100%">
            <el-option
              v-for="genre in genres"
              :key="genre"
              :label="genre"
              :value="genre"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="音乐文件">
          <el-upload
            :action="uploadUrl"
            :data="{ type: 'music' }"
            :on-success="handleMusicUploadSuccess"
            :before-upload="beforeUpload"
            :show-file-list="false"
          >
            <el-button type="primary">上传MP3文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持MP3、WAV、FLAC格式，最大50MB</div>
            </template>
          </el-upload>
          <div v-if="formData.fileUrl" class="file-info">
            <el-icon><Check /></el-icon>
            <span>已上传: {{ formData.fileUrl }}</span>
          </div>
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            :action="uploadUrl"
            :data="{ type: 'cover' }"
            :on-success="handleCoverUploadSuccess"
            :before-upload="beforeUpload"
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
import { Plus, Search, Check } from '@element-plus/icons-vue'
import { musicApi, uploadFile } from '@/api'

const loading = ref(false)
const musicList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const filterGenre = ref('')
const genres = ref([])
const selectedIds = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加音乐')
const formRef = ref(null)
const uploadUrl = '/api/upload'

const formData = reactive({
  id: null,
  title: '',
  artist: '',
  album: '',
  genre: '',
  fileUrl: '',
  coverImage: '',
  status: 'published'
})

const formRules = {
  title: [{ required: true, message: '请输入歌曲标题', trigger: 'blur' }],
  artist: [{ required: true, message: '请输入歌手名称', trigger: 'blur' }]
}

// 加载音乐列表
const loadMusicList = async () => {
  loading.value = true
  try {
    const res = await musicApi.list({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      genre: filterGenre.value
    })
    if (res.code === 200) {
      musicList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载音乐列表失败')
  } finally {
    loading.value = false
  }
}

// 加载流派列表
const loadGenres = async () => {
  try {
    const res = await musicApi.getGenres()
    if (res.code === 200) {
      genres.value = res.data || []
    }
  } catch (error) {
    console.error('加载流派列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadMusicList()
}

// 添加
const handleAdd = () => {
  dialogTitle.value = '添加音乐'
  Object.assign(formData, {
    id: null,
    title: '',
    artist: '',
    album: '',
    genre: '',
    fileUrl: '',
    coverImage: '',
    status: 'published'
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑音乐'
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这首音乐吗？', '提示', {
      type: 'warning'
    })
    const res = await musicApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadMusicList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 首音乐吗？`, '提示', {
      type: 'warning'
    })
    const res = await musicApi.batchDelete(selectedIds.value)
    if (res.code === 200) {
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      loadMusicList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (formData.id) {
          res = await musicApi.update(formData.id, formData)
        } else {
          res = await musicApi.create(formData)
        }
        if (res.code === 200) {
          ElMessage.success(formData.id ? '更新成功' : '添加成功')
          dialogVisible.value = false
          loadMusicList()
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 分页变化
const handlePageChange = () => {
  loadMusicList()
}

const handleSizeChange = () => {
  currentPage.value = 1
  loadMusicList()
}

// 文件上传前验证
const beforeUpload = (file) => {
  const isValidType = ['audio/mpeg', 'audio/wav', 'audio/flac', 'image/jpeg', 'image/png', 'image/gif'].includes(file.type)
  const isLt50M = file.size / 1024 / 1024 < 50

  if (!isValidType) {
    ElMessage.error('文件格式不正确！')
    return false
  }
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过50MB！')
    return false
  }
  return true
}

// 音乐文件上传成功
const handleMusicUploadSuccess = (response) => {
  if (response.code === 200) {
    formData.fileUrl = response.data.fileUrl
    ElMessage.success('音乐文件上传成功')
  }
}

// 封面图片上传成功
const handleCoverUploadSuccess = (response) => {
  if (response.code === 200) {
    formData.coverImage = response.data.fileUrl
    ElMessage.success('封面图片上传成功')
  }
}

onMounted(() => {
  loadMusicList()
  loadGenres()
})
</script>

<style scoped>
.music-page {
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
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.file-info {
  margin-top: 8px;
  color: #10b981;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>

