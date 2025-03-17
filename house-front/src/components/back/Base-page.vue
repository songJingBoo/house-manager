<template>
  <div class="common-page">
    <h2 class="common-page__title">{{ title }}</h2>
    <div class="common-page__content">
      <div v-if="$slots.header" class="common-page__content--header">
        <slot name="header" />
      </div>

      <div v-if="$slots.operate" class="common-page__content--operate">
        <slot name="operate" />
      </div>

      <div class="common-page__content--content">
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup>
const { title } = defineProps(['title'])
</script>

<style lang="scss" scoped>
.common-page {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  .common-page__title {
    margin-bottom: 15px;
  }
  .common-page__content {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    background: #fbfbfb;
    border-radius: 4px;
    padding: 20px;
    .common-page__content--header {
      flex-shrink: 0;
      min-height: 36px;
      border-bottom: 1px solid #eee;
    }
    .common-page__content--operate {
      min-height: 36px;
      padding: 8px 0;
      border-bottom: 1px solid #eee;
    }
    .common-page__content--content {
      flex-grow: 1;
    }
  }
}
</style>
