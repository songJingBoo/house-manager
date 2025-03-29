import request from '@/utils/request'

// 获取发布列表
export function queryHouseList(data) {
  return request({
    url: '/back/list',
    method: 'post',
    data,
  })
}

// 更改信息
export function editHouse(data) {
  return request({
    url: '/back/edit',
    method: 'post',
    data,
  })
}
