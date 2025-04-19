<script setup>
import Header from './back-header.vue'
import Menu from './back-menu.vue'
</script>

<template>
  <div class="back-page">
    <Header />
    <div class="back-page-content">
      <div class="back-page-content__menu">
        <Menu />
      </div>
      <div class="back-page-content__container">
        <router-view />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.back-page {
  min-width: 1174px;
  height: 100%;
  position: relative;
  .back-page-content {
    display: flex;
    height: calc(100% - 50px);
    .back-page-content__menu {
      flex-shrink: 0;
    }
    .back-page-content__container {
      flex-grow: 1;
      min-width: 900px;
      margin: 10px 20px;
    }
  }
}
</style>
