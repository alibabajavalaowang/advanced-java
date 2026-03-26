import request from '@/config/axios'

export interface FamilyEventVO {
  id?: number
  treeId: number
  title: string
  content?: string
  eventDate: string
  eventType?: number
  location?: string
  participants?: string
  images?: string
  sortOrder?: number
  status: number
  createTime?: Date
}

// 创建
export const createFamilyEvent = (data: FamilyEventVO) => {
  return request.post({ url: '/family/event/create', data })
}
// 更新
export const updateFamilyEvent = (data: FamilyEventVO) => {
  return request.put({ url: '/family/event/update', data })
}
// 删除
export const deleteFamilyEvent = (id: number) => {
  return request.delete({ url: '/family/event/delete?id=' + id })
}
// 获取详情
export const getFamilyEvent = (id: number) => {
  return request.get({ url: '/family/event/get?id=' + id })
}
// 分页列表
export const getFamilyEventPage = (params: any) => {
  return request.get({ url: '/family/event/page', params })
}
