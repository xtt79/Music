<template>
  <div class="dashboard">
    <!-- 背景特效层：柔和渐变+音符，不遮挡内容 -->
    <div class="bg-effects" aria-hidden="true">
      <div class="note note-1">♪</div>
      <div class="note note-2">♫</div>
      <div class="note note-3">♪</div>
      <div class="note note-4">♫</div>
    </div>

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
          <h3 class="chart-title">热门音乐排行（播放量 TOP10）</h3>
          <div ref="topChartRef" class="chart-container"></div>
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
import { dashboardApi, musicApi } from '@/api'

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

const topChartRef = ref(null)
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

// 初始化热门音乐排行（播放量 TOP10）
const initTopChart = async () => {
  if (!topChartRef.value) return

  const chart = echarts.init(topChartRef.value)

  try {
    const res = await musicApi.list({ page: 1, pageSize: 100 })
    const list = res.code === 200 ? res.data.list || [] : []
    const top10 = [...list]
      .sort((a, b) => (b.playCount || 0) - (a.playCount || 0))
      .slice(0, 10)

    const names = top10.map(item => item.title || '-').reverse()
    const counts = top10.map(item => item.playCount || 0).reverse()

    const option = {
      tooltip: {
        trigger: 'axis',
        formatter: params => {
          const p = params[0]
          return `${p.name}<br/>播放量：${p.value?.toLocaleString?.() || p.value}`
        }
      },
      grid: { left: 80, right: 20, top: 20, bottom: 20 },
      xAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#64748b' } },
        axisLabel: { color: '#94a3b8' },
        splitLine: { lineStyle: { color: '#1f2937' } }
      },
      yAxis: {
        type: 'category',
        data: names,
        axisLine: { lineStyle: { color: '#64748b' } },
        axisLabel: { color: '#94a3b8' }
      },
      series: [{
        type: 'bar',
        data: counts,
        barWidth: 14,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#4f46e5' },
            { offset: 1, color: '#06b6d4' }
          ]),
          borderRadius: [6, 6, 6, 6]
        },
        label: {
          show: true,
          position: 'right',
          color: '#cbd5e1',
          formatter: ({ value }) => (value || 0).toLocaleString()
        }
      }]
    }

    chart.setOption(option)
  } catch (error) {
    console.error('加载热门排行失败:', error)
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
  if (topChartRef.value) {
    const chart1 = echarts.getInstanceByDom(topChartRef.value)
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
    initTopChart()
    initGenreChart()
  }, 100)

  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(circle at 18% 18%, rgba(99, 102, 241, 0.10), transparent 38%),
    radial-gradient(circle at 78% 18%, rgba(56, 189, 248, 0.12), transparent 36%),
    radial-gradient(circle at 26% 72%, rgba(14, 165, 233, 0.10), transparent 34%),
    linear-gradient(135deg, #0a1020 0%, #0d1528 42%, #0f1c2f 100%);
  color: #e5e7eb;
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
  animation: float-dash 12s linear infinite;
  filter: blur(0.3px);
}

.note-1 { top: 70%; left: 16%; animation-duration: 12s; animation-delay: 0s; color: rgba(59, 130, 246, 0.18); }
.note-2 { top: 18%; left: 82%; animation-duration: 14s; animation-delay: 1.2s; color: rgba(45, 212, 191, 0.18); }
.note-3 { top: 32%; left: 46%; animation-duration: 11s; animation-delay: 0.7s; color: rgba(129, 140, 248, 0.20); }
.note-4 { top: 8%; left: 24%; animation-duration: 16s; animation-delay: 2s; color: rgba(56, 189, 248, 0.22); font-size: 40px; }

@keyframes float-dash {
  0% { transform: translateY(0) translateX(0) scale(1); opacity: 0.7; }
  40% { opacity: 0.95; }
  60% { transform: translateY(-16px) translateX(5px) scale(1.05); opacity: 0.85; }
  100% { transform: translateY(-36px) translateX(-4px) scale(1.08); opacity: 0; }
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 24px;
  color: #ffffff;
  position: relative;
  z-index: 1;
}

.stats-row {
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
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
  position: relative;
  z-index: 1;
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
  position: relative;
  z-index: 1;
}

.chart-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  height: 400px;
  transition: transform 0.3s;
  position: relative;
  z-index: 1;
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