<template>
  <div class="family-chart" ref="containerRef">
    <!-- 顶部工具栏 -->
    <div class="chart-toolbar">
      <div class="toolbar-left">
        <el-select v-model="currentTreeId" placeholder="选择家谱" style="width: 200px" @change="handleTreeChange">
          <el-option v-for="item in treeList" :key="item.id" :label="item.name" :value="item.id!" />
        </el-select>
        <el-divider direction="vertical" />
        <el-radio-group v-model="layout" size="small" @change="handleLayoutChange">
          <el-radio-button label="vertical">竖向</el-radio-button>
          <el-radio-button label="horizontal">横向</el-radio-button>
        </el-radio-group>
        <el-divider direction="vertical" />
        <el-button-group>
          <el-button size="small" @click="handleZoomIn" title="放大">
            <el-icon><ZoomIn /></el-icon>
          </el-button>
          <el-button size="small" @click="handleZoomOut" title="缩小">
            <el-icon><ZoomOut /></el-icon>
          </el-button>
          <el-button size="small" @click="handleZoomReset" title="重置">
            <el-icon><RefreshRight /></el-icon>
          </el-button>
        </el-button-group>
        <el-divider direction="vertical" />
        <el-button size="small" @click="handleFullscreen" title="全屏">
          <el-icon><FullScreen /></el-icon>
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索成员姓名"
          style="width: 200px"
          clearable
          @keyup.enter="handleSearch"
          @clear="clearSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" size="small" @click="handleSearch" class="ml-8px">搜索</el-button>
        <el-button size="small" @click="handleExport" class="ml-8px">
          <el-icon class="mr-4px"><Download /></el-icon>导出图片
        </el-button>
      </div>
    </div>

    <!-- SVG 画布 -->
    <div class="chart-canvas" ref="canvasRef">
      <svg ref="svgRef" class="tree-svg"></svg>
    </div>

    <!-- 成员详情抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="成员详情"
      direction="rtl"
      size="380px"
    >
      <div class="drawer-content" v-if="selectedMember">
        <div class="drawer-header">
          <el-avatar :size="80" :src="selectedMember.avatar">
            {{ selectedMember.name?.substring(0, 1) }}
          </el-avatar>
          <h3 class="drawer-name">{{ selectedMember.name }}</h3>
          <div class="drawer-tags">
            <el-tag :type="selectedMember.gender === 1 ? '' : 'danger'" size="small">
              {{ selectedMember.gender === 1 ? '男' : '女' }}
            </el-tag>
            <el-tag v-if="selectedMember.generationName" type="warning" size="small" class="ml-8px">
              {{ selectedMember.generationName }}字辈
            </el-tag>
            <el-tag :type="selectedMember.isAlive ? 'success' : 'info'" size="small" class="ml-8px">
              {{ selectedMember.isAlive ? '在世' : '已故' }}
            </el-tag>
          </div>
        </div>

        <el-divider />

        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="辈分" v-if="selectedMember.generation">
            第{{ selectedMember.generation }}代
          </el-descriptions-item>
          <el-descriptions-item label="出生日期" v-if="selectedMember.birthDate">
            {{ selectedMember.birthDate }}
          </el-descriptions-item>
          <el-descriptions-item label="去世日期" v-if="selectedMember.deathDate">
            {{ selectedMember.deathDate }}
          </el-descriptions-item>
          <el-descriptions-item label="职业" v-if="selectedMember.occupation">
            {{ selectedMember.occupation }}
          </el-descriptions-item>
          <el-descriptions-item label="配偶" v-if="selectedMember.spouseName">
            {{ selectedMember.spouseName }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="drawer-actions">
          <el-button type="primary" @click="goMemberDetail">查看完整详情</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ZoomIn, ZoomOut, RefreshRight, FullScreen, Search, Download } from '@element-plus/icons-vue'
import * as d3 from 'd3'
import { getMyFamilyTreeList, type FamilyTreeVO } from '@/api/family/tree'
import { getMemberTree, type FamilyMemberTreeVO } from '@/api/family/member'

defineOptions({ name: 'FamilyChart' })

const route = useRoute()
const router = useRouter()

const containerRef = ref<HTMLElement>()
const canvasRef = ref<HTMLElement>()
const svgRef = ref<SVGSVGElement>()
const currentTreeId = ref<number>()
const treeList = ref<FamilyTreeVO[]>([])
const treeData = ref<FamilyMemberTreeVO>()
const layout = ref<'vertical' | 'horizontal'>('vertical')
const searchKeyword = ref('')
const drawerVisible = ref(false)
const selectedMember = ref<FamilyMemberTreeVO | null>(null)

let zoomBehavior: d3.ZoomBehavior<SVGSVGElement, unknown> | null = null
let currentTransform: d3.ZoomTransform = d3.zoomIdentity

/** 加载家谱列表 */
async function loadTreeList() {
  try {
    const data = await getMyFamilyTreeList()
    treeList.value = data || []
    const urlTreeId = route.query.treeId
    if (urlTreeId) {
      currentTreeId.value = Number(urlTreeId)
    } else if (treeList.value.length > 0) {
      currentTreeId.value = treeList.value[0].id
    }
  } catch (e) {
    console.error('加载家谱列表失败', e)
  }
}

/** 加载家谱树数据 */
async function loadTreeData() {
  if (!currentTreeId.value) return
  try {
    const data = await getMemberTree(currentTreeId.value)
    treeData.value = data
    await nextTick()
    renderTree()
  } catch (e) {
    console.error('加载家谱树失败', e)
  }
}

/** 渲染家谱树 */
function renderTree() {
  if (!svgRef.value || !treeData.value) return

  const svg = d3.select(svgRef.value)
  svg.selectAll('*').remove()

  const width = canvasRef.value!.clientWidth
  const height = canvasRef.value!.clientHeight

  svg.attr('width', width).attr('height', height)

  // 添加阴影滤镜
  const defs = svg.append('defs')
  const filter = defs.append('filter').attr('id', 'shadow').attr('x', '-20%').attr('y', '-20%').attr('width', '140%').attr('height', '140%')
  filter.append('feDropShadow')
    .attr('dx', 0).attr('dy', 2)
    .attr('stdDeviation', 3)
    .attr('flood-opacity', 0.1)

  // 头像剪切路径
  defs.append('clipPath')
    .attr('id', 'avatar-clip')
    .append('circle')
    .attr('cx', 0).attr('cy', 0).attr('r', 16)

  const g = svg.append('g')
    .attr('class', 'tree-container')

  // zoom
  zoomBehavior = d3.zoom<SVGSVGElement, unknown>()
    .scaleExtent([0.1, 3])
    .on('zoom', (event) => {
      currentTransform = event.transform
      g.attr('transform', event.transform.toString())
    })
  svg.call(zoomBehavior)

  // 设置初始位置
  const initialTransform = d3.zoomIdentity.translate(width / 2, 60)
  svg.call(zoomBehavior.transform, initialTransform)

  const root = d3.hierarchy(treeData.value, (d: FamilyMemberTreeVO) => d.children)

  const isVertical = layout.value === 'vertical'

  const treeLayout = d3.tree<FamilyMemberTreeVO>()
    .nodeSize(isVertical ? [220, 140] : [140, 260])
    .separation((a, b) => a.parent === b.parent ? 1.2 : 1.5)

  treeLayout(root)

  // 绘制连线
  if (isVertical) {
    g.selectAll('.link')
      .data(root.links())
      .join('path')
      .attr('class', 'link')
      .attr('d', d3.linkVertical<d3.HierarchyLink<FamilyMemberTreeVO>, d3.HierarchyPointNode<FamilyMemberTreeVO>>()
        .x(d => d.x)
        .y(d => d.y) as any
      )
      .attr('fill', 'none')
      .attr('stroke', '#c0c4cc')
      .attr('stroke-width', 1.5)
  } else {
    g.selectAll('.link')
      .data(root.links())
      .join('path')
      .attr('class', 'link')
      .attr('d', d3.linkHorizontal<d3.HierarchyLink<FamilyMemberTreeVO>, d3.HierarchyPointNode<FamilyMemberTreeVO>>()
        .x(d => d.y)
        .y(d => d.x) as any
      )
      .attr('fill', 'none')
      .attr('stroke', '#c0c4cc')
      .attr('stroke-width', 1.5)
  }

  // 绘制节点
  const nodes = g.selectAll('.node')
    .data(root.descendants())
    .join('g')
    .attr('class', 'node')
    .attr('transform', d => {
      if (isVertical) return `translate(${d.x},${d.y})`
      return `translate(${d.y},${d.x})`
    })
    .style('cursor', 'pointer')
    .on('click', (_event, d) => showMemberDetail(d.data))

  // 节点背景矩形
  nodes.append('rect')
    .attr('x', -80).attr('y', -40)
    .attr('width', 160).attr('height', 80)
    .attr('rx', 10).attr('ry', 10)
    .attr('fill', d => d.data.isAlive ? '#fff' : '#f5f5f5')
    .attr('stroke', d => d.data.gender === 1 ? '#4A90D9' : '#E8758F')
    .attr('stroke-width', 2)
    .attr('filter', 'url(#shadow)')

  // 头像圆形
  nodes.each(function(d) {
    const node = d3.select(this)
    if (d.data.avatar) {
      const clipId = `clip-${d.data.id}`
      defs.append('clipPath')
        .attr('id', clipId)
        .append('circle')
        .attr('cx', 0).attr('cy', 0).attr('r', 16)

      node.append('image')
        .attr('x', -56 - 16).attr('y', -16)
        .attr('width', 32).attr('height', 32)
        .attr('href', d.data.avatar)
        .attr('clip-path', `url(#${clipId})`)
        .attr('transform', 'translate(-56, 0)')
      // Correct position: circle at center-left of the rect
      node.select('image')
        .attr('x', -16).attr('y', -16)
        .attr('transform', 'translate(-50, 0)')
    } else {
      node.append('circle')
        .attr('cx', -50).attr('cy', 0)
        .attr('r', 16)
        .attr('fill', d.data.gender === 1 ? '#E3F2FD' : '#FCE4EC')
        .attr('stroke', d.data.gender === 1 ? '#4A90D9' : '#E8758F')
        .attr('stroke-width', 1)

      node.append('text')
        .attr('x', -50).attr('y', 5)
        .attr('text-anchor', 'middle')
        .attr('font-size', '12px')
        .attr('fill', d.data.gender === 1 ? '#4A90D9' : '#E8758F')
        .text(d.data.name?.substring(0, 1) || '')
    }
  })

  // 姓名
  nodes.append('text')
    .attr('x', 0).attr('dy', -8)
    .attr('text-anchor', 'middle')
    .attr('font-size', '14px')
    .attr('font-weight', 'bold')
    .attr('fill', '#333')
    .text(d => d.data.name)

  // 辈分信息
  nodes.append('text')
    .attr('x', 0).attr('dy', 12)
    .attr('text-anchor', 'middle')
    .attr('font-size', '11px')
    .attr('fill', '#999')
    .text(d => {
      const gen = d.data.generationName ? `${d.data.generationName}字辈` : ''
      const birth = d.data.birthDate ? d.data.birthDate.substring(0, 4) + '年' : ''
      return [gen, birth].filter(Boolean).join(' · ')
    })

  // 在世/已故标记
  nodes.append('text')
    .attr('x', 0).attr('dy', 28)
    .attr('text-anchor', 'middle')
    .attr('font-size', '10px')
    .attr('fill', d => d.data.isAlive ? '#67C23A' : '#909399')
    .text(d => d.data.occupation || '')

  // 配偶节点（在右侧并列显示）
  nodes.filter(d => !!d.data.spouseName)
    .each(function(d) {
      const spouseGroup = d3.select(this)

      // 虚线连接
      spouseGroup.append('line')
        .attr('x1', 80).attr('y1', 0)
        .attr('x2', 130).attr('y2', 0)
        .attr('stroke', '#E8758F')
        .attr('stroke-dasharray', '4,4')
        .attr('stroke-width', 1.5)

      // 配偶矩形
      spouseGroup.append('rect')
        .attr('x', 130).attr('y', -25)
        .attr('width', 100).attr('height', 50)
        .attr('rx', 8).attr('ry', 8)
        .attr('fill', '#FFF5F5')
        .attr('stroke', d.data.spouseGender === 1 ? '#4A90D9' : '#E8758F')
        .attr('stroke-width', 1.5)
        .attr('filter', 'url(#shadow)')

      // 配偶头像占位
      spouseGroup.append('circle')
        .attr('cx', 152).attr('cy', 0)
        .attr('r', 12)
        .attr('fill', d.data.spouseGender === 1 ? '#E3F2FD' : '#FCE4EC')
        .attr('stroke', d.data.spouseGender === 1 ? '#4A90D9' : '#E8758F')
        .attr('stroke-width', 1)

      spouseGroup.append('text')
        .attr('x', 152).attr('y', 4)
        .attr('text-anchor', 'middle')
        .attr('font-size', '10px')
        .attr('fill', d.data.spouseGender === 1 ? '#4A90D9' : '#E8758F')
        .text(d.data.spouseName?.substring(0, 1) || '')

      // 配偶姓名
      spouseGroup.append('text')
        .attr('x', 195).attr('y', 5)
        .attr('text-anchor', 'middle')
        .attr('font-size', '12px')
        .attr('fill', '#666')
        .text(d.data.spouseName || '')
    })
}

/** 显示成员详情 */
function showMemberDetail(data: FamilyMemberTreeVO) {
  selectedMember.value = data
  drawerVisible.value = true
}

/** 跳转成员详情 */
function goMemberDetail() {
  if (selectedMember.value) {
    router.push('/family/member/detail/' + selectedMember.value.id)
  }
}

/** 切换家谱 */
function handleTreeChange() {
  loadTreeData()
}

/** 切换布局 */
function handleLayoutChange() {
  renderTree()
}

/** 放大 */
function handleZoomIn() {
  if (!svgRef.value || !zoomBehavior) return
  const svg = d3.select(svgRef.value)
  svg.transition().duration(300).call(zoomBehavior.scaleBy, 1.3)
}

/** 缩小 */
function handleZoomOut() {
  if (!svgRef.value || !zoomBehavior) return
  const svg = d3.select(svgRef.value)
  svg.transition().duration(300).call(zoomBehavior.scaleBy, 0.7)
}

/** 重置缩放 */
function handleZoomReset() {
  if (!svgRef.value || !zoomBehavior || !canvasRef.value) return
  const svg = d3.select(svgRef.value)
  const width = canvasRef.value.clientWidth
  const resetTransform = d3.zoomIdentity.translate(width / 2, 60)
  svg.transition().duration(500).call(zoomBehavior.transform, resetTransform)
}

/** 全屏 */
function handleFullscreen() {
  if (!containerRef.value) return
  if (document.fullscreenElement) {
    document.exitFullscreen()
  } else {
    containerRef.value.requestFullscreen()
  }
}

/** 搜索成员 */
function handleSearch() {
  if (!searchKeyword.value || !svgRef.value || !zoomBehavior) return

  const svg = d3.select(svgRef.value)

  // 先清除所有高亮
  svg.selectAll('.node rect').attr('stroke-width', 2)
  svg.selectAll('.search-highlight').remove()

  // 查找匹配节点
  const matchedNode = svg.selectAll<SVGGElement, d3.HierarchyPointNode<FamilyMemberTreeVO>>('.node')
    .filter(d => d.data.name?.includes(searchKeyword.value))

  if (matchedNode.empty()) {
    ElMessage.warning('未找到匹配的成员')
    return
  }

  // 高亮第一个匹配的节点
  matchedNode.select('rect')
    .attr('stroke-width', 4)
    .attr('stroke', '#E6A23C')

  // 添加高亮光晕
  matchedNode.insert('rect', ':first-child')
    .attr('class', 'search-highlight')
    .attr('x', -84).attr('y', -44)
    .attr('width', 168).attr('height', 88)
    .attr('rx', 12).attr('ry', 12)
    .attr('fill', 'none')
    .attr('stroke', '#E6A23C')
    .attr('stroke-width', 3)
    .attr('stroke-dasharray', '6,3')
    .attr('opacity', 0.8)

  // 缩放定位到该节点
  const firstMatch = matchedNode.datum()
  if (firstMatch) {
    const isVertical = layout.value === 'vertical'
    const x = isVertical ? firstMatch.x : firstMatch.y
    const y = isVertical ? firstMatch.y : firstMatch.x
    const width = canvasRef.value!.clientWidth
    const height = canvasRef.value!.clientHeight

    const targetTransform = d3.zoomIdentity
      .translate(width / 2 - x, height / 2 - y)
      .scale(1)

    svg.transition().duration(750).call(zoomBehavior!.transform, targetTransform)
  }

  ElMessage.success(`找到 ${matchedNode.size()} 个匹配成员`)
}

/** 清除搜索 */
function clearSearch() {
  if (!svgRef.value) return
  const svg = d3.select(svgRef.value)
  svg.selectAll('.search-highlight').remove()
  svg.selectAll('.node rect')
    .attr('stroke-width', 2)
    .attr('stroke', (d: any) => d.data.gender === 1 ? '#4A90D9' : '#E8758F')
}

/** 导出图片 */
function handleExport() {
  if (!svgRef.value) return

  try {
    const svgElement = svgRef.value
    const svgData = new XMLSerializer().serializeToString(svgElement)
    const svgBlob = new Blob([svgData], { type: 'image/svg+xml;charset=utf-8' })
    const url = URL.createObjectURL(svgBlob)

    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')!
    const img = new Image()

    img.onload = () => {
      canvas.width = svgElement.clientWidth * 2
      canvas.height = svgElement.clientHeight * 2
      ctx.scale(2, 2)
      ctx.fillStyle = '#ffffff'
      ctx.fillRect(0, 0, canvas.width, canvas.height)
      ctx.drawImage(img, 0, 0)

      const pngUrl = canvas.toDataURL('image/png')
      const downloadLink = document.createElement('a')
      downloadLink.href = pngUrl
      downloadLink.download = `家谱图谱_${Date.now()}.png`
      document.body.appendChild(downloadLink)
      downloadLink.click()
      document.body.removeChild(downloadLink)
      URL.revokeObjectURL(url)

      ElMessage.success('导出成功')
    }

    img.onerror = () => {
      ElMessage.error('导出失败')
      URL.revokeObjectURL(url)
    }

    img.src = url
  } catch (e) {
    console.error('导出失败', e)
    ElMessage.error('导出失败')
  }
}

/** 窗口大小变化 */
function handleResize() {
  if (svgRef.value && canvasRef.value) {
    d3.select(svgRef.value)
      .attr('width', canvasRef.value.clientWidth)
      .attr('height', canvasRef.value.clientHeight)
  }
}

onMounted(async () => {
  await loadTreeList()
  if (currentTreeId.value) {
    loadTreeData()
  }
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.family-chart {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
  background: #f5f7fa;
}

.chart-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
}

.ml-8px {
  margin-left: 8px;
}

.mr-4px {
  margin-right: 4px;
}

.chart-canvas {
  flex: 1;
  overflow: hidden;
  position: relative;
}

.tree-svg {
  width: 100%;
  height: 100%;
  background: #fafbfc;
}

.tree-svg :deep(.node) {
  transition: opacity 0.3s;
}

.tree-svg :deep(.node:hover rect) {
  filter: url(#shadow) brightness(0.98);
}

.tree-svg :deep(.link) {
  transition: stroke 0.3s;
}

/* 抽屉内容 */
.drawer-content {
  padding: 0 8px;
}

.drawer-header {
  text-align: center;
  padding: 16px 0;
}

.drawer-name {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 12px 0 8px;
}

.drawer-tags {
  margin-top: 8px;
}

.drawer-actions {
  margin-top: 24px;
  text-align: center;
}

/* 全屏样式覆盖 */
.family-chart:fullscreen {
  height: 100vh;
  background: #fff;
}

.family-chart:fullscreen .chart-canvas {
  height: calc(100vh - 56px);
}
</style>
