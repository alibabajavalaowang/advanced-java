<template>
  <div class="family-album">
    <!-- 顶部操作栏 -->
    <el-card shadow="never" class="mb-16px">
      <div class="album-header">
        <div class="header-left">
          <el-select v-model="currentTreeId" placeholder="选择家谱" style="width: 200px" @change="handleTreeChange">
            <el-option v-for="item in treeList" :key="item.id" :label="item.name" :value="item.id!" />
          </el-select>
          <el-select v-model="queryParams.memberId" placeholder="筛选成员" clearable filterable style="width: 180px; margin-left: 12px" @change="loadPhotos">
            <el-option label="全部" :value="undefined" />
          </el-select>
        </div>
        <el-upload
          action="/admin-api/infra/file/upload"
          :show-file-list="false"
          :on-success="handleUploadSuccess"
          accept="image/*"
          :disabled="!currentTreeId"
        >
          <el-button type="primary" :disabled="!currentTreeId">
            <el-icon class="mr-4px"><Upload /></el-icon>上传照片
          </el-button>
        </el-upload>
      </div>
    </el-card>

    <!-- 照片网格 -->
    <el-row :gutter="16" v-loading="loading">
      <el-col
        v-for="photo in photoList"
        :key="photo.id"
        :xs="12"
        :sm="8"
        :md="6"
        :lg="4"
        class="mb-16px"
      >
        <el-card shadow="hover" class="photo-card" :body-style="{ padding: '0' }">
          <el-image
            :src="photo.thumbnailUrl || photo.mediaUrl"
            fit="cover"
            class="photo-image"
            :preview-src-list="photoList.map(p => p.mediaUrl)"
            :initial-index="photoList.indexOf(photo)"
            lazy
          />
          <div class="photo-info">
            <h4 class="photo-title">{{ photo.title || '未命名' }}</h4>
            <p class="photo-desc" v-if="photo.description">{{ photo.description }}</p>
            <div class="photo-meta">
              <span class="photo-date" v-if="photo.createTime">
                {{ new Date(photo.createTime).toLocaleDateString() }}
              </span>
              <el-button link type="danger" size="small" @click="handleDeletePhoto(photo.id!)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && photoList.length === 0" description="暂无照片" />

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > queryParams.pageSize">
      <el-pagination
        v-model:current-page="queryParams.pageNo"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[12, 24, 48]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadPhotos"
        @current-change="loadPhotos"
      />
    </div>

    <!-- 上传成功后编辑信息弹窗 -->
    <el-dialog v-model="editDialogVisible" title="照片信息" width="500px" destroy-on-close>
      <el-form ref="editFormRef" :model="editForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="关联成员">
          <el-input-number v-model="editForm.memberId" placeholder="成员ID" style="width: 100%" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPhotoInfo">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type UploadProps } from 'element-plus'
import { Upload, Delete } from '@element-plus/icons-vue'
import { getMyFamilyTreeList, type FamilyTreeVO } from '@/api/family/tree'
import {
  getFamilyMediaPage,
  createFamilyMedia,
  deleteFamilyMedia,
  type FamilyMediaVO
} from '@/api/family/media'

defineOptions({ name: 'FamilyAlbum' })

const loading = ref(false)
const currentTreeId = ref<number>()
const treeList = ref<FamilyTreeVO[]>([])
const photoList = ref<FamilyMediaVO[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNo: 1,
  pageSize: 24,
  treeId: undefined as number | undefined,
  memberId: undefined as number | undefined,
  mediaType: 0 // 图片类型
})

const editDialogVisible = ref(false)
const editFormRef = ref()
const editForm = ref<FamilyMediaVO>({
  treeId: 0,
  title: '',
  description: '',
  mediaType: 0,
  mediaUrl: '',
  memberId: undefined,
  status: 0
})

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
    console.error('加载家谱列表失败', e)
  }
}

/** 加载照片列表 */
async function loadPhotos() {
  if (!currentTreeId.value) return
  queryParams.treeId = currentTreeId.value
  loading.value = true
  try {
    const data = await getFamilyMediaPage(queryParams)
    photoList.value = data?.list || []
    total.value = data?.total || 0
  } catch (e) {
    console.error('加载照片失败', e)
  } finally {
    loading.value = false
  }
}

function handleTreeChange() {
  queryParams.pageNo = 1
  loadPhotos()
}

/** 上传成功 */
const handleUploadSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 0) {
    editForm.value = {
      treeId: currentTreeId.value!,
      title: '',
      description: '',
      mediaType: 0,
      mediaUrl: response.data,
      memberId: undefined,
      status: 0
    }
    editDialogVisible.value = true
  } else {
    ElMessage.error('上传失败')
  }
}

/** 保存照片信息 */
async function submitPhotoInfo() {
  try {
    await createFamilyMedia(editForm.value)
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    loadPhotos()
  } catch (e) {
    console.error('保存失败', e)
  }
}

/** 删除照片 */
async function handleDeletePhoto(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该照片吗？', '提示', { type: 'warning' })
    await deleteFamilyMedia(id)
    ElMessage.success('删除成功')
    loadPhotos()
  } catch (e) {
    // 取消
  }
}

onMounted(async () => {
  await loadTreeList()
  if (currentTreeId.value) {
    loadPhotos()
  }
})
</script>

<style scoped>
.family-album {
  padding: 16px;
}

.mb-16px {
  margin-bottom: 16px;
}

.mr-4px {
  margin-right: 4px;
}

.album-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.photo-card {
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s;
}

.photo-card:hover {
  transform: translateY(-4px);
}

.photo-image {
  width: 100%;
  height: 200px;
  display: block;
}

.photo-info {
  padding: 12px;
}

.photo-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.photo-desc {
  font-size: 12px;
  color: #909399;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.photo-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.photo-date {
  font-size: 12px;
  color: #c0c4cc;
}

.pagination-wrap {
  text-align: center;
  margin-top: 16px;
}
</style>
