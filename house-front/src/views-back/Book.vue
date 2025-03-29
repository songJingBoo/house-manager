<template>
  <div>
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="待定" name="pending"></el-tab-pane>
      <el-tab-pane label="已确认" name="confirmed"></el-tab-pane>
      <el-tab-pane label="已取消" name="cancelled"></el-tab-pane>
    </el-tabs>
    <div v-if="activeTab === 'pending'">
      <!-- 待定状态列表及编辑功能 -->
    </div>
    <div v-if="activeTab === 'confirmed'">
      <!-- 已确认状态列表 -->
    </div>
    <div v-if="activeTab === 'cancelled'">
      <!-- 已取消状态列表 -->
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeTab = ref('pending')

const handleTabClick = (tab) => {
  // 处理Tab切换逻辑
}
</script>

<style scoped>
/* 样式 */
</style>

<style lang="scss"></style>
