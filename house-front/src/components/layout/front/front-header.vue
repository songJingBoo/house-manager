<script setup>
import { ref, onMounted, useTemplateRef, computed } from 'vue'
import { UserFilled, Bell, Warning } from '@element-plus/icons-vue'
import { logout } from '@/api/login'
import { queryMessage, updateRead } from '@/api/message'
import ResetPasswordDialog from '@/components/reset-password-dialog.vue'
import { useRouter, useRoute } from 'vue-router'
import { useInfoStore } from '@/stores/userInfo'
const store = useInfoStore()
const router = useRouter()
const route = useRoute()

onMounted(() => {
  queryMessageFn()
})

// 菜单列表
const menus = [
  { name: '房产列表', path: '/list', pathname: 'HouseList' },
  { name: '发布房源', path: '/publish', pathname: 'HousePublish' },
  { name: '系统管理', path: '/back', pathname: 'Back', disabledRoles: ['Customer'] },
]
const menuList = computed(() => {
  return menus.filter((menu) => !menu.disabledRoles || !menu.disabledRoles.includes(store.user.role))
})

// 菜单跳转
function linkTo(pathname) {
  router.push({ name: pathname })
}
// 跳转房屋详情
function linkToHouse(id) {
  router.push({ name: 'HouseDetail', params: { id } })
}
// 跳转用户中心
function linkToUserCenter() {
  router.push({ name: 'UserCenter' })
}

/**
 * 登出
 */
async function logoutFn() {
  try {
    await logout()
  } catch (e) {
    console.log('Logout err:', e)
  } finally {
    store.removeToken()
    linkTo('Login')
  }
}

/**
 * 获取消息列表
 */
const messageLoading = ref(false)
const messageList = ref([])
async function queryMessageFn() {
  try {
    messageLoading.value = true
    const res = await queryMessage()
    if (res.status === 200) {
      messageList.value = res.data
    }
  } catch (e) {
    console.log('Logout err:', e)
  } finally {
    messageLoading.value = false
  }
}
// 未读消息数
const unreadNum = computed(() => {
  return (messageList.value || []).filter((item) => !item.isRead).length
})

/**
 * 消息弹窗展示（消息更新为已读）
 */
async function showMessageFn() {
  if (!unreadNum.value) return

  try {
    const res = await updateRead({
      ids: messageList.value.filter((item) => !item.isRead).map((item) => item.id),
    })
    if (res.status === 200) {
      setTimeout(() => {
        queryMessageFn()
      }, 1000)
    }
  } catch (e) {
    console.log('Logout err:', e)
  }
}

/**
 * 重置密码弹窗
 */
const resetPasswordRef = useTemplateRef('resetPasswordRef')
function openResetPasswordDialog() {
  resetPasswordRef.value.open()
}

// 选中的菜单
const selected = (menu) => {
  return route.path == menu.path
}
</script>

<template>
  <div class="page-header">
    <div class="page-header__content">
      <div class="menu">
        <div v-for="item in menuList" class="menu-item" :class="{ selected: selected(item) }" :key="item.path" @click="linkTo(item.pathname)">
          {{ item.name }}
        </div>
      </div>

      <div class="user-center">
        <!-- 站内信 -->
        <el-popover placement="top" :width="260" :hide-after="0" trigger="click" popper-class="message-popover" @show="showMessageFn">
          <template #reference>
            <el-badge :value="unreadNum" :hidden="unreadNum === 0" class="message-bell"><Bell /></el-badge>
          </template>
          <div class="message-wrap">
            <div class="message-wrap__content" :loading="messageLoading">
              <el-scrollbar v-if="!messageLoading && messageList.length" wrap-class="message-scrollbar">
                <div class="message" v-for="item in messageList" :key="item.id" @click="linkToHouse(item.houseId)">
                  <div class="message__title">
                    <template v-if="!item.isRead">
                      <el-badge is-dot class="item">{{ item.title }}</el-badge>
                    </template>
                    <template v-else>{{ item.title }}</template>
                  </div>
                  <div class="message__content">{{ item.content }}</div>
                </div>
              </el-scrollbar>
              <el-empty v-if="!messageLoading && !messageList.length" :image-size="90" description="暂无消息~" />
            </div>

            <div class="message-wrap__tip">
              <Warning class="message-tip__icon" />
              点击查看对应房屋详情
            </div>
          </div>
        </el-popover>
        <el-avatar class="user-center__icon" :icon="UserFilled" />
        <el-dropdown placement="bottom-end">
          <div class="user-center__name">{{ store.user?.username }}</div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="linkToUserCenter">User Center</el-dropdown-item>
              <el-dropdown-item @click="openResetPasswordDialog">Reset Password</el-dropdown-item>
              <el-dropdown-item @click="logoutFn">Logout</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 重置密码 -->
    <ResetPasswordDialog ref="resetPasswordRef" @submit="logoutFn" />
  </div>
</template>

<style lang="scss" scoped>
.page-header {
  width: 100%;
  height: 50px;
  background: #101d37;
  position: fixed;
  top: 0;
  z-index: 100;

  .page-header__content {
    width: 1174px;
    height: 100%;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .menu {
      display: flex;
      .menu-item {
        padding: 0 4px;
        margin-right: 20px;
        cursor: pointer;
        color: #a9abab;
        &:hover,
        &.selected {
          color: #fff;
        }
      }
    }

    .user-center {
      color: #fff;
      display: flex;
      align-items: center;
      &:hover {
        cursor: pointer;
      }
      .user-center__icon {
        height: 24px;
        width: 24px;
        margin-right: 10px;
      }
      .user-center__name {
        color: #fff;
      }

      .message-bell {
        width: 22px;
        margin-right: 20px;
        margin-bottom: -4px;
      }
    }
  }
}
</style>

<style lang="scss">
.message-wrap {
  height: 300px;
  background: #fff;
  border-radius: 4px;
  .message-wrap__content {
    height: calc(100% - 36px);
    .message {
      padding: 5px 10px;
      margin: 8px 0;
      border-radius: 4px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      .message__title {
        font-weight: bold;
        font-size: 13px;
      }
      .message__content {
        font-size: 12px;
      }
      &:hover {
        cursor: pointer;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3);
      }
    }
  }
  .message-wrap__tip {
    font-size: 12px;
    color: #ccc;
    display: flex;
    align-items: center;
    margin-top: 10px;
    line-height: 26px;
    padding-left: 10px;
    .message-tip__icon {
      width: 14px;
      margin-right: 5px;
    }
  }
}

.message-scrollbar {
  padding: 0 10px;
}

.message-popover {
  padding: 0 !important;
}
</style>
