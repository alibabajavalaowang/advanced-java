import request from '@/config/axios'

export interface FamilyNoticeVO {
  id?: number
  treeId: number
  title: string
  content: string
  noticeType?: number
  topFlag?: number
  publishTime?: Date
  publishStatus?: number
  status: number
  createTime?: Date
}

// 创建
export const createFamilyNotice = (data: FamilyNoticeVO) => {
  return request.post({ url: '/family/notice/create', data })
}
// 更新
export const updateFamilyNotice = (data: FamilyNoticeVO) => {
  return request.put({ url: '/family/notice/update', data })
}
// 删除
export const deleteFamilyNotice = (id: number) => {
  return request.delete({ url: '/family/notice/delete?id=' + id })
}
// 获取详情
export const getFamilyNotice = (id: number) => {
  return request.get({ url: '/family/notice/get?id=' + id })
}
// 分页列表
export const getFamilyNoticePage = (params: any) => {
  return request.get({ url: '/family/notice/page', params })
}
// 发布公告
export const publishFamilyNotice = (id: number) => {
  return request.put({ url: '/family/notice/publish?id=' + id })
}
