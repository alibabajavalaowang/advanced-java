<template>
  <div class="member-detail" v-loading="loading">
    <el-page-header @back="$router.back()" content="成员详情" class="mb-16px" />

    <el-row :gutter="20">
      <!-- 左侧：头像 + 基本信息 -->
      <el-col :span="8">
        <el-card shadow="never" class="profile-card">
          <div class="profile-header">
            <el-avatar :size="120" :src="member.avatar" class="profile-avatar">
              {{ member.name?.substring(0, 1) }}
            </el-avatar>
            <h2 class="profile-name">{{ member.name }}</h2>
            <div class="profile-tags">
              <el-tag :type="member.gender === 1 ? '' : 'danger'" size="small">
                {{ member.gender === 1 ? '男' : '女' }}
              </el-tag>
              <el-tag v-if="member.generationName" type="warning" size="small" class="ml-8px">
                {{ member.generationName }}字辈
              </el-tag>
              <el-tag :type="member.isAlive === 1 ? 'success' : 'info'" size="small" class="ml-8px">
                {{ member.isAlive === 1 ? '在世' : '已故' }}
              </el-tag>
            </div>
          </div>

          <el-divider />

          <div class="profile-info">
            <div class="info-item" v-if="member.birthDate">
              <span class="info-label">出生日期</span>
              <span class="info-value">{{ member.birthDate }}</span>
            </div>
            <div class="info-item" v-if="member.deathDate">
              <span class="info-label">去世日期</span>
              <span class="info-value">{{ member.deathDate }}</span>
            </div>
            <div class="info-item" v-if="member.generation">
              <span class="info-label">辈分</span>
              <span class="info-value">第{{ member.generation }}代</span>
            </div>
            <div class="info-item" v-if="member.birthplace">
              <span class="info-label">出生地</span>
              <span class="info-value">{{ member.birthplace }}</span>
            </div>
            <div class="info-item" v-if="member.residence">
              <span class="info-label">现居地</span>
              <span class="info-value">{{ member.residence }}</span>
            </div>
            <div class="info-item" v-if="member.phone">
              <span class="info-label">联系电话</span>
              <span class="info-value">{{ member.phone }}</span>
            </div>
            <div class="info-item" v-if="member.education">
              <span class="info-label">学历</span>
              <span class="info-value">{{ member.education }}</span>
            </div>
            <div class="info-item" v-if="member.occupation">
              <span class="info-label">职业</span>
              <span class="info-value">{{ member.occupation }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧 -->
      <el-col :span="16">
        <!-- 详细信息 -->
        <el-card shadow="never" class="mb-16px">
          <template #header><span>详细信息</span></template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ member.name }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ member.gender === 1 ? '男' : '女' }}</el-descriptions-item>
            <el-descriptions-item label="出生日期">{{ member.birthDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="去世日期">{{ member.deathDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="是否在世">{{ member.isAlive === 1 ? '是' : '否' }}</el-descriptions-item>
            <el-descriptions-item label="辈分序号">{{ member.generation ? `第${member.generation}代` : '-' }}</el-descriptions-item>
            <el-descriptions-item label="字辈">{{ member.generationName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="出生地">{{ member.birthplace || '-' }}</el-descriptions-item>
            <el-descriptions-item label="现居地">{{ member.residence || '-' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ member.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="学历">{{ member.education || '-' }}</el-descriptions-item>
            <el-descriptions-item label="职业">{{ member.occupation || '-' }}</el-descriptions-item>
            <el-descriptions-item label="主要成就" :span="2">{{ member.achievements || '-' }}</el-descriptions-item>
            <el-descriptions-item label="生平简介" :span="2">{{ member.biography || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 家庭关系 -->
        <el-card shadow="never" class="mb-16px">
          <template #header><span>家庭关系</span></template>
          <el-row :gutter="16">
            <el-col :span="6" v-if="fatherInfo">
              <div class="relation-card" @click="goMemberDetail(fatherInfo.id!)">
                <el-avatar :size="48" :src="fatherInfo.avatar">{{ fatherInfo.name?.substring(0, 1) }}</el-avatar>
                <div class="relation-info">
                  <div class="relation-type">父亲</div>
                  <div class="relation-name">{{ fatherInfo.name }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6" v-if="motherInfo">
              <div class="relation-card" @click="goMemberDetail(motherInfo.id!)">
                <el-avatar :size="48" :src="motherInfo.avatar">{{ motherInfo.name?.substring(0, 1) }}</el-avatar>
                <div class="relation-info">
                  <div class="relation-type">母亲</div>
                  <div class="relation-name">{{ motherInfo.name }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6" v-if="spouseInfo">
              <div class="relation-card" @click="goMemberDetail(spouseInfo.id!)">
                <el-avatar :size="48" :src="spouseInfo.avatar">{{ spouseInfo.name?.substring(0, 1) }}</el-avatar>
                <div class="relation-info">
                  <div class="relation-type">配偶</div>
                  <div class="relation-name">{{ spouseInfo.name }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6" v-for="child in children" :key="child.id">
              <div class="relation-card" @click="goMemberDetail(child.id!)">
                <el-avatar :size="48" :src="child.avatar">{{ child.name?.substring(0, 1) }}</el-avatar>
                <div class="relation-info">
                  <div class="relation-type">{{ child.gender === 1 ? '儿子' : '女儿' }}</div>
                  <div class="relation-name">{{ child.name }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
          <el-empty v-if="!fatherInfo && !motherInfo && !spouseInfo && children.length === 0"
            description="暂无家庭关系记录" :image-size="60" />
        </el-card>

        <!-- 生平事迹时间线 -->
        <el-card shadow="never" class="mb-16px">
          <template #header><span>生平事迹</span></template>
          <el-timeline v-if="events.length > 0">
            <el-timeline-item
              v-for="event in events"
              :key="event.id"
              :timestamp="event.eventDate"
              :type="getEventColor(event.eventType)"
              placement="top"
            >
              <h4>{{ event.title }}</h4>
              <p class="event-text">{{ event.content }}</p>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无生平事迹" :image-size="60" />
        </el-card>

        <!-- 相关照片 -->
        <el-card shadow="never">
          <template #header><span>相关照片</span></template>
          <div class="photo-grid" v-if="photos.length > 0">
            <el-image
              v-for="photo in photos"
              :key="photo.id"
              :src="photo.mediaUrl"
              fit="cover"
              class="photo-item"
              :preview-src-list="photos.map(p => p.mediaUrl)"
              lazy
            />
          </div>
          <el-empty v-else description="暂无相关照片" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getFamilyMember, getChildren, type FamilyMemberVO } from '@/api/family/member'
import { getFamilyEventPage, type FamilyEventVO } from '@/api/family/event'
import { getFamilyMediaPage, type FamilyMediaVO } from '@/api/family/media'

defineOptions({ name: 'FamilyMemberDetail' })

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const memberId = ref<number>(Number(route.params.id))

const member = ref<FamilyMemberVO>({
  treeId: 0,
  name: '',
  gender: 1,
  isAlive: 1,
  status: 0
})

const fatherInfo = ref<FamilyMemberVO | null>(null)
const motherInfo = ref<FamilyMemberVO | null>(null)
const spouseInfo = ref<FamilyMemberVO | null>(null)
const children = ref<FamilyMemberVO[]>([])
const events = ref<FamilyEventVO[]>([])
const photos = ref<FamilyMediaVO[]>([])

function getEventColor(type?: number): string {
  const colorMap: Record<number, string> = {
    0: 'primary',
    1: 'success',
    2: 'danger',
    3: 'warning'
  }
  return colorMap[type || 0] || 'primary'
}

function goMemberDetail(id: number) {
  router.push('/family/member/detail/' + id)
}

async function loadMemberDetail() {
  loading.value = true
  try {
    const data = await getFamilyMember(memberId.value)
    if (data) {
      member.value = data
      // 加载关联关系
      if (data.parentId) {
        try { fatherInfo.value = await getFamilyMember(data.parentId) } catch (e) { /* ignore */ }
      }
      if (data.motherId) {
        try { motherInfo.value = await getFamilyMember(data.motherId) } catch (e) { /* ignore */ }
      }
      if (data.spouseId) {
        try { spouseInfo.value = await getFamilyMember(data.spouseId) } catch (e) { /* ignore */ }
      }
      // 加载子女
      try {
        const childData = await getChildren(memberId.value)
        children.value = childData || []
      } catch (e) { /* ignore */ }
    }
  } catch (e) {
    console.error('加载成员详情失败', e)
  } finally {
    loading.value = false
  }
}

async function loadEvents() {
  try {
    const data = await getFamilyEventPage({
      treeId: member.value.treeId,
      participants: String(memberId.value),
      pageNo: 1,
      pageSize: 50
    })
    events.value = data?.list || []
  } catch (e) {
    console.error('加载事件失败', e)
  }
}

async function loadPhotos() {
  try {
    const data = await getFamilyMediaPage({
      memberId: memberId.value,
      mediaType: 0,
      pageNo: 1,
      pageSize: 20
    })
    photos.value = data?.list || []
  } catch (e) {
    console.error('加载照片失败', e)
  }
}

onMounted(async () => {
  await loadMemberDetail()
  loadEvents()
  loadPhotos()
})
</script>

<style scoped>
.member-detail {
  padding: 16px;
}

.mb-16px {
  margin-bottom: 16px;
}

.ml-8px {
  margin-left: 8px;
}

.profile-card {
  text-align: center;
}

.profile-header {
  padding: 20px 0;
}

.profile-avatar {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.profile-name {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin: 16px 0 8px;
}

.profile-tags {
  margin-top: 8px;
}

.profile-info {
  text-align: left;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  color: #909399;
  font-size: 14px;
}

.info-value {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.relation-card {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 12px;
}

.relation-card:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.relation-info {
  margin-left: 12px;
}

.relation-type {
  font-size: 12px;
  color: #909399;
}

.relation-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-top: 2px;
}

.event-text {
  font-size: 13px;
  color: #666;
  margin-top: 4px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.photo-item {
  width: 100%;
  height: 150px;
  border-radius: 8px;
  cursor: pointer;
}
</style>
