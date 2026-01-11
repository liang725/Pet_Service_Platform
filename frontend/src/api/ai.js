import request from '@/utils/request'

/**
 * AI聊天
 * @param {string} message 用户消息
 */
export function chatWithAi(message) {
  return request({
    url: '/api/ai/chat',
    method: 'post',
    data: { message }
  })
}

/**
 * 获取推荐问题
 */
export function getAiSuggestions() {
  return request({
    url: '/api/ai/suggestions',
    method: 'get'
  })
}
