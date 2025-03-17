<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()

function handleSelect() {}

const activeIndex = computed(() => {
  return route.path
})
</script>

<template>
  <el-menu :default-active="activeIndex" @select="handleSelect" class="menu_wrap" unique-opened router background-color="#1b1f22" text-color="#c5c5c5" active-text-color="#fff">
    <el-menu-item index="/back/audit">发布审核</el-menu-item>
    <el-menu-item index="/back/house">房屋管理</el-menu-item>
    <el-menu-item index="/back/book">预约管理</el-menu-item>
    <el-menu-item index="/back/customer">客户管理</el-menu-item>
    <el-menu-item index="/back/manager">系统管理员</el-menu-item>
    <!-- <el-submenu index="dataTransfer">
      <template v-slot:title>
        <i class="el-icon-sort"></i>
        <span>数据传输</span>
      </template>
      <el-menu-item index="/back">数据上传</el-menu-item>
      <el-menu-item index="/download">数据下载</el-menu-item>
    </el-submenu> -->
  </el-menu>
</template>

<style lang="scss" scoped>
.menu_wrap {
  width: 175px;
  height: 100%;
}
</style>
