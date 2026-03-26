<template>
  <div class="family-ai">
    <el-row :gutter="16">
      <!-- 左侧：功能选择 -->
      <el-col :span="6">
        <el-card shadow="never" class="function-card">
          <template #header><span>AI 助手功能</span></template>
          <div class="function-list">
            <div
              v-for="func in functionList"
              :key="func.type"
              class="function-item"
              :class="{ active: currentType === func.type }"
              @click="selectFunction(func.type)"
            >
              <el-icon :size="24" :color="currentType === func.type ? '#409EFF' : '#909399'">
                <component :is="func.icon" />
              </el-icon>
              <div class="function-info">
                <h4>{{ func.title }}</h4>
                <p>{{ func.desc }}</p>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 历史记录 -->
        <el-card shadow="never" class="mt-16px history-card">
          <template #header>
            <div class="card-header">
              <span>历史记录</span>
              <el-button link type="danger" size="small" @click="clearHistory" v-if="historyList.length > 0">
                清空
              </el-button>
            </div>
          </template>
          <div class="history-list">
            <div
              v-for="(item, index) in historyList"
              :key="index"
              class="history-item"
              @click="loadHistory(item)"
            >
              <el-tag :type="getTypeTag(item.type)" size="small">{{ getTypeLabel(item.type) }}</el-tag>
              <span class="history-text">{{ item.content.substring(0, 30) }}...</span>
              <span class="history-time">{{ new Date(item.time).toLocaleDateString() }}</span>
            </div>
            <el-empty v-if="historyList.length === 0" description="暂无历史记录" :image-size="50" />
          </div>
        </el-card>
      </el-col>

      <!-- 中间+右侧：输入 + 结果 -->
      <el-col :span="18">
        <!-- 输入区域 -->
        <el-card shadow="never" class="mb-16px">
          <template #header>
            <span>{{ currentFunctionTitle }}</span>
          </template>
          <el-form label-width="100px">
            <el-form-item label="选择家谱" required>
              <el-select v-model="currentTreeId" placeholder="请选择家谱" style="width: 300px">
                <el-option v-for="item in treeList" :key="item.id" :label="item.name" :value="item.id!" />
              </el-select>
            </el-form-item>
            <el-form-item label="额外提示">
              <el-input
                v-model="extraPrompt"
                type="textarea"
                :rows="3"
                :placeholder="getPromptPlaceholder()"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="generating"
                :disabled="!currentTreeId"
                @click="handleGenerate"
              >
                <el-icon class="mr-4px" v-if="!generating"><MagicStick /></el-icon>
                {{ generating ? 'AI 生成中...' : '开始生成' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 结果展示区域 -->
        <el-card shadow="never" class="result-card">
          <template #header>
            <div class="card-header">
              <span>生成结果</span>
              <el-button
                v-if="resultContent"
                link type="primary"
                @click="copyResult"
              >
                <el-icon class="mr-4px"><CopyDocument /></el-icon>复制
              </el-button>
            </div>
          </template>

          <div class="result-area" v-if="resultContent || generating">
            <div class="result-content" v-html="renderedContent"></div>
            <span class="typing-cursor" v-if="generating">|</span>
          </div>

          <el-empty v-else description="请选择功能并点击生成" :image-size="120">
            <template #description>
              <p>选择左侧的 AI 功能，配置参数后点击"开始生成"</p>
              <p class="tip-text">AI 将基于家谱数据为您生成精彩内容</p>
            </template>
          </el-empty>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, markRaw } from 'vue'
import { ElMessage } from 'element-plus'
import { MagicStick, CopyDocument, EditPen, Notebook, Reading, DataAnalysis } from '@element-plus/icons-vue'
import { getMyFamilyTreeList, type FamilyTreeVO } from '@/api/family/tree'
import { generateAiContent, type CozeAiReqVO, type CozeAiRespVO } from '@/api/family/ai'

defineOptions({ name: 'FamilyAi' })

const currentTreeId = ref<number>()
const treeList = ref<FamilyTreeVO[]>([])
const currentType = ref<'story' | 'motto' | 'poem' | 'summary'>('story')
const extraPrompt = ref('')
const generating = ref(false)
const resultContent = ref('')
const displayContent = ref('')

interface HistoryItem {
  type: string
  content: string
  time: number
}

const historyList = ref<HistoryItem[]>([])

const functionList = [
  {
    type: 'story' as const,
    title: '生成家族故事',
    desc: '基于家谱数据生成精彩的家族故事',
    icon: markRaw(Notebook)
  },
  {
    type: 'motto' as const,
    title: '生成家训建议',
    desc: '为家族量身定制家训内容',
    icon: markRaw(EditPen)
  },
  {
    type: 'poem' as const,
    title: '生成家族诗词',
    desc: '以诗词形式歌颂家族历史',
    icon: markRaw(Reading)
  },
  {
    type: 'summary' as const,
    title: '生成家族概况',
    desc: '智能总结家族整体概况',
    icon: markRaw(DataAnalysis)
  }
]

const currentFunctionTitle = computed(() => {
  return functionList.find(f => f.type === currentType.value)?.title || '生成家族故事'
})

function getTypeLabel(type: string): string {
  const map: Record<string, string> = { story: '故事', motto: '家训', poem: '诗词', summary: '概况' }
  return map[type] || type
}

function getTypeTag(type: string): string {
  const map: Record<string, string> = { story: '', motto: 'success', poem: 'warning', summary: 'info' }
  return map[type] || ''
}

function getPromptPlaceholder(): string {
  const map: Record<string, string> = {
    story: '例如：请着重描述始祖创业的艰辛历程...',
    motto: '例如：希望家训偏向教育子女方面...',
    poem: '例如：以七言律诗的形式...',
    summary: '例如：请重点分析人口分布和职业特征...'
  }
  return map[currentType.value] || ''
}

/** 简单的 Markdown 渲染 */
function renderMarkdown(text: string): string {
  return text
    .replace(/### (.+)/g, '<h3>$1</h3>')
    .replace(/## (.+)/g, '<h2>$1</h2>')
    .replace(/# (.+)/g, '<h1>$1</h1>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/\n/g, '<br/>')
}

const renderedContent = computed(() => {
  return renderMarkdown(displayContent.value)
})

function selectFunction(type: 'story' | 'motto' | 'poem' | 'summary') {
  currentType.value = type
}

/** 加载家谱列表 */
async function loadTreeList() {
  try {
    const data = await getMyFamilyTreeList()
    treeList.value = data || []
    if (treeList.value.length > 0) {
      currentTreeId.value = treeList.value[0].id
    }
  } catch (e) {
    console.error('加载家谱失败', e)
  }
}

/** 打字机效果 */
function typewriterEffect(text: string) {
  displayContent.value = ''
  let index = 0
  const timer = setInterval(() => {
    if (index < text.length) {
      displayContent.value += text[index]
      index++
    } else {
      clearInterval(timer)
      generating.value = false
    }
  }, 30)
}

/** 生成内容 */
async function handleGenerate() {
  if (!currentTreeId.value) {
    ElMessage.warning('请先选择家谱')
    return
  }

  generating.value = true
  resultContent.value = ''
  displayContent.value = ''

  try {
    const reqData: CozeAiReqVO = {
      treeId: currentTreeId.value,
      prompt: extraPrompt.value,
      type: currentType.value
    }

    const data: CozeAiRespVO = await generateAiContent(reqData)
    resultContent.value = data.content || ''

    // 保存到历史记录
    historyList.value.unshift({
      type: currentType.value,
      content: resultContent.value,
      time: Date.now()
    })
    // 最多保留20条
    if (historyList.value.length > 20) {
      historyList.value = historyList.value.slice(0, 20)
    }
    // 保存到本地存储
    localStorage.setItem('family-ai-history', JSON.stringify(historyList.value))

    // 打字机效果
    typewriterEffect(resultContent.value)
  } catch (e) {
    console.error('生成失败', e)
    ElMessage.error('AI 生成失败，请稍后重试')
    generating.value = false
  }
}

/** 复制结果 */
async function copyResult() {
  try {
    await navigator.clipboard.writeText(resultContent.value)
    ElMessage.success('已复制到剪贴板')
  } catch (e) {
    // 回退方案
    const textarea = document.createElement('textarea')
    textarea.value = resultContent.value
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    ElMessage.success('已复制到剪贴板')
  }
}

/** 加载历史记录 */
function loadHistory(item: HistoryItem) {
  currentType.value = item.type as any
  resultContent.value = item.content
  displayContent.value = item.content
}

/** 清空历史 */
function clearHistory() {
  historyList.value = []
  localStorage.removeItem('family-ai-history')
  ElMessage.success('已清空历史记录')
}

/** 加载本地历史记录 */
function loadLocalHistory() {
  try {
    const saved = localStorage.getItem('family-ai-history')
    if (saved) {
      historyList.value = JSON.parse(saved)
    }
  } catch (e) {
    // ignore
  }
}

onMounted(async () => {
  await loadTreeList()
  loadLocalHistory()
})
</script>

<style scoped>
.family-ai {
  padding: 16px;
}

.mb-16px {
  margin-bottom: 16px;
}

.mt-16px {
  margin-top: 16px;
}

.mr-4px {
  margin-right: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 功能卡片 */
.function-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.function-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 2px solid #e4e7ed;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.function-item:hover {
  border-color: #b3d8ff;
  background: #f0f7ff;
}

.function-item.active {
  border-color: #409EFF;
  background: #ecf5ff;
}

.function-info h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.function-info p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #909399;
}

/* 历史记录 */
.history-card :deep(.el-card__body) {
  max-height: 300px;
  overflow-y: auto;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.history-item:hover {
  background: #f5f7fa;
}

.history-text {
  flex: 1;
  font-size: 13px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-time {
  font-size: 11px;
  color: #c0c4cc;
  flex-shrink: 0;
}

/* 结果区域 */
.result-card {
  min-height: 400px;
}

.result-area {
  padding: 16px;
  background: #fafbfc;
  border-radius: 8px;
  min-height: 300px;
}

.result-content {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
}

.result-content :deep(h1) {
  font-size: 22px;
  margin: 16px 0 8px;
}

.result-content :deep(h2) {
  font-size: 18px;
  margin: 14px 0 8px;
}

.result-content :deep(h3) {
  font-size: 16px;
  margin: 12px 0 6px;
}

.typing-cursor {
  display: inline-block;
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
  animation: blink 0.8s infinite;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

.tip-text {
  font-size: 13px;
  color: #c0c4cc;
  margin-top: 4px;
}
</style>
