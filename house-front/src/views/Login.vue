<template>
  <div class="login">
    <div class="login-island">
      <div class="login-island__cover"></div>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="form-container">
        <div class="title-container">
          <el-tabs v-model="formType" class="demo-tabs">
            <el-tab-pane label="Login" name="login"></el-tab-pane>
            <el-tab-pane label="Register" name="register"></el-tab-pane>
          </el-tabs>
        </div>

        <el-form-item prop="username">
          <el-input v-model="loginForm.username" autocomplete="off" :suffix-icon="Avatar" placeholder="Username" size="large" minlength="3" maxlength="16"> </el-input>
        </el-form-item>

        <el-form-item prop="password" class="password-box">
          <el-input
            ref="passwordRef"
            v-model="loginForm.password"
            autocomplete="off"
            :type="showPwd ? 'text' : 'password'"
            :suffix-icon="Flag"
            placeholder="Password"
            size="large"
            minlength="6"
            maxlength="16"
          >
          </el-input>
          <span class="password-icon" @click="togglePwd()">
            <svg-icon :icon-class="showPwd ? 'open' : 'close'"></svg-icon>
          </span>
        </el-form-item>

        <el-form-item v-if="formType === 'register'" prop="rePassword" class="password-box">
          <el-input v-model="loginForm.rePassword" autocomplete="off" type="password" :suffix-icon="Flag" placeholder="Re-password" size="large" minlength="6" maxlength="16"> </el-input>
        </el-form-item>

        <el-form-item v-if="formType === 'login'" prop="captcha">
          <el-input class="captch-input" v-model="loginForm.captcha" autocomplete="off" placeholder="Captcha" size="large"> </el-input>
          <img class="captch-input__img" @click="updateCaptcha" :src="captchaUrl" alt="" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width: 100%; margin-bottom: 30px" class="submit-btn" @click.prevent="submitForm">Login</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, useTemplateRef } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '@/api/login'
import md5 from 'md5'
import { Avatar, Flag } from '@element-plus/icons-vue'
import { useInfoStore } from '@/stores/userInfo'
const store = useInfoStore()
const router = useRouter()

const rules = reactive({
  username: [
    { required: true, message: 'Please enter username' },
    { min: 3, message: 'At least 3 characters', trigger: 'blur' },
  ],
  password: [
    { required: true, message: 'Please enter password' },
    { min: 6, message: 'At least 3 characters', trigger: 'blur' },
  ],
  rePassword: [
    { required: true, message: 'Please enter password' },
    { min: 6, message: 'At least 3 characters', trigger: 'blur' },
  ],
  captcha: [{ required: true, message: 'Please enter captcha' }],
})

/**
 * 密码
 */
const showPwd = ref(false)
const passwordRef = useTemplateRef('passwordRef')
function togglePwd() {
  showPwd.value = !showPwd.value
  // 否则焦点在最前面
  this.$nextTick(() => {
    passwordRef.value.password.focus()
  })
}

/**
 * 验证码
 */
const captchaUrl = ref(`${import.meta.env.VITE_BASE_API}/user/permit/captcha`)
function updateCaptcha() {
  captchaUrl.value = `${import.meta.env.VITE_BASE_API}/user/permit/captcha?v=${new Date().getTime()}`
}

/**
 * 登录
 */
const redirect = ref('') // 回源地址
const otherQuery = ref('') // 路径其他传值
const formType = ref('login') // 表单类型
const loginFormRef = useTemplateRef('loginFormRef')
const loginForm = reactive({
  username: 'admin',
  password: 'zsxdc153',
  captcha: '123',
})
async function submitForm() {
  const isLogin = formType.value === 'login'

  if (!isLogin && loginForm.password !== loginForm.rePassword) {
    return ElMessage.error("Passwords don't match")
  }
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      isLogin ? loginFn() : registerFn()
    } else {
      return false
    }
  })
}

async function loginFn() {
  const param = {
    username: loginForm.username,
    password: md5(loginForm.password),
    captcha: loginForm.captcha,
  }
  const res = await login(param)
  if (res.status === 200) {
    store.setUser(res.data)
    store.setToken(res.data.token)
    router.push({ path: redirect.value || '/', query: otherQuery.value })
  } else {
    ElMessage.error(res.message)
  }
}

async function registerFn() {
  const param = {
    username: loginForm.username,
    password: md5(loginForm.password),
  }
  const res = await register(param)
  if (res.status === 200) {
    if (res.data) {
      ElMessage.success('registered successfully!')
      formType.value = 'login' // 切换到登录Tab
    } else {
      ElMessage.error(res.message || 'Registration failed.')
    }
  } else {
    ElMessage.error(res.message)
  }
}
</script>

<style lang="scss" scoped>
.login {
  // background-color: #2d3a4b;
  background: url('@/assets/img/login-bg.jpg') no-repeat;
  background-size: cover;
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  .login-island {
    width: 805px;
    height: 332px;
    display: flex;
    box-shadow: 0px 0px 100px 45px #fff;
    .login-island__cover {
      width: 60%;
      height: 100%;
      background: url('@/assets/img/login-island-cover.jpg') no-repeat;
      background-size: cover;
    }
    .form-container {
      width: 355px;
      padding: 10px 40px;
      background: #fff;
      .title {
        text-align: center;
      }
      .password-box {
        position: relative;
        .password-icon {
          position: absolute;
          right: 0;
          width: 40px;
          text-align: center;
          cursor: pointer;
        }
      }
    }
  }
}

.captch-input {
  width: calc(100% - 110px);
}
.captch-input__img {
  background: #fff;
  border-radius: 4px;
  vertical-align: middle;
  margin-left: 10px;
  cursor: pointer;
}

.username-input {
  width: calc(100% - 110px);

  &__btn {
    width: 100px;
    margin-left: 10px;
  }
}

.submit-btn {
  width: 100%;
  height: 45px;
  margin-top: 20px;
}
</style>

<style lang="scss">
.login-island {
  .el-tabs {
    .el-tabs__nav-wrap:after {
      height: 0 !important;
    }
    .el-tabs__item {
      width: 72px;
    }
  }

  .el-input {
    .el-input__wrapper {
      box-shadow: none !important;
      border-radius: 0px;
      border-bottom: 1px solid #eee;
    }
  }

  .el-form-item__error {
    margin-top: 4px;
    margin-left: 15px;
  }
}
</style>
