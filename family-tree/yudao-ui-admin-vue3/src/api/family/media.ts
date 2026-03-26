import request from '@/config/axios'

export interface FamilyMediaVO {
  id?: number
  treeId: number
  memberId?: number
  title?: string
  description?: string
  mediaType: number
  mediaUrl: string
  thumbnailUrl?: string
  fileSize?: number
  sortOrder?: number
  status: number
  createTime?: Date
}

// 创建
export const createFamilyMedia = (data: FamilyMediaVO) => {
  return request.post({ url: '/family/media/create', data })
}
// 更新
export const updateFamilyMedia = (data: FamilyMediaVO) => {
  return request.put({ url: '/family/media/update', data })
}
// 删除
export const deleteFamilyMedia = (id: number) => {
  return request.delete({ url: '/family/media/delete?id=' + id })
}
// 获取详情
export const getFamilyMedia = (id: number) => {
  return request.get({ url: '/family/media/get?id=' + id })
}
// 分页列表
export const getFamilyMediaPage = (params: any) => {
  return request.get({ url: '/family/media/page', params })
}
