<template>
  <div class="house-detail-page common-page">
    <h1 class="page-title">{{ houseDetail.title }}</h1>
    <div class="detail-wrap">
      <div class="image-list">
        <el-image v-for="url in houseDetail.imgList" :key="url" :src="url" lazy />
      </div>
      <div class="house-overview">
        <div class="detail-price">
          <div class="total-price">{{ houseDetail.expectPrice }}万</div>
          <!-- <div class="unit-price">{{ houseDetail.unitPrice }}元/平</div> -->
        </div>
        <!-- <div class="house-prop">
          <HomeFilled class="small-icon" />
          {{ houseDetail.model }} -- {{ houseDetail.area }}平米
        </div> -->
        <div><MapLocation class="item-info__icon" />{{ houseDetail.address }}</div>
        <div><OfficeBuilding class="item-info__icon" />Layout：{{ getLayoutTxt(houseDetail.layout) }}</div>
        <div><Message class="item-info__icon" />Area: {{ houseDetail.area }} ㎡</div>
        <div><Message class="item-info__icon" />Agent: {{ houseDetail.agent }} / {{ houseDetail.agentPhone || '--' }}</div>
        <div><Clock class="item-info__icon" />{{ houseDetail.updateTime }}</div>
        <el-button type="success" @click="bookView">预约看房</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { queryHouseDetail } from '@/api/house'
import { MapLocation, OfficeBuilding, Message, Clock } from '@element-plus/icons-vue'
import 'vue3-carousel/carousel.css'
import { useRoute } from 'vue-router'
const route = useRoute()

const houseId = ref('')
const houseDetail = ref({})
onMounted(() => {
  houseId.value = route.params.id
  getHouseDetail()
})

function getLayoutTxt(layout) {
  return `${layout?.toLowerCase()}-bedroom`
}

async function getHouseDetail() {
  try {
    const res = await queryHouseDetail({ id: houseId.value })
    if (res.status === 200) {
      houseDetail.value = res.data || {}
      console.log(houseDetail.value)
    } else {
      this.$message.error(res.message)
    }
  } catch (e) {
    console.log(e)
  }
}

/**
 * 预约看房
 */
function bookView() {}
</script>

<style lang="scss" scoped>
.house-detail-page {
  padding-top: 30px;

  .image-list {
    border: 1px solid #eee;
  }
  .page-title {
    margin-bottom: 20px;
  }
  .detail-wrap {
    display: flex;
    .image-list {
      width: 640px;
      height: 400px;
      overflow-y: auto;
      .el-image {
        display: block;
        min-height: 200px;
        margin-bottom: 10px;
      }
      .el-image:last-child {
        margin-bottom: 0;
      }
    }
    .house-overview {
      padding-left: 30px;
      font-size: 18px;
      line-height: 51px;
      .detail-price {
        display: flex;
        align-items: center;
        .total-price {
          color: var(--theme-color);
          font-size: 26px;
          font-weight: bold;
          margin-right: 12px;
        }
      }
      .house-prop {
        display: flex;
        align-items: center;
        font-weight: bold;
      }
    }
  }
  .item-info__icon {
    width: 16px;
    height: 16px;
    margin-right: 8px;
  }

  .small-icon {
    width: 20px;
    vertical-align: middle;
    margin-right: 8px;
  }
}
</style>
