import { ref } from 'vue'
import { defineStore } from 'pinia'

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
    function setUser(obj) {
      user.value = obj
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
    }
  },
  {
    persist: true, // 持久化全部
  },
)
