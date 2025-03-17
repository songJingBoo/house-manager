<template>
  <div class="house-list-page common-page">
    <div class="house-filter">
      <div class="filter-item" v-for="item in filterList" :key="item.code">
        <div class="filter-item__name">{{ item.name }}</div>
        <div class="filter-item__options">
          <el-checkbox-group v-model="item.checked" @change="onFilterChange">
            <el-checkbox v-for="(opt, i) in item.options" :key="i" :label="opt.label" :value="opt.value" />
          </el-checkbox-group>
          <!-- <div class="option" v-for="(opt, i) in item.options" :key="i">
            <span>{{ opt.min }}</span>
            <span>{{ opt.eq }}</span>
            <span>{{ opt.max }}</span>
          </div> -->
        </div>
      </div>
    </div>

    <div class="house-content">
      <div class="house-content--left">
        <div class="house-sort">
          <el-tabs v-model="sortType" class="theme" @tab-click="getHouseList">
            <el-tab-pane label="最新发布" name="createTime"></el-tab-pane>
            <el-tab-pane label="总价" name="totalPrice"></el-tab-pane>
            <el-tab-pane label="单价" name="unitPrice"></el-tab-pane>
            <el-tab-pane label="面积" name=""></el-tab-pane>
          </el-tabs>
        </div>

        <div class="house-total">共找到 {{ total }} 套房源</div>

        <div class="house-list">
          <div class="house-list__item" v-for="item in houseList" :key="item.houseId">
            <img class="house-img cursor" :src="item.img" @click="linkToDetail(item.houseId)" />
            <div class="house-info">
              <div class="title" @click="linkToDetail(item.houseId)">
                {{ item.title }}
                <span v-if="item.status === 'PENDING'" class="status status--pending">审核</span>
                <span v-if="item.status === 'AVAILABLE'" class="status status--available">{{ item.intention === 'RENT' ? '待租' : '在售' }}</span>
                <span v-if="item.status === 'RENTED'" class="status status--rented">已租</span>
                <span v-if="item.status === 'SOLD'" class="status status--sold">已售</span>
                <span v-if="item.status === 'UNAVAILABLE'" class="status status--unavailable">下架</span>
              </div>
              <div class="detail">
                <div class="detail__left">
                  <div class="item-info"><MapLocation class="item-info__icon" />{{ item.address }}</div>
                  <div class="item-info"><OfficeBuilding class="item-info__icon" />Layout: {{ getLayoutTxt(item.layout) }}&nbsp;&nbsp;&nbsp;Area: {{ item.area }} ㎡</div>
                  <div class="item-info"><Message class="item-info__icon" />Agent: {{ item.agentName }} / {{ item.agentPhone || '--' }}</div>
                  <div class="item-info"><Clock class="item-info__icon" />{{ item.updateTime }}</div>
                </div>
                <div class="detail__price">
                  <div class="total-price">{{ item.expectPrice }}万</div>
                  <div class="unit-price">{{ item.unitPrice }}元/平</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="house-content--right">
        <div class="title">热门房源</div>
        <div class="hot-list">
          <div class="hot-list__item" v-for="item in houseList" :key="item.houseId">
            <img class="hot-img cursor" :src="item.img" @click="linkToDetail(item.houseId)" />
            <div class="hot-info">
              <div class="title" @click="linkToDetail(item.houseId)">
                <div class="text-over" style="max-width: calc(100% - 40px)">{{ item.title }}</div>
                <span v-if="item.status === 'PENDING'" class="status status--pending">审核</span>
                <span v-if="item.status === 'AVAILABLE'" class="status status--available">{{ item.intention === 'RENT' ? '待租' : '在售' }}</span>
                <span v-if="item.status === 'RENTED'" class="status status--rented">已租</span>
                <span v-if="item.status === 'SOLD'" class="status status--sold">已售</span>
                <span v-if="item.status === 'UNAVAILABLE'" class="status status--unavailable">下架</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { queryHouseList, queryFilterConfig } from '@/api/house'
import { ElMessage } from 'element-plus'
import { MapLocation, OfficeBuilding, Message, Clock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
const router = useRouter()

onMounted(() => {
  getFilterConfig()
  getHouseList()
})

// 过滤项

/**
 * 获取过滤项目[min, max)
 */
const filterList = ref([])
async function getFilterConfig() {
  try {
    const res = await queryFilterConfig()
    if (res.code === 200) {
      filterList.value = handlerFilter(res.data)
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    console.log(e)
  }
}

/**
 * 选中过滤项
 */
function onFilterChange() {
  const filterChecked = filterList.value
    .filter((item) => item.checked?.length > 0)
    .map((item) => {
      return {
        code: item.code,
        checked: item.checked,
      }
    })
  console.log(filterChecked)
}

// 处理过滤项（计算单项label / value值）
function handlerFilter(filterList) {
  ;(filterList || []).forEach((item) => {
    item.options.forEach((opt) => {
      const { min, eq, max } = opt
      // 固定值
      if (eq !== undefined) {
        opt.label = `${eq}${item.suffix}`
        opt.value = eq
        return
      }

      // 区间值 [min, max)
      if (min !== undefined && max !== undefined) {
        opt.label = `${min}-${max}${item.suffix}`
        opt.value = { min, max }
        return
      }

      // 半区间值 < max
      if (min === undefined && max !== undefined) {
        opt.label = `${max}${item.suffix}以下`
        opt.value = { max }
        return
      }

      // 半区间值 >= min
      if (min !== undefined && max === undefined) {
        opt.label = `${min}${item.suffix}以上`
        opt.value = { min }
        return
      }
    })
  })
  return filterList
}

function getLayoutTxt(layout) {
  return `${layout.toLowerCase()}-bedroom`
}

/**
 * 获取房屋列表
 */
const sortType = ref('createTime')
const total = ref(0)
const houseList = ref([])
async function getHouseList() {
  try {
    const res = await queryHouseList({})
    if (res.status === 200) {
      houseList.value = res.data || []
      total.value = res.data.length || 0
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    console.log(e)
  }
}

/**
 * 跳转房屋详情页
 * @param id
 */
function linkToDetail(id) {
  router.push({ name: 'HouseDetail', params: { id } })
}
</script>

<style lang="scss" scoped>
.house-list-page {
  .house-filter {
    background: #fbfbfb;
    border-radius: 4px;
    margin-top: 26px;
    padding: 15px 25px;
    .filter-item {
      display: flex;
      align-items: flex-start;
      .filter-item__name {
        margin-right: 20px;
        line-height: 32px;
        font-weight: bold;
      }
    }
  }

  .house-sort {
    margin-top: 20px;
  }

  .house-total {
    line-height: 48px;
    font-size: 20px;
    font-weight: bold;
    border-bottom: 1px solid #eee;
  }

  .house-content {
    display: flex;
    justify-content: space-between;
    .house-content--left {
      width: calc(100% - 220px);
      .house-list {
        .house-list__item {
          height: 234px;
          padding: 30px 0;
          display: flex;
          align-items: center;
          border-bottom: 1px solid #eee;
          .house-img {
            width: 232px;
            height: 174px;
            margin-right: 30px;
          }
          .house-info {
            flex-grow: 1;
            height: 100%;
            .title {
              font-size: 20px;
              font-weight: bold;
              margin-bottom: 12px;
              display: flex;
              align-items: center;
              &:hover {
                cursor: pointer;
                color: var(--theme-color);
              }
              .status {
                display: inline-block;
                height: 24px;
                line-height: 24px;
                font-size: 12px;
                color: #fff;
                padding: 0 7px;
                border-radius: 2px;
                margin-left: 6px;
              }
            }
            .detail {
              display: flex;
              justify-content: space-between;
              .detail__left {
                font-size: 16px;
                line-height: 36px;
                .item-info {
                  display: flex;
                  align-items: center;
                  .item-info__icon {
                    width: 16px;
                    height: 16px;
                    margin-right: 8px;
                  }
                }
              }
              .detail__price {
                display: flex;
                justify-content: center;
                flex-direction: column;
                .total-price {
                  color: var(--theme-color);
                  font-size: 26px;
                  font-weight: bold;
                  margin-bottom: 6px;
                }
              }
            }
          }
        }
      }
    }
    .house-content--right {
      width: 182px;
      padding-top: 28px;
      > .title {
        font-size: 20px;
        font-weight: bold;
        margin-bottom: 20px;
      }
      .hot-list {
        .hot-list__item {
          margin-bottom: 20px;
          .hot-img {
            width: 182px;
            height: 128px;
          }
          .hot-info {
            flex-grow: 1;
            height: 100%;
            .title {
              font-size: 12px;
              margin-bottom: 12px;
              display: flex;
              align-items: center;
              justify-content: space-between;
              &:hover {
                cursor: pointer;
                color: var(--theme-color);
              }
              .status {
                display: inline-block;
                height: 18px;
                line-height: 18px;
                font-size: 12px;
                color: #fff;
                padding: 0 5px;
                border-radius: 2px;
              }
            }
          }
        }
      }
    }
  }

  .status {
    &.status--pending {
      background: #e6a23c;
    }
    &.status--available {
      background: #409eff;
    }
    &.status--rented {
      background: #67c23a;
    }
    &.status--sold {
      background: #67c23a;
    }
    &.status--unavailable {
      background: #909399;
    }
  }
}
</style>

<style lang="scss">
.house-list-page {
  .house-filter {
    .el-checkbox {
      width: 140px !important;
      .el-checkbox__label {
        color: #101d37;
      }
    }
  }
  .el-tabs {
    .el-tabs__item {
      font-weight: bold;
    }
    .el-tabs__item:nth-child(2) {
      padding-left: 20px !important;
    }
    .el-tabs__item:last-child {
      padding-right: 20px !important;
    }
    .el-tabs__item.is-active {
      color: #fff !important;
      background: var(--theme-color);
    }
  }
}
</style>
