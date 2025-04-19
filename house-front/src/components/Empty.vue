<template>
  <div class="al-empty" :style="{ 'padding-top': paddingTop + 'px' }">
    <div>
      <div class="al-empty__img" :class="[size === 'small' ? 'al-empty__img--small' : '', imgClass]"></div>
      <div class="al-empty__text">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps(['imgClass', 'size', 'paddingTop'])
</script>

<style lang="scss" scoped>
.al-empty {
  height: 100%;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  &__img {
    height: 200px;
    width: 200px;
    margin: 0 auto;
    background: url(@/assets/icon/empty.svg) no-repeat;
  }
  &__text {
    color: #999898;
    margin-top: 7px;
    font-size: 14px;
  }
}
</style>
