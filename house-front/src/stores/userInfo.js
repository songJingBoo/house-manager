import { ref } from 'vue'
import { defineStore } from 'pinia'
import { menus } from '@/config/role'

export const useInfoStore = defineStore(
  'user-info',
  () => {
    const token = ref('')
    function setToken(t) {
      token.value = t
    }
    function removeToken() {
      token.value = ''
    }

    const user = ref({})
    const menuList = ref({})
    function setUser(obj) {
      user.value = obj
      menuList.value = obj.role ? menus.filter(menu => {
        return menu.role.includes(obj.role)
      }) : []
    }
    function removeUser() {
      user.value = {}
    }

    return {
      token,
      setToken,
      removeToken,

      user,
      setUser,
      removeUser,
      menuList,
    }
  },
  {
    persist: true, // 持久化全部
  },
)
