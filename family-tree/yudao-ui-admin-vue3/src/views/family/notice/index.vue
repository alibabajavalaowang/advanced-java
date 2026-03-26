<template>
  <div class="family-notice">
    <!-- 顶部操作栏 -->
    <el-card shadow="never" class="mb-16px">
      <div class="notice-header">
        <div class="header-left">
          <el-select v-model="currentTreeId" placeholder="选择家谱" style="width: 200px" @change="handleTreeChange">
            <el-option v-for="item in treeList" :key="item.id" :label="item.name" :value="item.id!" />
          </el-select>
          <el-select v-model="queryParams.publishStatus" placeholder="发布状态" clearable style="width: 150px; margin-left: 12px" @change="loadNotices">
            <el-option label="全部" :value="undefined" />
            <el-option label="已发布" :value="1" />
            <el-option label="未发布" :value="0" />
          </el-select>
        </div>
        <el-button type="primary" :disabled="!currentTreeId" @click="openForm('create')">
          <el-icon class="mr-4px"><Plus /></el-icon>新增公告
        </el-button>
      </div>
    </el-card>

    <!-- 公告列表 -->
    <el-card shadow="never" v-loading="loading">
      <el-table :data="noticeList" stripe>
        <el-table-column label="置顶" width="70" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.topFlag === 1" type="danger" size="small">置顶</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <span class="notice-title-text">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getNoticeTypeTag(row.noticeType)" size="small">
              {{ getNoticeTypeLabel(row.noticeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.publishStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.publishStatus === 1 ? '已发布' : '未发布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="170" align="center">
          <template #default="{ row }">
            {{ row.publishTime ? new Date(row.publishTime).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170" align="center">
          <template #default="{ row }">
            {{ row.createTime ? new Date(row.createTime).toLocaleString() : '' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="showContent(row)">查看</el-button>
            <el-button link type="primary" @click="openForm('update', row.id!)">编辑</el-button>
            <el-button
              v-if="row.publishStatus !== 1"
              link type="success"
              @click="handlePublish(row.id!)"
            >发布</el-button>
            <el-button
              v-else
              link type="warning"
              @click="handleRevoke(row.id!)"
            >撤回</el-button>
            <el-button link type="danger" @click="handleDelete(row.id!)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="mt-16px"
        v-model:current-page="queryParams.pageNo"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadNotices"
        @current-change="loadNotices"
      />
    </el-card>

    <!-- 查看公告内容 -->
    <el-dialog v-model="contentDialogVisible" :title="currentNotice?.title" width="600px">
      <div class="notice-view-content" v-html="currentNotice?.content"></div>
      <div class="notice-view-meta">
        <span v-if="currentNotice?.publishTime">
          发布时间：{{ new Date(currentNotice.publishTime).toLocaleString() }}
        </span>
      </div>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="类型" prop="noticeType">
          <el-select v-model="formData.noticeType" placeholder="请选择" style="width: 100%">
            <el-option label="通知" :value="0" />
            <el-option label="公告" :value="1" />
            <el-option label="活动" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="置顶" prop="topFlag">
          <el-switch v-model="formData.topFlag" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="10"
            placeholder="请输入公告内容，支持HTML格式"
          />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMyFamilyTreeList, type FamilyTreeVO } from '@/api/family/tree'
import {
  getFamilyNoticePage,
  getFamilyNotice,
  createFamilyNotice,
  updateFamilyNotice,
  deleteFamilyNotice,
  publishFamilyNotice,
  type FamilyNoticeVO
} from '@/api/family/notice'

defineOptions({ name: 'FamilyNotice' })

const loading = ref(false)
const currentTreeId = ref<number>()
const treeList = ref<FamilyTreeVO[]>([])
const noticeList = ref<FamilyNoticeVO[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  treeId: undefined as number | undefined,
  publishStatus: undefined as number | undefined
})

function getNoticeTypeLabel(type?: number): string {
  const map: Record<number, string> = { 0: '通知', 1: '公告', 2: '活动' }
  return map[type ?? 0] || '通知'
}

function getNoticeTypeTag(type?: number): string {
  const map: Record<number, string> = { 0: '', 1: 'warning', 2: 'success' }
  return map[type ?? 0] || ''
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

/** 加载公告列表 */
async function loadNotices() {
  if (!currentTreeId.value) return
  queryParams.treeId = currentTreeId.value
  loading.value = true
  try {
    const data = await getFamilyNoticePage(queryParams)
    noticeList.value = data?.list || []
    total.value = data?.total || 0
  } catch (e) {
    console.error('加载公告失败', e)
  } finally {
    loading.value = false
  }
}

function handleTreeChange() {
  queryParams.pageNo = 1
  loadNotices()
}

/** 查看内容 */
const contentDialogVisible = ref(false)
const currentNotice = ref<FamilyNoticeVO | null>(null)

function showContent(notice: FamilyNoticeVO) {
  currentNotice.value = notice
  contentDialogVisible.value = true
}

/** 发布 */
async function handlePublish(id: number) {
  try {
    await ElMessageBox.confirm('确认发布该公告吗？', '提示', { type: 'info' })
    await publishFamilyNotice(id)
    ElMessage.success('发布成功')
    loadNotices()
  } catch (e) {
    // 取消
  }
}

/** 撤回 */
async function handleRevoke(id: number) {
  try {
    await ElMessageBox.confirm('确认撤回该公告吗？撤回后将不再显示。', '提示', { type: 'warning' })
    const notice = await getFamilyNotice(id)
    notice.publishStatus = 0
    await updateFamilyNotice(notice)
    ElMessage.success('撤回成功')
    loadNotices()
  } catch (e) {
    // 取消
  }
}

/** 删除 */
async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该公告吗？', '提示', { type: 'warning' })
    await deleteFamilyNotice(id)
    ElMessage.success('删除成功')
    loadNotices()
  } catch (e) {
    // 取消
  }
}

/** 表单 */
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const formData = ref<FamilyNoticeVO>({
  treeId: 0,
  title: '',
  content: '',
  noticeType: 0,
  topFlag: 0,
  publishStatus: 0,
  status: 0
})

const formRules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

async function openForm(type: 'create' | 'update', id?: number) {
  dialogVisible.value = true
  if (type === 'create') {
    dialogTitle.value = '新增公告'
    formData.value = {
      treeId: currentTreeId.value!,
      title: '',
      content: '',
      noticeType: 0,
      topFlag: 0,
      publishStatus: 0,
      status: 0
    }
  } else {
    dialogTitle.value = '编辑公告'
    try {
      const data = await getFamilyNotice(id!)
      formData.value = data
    } catch (e) {
      ElMessage.error('获取详情失败')
    }
  }
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (formData.value.id) {
      await updateFamilyNotice(formData.value)
      ElMessage.success('修改成功')
    } else {
      await createFamilyNotice(formData.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadNotices()
  } catch (e) {
    console.error('提交失败', e)
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  await loadTreeList()
  if (currentTreeId.value) {
    loadNotices()
  }
})
</script>

<style scoped>
.family-notice {
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

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.notice-title-text {
  font-weight: 500;
}

.notice-view-content {
  line-height: 1.8;
  font-size: 15px;
  color: #303133;
  white-space: pre-wrap;
  min-height: 100px;
}

.notice-view-meta {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  font-size: 13px;
  color: #909399;
}
</style>
