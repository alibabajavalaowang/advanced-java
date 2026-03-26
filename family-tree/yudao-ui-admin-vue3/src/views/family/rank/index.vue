<template>
  <div class="generation-rank">
    <!-- 顶部操作栏 -->
    <el-card shadow="never" class="mb-16px">
      <div class="rank-header">
        <el-select v-model="currentTreeId" placeholder="选择家谱" style="width: 200px" @change="handleTreeChange">
          <el-option v-for="item in treeList" :key="item.id" :label="item.name" :value="item.id!" />
        </el-select>
        <el-button type="primary" :disabled="!currentTreeId" @click="openForm('create')">
          <el-icon class="mr-4px"><Plus /></el-icon>新增字辈
        </el-button>
      </div>
    </el-card>

    <!-- 字辈诗展示 -->
    <el-card shadow="never" class="mb-16px poem-card" v-if="rankList.length > 0">
      <div class="poem-section">
        <h3 class="poem-label">字辈诗</h3>
        <div class="poem-text">
          <span
            v-for="(rank, index) in rankList"
            :key="rank.id"
            class="poem-char"
            :title="`第${rank.generationNumber}代 - ${rank.meaning || ''}`"
          >{{ rank.rankChar }}</span>
        </div>
        <p class="poem-hint">（鼠标悬停可查看各字辈说明）</p>
      </div>
    </el-card>

    <!-- 字辈表格 -->
    <el-card shadow="never" v-loading="loading">
      <template #header><span>字辈列表</span></template>
      <el-table :data="rankList" stripe>
        <el-table-column prop="generationNumber" label="世代序号" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">第{{ row.generationNumber }}代</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rankChar" label="字辈用字" width="120" align="center">
          <template #default="{ row }">
            <span class="rank-char-display">{{ row.rankChar }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="meaning" label="说明" min-width="300">
          <template #default="{ row }">
            {{ row.meaning || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openForm('update', row.id!)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row.id!)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && rankList.length === 0" description="暂无字辈信息" />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="世代序号" prop="generationNumber">
          <el-input-number v-model="formData.generationNumber" :min="1" :max="200" style="width: 100%" />
        </el-form-item>
        <el-form-item label="字辈用字" prop="rankChar">
          <el-input v-model="formData.rankChar" placeholder="请输入字辈用字" maxlength="2" />
        </el-form-item>
        <el-form-item label="说明" prop="meaning">
          <el-input v-model="formData.meaning" type="textarea" :rows="3" placeholder="请输入说明" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMyFamilyTreeList, type FamilyTreeVO } from '@/api/family/tree'
import {
  getListByTreeId,
  getGenerationRank,
  createGenerationRank,
  updateGenerationRank,
  deleteGenerationRank,
  type GenerationRankVO
} from '@/api/family/rank'

defineOptions({ name: 'GenerationRank' })

const loading = ref(false)
const currentTreeId = ref<number>()
const treeList = ref<FamilyTreeVO[]>([])
const rankList = ref<GenerationRankVO[]>([])

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

/** 加载字辈列表 */
async function loadRankList() {
  if (!currentTreeId.value) return
  loading.value = true
  try {
    const data = await getListByTreeId(currentTreeId.value)
    rankList.value = (data || []).sort((a: GenerationRankVO, b: GenerationRankVO) =>
      (a.generationNumber || 0) - (b.generationNumber || 0)
    )
  } catch (e) {
    console.error('加载字辈失败', e)
  } finally {
    loading.value = false
  }
}

function handleTreeChange() {
  loadRankList()
}

/** 删除 */
async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该字辈吗？', '提示', { type: 'warning' })
    await deleteGenerationRank(id)
    ElMessage.success('删除成功')
    loadRankList()
  } catch (e) {
    // 取消
  }
}

/** 表单 */
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const formData = ref<GenerationRankVO>({
  treeId: 0,
  generationNumber: 1,
  rankChar: '',
  meaning: '',
  sortOrder: 0,
  status: 0
})

const formRules: FormRules = {
  generationNumber: [{ required: true, message: '请输入世代序号', trigger: 'blur' }],
  rankChar: [{ required: true, message: '请输入字辈用字', trigger: 'blur' }]
}

async function openForm(type: 'create' | 'update', id?: number) {
  dialogVisible.value = true
  if (type === 'create') {
    dialogTitle.value = '新增字辈'
    const nextGen = rankList.value.length > 0
      ? Math.max(...rankList.value.map(r => r.generationNumber)) + 1
      : 1
    formData.value = {
      treeId: currentTreeId.value!,
      generationNumber: nextGen,
      rankChar: '',
      meaning: '',
      sortOrder: nextGen,
      status: 0
    }
  } else {
    dialogTitle.value = '编辑字辈'
    try {
      const data = await getGenerationRank(id!)
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
      await updateGenerationRank(formData.value)
      ElMessage.success('修改成功')
    } else {
      await createGenerationRank(formData.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadRankList()
  } catch (e) {
    console.error('提交失败', e)
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  await loadTreeList()
  if (currentTreeId.value) {
    loadRankList()
  }
})
</script>

<style scoped>
.generation-rank {
  padding: 16px;
}

.mb-16px {
  margin-bottom: 16px;
}

.mr-4px {
  margin-right: 4px;
}

.rank-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.poem-card {
  background: linear-gradient(135deg, #fdf6ec, #fef9f0);
}

.poem-section {
  text-align: center;
  padding: 16px 0;
}

.poem-label {
  font-size: 16px;
  color: #8B6914;
  margin-bottom: 16px;
  font-weight: 600;
}

.poem-text {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 4px;
}

.poem-char {
  display: inline-block;
  width: 48px;
  height: 48px;
  line-height: 48px;
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  color: #8B4513;
  font-family: 'KaiTi', 'STKaiti', serif;
  cursor: default;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.poem-char:hover {
  color: #c0392b;
  border-bottom-color: #c0392b;
  transform: scale(1.15);
}

.poem-hint {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 12px;
}

.rank-char-display {
  font-size: 22px;
  font-weight: 700;
  color: #8B4513;
  font-family: 'KaiTi', 'STKaiti', serif;
}
</style>
