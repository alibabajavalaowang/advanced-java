import request from '@/config/axios'

export interface GenerationRankVO {
  id?: number
  treeId: number
  generationNumber: number
  rankChar: string
  meaning?: string
  sortOrder?: number
  status: number
  createTime?: Date
}

// 创建
export const createGenerationRank = (data: GenerationRankVO) => {
  return request.post({ url: '/family/generation-rank/create', data })
}
// 更新
export const updateGenerationRank = (data: GenerationRankVO) => {
  return request.put({ url: '/family/generation-rank/update', data })
}
// 删除
export const deleteGenerationRank = (id: number) => {
  return request.delete({ url: '/family/generation-rank/delete?id=' + id })
}
// 获取详情
export const getGenerationRank = (id: number) => {
  return request.get({ url: '/family/generation-rank/get?id=' + id })
}
// 分页列表
export const getGenerationRankPage = (params: any) => {
  return request.get({ url: '/family/generation-rank/page', params })
}
// 根据家谱ID获取列表
export const getListByTreeId = (treeId: number) => {
  return request.get({ url: '/family/generation-rank/list-by-tree?treeId=' + treeId })
}
