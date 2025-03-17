export default {
  validatorAccount(rule, value, callback) {
    if (!/^1\d{10}$/.test(value)) {
      callback(new Error('请输入正确格式的手机号！'))
    } else {
      callback()
    }
  },
}
