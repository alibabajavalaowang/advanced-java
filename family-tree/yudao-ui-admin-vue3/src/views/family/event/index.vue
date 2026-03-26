<template>
  <div class="family-event">
    <!-- 顶部操作栏 -->
    <el-card shadow="never" class="mb-16px">
      <div class="event-header">
        <div class="header-left">
          <el-select v-model="currentTreeId" placeholder="选择家谱" style="width: 200px" @change="handleTreeChange">
            <el-option v-for="item in treeList" :key="item.id" :label="item.name" :value="item.id!" />
          </el-select>
          <el-select v-model="queryParams.eventType" placeholder="事件类型" clearable style="width: 150px; margin-left: 12px" @change="loadEvents">
            <el-option label="全部" :value="undefined" />
            <el-option label="出生" :value="0" />
            <el-option label="婚嫁" :value="1" />
            <el-option label="逝世" :value="2" />
            <el-option label="迁徙" :value="3" />
            <el-option label="成就" :value="4" />
            <el-option label="其他" :value="5" />
          </el-select>
        </div>
        <el-button type="primary" :disabled="!currentTreeId" @click="openForm('create')">
          <el-icon class="mr-4px"><Plus /></el-icon>新增事件
        </el-button>
      </div>
    </el-card>

    <!-- 时间线 -->
    <el-card shadow="never" v-loading="loading">
      <el-timeline v-if="eventList.length > 0">
        <el-timeline-item
          v-for="event in eventList"
          :key="event.id"
          :timestamp="event.eventDate"
          :type="getEventType(event.eventType)"
          :icon="getEventIcon(event.eventType)"
          :hollow="false"
          :size="'large'"
          placement="top"
        >
          <el-card shadow="hover" class="event-card">
            <div class="event-card-header">
              <div class="event-title-row">
                <el-tag :type="getEventType(event.eventType)" size="small" class="mr-8px">
                  {{ getEventLabel(event.eventType) }}
                </el-tag>
                <h3 class="event-title">{{ event.title }}</h3>
              </div>
              <div class="event-actions">
                <el-button link type="primary" size="small" @click="openForm('update', event.id!)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleDelete(event.id!)">删除</el-button>
              </div>
            </div>
            <p class="event-content" v-if="event.content">{{ event.content }}</p>
            <div class="event-meta">
              <span v-if="event.location" class="meta-item">
                <el-icon><Location /></el-icon> {{ event.location }}
              </span>
              <span v-if="event.participants" class="meta-item">
                <el-icon><User /></el-icon> {{ event.participants }}
              </span>
            </div>
            <div class="event-images" v-if="event.images">
              <el-image
                v-for="(img, idx) in event.images.split(',')"
                :key="idx"
                :src="img"
                fit="cover"
                class="event-thumb"
                :preview-src-list="event.images.split(',')"
                :initial-index="idx"
              />
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-if="!loading && eventList.length === 0" description="暂无大事记" />

      <el-pagination
        v-if="total > queryParams.pageSize"
        class="mt-16px"
        v-model:current-page="queryParams.pageNo"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, prev, pager, next"
        @size-change="loadEvents"
        @current-change="loadEvents"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入事件标题" />
        </el-form-item>
        <el-form-item label="事件类型" prop="eventType">
          <el-select v-model="formData.eventType" placeholder="请选择" style="width: 100%">
            <el-option label="出生" :value="0" />
            <el-option label="婚嫁" :value="1" />
            <el-option label="逝世" :value="2" />
            <el-option label="迁徙" :value="3" />
            <el-option label="成就" :value="4" />
            <el-option label="其他" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="eventDate">
          <el-date-picker
            v-model="formData.eventDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="地点" prop="location">
          <el-input v-model="formData.location" placeholder="请输入地点" />
        </el-form-item>
        <el-form-item label="相关人员" prop="participants">
          <el-input v-model="formData.participants" placeholder="请输入相关人员，逗号分隔" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="4" placeholder="请输入事件内容" />
        </el-form-item>
        <el-form-item label="附图">
          <el-upload
            action="/admin-api/infra/file/upload"
            :file-list="imageFileList"
            list-type="picture-card"
            :on-success="handleImageSuccess"
            :on-remove="handleImageRemove"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, markRaw } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadProps, type UploadUserFile } from 'element-plus'
import { Plus, Location, User, Sunrise, Present, Moon, Position, Trophy, MoreFilled } from '@element-plus/icons-vue'
import { getMyFamilyTreeList, type FamilyTreeVO } from '@/api/family/tree'
import {
  getFamilyEventPage,
  getFamilyEvent,
  createFamilyEvent,
  updateFamilyEvent,
  deleteFamilyEvent,
  type FamilyEventVO
} from '@/api/family/event'

defineOptions({ name: 'FamilyEvent' })

const loading = ref(false)
const currentTreeId = ref<number>()
const treeList = ref<FamilyTreeVO[]>([])
const eventList = ref<FamilyEventVO[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  treeId: undefined as number | undefined,
  eventType: undefined as number | undefined
})

/** 事件类型映射 */
function getEventType(type?: number): string {
  const map: Record<number, string> = { 0: 'success', 1: 'danger', 2: 'info', 3: 'warning', 4: 'primary', 5: '' }
  return map[type ?? 5] || ''
}

function getEventLabel(type?: number): string {
  const map: Record<number, string> = { 0: '出生', 1: '婚嫁', 2: '逝世', 3: '迁徙', 4: '成就', 5: '其他' }
  return map[type ?? 5] || '其他'
}

function getEventIcon(type?: number) {
  const map: Record<number, any> = {
    0: markRaw(Sunrise),
    1: markRaw(Present),
    2: markRaw(Moon),
    3: markRaw(Position),
    4: markRaw(Trophy),
    5: markRaw(MoreFilled)
  }
  return map[type ?? 5] || markRaw(MoreFilled)
}

/** 加载家谱列表 */
async function loadTreeList() {
  try {
    const data = await getMyFamilyTreeList()
    treeList.value = data || []
    if (treeList.value.length > 0) {
      currentTreeId.value = treeList.value[0].id
      queryParams.treeId = currentTreeId.value
    }
  } catch (e) {
    console.error('加载家谱失败', e)
  }
}

/** 加载事件列表 */
async function loadEvents() {
  if (!currentTreeId.value) return
  queryParams.treeId = currentTreeId.value
  loading.value = true
  try {
    const data = await getFamilyEventPage(queryParams)
    eventList.value = data?.list || []
    total.value = data?.total || 0
  } catch (e) {
    console.error('加载事件失败', e)
  } finally {
    loading.value = false
  }
}

function handleTreeChange() {
  queryParams.pageNo = 1
  loadEvents()
}

/** 删除 */
async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该事件吗？', '提示', { type: 'warning' })
    await deleteFamilyEvent(id)
    ElMessage.success('删除成功')
    loadEvents()
  } catch (e) {
    // 取消
  }
}

/** 表单 */
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const imageFileList = ref<UploadUserFile[]>([])
const uploadedImages = ref<string[]>([])

const formData = ref<FamilyEventVO>({
  treeId: 0,
  title: '',
  content: '',
  eventDate: '',
  eventType: 0,
  location: '',
  participants: '',
  images: '',
  status: 0
})

const formRules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  eventDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

async function openForm(type: 'create' | 'update', id?: number) {
  dialogVisible.value = true
  imageFileList.value = []
  uploadedImages.value = []

  if (type === 'create') {
    dialogTitle.value = '新增事件'
    formData.value = {
      treeId: currentTreeId.value!,
      title: '',
      content: '',
      eventDate: '',
      eventType: 0,
      location: '',
      participants: '',
      images: '',
      status: 0
    }
  } else {
    dialogTitle.value = '编辑事件'
    try {
      const data = await getFamilyEvent(id!)
      formData.value = data
      if (data.images) {
        uploadedImages.value = data.images.split(',')
        imageFileList.value = uploadedImages.value.map((url, idx) => ({
          name: `image_${idx}`,
          url
        }))
      }
    } catch (e) {
      ElMessage.error('获取详情失败')
    }
  }
}

const handleImageSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 0) {
    uploadedImages.value.push(response.data)
    formData.value.images = uploadedImages.value.join(',')
  }
}

const handleImageRemove: UploadProps['onRemove'] = (file) => {
  const url = file.url || (file.response as any)?.data
  uploadedImages.value = uploadedImages.value.filter(u => u !== url)
  formData.value.images = uploadedImages.value.join(',')
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (formData.value.id) {
      await updateFamilyEvent(formData.value)
      ElMessage.success('修改成功')
    } else {
      await createFamilyEvent(formData.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadEvents()
  } catch (e) {
    console.error('提交失败', e)
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  await loadTreeList()
  if (currentTreeId.value) {
    loadEvents()
  }
})
</script>

<style scoped>
.family-event {
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

.mr-8px {
  margin-right: 8px;
}

.event-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.event-card {
  margin-bottom: 0;
}

.event-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.event-title-row {
  display: flex;
  align-items: center;
}

.event-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.event-actions {
  flex-shrink: 0;
}

.event-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 8px 0;
  white-space: pre-wrap;
}

.event-meta {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.event-images {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.event-thumb {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  cursor: pointer;
}
</style>
