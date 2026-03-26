import request from '@/config/axios'

export interface CozeAiReqVO {
  treeId: number
  prompt: string
  type: 'story' | 'motto' | 'poem' | 'summary'
}

export interface CozeAiRespVO {
  content: string
  type: string
  treeId: number
}

export const generateAiContent = (data: CozeAiReqVO) => {
  return request.post({ url: '/family/ai/generate', data })
}
