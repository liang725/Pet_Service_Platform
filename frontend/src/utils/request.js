// src/utils/request.js - 简化版，只处理基本错误
const BASE_URL = 'http://localhost:8080'

async function request(options) {
  const { url, method = 'GET', data, params, headers = {} } = options

  // 构建完整URL
  let fullUrl = `${BASE_URL}${url}`

  // 处理查询参数
  if (params) {
    const queryString = new URLSearchParams(params).toString()
    fullUrl += `?${queryString}`
  }

  console.log(`[API Request] ${method} ${fullUrl}`)

  // 从 localStorage 获取 token
  const token = localStorage.getItem('token')

  // 配置请求选项
  const requestOptions = {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...headers
    }
  }

  // 如果有 token，添加到请求头
  if (token) {
    requestOptions.headers['Authorization'] = `Bearer ${token}`
  }

  // 处理请求体
  if (data) {
    requestOptions.body = JSON.stringify(data)
  }

  try {
    const response = await fetch(fullUrl, requestOptions)

    // 检查响应状态
    if (!response.ok) {
      const errorText = await response.text()
      const error = new Error(`HTTP ${response.status}: ${errorText}`)
      error.status = response.status
      error.response = response
      throw error
    }

    // 解析JSON响应
    const result = await response.json()
    console.log(`[API Response] ${fullUrl}`, result)

    // 检查业务代码
    // 兼容两种格式：{code: 200, data: ...} 和 直接返回数据
    if (result.code === 200 || result.code === undefined) {
      return result
    } else {
      // 对于业务逻辑错误，不抛出异常，而是返回结果让调用方处理
      return result
    }

  } catch (error) {
    console.error(`[API Error] ${method} ${fullUrl}:`, error.message)
    throw error
  }
}

export default request
