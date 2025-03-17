import request from '@/utils/request'

// 获取管理员列表
export function queryManagerList(data) {
  return request({
    url: '/back/manager/list',
    method: 'post',
    data,
  })
}

// 保存管理员
export function saveManager(data) {
  return request({
    url: '/back/manager/save',
    method: 'post',
    data,
  })
}

// 删除管理员
export function deleteManager(params) {
  return request({
    url: '/back/manager/delete',
    method: 'delete',
    params,
  })
}
