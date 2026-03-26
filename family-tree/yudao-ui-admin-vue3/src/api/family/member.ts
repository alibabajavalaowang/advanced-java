import request from '@/config/axios'

export interface FamilyMemberVO {
  id?: number
  treeId: number
  name: string
  gender: number
  birthDate?: string
  deathDate?: string
  isAlive: number
  generation?: number
  generationName?: string
  birthplace?: string
  residence?: string
  phone?: string
  education?: string
  occupation?: string
  achievements?: string
  biography?: string
  avatar?: string
  parentId?: number
  motherId?: number
  spouseId?: number
  sortOrder?: number
  userId?: number
  status: number
  createTime?: Date
  parentName?: string
  spouseName?: string
}

// 家谱树节点
export interface FamilyMemberTreeVO {
  id: number
  name: string
  gender: number
  birthDate?: string
  deathDate?: string
  isAlive: boolean
  generation?: number
  generationName?: string
  avatar?: string
  occupation?: string
  parentId?: number
  spouseId?: number
  spouseName?: string
  spouseGender?: number
  spouseAvatar?: string
  children?: FamilyMemberTreeVO[]
}

// 统计
export interface FamilyTreeStatsVO {
  totalMembers: number
  aliveMembers: number
  maleCount: number
  femaleCount: number
  generationCount: number
  averageAge: number
  generationDistribution: Record<string, number>
  provinceDistribution: Record<string, number>
  ageDistribution: Record<string, number>
  recentEvents: any[]
}

// CRUD
export const createFamilyMember = (data: FamilyMemberVO) => {
  return request.post({ url: '/family/member/create', data })
}
export const updateFamilyMember = (data: FamilyMemberVO) => {
  return request.put({ url: '/family/member/update', data })
}
export const deleteFamilyMember = (id: number) => {
  return request.delete({ url: '/family/member/delete?id=' + id })
}
export const getFamilyMember = (id: number) => {
  return request.get({ url: '/family/member/get?id=' + id })
}
export const getFamilyMemberPage = (params: any) => {
  return request.get({ url: '/family/member/page', params })
}
// 获取家谱树
export const getMemberTree = (treeId: number) => {
  return request.get({ url: '/family/member/tree?treeId=' + treeId })
}
// 统计数据
export const getTreeStats = (treeId: number) => {
  return request.get({ url: '/family/member/stats?treeId=' + treeId })
}
// 获取子女
export const getChildren = (parentId: number) => {
  return request.get({ url: '/family/member/children?parentId=' + parentId })
}
