import request from '@/utils/request'

// 文件上传
export function upload(data) {
  return request({
    url: '/file/upload',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}