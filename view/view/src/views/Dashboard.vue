<template>
  <div class="dashboard">
    <h2 class="page-title">仪表盘</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.key">
        <div class="stat-card">
          <div class="stat-icon" :style="{ background: stat.color }">
            <el-icon :size="24"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-value">{{ stat.value }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :md="12">
        <div class="chart-card">
          <h3 class="chart-title">用户增长趋势</h3>
          <div ref="userGrowthChartRef" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :xs="24" :md="12">
        <div class="chart-card">
          <h3 class="chart-title">音乐流派分布</h3>
          <div ref="genreChartRef" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { User, Headset, List, VideoPlay } from '@element-plus/icons-vue'
import { dashboardApi } from '@/api'

const stats = ref([
  {
    key: 'users',
    label: '总用户数',
    value: 0,
    icon: User,
    color: 'rgba(59, 130, 246, 0.2)'
  },
  {
    key: 'music',
    label: '总音乐数',
    value: 0,
    icon: Headset,
    color: 'rgba(139, 92, 246, 0.2)'
  },
  {
    key: 'playlists',
    label: '总歌单数',
    value: 0,
    icon: List,
    color: 'rgba(16, 185, 129, 0.2)'
  },
  {
    key: 'plays',
    label: '总播放量',
    value: 0,
    icon: VideoPlay,
    color: 'rgba(245, 158, 11, 0.2)'
  }
])

const userGrowthChartRef = ref(null)
const genreChartRef = ref(null)

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await dashboardApi.getStatistics()
    if (res.code === 200 && res.data) {
      stats.value[0].value = res.data.totalUsers || 0
      stats.value[1].value = res.data.totalMusic || 0
      stats.value[2].value = res.data.totalPlaylists || 0
      stats.value[3].value = (res.data.totalPlayCount || 0).toLocaleString()
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 初始化用户增长趋势图
const initUserGrowthChart = async () => {
  if (!userGrowthChartRef.value) return

  const chart = echarts.init(userGrowthChartRef.value)

  try {
    const res = await dashboardApi.getUserGrowthTrend()
    const data = res.code === 200 ? res.data : []

    const option = {
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: data.map(item => item.date),
        axisLine: { lineStyle: { color: '#64748b' } },
        axisLabel: { color: '#94a3b8' }
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#64748b' } },
        axisLabel: { color: '#94a3b8' }
      },
      series: [{
        data: data.map(item => item.count),
        type: 'line',
        smooth: true,
        lineStyle: { color: '#6366f1' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(99, 102, 241, 0.3)' },
              { offset: 1, color: 'rgba(99, 102, 241, 0.1)' }
            ]
          }
        }
      }]
    }

    chart.setOption(option)
  } catch (error) {
    console.error('加载用户增长趋势失败:', error)
  }
}

// 初始化流派分布图
const initGenreChart = async () => {
  if (!genreChartRef.value) return

  const chart = echarts.init(genreChartRef.value)

  try {
    const res = await dashboardApi.getGenreDistribution()
    const data = res.code === 200 ? res.data : []

    const option = {
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: '70%',
        data: data.map(item => ({
          value: item.count,
          name: item.genre
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    }

    chart.setOption(option)
  } catch (error) {
    console.error('加载流派分布失败:', error)
  }
}

onMounted(() => {
  loadStatistics()
  setTimeout(() => {
    initUserGrowthChart()
    initGenreChart()
  }, 100)
})

// 添加窗口大小变化时的图表重绘
const handleResize = () => {
  if (userGrowthChartRef.value) {
    const chart1 = echarts.getInstanceByDom(userGrowthChartRef.value)
    chart1 && chart1.resize()
  }
  if (genreChartRef.value) {
    const chart2 = echarts.getInstanceByDom(genreChartRef.value)
    chart2 && chart2.resize()
  }
}

onMounted(() => {
  loadStatistics()
  setTimeout(() => {
    initUserGrowthChart()
    initGenreChart()
  }, 100)

  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 24px;
  color: #ffffff;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.3s;
  min-height: 100px;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
}

.stat-content {
  flex: 1;
  min-width: 0; /* 防止内容溢出 */
}

.stat-label {
  color: #94a3b8;
  font-size: 14px;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-value {
  color: #ffffff;
  font-size: 24px;
  font-weight: bold;
  font-family: 'Arial', sans-serif;
}

.charts-row {
  margin-top: 24px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  height: 400px;
  transition: transform 0.3s;
}

.chart-card:hover {
  transform: translateY(-5px);
}

.chart-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #ffffff;
  padding-left: 10px;
  border-left: 4px solid #6366f1;
}

.chart-container {
  width: 100%;
  height: 320px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .dashboard {
    padding: 10px;
  }

  .page-title {
    font-size: 20px;
    margin-bottom: 16px;
  }

  .stat-card {
    padding: 15px;
    min-height: 80px;
  }

  .stat-icon {
    width: 48px;
    height: 48px;
  }

  .stat-value {
    font-size: 20px;
  }

  .chart-card {
    height: 350px;
    padding: 15px;
  }

  .chart-container {
    height: 280px;
  }
}

@media (max-width: 576px) {
  .stats-row {
    margin-bottom: 16px;
  }

  .stat-card {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .stat-content {
    width: 100%;
  }
}
</style>