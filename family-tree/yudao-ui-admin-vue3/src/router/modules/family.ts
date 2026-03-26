import type { AppRouteRecordRaw } from '@/router/types'
import { LAYOUT } from '@/router/constant'

const family: AppRouteRecordRaw = {
  path: '/family',
  component: LAYOUT,
  name: 'Family',
  meta: { title: '家谱管理', icon: 'ep:house', alwaysShow: true },
  children: [
    {
      path: 'dashboard',
      name: 'FamilyDashboard',
      component: () => import('@/views/family/dashboard/index.vue'),
      meta: { title: '家族总览', icon: 'ep:data-analysis' }
    },
    {
      path: 'tree',
      name: 'FamilyTreeList',
      component: () => import('@/views/family/tree/index.vue'),
      meta: { title: '家谱列表', icon: 'ep:notebook' }
    },
    {
      path: 'tree/detail/:id',
      name: 'FamilyTreeDetail',
      component: () => import('@/views/family/tree/detail.vue'),
      meta: { title: '家谱详情', noCache: true, hidden: true, activeMenu: '/family/tree' }
    },
    {
      path: 'member',
      name: 'FamilyMember',
      component: () => import('@/views/family/member/index.vue'),
      meta: { title: '成员管理', icon: 'ep:user' }
    },
    {
      path: 'member/detail/:id',
      name: 'FamilyMemberDetail',
      component: () => import('@/views/family/member/detail.vue'),
      meta: { title: '成员详情', noCache: true, hidden: true, activeMenu: '/family/member' }
    },
    {
      path: 'chart/:id',
      name: 'FamilyChart',
      component: () => import('@/views/family/chart/index.vue'),
      meta: { title: '世系图谱', icon: 'ep:share', noCache: true, hidden: true, activeMenu: '/family/tree' }
    },
    {
      path: 'album',
      name: 'FamilyAlbum',
      component: () => import('@/views/family/album/index.vue'),
      meta: { title: '家族相册', icon: 'ep:picture' }
    },
    {
      path: 'event',
      name: 'FamilyEvent',
      component: () => import('@/views/family/event/index.vue'),
      meta: { title: '大事记', icon: 'ep:calendar' }
    },
    {
      path: 'rank',
      name: 'GenerationRank',
      component: () => import('@/views/family/rank/index.vue'),
      meta: { title: '字辈管理', icon: 'ep:sort' }
    },
    {
      path: 'notice',
      name: 'FamilyNotice',
      component: () => import('@/views/family/notice/index.vue'),
      meta: { title: '家族公告', icon: 'ep:bell' }
    },
    {
      path: 'ai',
      name: 'FamilyAi',
      component: () => import('@/views/family/ai/index.vue'),
      meta: { title: 'AI助手', icon: 'ep:magic-stick' }
    }
  ]
}

export default family
