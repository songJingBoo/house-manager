import request from '@/utils/request'

// 获取用户列表
export function queryCustomerList(params) {
  return request({
    url: '/back/user/list',
    method: 'get',
    params,
  })
}
