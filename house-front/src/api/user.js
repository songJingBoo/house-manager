import request from '@/utils/request'

// 获取当前用户关注的房屋
export function register(params) {
  return request({
    url: '/my/likes',
    method: 'get',
    params,
  })
}

// 获取当前用户发布的房屋
export function register(params) {
  return request({
    url: '/my/houses',
    method: 'get',
    params,
  })
}