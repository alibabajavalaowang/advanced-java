import request from '@/config/axios'

export interface FamilyTreeVO {
  id?: number
  name: string
  surname: string
  hallName?: string
  origin?: string
  motto?: string
  clanRules?: string
  description?: string
  ancestorName?: string
  ancestorStory?: string
  coverImage?: string
  visibility: number
  memberCount?: number
  generationCount?: number
  status: number
  createTime?: Date
}

// 创建
export const createFamilyTree = (data: FamilyTreeVO) => {
  return request.post({ url: '/family/tree/create', data })
}
// 更新
export const updateFamilyTree = (data: FamilyTreeVO) => {
  return request.put({ url: '/family/tree/update', data })
}
// 删除
export const deleteFamilyTree = (id: number) => {
  return request.delete({ url: '/family/tree/delete?id=' + id })
}
// 获取详情
export const getFamilyTree = (id: number) => {
  return request.get({ url: '/family/tree/get?id=' + id })
}
// 分页列表
export const getFamilyTreePage = (params: any) => {
  return request.get({ url: '/family/tree/page', params })
}
// 我的家谱列表
export const getMyFamilyTreeList = () => {
  return request.get({ url: '/family/tree/my-list' })
}
