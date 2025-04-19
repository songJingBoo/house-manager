import request from '@/utils/request'

export function queryMyHouses(params) {
  return request({
    url: '/my/houses',
    method: 'get',
    params,
  })
}
export function queryMyLikes(params) {
  return request({
    url: '/my/likes',
    method: 'get',
    params,
  })
}

export function queryMyAppointment(params) {
  return request({
    url: '/my/appoints',
    method: 'get',
    params,
  })
}
