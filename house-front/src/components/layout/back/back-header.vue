<script setup>
import { ref } from 'vue'
import { UserFilled } from '@element-plus/icons-vue'
import { logout } from '@/api/login'
import { useRouter, useRoute } from 'vue-router'
import { useInfoStore } from '@/stores/userInfo'
const store = useInfoStore()
const router = useRouter()
const route = useRoute()

// 菜单列表
const menuList = ref([
  { name: '房产列表', path: '/list', pathname: 'HouseList' },
  { name: '发布房源', path: '/publish', pathname: 'HousePublish' },
])
// 菜单跳转
function linkTo(pathname) {
  router.push({ name: pathname })
}

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

const selected = (menu) => {
  return route.path == menu.path
}
</script>

<template>
  <div class="page-header">
    <h3 class="page-header__logo">房屋租售管理系统</h3>
    <div class="page-header__content">
      <div class="menu">
        <div v-for="item in menuList" class="menu-item" :class="{ selected: selected(item) }" :key="item.path" @click="linkTo(item.pathname)">
          {{ item.name }}
        </div>
      </div>

      <div class="user-center">
        <el-avatar class="user-center__icon" :icon="UserFilled" />
        <el-dropdown placement="bottom-end">
          <div class="user-center__name">{{ store.user?.username }}</div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="logoutFn">Logout</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.page-header {
  width: 100%;
  height: 40px;
  background: #101d37;
  z-index: 100;

  .page-header__logo {
    position: absolute;
    color: #fff;
    padding-left: 20px;
    line-height: 40px;
  }

  .page-header__content {
    height: 100%;
    padding-left: 192px;
    padding-right: 20px;
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
        &:hover {
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
    }
  }
}
</style>
