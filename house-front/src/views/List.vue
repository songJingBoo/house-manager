<template>
  <div class="house-list-page common-page">
    <div class="house-filter">
      <div class="filter-item" v-for="item in filterList" :key="item.code">
        <div class="filter-item__name">{{ item.name }}</div>
        <div class="filter-item__options">
          <el-checkbox-group v-model="item.checked" @change="onFilterChange">
            <el-checkbox v-for="(opt, i) in item.config" :key="i" :label="opt.label" :value="opt.value" />
          </el-checkbox-group>
        </div>
      </div>
    </div>

    <div class="house-content">
      <div class="house-content--left">
        <div class="house-sort">
          <el-tabs v-model="sortType" class="theme" @tab-click="tabChange">
            <el-tab-pane v-for="item in tabList" :key="item.prop" :label="item.label" :name="item.prop">
              <template #label>
                <span class="custom-tabs-label">
                  <span class="custom-tabs-label__text">{{ item.label }}</span>
                  <el-icon v-if="sortType === item.prop" class="custom-tabs-label__icon">
                    <SortUp v-if="sortDir === 'desc'" />
                    <SortDown v-if="sortDir === 'asc'" />
                  </el-icon>
                </span>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>

        <div class="house-total">
          共找到 {{ total }} 套房源
          <div class="view-finished">
            <el-switch v-model="showFinish" size="small" active-text="View Finished" @change="getHouseList()" />
          </div>
        </div>

        <div class="house-list" v-loading="houseLoading">
          <div
            v-for="item in houseList"
            :key="item.houseId"
            class="house-list__item"
            :class="{
              'is-sold': item.status === 'FINISHED' && item.intention === 'SALE',
              'is-rented': item.status === 'FINISHED' && item.intention === 'RENT',
              'is-removed': item.status === 'REMOVED',
            }"
          >
            <img class="house-img cursor" :src="item.img" @click="linkToDetail(item.houseId)" />
            <div class="house-info">
              <div class="title" @click="linkToDetail(item.houseId)">
                {{ item.title }}
                <span v-if="item.status === 'PUBLISHED'" class="status status--available">{{ item.intention === 'RENT' ? '待租' : '在售' }}</span>
                <span v-if="item.status === 'FINISHED' && item.intention === 'RENT'" class="status status--rented">已租</span>
                <span v-if="item.status === 'FINISHED' && item.intention === 'SALE'" class="status status--sold">已售</span>
                <span v-if="item.status === 'REMOVED'" class="status status--unavailable">下架</span>
              </div>
              <div class="detail">
                <div class="detail__left">
                  <div class="item-info"><MapLocation class="item-info__icon" />{{ item.address }}</div>
                  <div class="item-info"><OfficeBuilding class="item-info__icon" />Layout: {{ getLayoutTxt(item.layout) }}&nbsp;&nbsp;&nbsp;Area: {{ item.area }} ㎡</div>
                  <div class="item-info"><Message class="item-info__icon" />Agent: {{ item.agentName }} / {{ item.agentPhone || '--' }}</div>
                  <div class="item-info"><Clock class="item-info__icon" />{{ item.updateTime }}</div>
                </div>
                <div class="detail__price">
                  <div class="total-price">{{ item.status === 'FINISHED' ? item.finalPrice : item.expectPrice }}万</div>
                  <!-- <div class="unit-price">{{ item.unitPrice }}元/平</div> -->
                </div>
              </div>
            </div>
            <!-- 关注按钮 -->
            <div class="like-btn" :class="{ 'is-liked': item.isLiked }" @click="likeHouseFn(item)"></div>
          </div>
          <Empty v-if="!houseLoading && !houseList.length" paddingTop="80">暂未找到合适的房屋~</Empty>
        </div>
      </div>

      <div class="house-content--right">
        <div class="title">热门房源</div>
        <div class="hot-list" v-loading="hotLoading">
          <div class="hot-list__item" v-for="item in hotList" :key="item.houseId">
            <img class="hot-img cursor" :src="item.img" @click="linkToDetail(item.houseId)" />
            <div class="hot-info">
              <div class="title" @click="linkToDetail(item.houseId)">
                <div class="text-over" style="max-width: calc(100% - 40px)">{{ item.title }}</div>
                <span v-if="item.status === 'PUBLISHED'" class="status status--available">{{ item.intention === 'RENT' ? '待租' : '待售' }}</span>
                <span v-if="item.status === 'FINISHED' && item.intention === 'RENT'" class="status status--rented">已租</span>
                <span v-if="item.status === 'FINISHED' && item.intention === 'SALE'" class="status status--sold">已售</span>
              </div>
            </div>
          </div>
          <el-empty v-if="!hotLoading && !hotList.length" class="hot-empty" :image-size="90" description="暂无热门房源~" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { queryHouseList, queryHotList, likeHouse } from '@/api/house'
import { queryHouseFilter } from '@/api/back/house-manage'
import { ElMessage } from 'element-plus'
import { MapLocation, OfficeBuilding, Message, Clock, SortUp, SortDown } from '@element-plus/icons-vue'
import Empty from '@/components/Empty.vue'
import { useRouter } from 'vue-router'
const router = useRouter()

onMounted(() => {
  getFilterConfig()
  getHouseList()
  getHotList()
})

/**
 * tab配置
 */
const tabList = ref([
  { label: '最新发布', prop: 'create_time' },
  { label: '总价', prop: 'expect_price' },
  { label: '面积', prop: 'area' },
])
const sortType = ref('create_time')
const sortDir = ref('desc')
const oldTab = ref('')
function tabChange() {
  if (oldTab.value === sortType.value) {
    // 点击当前，则toggle
    sortDir.value = sortDir.value === 'desc' ? 'asc' : 'desc'
  } else {
    sortDir.value = 'desc'
  }
  oldTab.value = sortType.value
  getHouseList()
}

/**
 * 获取过滤项目[min, max)
 */
const filterList = ref([])
async function getFilterConfig() {
  try {
    const res = await queryHouseFilter()
    if (res.status === 200) {
      console.log(res)
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
const filterChecked = ref([])
function onFilterChange() {
  filterChecked.value = filterList.value
    .filter((item) => item.checked?.length > 0)
    .map((item) => {
      return {
        code: item.code,
        checked: item.checked,
      }
    })
  getHouseList()
}

// 处理过滤项（计算单项label / value值）
function handlerFilter(filterList) {
  ;(filterList || []).forEach((item) => {
    item.config = JSON.parse(item.config) || []
    item.config.forEach((opt) => {
      const { min, eql, max } = opt
      // 固定值
      if (eql !== undefined) {
        opt.label = `${eql}${item.suffix}`
        opt.value = { eql }
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
const showFinish = ref(true) // 是否展示已售/租房屋
const total = ref(0)
const houseList = ref([])
const houseLoading = ref(false)
async function getHouseList() {
  try {
    houseLoading.value = true
    const res = await queryHouseList({
      sortDir: sortDir.value,
      sortType: sortType.value,
      showFinish: showFinish.value,
      filter: filterChecked.value,
    })
    if (res.status === 200) {
      houseList.value = (res.data || []).map((item) => {
        return {
          ...item,
          img: `${import.meta.env.VITE_FILE_DOMAIN}${item.imageCover}`,
        }
      })
      total.value = res.data.length || 0
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    console.log(e)
  } finally {
    houseLoading.value = false
  }
}

/**
 * 获取热门房屋
 */
const hotLoading = ref(false)
const hotList = ref([])
async function getHotList() {
  try {
    hotLoading.value = true
    const res = await queryHotList()
    if (res.status === 200) {
      hotList.value = (res.data || []).map((item) => {
        return {
          ...item,
          img: `${import.meta.env.VITE_FILE_DOMAIN}${item.imageCover}`,
        }
      })
      total.value = res.data.length || 0
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    console.log(e)
  } finally {
    hotLoading.value = false
  }
}

/**
 * 关注/取消关注
 */
async function likeHouseFn(houseItem) {
  try {
    const res = await likeHouse({
      id: houseItem.houseId,
      status: houseItem.isLiked ? 0 : 1,
    })
    if (res.status === 200) {
      houseItem.isLiked = !houseItem.isLiked
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
    background: #f5f5f6;
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
    margin-bottom: 20px;
    font-weight: bold;
    border-bottom: 1px solid #eee;
    position: relative;
    .view-finished {
      position: absolute;
      top: 10px;
      right: 20px;
      line-height: 30px;
    }
  }

  .house-content {
    display: flex;
    justify-content: space-between;
    .house-content--left {
      width: calc(100% - 220px);
      margin-bottom: 100px;
      .house-list {
        .house-list__item {
          height: 234px;
          padding: 16px 0;
          display: flex;
          align-items: center;
          position: relative;
          margin-bottom: 20px;
          border-radius: 4px;
          transition: 0.3s;
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
          &:hover {
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3);
          }
          .house-img {
            width: 276px;
            height: 200px;
            margin-left: 20px;
            margin-right: 24px;
          }
          .house-info {
            flex-grow: 1;
            height: 100%;
            padding: 10px 0;
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
                margin-right: 18px;
                .total-price {
                  color: var(--theme-color);
                  font-size: 26px;
                  font-weight: bold;
                  margin-bottom: 6px;
                }
              }
            }
          }
          .like-btn {
            display: none;
            position: absolute;
            background: url('@/assets/icon/like.svg') no-repeat;
            width: 32px;
            height: 32px;
            right: 10px;
            top: 10px;
            cursor: pointer;
            &.is-liked {
              display: block;
              background: url('@/assets/icon/liked.svg') no-repeat;
            }
          }
          &:hover {
            .like-btn {
              display: block;
            }
          }

          &.is-sold {
            background: url('@/assets/icon/sold.svg') 100% 0 no-repeat;
          }
          &.is-sold,
          &.is-rented,
          &.is-removed {
            opacity: 0.5;
          }
        }
      }
    }
    .house-content--right {
      width: 182px;
      padding-top: 40px;
      > .title {
        font-size: 20px;
        font-weight: bold;
        margin-bottom: 20px;
      }
      .hot-list {
        .hot-list__item {
          margin-bottom: 12px;
          padding: 10px;
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
          &:hover {
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3);
          }
          .hot-img {
            width: 164px;
            height: 120px;
          }
          .hot-info {
            flex-grow: 1;
            height: 100%;
            .title {
              font-size: 12px;
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

.custom-tabs-label {
  display: flex;
  align-items: center;
  .custom-tabs-label__text {
  }
  .custom-tabs-label__icon {
    font-size: 16px;
    margin-left: 5px;
  }
}

.hot-empty {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
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
