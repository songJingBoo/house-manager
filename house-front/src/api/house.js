import request from '@/utils/request'
import { Res, filterConfig } from '@/mock'

// 获取房屋列表
export function queryHouseList(data) {
  return request({
    url: '/house/list',
    method: 'post',
    data,
  })
}
// 获取房屋列表
export function queryFilterConfig(params) {
  // return request({
  //   url: '/house/filter',
  //   method: 'get',
  //   params,
  // })
  return Res(filterConfig)
}
// 获取房屋详情
export function queryHouseDetail(params) {
  return request({
    url: '/house/detail',
    method: 'get',
    params,
  })
}
// 发布房源
export function publishHouse(data) {
  return request({
    url: '/house/create',
    method: 'post',
    data,
  })
}
