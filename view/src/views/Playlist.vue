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

    <el-table v-loading="loading" :data="playlistList" class="music-table">
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
      <el-table-column prop="creatorName" label="创建者" width="120" />
      <el-table-column prop="createdAt" label="创建时间" width="160" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <template v-if="isAdmin || row.creatorId === currentUser?.id">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleManageMusic(row)">管理音乐</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
          <template v-else>
            <el-button link type="primary" @click="handleManageMusic(row)">查看</el-button>
          </template>
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

    <!-- 管理歌单音乐 -->
    <el-dialog
      v-model="manageVisible"
      :title="`管理音乐 - ${managePlaylist.name || ''}`"
      width="1100px"
      class="playlist-manage-dialog"
    >
      <div class="manage-header">
        <div class="info">
          <div class="title">{{ managePlaylist.name || '未命名歌单' }}</div>
          <div class="meta">
            <div>创建者：{{ managePlaylist.creatorName || '-' }}</div>
            <div>创建时间：{{ managePlaylist.createdAt || '-' }}</div>
          </div>
        </div>
        <div class="actions">
          <el-tag type="success" v-if="canEdit">可编辑</el-tag>
          <el-tag type="info" v-else>仅查看</el-tag>
        </div>
      </div>

      <div class="manage-sections">
        <!-- 歌单内音乐 -->
        <div class="section">
          <div class="section-title">歌单歌曲</div>
          <div class="section-search">
            <el-input
              v-model="playlistSongKeyword"
              placeholder="搜索歌单内的音乐..."
              clearable
              @keyup.enter="filterPlaylistSongs"
              @clear="filterPlaylistSongs"
              style="width: 260px"
            />
          </div>
          <el-table
            v-loading="playlistMusicLoading"
            :data="filteredPlaylistSongs"
            class="music-table"
            height="320"
          >
            <el-table-column prop="title" label="歌曲" min-width="160" />
            <el-table-column prop="artist" label="歌手" width="140" />
            <el-table-column prop="album" label="专辑" width="140" />
            <el-table-column prop="genre" label="流派" width="100" />
            <el-table-column prop="addedAt" label="添加时间" width="170">
              <template #default="{ row }">
                {{ row.addedAt || row.createdAt || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handlePlay(row)">播放</el-button>
                <el-button link type="primary" :disabled="!canEdit" @click="handleEditMusic(row)">编辑</el-button>
                <el-button link type="danger" :disabled="!canEdit" @click="handleRemoveMusic(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 音乐库（添加到歌单） -->
        <div class="section">
          <div class="section-title">音乐库</div>
          <div class="section-search">
            <el-input
              v-model="libraryKeyword"
              placeholder="搜索音乐库..."
              clearable
              @keyup.enter="loadMusicLibrary"
              @clear="loadMusicLibrary"
              style="width: 260px"
            />
            <el-select v-model="libraryGenre" placeholder="选择流派" clearable style="width: 160px" @change="loadMusicLibrary">
              <el-option v-for="genre in genres" :key="genre" :label="genre" :value="genre" />
            </el-select>
            <el-button type="primary" :disabled="!canEdit" @click="loadMusicLibrary">刷新</el-button>
          </div>
          <el-table
            v-loading="libraryLoading"
            :data="musicLibrary"
            class="music-table"
            height="320"
          >
            <el-table-column prop="title" label="歌曲" min-width="160" />
            <el-table-column prop="artist" label="歌手" width="140" />
            <el-table-column prop="album" label="专辑" width="140" />
            <el-table-column prop="genre" label="流派" width="100" />
            <el-table-column prop="playCount" label="播放量" width="100">
              <template #default="{ row }">
                {{ row.playCount?.toLocaleString() || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" :disabled="!canEdit" @click="handleAddMusic(row)">添加</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { playlistApi, musicApi } from '@/api'
import { getUser } from '@/utils/auth'

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
  coverImage: '',
  creatorId: null,
  creatorName: ''
})

// 管理音乐
const manageVisible = ref(false)
const managePlaylist = reactive({
  id: null,
  name: '',
  creatorId: null,
  creatorName: '',
  createdAt: ''
})
const playlistMusic = ref([])
const playlistMusicLoading = ref(false)
const playlistSongKeyword = ref('')
const musicLibrary = ref([])
const libraryLoading = ref(false)
const libraryKeyword = ref('')
const libraryGenre = ref('')
const genres = ref([])
const audioRef = ref(null)
const currentPlayingId = ref(null)

const currentUser = getUser()
const isAdmin = currentUser?.role === 'admin'
const canEdit = computed(() => isAdmin || managePlaylist.creatorId === currentUser?.id)

const filteredPlaylistSongs = computed(() => {
  const keyword = playlistSongKeyword.value.trim().toLowerCase()
  if (!keyword) return playlistMusic.value
  return playlistMusic.value.filter(
    item =>
      item.title?.toLowerCase().includes(keyword) ||
      item.artist?.toLowerCase().includes(keyword) ||
      item.album?.toLowerCase().includes(keyword) ||
      item.genre?.toLowerCase().includes(keyword)
  )
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
    coverImage: '',
    creatorId: currentUser?.id || null,
    creatorName: currentUser?.nickname || currentUser?.username || ''
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
  if (!row?.id) {
    ElMessage.error('无效的歌单ID')
    return
  }
  managePlaylist.id = row.id
  managePlaylist.name = row.name
  managePlaylist.creatorId = row.creatorId
  managePlaylist.creatorName = row.creatorName
  managePlaylist.createdAt = row.createdAt
  manageVisible.value = true
  loadPlaylistMusic()
  loadMusicLibrary()
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
      // 创建时带上创建者
      formData.creatorId = currentUser?.id || null
      formData.creatorName = currentUser?.nickname || currentUser?.username || ''
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
  loadGenres()
})

// 加载歌单歌曲
const loadPlaylistMusic = async () => {
  if (!managePlaylist.id) return
  playlistMusicLoading.value = true
  try {
    const res = await playlistApi.getMusic(managePlaylist.id)
    if (res.code === 200) {
      playlistMusic.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载歌单音乐失败')
  } finally {
    playlistMusicLoading.value = false
  }
}

// 加载音乐库
const loadMusicLibrary = async () => {
  libraryLoading.value = true
  try {
    const res = await musicApi.list({
      page: 1,
      pageSize: 50,
      keyword: libraryKeyword.value,
      genre: libraryGenre.value
    })
    if (res.code === 200) {
      musicLibrary.value = res.data.list || []
    }
  } catch (error) {
    ElMessage.error('加载音乐库失败')
  } finally {
    libraryLoading.value = false
  }
}

const loadGenres = async () => {
  try {
    const res = await musicApi.getGenres()
    if (res.code === 200) {
      genres.value = res.data || []
    }
  } catch (error) {
    // ignore
  }
}

// 过滤歌单歌曲
const filterPlaylistSongs = () => {
  // computed already handles; just trigger reactivity
}

// 添加音乐到歌单
const handleAddMusic = async (row) => {
  if (!canEdit.value) {
    ElMessage.warning('无权限编辑此歌单')
    return
  }
  try {
    const res = await playlistApi.addMusic(managePlaylist.id, row.id)
    if (res.code === 200) {
      ElMessage.success('已添加到歌单')
      loadPlaylistMusic()
      loadPlaylistList() // 更新歌曲数
    }
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

// 从歌单移除音乐
const handleRemoveMusic = async (row) => {
  if (!canEdit.value) {
    ElMessage.warning('无权限编辑此歌单')
    return
  }
  try {
    await ElMessageBox.confirm('确定要从歌单移除这首歌吗？', '提示', { type: 'warning' })
    const res = await playlistApi.removeMusic(managePlaylist.id, row.id)
    if (res.code === 200) {
      ElMessage.success('已移除')
      loadPlaylistMusic()
      loadPlaylistList() // 更新歌曲数
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('移除失败')
    }
  }
}

// 播放
const handlePlay = (row) => {
  if (!row.fileUrl) {
    ElMessage.warning('未上传音乐文件')
    return
  }
  if (!audioRef.value) audioRef.value = new Audio()
  if (currentPlayingId.value === row.id && !audioRef.value.paused) {
    audioRef.value.pause()
    audioRef.value.currentTime = 0
    currentPlayingId.value = null
    return
  }
  if (!audioRef.value.paused) audioRef.value.pause()
  audioRef.value.src = row.fileUrl
  audioRef.value.play().then(() => {
    currentPlayingId.value = row.id
    audioRef.value.onended = () => {
      currentPlayingId.value = null
    }
  }).catch(() => {
    ElMessage.error('播放失败')
  })
}

// 编辑音乐（跳转到音乐管理页）
const handleEditMusic = (row) => {
  ElMessage.info('请在音乐管理中编辑该歌曲')
}
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

/* 深色弹窗样式，让管理歌单弹窗与页面保持一致 */
.playlist-manage-dialog :deep(.el-dialog) {
  background: #0b1220;
  border: none;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.45);
  color: #e5e7eb;
}

.playlist-manage-dialog :deep(.el-dialog__header) {
  padding: 18px 24px 10px;
  border-bottom: 1px solid #111827;
  margin-right: 0;
}

.playlist-manage-dialog :deep(.el-dialog__title) {
  color: #f3f4f6;
  font-weight: 600;
}

.playlist-manage-dialog :deep(.el-dialog__close) {
  color: #9ca3af;
}

.playlist-manage-dialog :deep(.el-dialog__body) {
  background: #0b1220;
  padding: 16px 24px 24px;
  color: #e5e7eb;
}

.playlist-manage-dialog .manage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid #1f2937;
  border-radius: 10px;
  background: #111827;
  margin-bottom: 16px;
}

.playlist-manage-dialog .manage-header .title {
  font-size: 18px;
  font-weight: 700;
  color: #f3f4f6;
  margin-bottom: 4px;
}

.playlist-manage-dialog .manage-header .meta {
  display: flex;
  gap: 14px;
  color: #9ca3af;
  font-size: 13px;
  line-height: 1.4;
}

.playlist-manage-dialog .manage-sections {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.playlist-manage-dialog .section {
  border: 1px solid #111827;
  border-radius: 10px;
  background: #0f172a;
  padding: 12px;
  min-height: 360px;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.playlist-manage-dialog .section-title {
  font-size: 15px;
  font-weight: 600;
  color: #f3f4f6;
  margin-bottom: 10px;
}

.playlist-manage-dialog .section-search {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.playlist-manage-dialog :deep(.el-input__wrapper),
.playlist-manage-dialog :deep(.el-select .el-input__wrapper) {
  background: #0b1220;
  border: 1px solid #1f2937;
  box-shadow: none;
}

.playlist-manage-dialog :deep(.el-input__inner),
.playlist-manage-dialog :deep(.el-select__caret),
.playlist-manage-dialog :deep(.el-select-dropdown__item),
.playlist-manage-dialog :deep(.el-input__prefix),
.playlist-manage-dialog :deep(.el-input__suffix) {
  color: #e5e7eb;
}

.playlist-manage-dialog :deep(.el-input__inner::placeholder) {
  color: #6b7280;
}

.playlist-manage-dialog :deep(.el-table) {
  --el-table-bg-color: #0b1220;
  --el-table-header-bg-color: #111827;
  --el-table-tr-bg-color: #0b1220;
  --el-table-row-hover-bg-color: rgba(59, 130, 246, 0.08);
  color: #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.playlist-manage-dialog :deep(.el-table thead th) {
  color: #cbd5e1;
  font-weight: 600;
}

.playlist-manage-dialog :deep(.el-table__cell) {
  padding: 12px 10px;
}

.playlist-manage-dialog :deep(.el-button.is-link) {
  font-weight: 600;
}

.playlist-manage-dialog :deep(.el-tag) {
  border-color: transparent;
}
</style>

