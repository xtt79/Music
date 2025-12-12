<template>
  <div class="log-page">
    <!-- 背景特效层：渐变+音符，不遮挡内容 -->
    <div class="bg-effects" aria-hidden="true">
      <div class="note note-1">♪</div>
      <div class="note note-2">♫</div>
      <div class="note note-3">♪</div>
      <div class="note note-4">♫</div>
    </div>

    <div class="page-header">
      <h2 class="page-title">操作日志</h2>
    </div>

    <div class="search-bar">
      <el-input
        v-model="operationType"
        placeholder="操作类型"
        style="width: 200px"
        clearable
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <div class="table-wrapper">
      <el-table v-loading="loading" :data="logList" class="log-table">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="operationType" label="操作类型" width="150" />
        <el-table-column prop="operationTarget" label="操作对象" min-width="200" />
        <el-table-column prop="createdAt" label="操作时间" width="180" />
      </el-table>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { logApi } from '@/api'

const loading = ref(false)
const logList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const operationType = ref('')

const loadLogList = async () => {
  loading.value = true
  try {
    const res = await logApi.list({
      page: currentPage.value,
      pageSize: pageSize.value,
      operationType: operationType.value || undefined
    })
    if (res.code === 200) {
      logList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载日志列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadLogList()
}

const handlePageChange = () => {
  loadLogList()
}

const handleSizeChange = () => {
  currentPage.value = 1
  loadLogList()
}

onMounted(() => {
  loadLogList()
})
</script>

<style scoped>
.log-page {
  padding: 20px;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(circle at 20% 18%, rgba(99, 102, 241, 0.12), transparent 40%),
    radial-gradient(circle at 78% 18%, rgba(45, 212, 191, 0.12), transparent 36%),
    radial-gradient(circle at 28% 78%, rgba(236, 72, 153, 0.10), transparent 34%),
    linear-gradient(135deg, #0a1020 0%, #0d182c 42%, #101f35 100%);
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
  animation: float-log 12s linear infinite;
  filter: blur(0.3px);
}

.note-1 { top: 70%; left: 16%; animation-duration: 12s; animation-delay: 0s; color: rgba(99, 102, 241, 0.18); }
.note-2 { top: 18%; left: 82%; animation-duration: 14s; animation-delay: 1s; color: rgba(45, 212, 191, 0.20); }
.note-3 { top: 34%; left: 46%; animation-duration: 11s; animation-delay: 0.6s; color: rgba(236, 72, 153, 0.18); }
.note-4 { top: 10%; left: 26%; animation-duration: 15s; animation-delay: 1.8s; color: rgba(79, 70, 229, 0.22); font-size: 42px; }

@keyframes float-log {
  0% { transform: translateY(0) translateX(0) scale(1); opacity: 0.7; }
  40% { opacity: 0.95; }
  60% { transform: translateY(-16px) translateX(5px) scale(1.05); opacity: 0.85; }
  100% { transform: translateY(-36px) translateX(-4px) scale(1.08); opacity: 0; }
}

.page-header {
  margin-bottom: 20px;
  position: relative;
  z-index: 1;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #ffffff;
  position: relative;
  z-index: 1;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  position: relative;
  z-index: 1;
}

.table-wrapper {
  margin-top: 4px;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.03);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.28);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  position: relative;
  z-index: 1;
}

/* 表格样式（透明背景以露出渐变，圆角由外层包裹） */
:deep(.log-table) {
  --el-table-bg-color: transparent;
  --el-table-header-bg-color: rgba(17, 24, 39, 0.78);
  --el-table-header-text-color: #cbd5e1;
  --el-table-text-color: #e5e7eb;
  --el-table-border-color: rgba(255, 255, 255, 0.08);
  --el-table-row-hover-bg-color: rgba(30, 41, 59, 0.55);
  background: transparent;
}

:deep(.log-table thead .el-table__cell) {
  font-weight: 700;
  color: #cbd5e1;
  background: rgba(17, 24, 39, 0.78);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

:deep(.log-table .el-table__row) {
  background: rgba(255, 255, 255, 0.02);
  transition: background 0.25s ease, transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

:deep(.log-table .el-table__row:hover) {
  background: linear-gradient(90deg, rgba(30, 41, 59, 0.7), rgba(51, 65, 85, 0.7));
}

:deep(.log-table .el-table__cell) {
  border-color: rgba(255, 255, 255, 0.06);
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

