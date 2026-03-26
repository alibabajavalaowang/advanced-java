<template>
  <div class="family-tree-detail" v-loading="loading">
    <!-- 顶部 Hero Banner -->
    <div class="hero-banner" :style="{ backgroundImage: treeData.coverImage ? `url(${treeData.coverImage})` : '' }">
      <div class="hero-overlay">
        <div class="hero-content">
          <h1 class="tree-name">{{ treeData.name }}</h1>
          <div class="tree-meta">
            <el-tag type="info" size="large" effect="dark">{{ treeData.surname }}氏</el-tag>
            <el-tag v-if="treeData.hallName" type="warning" size="large" effect="dark" class="ml-12px">
              {{ treeData.hallName }}
            </el-tag>
          </div>
          <p class="tree-stats">
            <span>成员 {{ treeData.memberCount || 0 }} 人</span>
            <el-divider direction="vertical" />
            <span>传承 {{ treeData.generationCount || 0 }} 代</span>
          </p>
        </div>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="goChart">
            <el-icon class="mr-4px"><Share /></el-icon>世系图谱
          </el-button>
          <el-button size="large" @click="goMember">
            <el-icon class="mr-4px"><User /></el-icon>成员管理
          </el-button>
          <el-button size="large" @click="goEdit">
            <el-icon class="mr-4px"><Edit /></el-icon>编辑家谱
          </el-button>
        </div>
      </div>
    </div>

    <!-- Tabs 区域 -->
    <el-card shadow="never" class="detail-card">
      <el-tabs v-model="activeTab">
        <!-- 家族简介 Tab -->
        <el-tab-pane label="家族简介" name="intro">
          <div class="intro-section">
            <div v-if="treeData.ancestorStory" class="intro-block">
              <h3 class="intro-title">始祖故事</h3>
              <div class="intro-ancestor">
                <el-tag v-if="treeData.ancestorName" type="warning" class="mb-8px">
                  始祖：{{ treeData.ancestorName }}
                </el-tag>
                <p class="intro-text">{{ treeData.ancestorStory }}</p>
              </div>
            </div>

            <div v-if="treeData.motto" class="intro-block">
              <h3 class="intro-title">家训</h3>
              <div class="motto-box">
                <p class="motto-text">{{ treeData.motto }}</p>
              </div>
            </div>

            <div v-if="treeData.clanRules" class="intro-block">
              <h3 class="intro-title">族规</h3>
              <p class="intro-text">{{ treeData.clanRules }}</p>
            </div>

            <div v-if="treeData.description" class="intro-block">
              <h3 class="intro-title">简介</h3>
              <p class="intro-text">{{ treeData.description }}</p>
            </div>

            <el-empty v-if="!treeData.ancestorStory && !treeData.motto && !treeData.clanRules && !treeData.description"
              description="暂无家族简介信息" />
          </div>
        </el-tab-pane>

        <!-- 统计概览 Tab -->
        <el-tab-pane label="统计概览" name="stats">
          <el-row :gutter="16" class="mb-16px">
            <el-col :span="6">
              <el-card shadow="hover">
                <el-statistic title="总人数" :value="stats.totalMembers" />
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <el-statistic title="在世人数" :value="stats.aliveMembers" />
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <el-statistic title="平均年龄" :value="stats.averageAge" suffix="岁" />
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <el-statistic title="世代数" :value="stats.generationCount" />
              </el-card>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-card shadow="never">
                <template #header><span>各代人数分布</span></template>
                <div ref="genChartRef" style="width: 100%; height: 300px"></div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="never">
                <template #header><span>地域分布</span></template>
                <div ref="regionChartRef" style="width: 100%; height: 300px"></div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 字辈排行 Tab -->
        <el-tab-pane label="字辈排行" name="rank">
          <div v-if="rankList.length > 0" class="rank-poem-box mb-16px">
            <h3>字辈诗</h3>
            <p class="rank-poem">{{ rankList.map(r => r.rankChar).join(' ') }}</p>
          </div>
          <el-table :data="rankList" stripe>
            <el-table-column prop="generationNumber" label="世代序号" width="100" align="center" />
            <el-table-column prop="rankChar" label="字辈用字" width="100" align="center">
              <template #default="{ row }">
                <span class="rank-char">{{ row.rankChar }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="meaning" label="说明" min-width="200" />
          </el-table>
          <el-empty v-if="rankList.length === 0" description="暂无字辈信息" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Share, User, Edit } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getFamilyTree, type FamilyTreeVO } from '@/api/family/tree'
import { getTreeStats, type FamilyTreeStatsVO } from '@/api/family/member'
import { getListByTreeId, type GenerationRankVO } from '@/api/family/rank'

defineOptions({ name: 'FamilyTreeDetail' })

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const activeTab = ref('intro')
const treeId = ref<number>(Number(route.params.id))

const treeData = ref<FamilyTreeVO>({
  name: '',
  surname: '',
  visibility: 0,
  status: 0
})

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

const rankList = ref<GenerationRankVO[]>([])

const genChartRef = ref<HTMLElement>()
const regionChartRef = ref<HTMLElement>()
let genChart: echarts.ECharts | null = null
let regionChart: echarts.ECharts | null = null

/** 加载家谱详情 */
async function loadTreeDetail() {
  loading.value = true
  try {
    const data = await getFamilyTree(treeId.value)
    if (data) {
      treeData.value = data
    }
  } catch (e) {
    console.error('加载家谱详情失败', e)
  } finally {
    loading.value = false
  }
}

/** 加载统计 */
async function loadStats() {
  try {
    const data = await getTreeStats(treeId.value)
    if (data) stats.value = data
    await nextTick()
    renderCharts()
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

/** 加载字辈 */
async function loadRanks() {
  try {
    const data = await getListByTreeId(treeId.value)
    rankList.value = data || []
  } catch (e) {
    console.error('加载字辈失败', e)
  }
}

function renderCharts() {
  // 各代人数
  if (genChartRef.value) {
    if (!genChart) genChart = echarts.init(genChartRef.value)
    const dist = stats.value.generationDistribution || {}
    const keys = Object.keys(dist).sort((a, b) => Number(a) - Number(b))
    genChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: keys.map(k => `第${k}代`) },
      yAxis: { type: 'value', name: '人数' },
      series: [{ type: 'bar', data: keys.map(k => dist[k]), itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] } }],
      grid: { left: 50, right: 20, top: 20, bottom: 40 }
    })
  }
  // 地域分布
  if (regionChartRef.value) {
    if (!regionChart) regionChart = echarts.init(regionChartRef.value)
    const dist = stats.value.provinceDistribution || {}
    const data = Object.entries(dist).map(([name, value]) => ({ name, value }))
    regionChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { type: 'scroll', bottom: 0 },
      series: [{ type: 'pie', radius: ['35%', '65%'], center: ['50%', '45%'], data, itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 } }]
    })
  }
}

function goChart() {
  router.push('/family/chart?treeId=' + treeId.value)
}
function goMember() {
  router.push('/family/member?treeId=' + treeId.value)
}
function goEdit() {
  router.push('/family/tree')
}

watch(activeTab, (val) => {
  if (val === 'stats') {
    loadStats()
  } else if (val === 'rank') {
    loadRanks()
  }
})

onMounted(() => {
  loadTreeDetail()
  loadRanks()
  window.addEventListener('resize', () => {
    genChart?.resize()
    regionChart?.resize()
  })
})
</script>

<style scoped>
.family-tree-detail {
  padding: 0;
}

.hero-banner {
  width: 100%;
  min-height: 260px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-size: cover;
  background-position: center;
  position: relative;
}

.hero-overlay {
  background: rgba(0, 0, 0, 0.45);
  min-height: 260px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40px 60px;
}

.hero-content {
  color: #fff;
}

.tree-name {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 12px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.tree-meta {
  margin-bottom: 12px;
}

.ml-12px {
  margin-left: 12px;
}

.tree-stats {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.85);
}

.hero-actions {
  display: flex;
  gap: 12px;
}

.mr-4px {
  margin-right: 4px;
}

.detail-card {
  margin: 16px;
}

.mb-8px {
  margin-bottom: 8px;
}

.mb-16px {
  margin-bottom: 16px;
}

.intro-section {
  max-width: 800px;
  margin: 0 auto;
}

.intro-block {
  margin-bottom: 32px;
}

.intro-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-left: 12px;
  border-left: 4px solid #409EFF;
}

.intro-text {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
}

.motto-box {
  background: linear-gradient(135deg, #fdf6ec, #fef9f0);
  border: 1px solid #faecd8;
  border-radius: 8px;
  padding: 20px 24px;
}

.motto-text {
  font-size: 16px;
  line-height: 2;
  color: #8B6914;
  font-style: italic;
  white-space: pre-wrap;
}

.rank-poem-box {
  background: linear-gradient(135deg, #f5f7fa, #fff);
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px 24px;
  text-align: center;
}

.rank-poem-box h3 {
  font-size: 16px;
  color: #606266;
  margin-bottom: 12px;
}

.rank-poem {
  font-size: 24px;
  font-weight: 600;
  color: #8B4513;
  letter-spacing: 8px;
  font-family: 'KaiTi', 'STKaiti', serif;
}

.rank-char {
  font-size: 18px;
  font-weight: 600;
  color: #8B4513;
  font-family: 'KaiTi', 'STKaiti', serif;
}
</style>
