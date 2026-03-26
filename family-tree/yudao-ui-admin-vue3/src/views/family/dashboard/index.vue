<template>
  <div class="family-dashboard">
    <!-- 家谱选择 -->
    <el-card shadow="never" class="mb-16px">
      <el-select v-model="currentTreeId" placeholder="请选择家谱" style="width: 300px" @change="handleTreeChange">
        <el-option v-for="item in treeList" :key="item.id" :label="item.name" :value="item.id!" />
      </el-select>
    </el-card>

    <!-- 顶部统计卡片 -->
    <el-row :gutter="16" class="mb-16px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="总人数" :value="stats.totalMembers">
            <template #prefix>
              <el-icon color="#409EFF"><User /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="在世人数" :value="stats.aliveMembers">
            <template #prefix>
              <el-icon color="#67C23A"><UserFilled /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="gender-ratio">
            <div class="ratio-title">男女比例</div>
            <div class="ratio-value">
              <span class="male">{{ stats.maleCount }}</span>
              <span class="divider"> : </span>
              <span class="female">{{ stats.femaleCount }}</span>
            </div>
            <div class="ratio-label">
              <span class="male-label">男</span>
              <span class="female-label">女</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="世代数" :value="stats.generationCount">
            <template #prefix>
              <el-icon color="#E6A23C"><Collection /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 中间区域：时间线 + 图表 -->
    <el-row :gutter="16" class="mb-16px">
      <!-- 左侧：最近大事记 -->
      <el-col :span="8">
        <el-card shadow="never" class="timeline-card">
          <template #header>
            <div class="card-header">
              <span>最近大事记</span>
              <el-button link type="primary" @click="$router.push('/family/event')">查看全部</el-button>
            </div>
          </template>
          <el-timeline v-if="stats.recentEvents && stats.recentEvents.length > 0">
            <el-timeline-item
              v-for="(event, index) in stats.recentEvents"
              :key="index"
              :timestamp="event.eventDate"
              :type="getEventColor(event.eventType)"
              placement="top"
            >
              <h4>{{ event.title }}</h4>
              <p class="event-content">{{ event.content }}</p>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无大事记" :image-size="80" />
        </el-card>
      </el-col>

      <!-- 右侧上：各代人数分布柱状图 -->
      <el-col :span="16">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-card shadow="never">
              <template #header>
                <span>各代人数分布</span>
              </template>
              <div ref="generationChartRef" style="width: 100%; height: 300px"></div>
            </el-card>
          </el-col>
          <!-- 右侧下：地域分布饼图 -->
          <el-col :span="12">
            <el-card shadow="never">
              <template #header>
                <span>地域分布</span>
              </template>
              <div ref="regionChartRef" style="width: 100%; height: 300px"></div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <!-- 底部：最近添加的成员列表 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>最近添加的成员</span>
          <el-button link type="primary" @click="$router.push('/family/member')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentMembers" stripe style="width: 100%">
        <el-table-column label="头像" width="70">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.avatar">
              {{ row.name?.substring(0, 1) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.gender === 1 ? '' : 'danger'" size="small">
              {{ row.gender === 1 ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column prop="generationName" label="字辈" width="100" />
        <el-table-column prop="birthplace" label="籍贯" width="150" />
        <el-table-column prop="occupation" label="职业" width="120" />
        <el-table-column prop="createTime" label="添加时间" min-width="160">
          <template #default="{ row }">
            {{ row.createTime ? new Date(row.createTime).toLocaleString() : '' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import { User, UserFilled, Collection } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getMyFamilyTreeList, type FamilyTreeVO } from '@/api/family/tree'
import { getTreeStats, getFamilyMemberPage, type FamilyTreeStatsVO } from '@/api/family/member'

defineOptions({ name: 'FamilyDashboard' })

const currentTreeId = ref<number>()
const treeList = ref<FamilyTreeVO[]>([])
const stats = ref<FamilyTreeStatsVO>({
  totalMembers: 0,
  aliveMembers: 0,
  maleCount: 0,
  femaleCount: 0,
  generationCount: 0,
  averageAge: 0,
  generationDistribution: {},
  provinceDistribution: {},
  ageDistribution: {},
  recentEvents: []
})
const recentMembers = ref<any[]>([])

const generationChartRef = ref<HTMLElement>()
const regionChartRef = ref<HTMLElement>()
let generationChart: echarts.ECharts | null = null
let regionChart: echarts.ECharts | null = null

/** 获取事件颜色 */
function getEventColor(type: number): string {
  const colorMap: Record<number, string> = {
    0: 'primary',
    1: 'success',
    2: 'danger',
    3: 'warning',
    4: 'info'
  }
  return colorMap[type] || 'primary'
}

/** 加载家谱列表 */
async function loadTreeList() {
  try {
    const data = await getMyFamilyTreeList()
    treeList.value = data || []
    if (treeList.value.length > 0 && !currentTreeId.value) {
      currentTreeId.value = treeList.value[0].id
    }
  } catch (e) {
    console.error('加载家谱列表失败', e)
  }
}

/** 加载统计数据 */
async function loadStats() {
  if (!currentTreeId.value) return
  try {
    const data = await getTreeStats(currentTreeId.value)
    if (data) {
      stats.value = data
    }
    await nextTick()
    renderGenerationChart()
    renderRegionChart()
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

/** 加载最近成员 */
async function loadRecentMembers() {
  if (!currentTreeId.value) return
  try {
    const data = await getFamilyMemberPage({
      treeId: currentTreeId.value,
      pageNo: 1,
      pageSize: 10
    })
    recentMembers.value = data?.list || []
  } catch (e) {
    console.error('加载最近成员失败', e)
  }
}

/** 渲染各代人数分布柱状图 */
function renderGenerationChart() {
  if (!generationChartRef.value) return
  if (!generationChart) {
    generationChart = echarts.init(generationChartRef.value)
  }
  const dist = stats.value.generationDistribution || {}
  const keys = Object.keys(dist).sort((a, b) => Number(a) - Number(b))
  generationChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: keys.map(k => `第${k}代`),
      axisLabel: { rotate: 30 }
    },
    yAxis: { type: 'value', name: '人数' },
    series: [{
      type: 'bar',
      data: keys.map(k => dist[k]),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#79bbff' }
        ]),
        borderRadius: [4, 4, 0, 0]
      },
      barMaxWidth: 40
    }],
    grid: { left: 50, right: 20, top: 20, bottom: 50 }
  })
}

/** 渲染地域分布饼图 */
function renderRegionChart() {
  if (!regionChartRef.value) return
  if (!regionChart) {
    regionChart = echarts.init(regionChartRef.value)
  }
  const dist = stats.value.provinceDistribution || {}
  const data = Object.entries(dist).map(([name, value]) => ({ name, value }))
  regionChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { type: 'scroll', bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['35%', '65%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{d}%' },
      data: data
    }]
  })
}

/** 切换家谱 */
function handleTreeChange() {
  loadStats()
  loadRecentMembers()
}

/** 窗口大小变化时重绘图表 */
function handleResize() {
  generationChart?.resize()
  regionChart?.resize()
}

onMounted(async () => {
  await loadTreeList()
  if (currentTreeId.value) {
    loadStats()
    loadRecentMembers()
  }
  window.addEventListener('resize', handleResize)
})

watch(currentTreeId, () => {
  if (currentTreeId.value) {
    loadStats()
    loadRecentMembers()
  }
})
</script>

<style scoped>
.family-dashboard {
  padding: 16px;
}

.mb-16px {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
}

.stat-card :deep(.el-statistic__head) {
  font-size: 14px;
  color: #909399;
}

.stat-card :deep(.el-statistic__content) {
  font-size: 28px;
}

.gender-ratio {
  text-align: center;
  padding: 4px 0;
}

.gender-ratio .ratio-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.gender-ratio .ratio-value {
  font-size: 28px;
  font-weight: 600;
}

.gender-ratio .male {
  color: #409EFF;
}

.gender-ratio .female {
  color: #E8758F;
}

.gender-ratio .divider {
  color: #ccc;
}

.gender-ratio .ratio-label {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.timeline-card {
  height: 100%;
}

.timeline-card :deep(.el-card__body) {
  max-height: 340px;
  overflow-y: auto;
}

.event-content {
  font-size: 13px;
  color: #666;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 250px;
}
</style>
