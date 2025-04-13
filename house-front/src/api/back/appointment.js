import request from '@/utils/request'

// 获取预约列表
export function queryAppointmentList(data) {
  return request({
    url: '/back/appoint/queryAppointList',
    method: 'post',
    data,
  })
}
