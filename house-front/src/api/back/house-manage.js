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

// 获取过滤项
export function queryHouseFilter(params) {
  return request({
    url: '/back/filter/get',
    method: 'get',
    params,
  })
}

// 保存过滤项
export function saveHouseFilter(data) {
  return request({
    url: '/back/filter/config',
    method: 'post',
    data,
  })
}
