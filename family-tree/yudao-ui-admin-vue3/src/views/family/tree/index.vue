<template>
  <div class="family-tree-list">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="mb-16px">
      <el-form :model="queryParams" :inline="true" label-width="68px">
        <el-form-item label="名称">
          <el-input v-model="queryParams.name" placeholder="请输入家谱名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="姓氏">
          <el-input v-model="queryParams.surname" placeholder="请输入姓氏" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="停用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon class="mr-4px"><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon class="mr-4px"><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>家谱列表</span>
          <el-button type="primary" @click="openForm('create')">
            <el-icon class="mr-4px"><Plus /></el-icon>新增家谱
          </el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="surname" label="姓氏" width="100" />
        <el-table-column prop="hallName" label="堂号" width="120" />
        <el-table-column prop="memberCount" label="成员数" width="90" align="center" />
        <el-table-column prop="generationCount" label="世代数" width="90" align="center" />
        <el-table-column label="可见性" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.visibility === 0 ? 'success' : row.visibility === 1 ? 'warning' : 'info'" size="small">
              {{ row.visibility === 0 ? '公开' : row.visibility === 1 ? '族内可见' : '私有' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170" align="center">
          <template #default="{ row }">
            {{ row.createTime ? new Date(row.createTime).toLocaleString() : '' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="goDetail(row.id!)">查看详情</el-button>
            <el-button link type="primary" @click="goChart(row.id!)">世系图谱</el-button>
            <el-button link type="primary" @click="openForm('update', row.id!)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row.id!)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="mt-16px"
        v-model:current-page="queryParams.pageNo"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入家谱名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓氏" prop="surname">
              <el-input v-model="formData.surname" placeholder="请输入姓氏" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="堂号" prop="hallName">
              <el-input v-model="formData.hallName" placeholder="请输入堂号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="郡望/发源地" prop="origin">
              <el-input v-model="formData.origin" placeholder="请输入郡望/发源地" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="始祖姓名" prop="ancestorName">
              <el-input v-model="formData.ancestorName" placeholder="请输入始祖姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="可见性" prop="visibility">
              <el-radio-group v-model="formData.visibility">
                <el-radio :label="0">公开</el-radio>
                <el-radio :label="1">族内可见</el-radio>
                <el-radio :label="2">私有</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="始祖故事" prop="ancestorStory">
          <el-input v-model="formData.ancestorStory" type="textarea" :rows="3" placeholder="请输入始祖故事" />
        </el-form-item>
        <el-form-item label="家训" prop="motto">
          <el-input v-model="formData.motto" type="textarea" :rows="3" placeholder="请输入家训" />
        </el-form-item>
        <el-form-item label="族规" prop="clanRules">
          <el-input v-model="formData.clanRules" type="textarea" :rows="3" placeholder="请输入族规" />
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入简介" />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <el-upload
            class="cover-uploader"
            action="/admin-api/infra/file/upload"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            accept="image/*"
          >
            <el-image
              v-if="formData.coverImage"
              :src="formData.coverImage"
              style="width: 178px; height: 120px"
              fit="cover"
            />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadProps } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getFamilyTreePage,
  getFamilyTree,
  createFamilyTree,
  updateFamilyTree,
  deleteFamilyTree,
  type FamilyTreeVO
} from '@/api/family/tree'

defineOptions({ name: 'FamilyTreeList' })

const router = useRouter()
const loading = ref(false)
const list = ref<FamilyTreeVO[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: '',
  surname: '',
  status: undefined as number | undefined
})

/** 查询列表 */
async function getList() {
  loading.value = true
  try {
    const data = await getFamilyTreePage(queryParams)
    list.value = data?.list || []
    total.value = data?.total || 0
  } catch (e) {
    console.error('加载列表失败', e)
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.pageNo = 1
  getList()
}

function resetQuery() {
  queryParams.name = ''
  queryParams.surname = ''
  queryParams.status = undefined
  queryParams.pageNo = 1
  getList()
}

/** 跳转详情 */
function goDetail(id: number) {
  router.push('/family/tree/detail/' + id)
}

/** 跳转世系图谱 */
function goChart(id: number) {
  router.push('/family/chart?treeId=' + id)
}

/** 删除 */
async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该家谱吗？删除后不可恢复！', '提示', {
      type: 'warning'
    })
    await deleteFamilyTree(id)
    ElMessage.success('删除成功')
    getList()
  } catch (e) {
    // 取消操作
  }
}

/** 表单相关 */
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const formData = ref<FamilyTreeVO>({
  name: '',
  surname: '',
  hallName: '',
  origin: '',
  ancestorName: '',
  ancestorStory: '',
  motto: '',
  clanRules: '',
  description: '',
  coverImage: '',
  visibility: 0,
  status: 0
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入家谱名称', trigger: 'blur' }],
  surname: [{ required: true, message: '请输入姓氏', trigger: 'blur' }]
}

async function openForm(type: 'create' | 'update', id?: number) {
  dialogVisible.value = true
  if (type === 'create') {
    dialogTitle.value = '新增家谱'
    formData.value = {
      name: '',
      surname: '',
      hallName: '',
      origin: '',
      ancestorName: '',
      ancestorStory: '',
      motto: '',
      clanRules: '',
      description: '',
      coverImage: '',
      visibility: 0,
      status: 0
    }
  } else {
    dialogTitle.value = '编辑家谱'
    try {
      const data = await getFamilyTree(id!)
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
      await updateFamilyTree(formData.value)
      ElMessage.success('修改成功')
    } else {
      await createFamilyTree(formData.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (e) {
    console.error('提交失败', e)
  } finally {
    submitLoading.value = false
  }
}

const handleCoverSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 0) {
    formData.value.coverImage = response.data
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.family-tree-list {
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

.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  width: 178px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
</style>
