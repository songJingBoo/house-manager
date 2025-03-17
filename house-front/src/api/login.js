import request from '@/utils/request'
// 注册
export function register(data) {
  return request({
    url: '/user/permit/register',
    method: 'post',
    data,
  })
}
// 登录
export function login(data) {
  return request({
    url: '/user/permit/login',
    method: 'post',
    data,
  })
}
// 验证码
export function captche() {
  return request({
    url: '/user/permit/captcha',
    method: 'get',
  })
}
// 登出
export function logout(params) {
  return request({
    url: '/user/logout',
    method: 'get',
    params,
  })
}
