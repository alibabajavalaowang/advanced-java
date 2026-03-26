<template>
  <div class="family-member-list">
    <el-row :gutter="16">
      <!-- 左侧：家谱选择 -->
      <el-col :span="5">
        <el-card shadow="never" class="tree-select-card">
          <template #header><span>选择家谱</span></template>
          <el-tree
            :data="treeOptions"
            :props="{ label: 'name', children: 'children' }"
            highlight-current
            node-key="id"
            :default-expanded-keys="treeOptions.map((t: any) => t.id)"
            @node-click="handleTreeClick"
          />
          <el-empty v-if="treeOptions.length === 0" description="暂无家谱" :image-size="60" />
        </el-card>
      </el-col>

      <!-- 右侧：成员表格 -->
      <el-col :span="19">
        <!-- 筛选 -->
        <el-card shadow="never" class="mb-16px">
          <el-form :model="queryParams" :inline="true" label-width="68px">
            <el-form-item label="姓名">
              <el-input v-model="queryParams.name" placeholder="请输入" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="queryParams.gender" placeholder="请选择" clearable>
                <el-option label="男" :value="1" />
                <el-option label="女" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="辈分">
              <el-input v-model="queryParams.generation" placeholder="第几代" clearable type="number" />
            </el-form-item>
            <el-form-item label="在世">
              <el-select v-model="queryParams.isAlive" placeholder="请选择" clearable>
                <el-option label="是" :value="1" />
                <el-option label="否" :value="0" />
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

        <!-- 表格 -->
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>成员列表</span>
              <el-button type="primary" :disabled="!currentTreeId" @click="openForm('create')">
                <el-icon class="mr-4px"><Plus /></el-icon>新增成员
              </el-button>
            </div>
          </template>

          <el-table v-loading="loading" :data="list" stripe>
            <el-table-column label="头像" width="70" align="center">
              <template #default="{ row }">
                <el-avatar :size="36" :src="row.avatar">{{ row.name?.substring(0, 1) }}</el-avatar>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column label="性别" width="70" align="center">
              <template #default="{ row }">
                <el-tag :type="row.gender === 1 ? '' : 'danger'" size="small">
                  {{ row.gender === 1 ? '男' : '女' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="birthDate" label="出生日期" width="120" />
            <el-table-column label="辈分/字辈" width="120">
              <template #default="{ row }">
                <span>第{{ row.generation }}代</span>
                <span v-if="row.generationName"> / {{ row.generationName }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="birthplace" label="籍贯" width="120" />
            <el-table-column prop="occupation" label="职业" width="100" />
            <el-table-column label="在世" width="70" align="center">
              <template #default="{ row }">
                <el-tag :type="row.isAlive === 1 ? 'success' : 'info'" size="small">
                  {{ row.isAlive === 1 ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="goDetail(row.id!)">详情</el-button>
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
      </el-col>
    </el-row>

    <!-- 新增/编辑成员弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" destroy-on-close top="5vh">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="formData.birthDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否在世" prop="isAlive">
              <el-radio-group v-model="formData.isAlive" @change="handleAliveChange">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="formData.isAlive === 0">
          <el-col :span="12">
            <el-form-item label="去世日期" prop="deathDate">
              <el-date-picker v-model="formData.deathDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="辈分序号" prop="generation">
              <el-input-number v-model="formData.generation" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="字辈" prop="generationName">
              <el-input v-model="formData.generationName" placeholder="请输入字辈" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生地" prop="birthplace">
              <el-input v-model="formData.birthplace" placeholder="请输入出生地" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="现居地" prop="residence">
              <el-input v-model="formData.residence" placeholder="请输入现居地" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学历" prop="education">
              <el-select v-model="formData.education" placeholder="请选择" clearable style="width: 100%">
                <el-option label="小学" value="小学" />
                <el-option label="初中" value="初中" />
                <el-option label="高中" value="高中" />
                <el-option label="大专" value="大专" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职业" prop="occupation">
              <el-input v-model="formData.occupation" placeholder="请输入职业" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="父亲" prop="parentId">
              <el-select
                v-model="formData.parentId"
                filterable
                remote
                :remote-method="(q: string) => searchMember(q, 'parent')"
                placeholder="搜索选择父亲"
                clearable
                style="width: 100%"
              >
                <el-option v-for="m in parentOptions" :key="m.id" :label="m.name" :value="m.id!" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="母亲" prop="motherId">
              <el-select
                v-model="formData.motherId"
                filterable
                remote
                :remote-method="(q: string) => searchMember(q, 'mother')"
                placeholder="搜索选择母亲"
                clearable
                style="width: 100%"
              >
                <el-option v-for="m in motherOptions" :key="m.id" :label="m.name" :value="m.id!" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="配偶" prop="spouseId">
              <el-select
                v-model="formData.spouseId"
                filterable
                remote
                :remote-method="(q: string) => searchMember(q, 'spouse')"
                placeholder="搜索选择配偶"
                clearable
                style="width: 100%"
              >
                <el-option v-for="m in spouseOptions" :key="m.id" :label="m.name" :value="m.id!" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="主要成就" prop="achievements">
          <el-input v-model="formData.achievements" type="textarea" :rows="3" placeholder="请输入主要成就" />
        </el-form-item>
        <el-form-item label="生平简介" prop="biography">
          <el-input v-model="formData.biography" type="textarea" :rows="3" placeholder="请输入生平简介" />
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            action="/admin-api/infra/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            accept="image/*"
          >
            <el-avatar v-if="formData.avatar" :size="80" :src="formData.avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
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
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadProps } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getMyFamilyTreeList, type FamilyTreeVO } from '@/api/family/tree'
import {
  getFamilyMemberPage,
  getFamilyMember,
  createFamilyMember,
  updateFamilyMember,
  deleteFamilyMember,
  type FamilyMemberVO
} from '@/api/family/member'

defineOptions({ name: 'FamilyMemberList' })

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const list = ref<FamilyMemberVO[]>([])
const total = ref(0)
const currentTreeId = ref<number>()
const treeOptions = ref<FamilyTreeVO[]>([])

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  treeId: undefined as number | undefined,
  name: '',
  gender: undefined as number | undefined,
  generation: undefined as number | undefined,
  isAlive: undefined as number | undefined
})

/** 加载家谱列表 */
async function loadTreeList() {
  try {
    const data = await getMyFamilyTreeList()
    treeOptions.value = data || []
    // 如果URL带了treeId参数
    const urlTreeId = route.query.treeId
    if (urlTreeId) {
      currentTreeId.value = Number(urlTreeId)
      queryParams.treeId = currentTreeId.value
    } else if (treeOptions.value.length > 0) {
      currentTreeId.value = treeOptions.value[0].id
      queryParams.treeId = currentTreeId.value
    }
  } catch (e) {
    console.error('加载家谱列表失败', e)
  }
}

function handleTreeClick(data: FamilyTreeVO) {
  currentTreeId.value = data.id
  queryParams.treeId = data.id
  queryParams.pageNo = 1
  getList()
}

/** 查询列表 */
async function getList() {
  if (!queryParams.treeId) return
  loading.value = true
  try {
    const data = await getFamilyMemberPage(queryParams)
    list.value = data?.list || []
    total.value = data?.total || 0
  } catch (e) {
    console.error('加载成员列表失败', e)
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
  queryParams.gender = undefined
  queryParams.generation = undefined
  queryParams.isAlive = undefined
  queryParams.pageNo = 1
  getList()
}

function goDetail(id: number) {
  router.push('/family/member/detail/' + id)
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该成员吗？', '提示', { type: 'warning' })
    await deleteFamilyMember(id)
    ElMessage.success('删除成功')
    getList()
  } catch (e) {
    // 取消
  }
}

/** 表单相关 */
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const parentOptions = ref<FamilyMemberVO[]>([])
const motherOptions = ref<FamilyMemberVO[]>([])
const spouseOptions = ref<FamilyMemberVO[]>([])

function getDefaultFormData(): FamilyMemberVO {
  return {
    treeId: currentTreeId.value!,
    name: '',
    gender: 1,
    birthDate: '',
    deathDate: '',
    isAlive: 1,
    generation: undefined,
    generationName: '',
    birthplace: '',
    residence: '',
    phone: '',
    education: '',
    occupation: '',
    achievements: '',
    biography: '',
    avatar: '',
    parentId: undefined,
    motherId: undefined,
    spouseId: undefined,
    status: 0
  }
}

const formData = ref<FamilyMemberVO>(getDefaultFormData())

const formRules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

function handleAliveChange(val: number) {
  if (val === 1) {
    formData.value.deathDate = ''
  }
}

async function searchMember(query: string, type: 'parent' | 'mother' | 'spouse') {
  if (!query || query.length < 1) return
  try {
    const data = await getFamilyMemberPage({
      treeId: currentTreeId.value,
      name: query,
      pageNo: 1,
      pageSize: 20
    })
    const options = data?.list || []
    if (type === 'parent') parentOptions.value = options
    else if (type === 'mother') motherOptions.value = options
    else spouseOptions.value = options
  } catch (e) {
    console.error('搜索成员失败', e)
  }
}

async function openForm(type: 'create' | 'update', id?: number) {
  dialogVisible.value = true
  parentOptions.value = []
  motherOptions.value = []
  spouseOptions.value = []

  if (type === 'create') {
    dialogTitle.value = '新增成员'
    formData.value = getDefaultFormData()
  } else {
    dialogTitle.value = '编辑成员'
    try {
      const data = await getFamilyMember(id!)
      formData.value = data
      // 预加载关联成员选项
      if (data.parentId) {
        const parent = await getFamilyMember(data.parentId)
        if (parent) parentOptions.value = [parent]
      }
      if (data.motherId) {
        const mother = await getFamilyMember(data.motherId)
        if (mother) motherOptions.value = [mother]
      }
      if (data.spouseId) {
        const spouse = await getFamilyMember(data.spouseId)
        if (spouse) spouseOptions.value = [spouse]
      }
    } catch (e) {
      ElMessage.error('获取成员详情失败')
    }
  }
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (formData.value.id) {
      await updateFamilyMember(formData.value)
      ElMessage.success('修改成功')
    } else {
      await createFamilyMember(formData.value)
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

const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 0) {
    formData.value.avatar = response.data
  }
}

onMounted(async () => {
  await loadTreeList()
  if (currentTreeId.value) {
    getList()
  }
})
</script>

<style scoped>
.family-member-list {
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

.tree-select-card {
  min-height: calc(100vh - 140px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  overflow: hidden;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 24px;
  color: #8c939d;
}
</style>
