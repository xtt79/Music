<template>
  <div class="log-page">
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

    <el-table v-loading="loading" :data="logList">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="operationType" label="操作类型" width="150" />
      <el-table-column prop="operationTarget" label="操作对象" min-width="200" />
      <el-table-column prop="createdAt" label="操作时间" width="180" />
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

