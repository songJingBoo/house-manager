<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useInfoStore } from '@/stores/userInfo'
const route = useRoute()
const store = useInfoStore()

function handleSelect() {}

const activeIndex = computed(() => {
  return route.path
})
const menuList = computed(() => {
  return store.menuList
})
</script>

<template>
  <el-menu
    class="menu_wrap"
    :default-active="activeIndex"
    @select="handleSelect"
    unique-opened router
    background-color="#1b1f22" 
    text-color="#c5c5c5" 
    active-text-color="#fff"
  >
    <el-menu-item v-for="(menu ,index) in menuList" :key="index" :index="menu.route">{{ menu.name }}</el-menu-item>
  </el-menu>
</template>

<style lang="scss" scoped>
.menu_wrap {
  width: 175px;
  height: 100%;
}
</style>
