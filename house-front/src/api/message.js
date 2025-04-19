import request from '@/utils/request'

// 获取消息
export function queryMessage(params) {
  return request({
    url: '/message/get',
    method: 'get',
    params,
  })
}
// 设置消息为已读
export function updateRead(data) {
  return request({
    url: '/message/read',
    method: 'post',
    data,
  })
}
