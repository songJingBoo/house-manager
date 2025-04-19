import request from '@/utils/request'

// 获取预约列表
export function queryAppointmentList(data) {
  return request({
    url: '/back/appoint/queryAppointList',
    method: 'post',
    data,
  })
}

// 审核预约申请
export function auditAppoint(data) {
  return request({
    url: '/back/appoint/auditAppoint',
    method: 'post',
    data,
  })
}
