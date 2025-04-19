<script setup>
import Header from './front-header.vue'
</script>

<template>
  <div class="client-page">
    <Header />
    <div class="client-page-content">
      <router-view />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.client-page {
  height: 100%;
  position: relative;
  .client-page-content {
    height: 100%;
    min-height: 100%;
    padding-top: 50px;
    margin: 0 auto;
  }
}
</style>
