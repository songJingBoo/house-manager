import request from '@/utils/request'

// 获取发布列表
export function queryAuditList(data) {
  return request({
    url: '/back/audit/list',
    method: 'post',
    data,
  })
}

// 更改信息
export function editInformation(data) {
  return request({
    url: '/back/audit/edit',
    method: 'post',
    data,
  })
}

// 审核信息
export function auditRecord(data) {
  return request({
    url: '/back/audit/status',
    method: 'post',
    data,
  })
}
