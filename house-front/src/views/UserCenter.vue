<template>
  <div class="house-list-page common-page">
    <div class="house-content">
      <div class="house-content--left">
        <div class="house-sort">
          <el-tabs v-model="type" class="theme" @tab-click="onTabChange">
            <el-tab-pane v-for="item in tabList" :key="item.prop" :label="item.label" :name="item.prop">
              <template #label>
                <span class="custom-tabs-label">
                  <span class="custom-tabs-label__text">{{ item.label }}</span>
                </span>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>

        <div v-if="type !== 'appointments'" class="house-list" v-loading="houseLoading">
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
          <el-empty v-if="!houseLoading && !houseList.length" description="暂无房屋~" />
        </div>

        <div v-if="type === 'appointments'" class="appoint-list">
          <el-table v-loading="appointLoading" class="common-table__table" :data="appointList" style="width: 100%">
            <el-table-column label="House Title" prop="title" width="180">
              <template #default="scope">
                <div class="text-over house-title" :title="scope.row.title" @click="linkToDetail(scope.row.houseId)">{{ scope.row.title || '--' }}</div>
              </template>
            </el-table-column>
            <el-table-column label="Time" prop="time" width="200">
              <template #default="scope">
                <div class="text-over">{{ scope.row.time || '--' }}</div>
              </template>
            </el-table-column>
            <el-table-column label="Agent" prop="agent" width="180">
              <template #default="scope">
                <div class="text-over">{{ scope.row.agent || '--' }}</div>
              </template>
            </el-table-column>
            <el-table-column label="Address" prop="address">
              <template #default="scope">
                <div class="text-over" :title="scope.row.address">{{ scope.row.address || '--' }}</div>
              </template>
            </el-table-column>
            <el-table-column label="Create Time" prop="createTime" width="180" />
            <el-table-column label="operate" prop="Operate" fixed="right" width="180">
              <template #default="scope">
                <el-button v-if="scope.row.status === 'PENDING'" type="primary" small @click="openCreateAppointDialog(scope.row)">Edit</el-button>
                <el-button v-if="scope.row.status === 'PENDING'" type="primary" small @click="cancelAppointmentFn(scope.row)">Cancel</el-button>
                <el-button v-if="scope.row.status === 'CONFIRMED'" type="primary" small disabled>Confirmed</el-button>
                <el-button v-if="scope.row.status === 'FINISHED'" type="primary" small disabled>Finished</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!appointLoading && !appointList.length" description="暂无预约~" />
        </div>
      </div>
    </div>

    <!-- 预约 -->
    <AppointmentDialog ref="appointmentDialogRef" @submit="getHouseDetail" />
  </div>
</template>

<script setup>
import { ref, onMounted, useTemplateRef } from 'vue'
import { likeHouse } from '@/api/house'
import { queryMyHouses, queryMyLikes, queryMyAppointment } from '@/api/my'
import { cancelAppointment } from '@/api/house'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MapLocation, OfficeBuilding, Message, Clock } from '@element-plus/icons-vue'
import AppointmentDialog from '@/components/Appointment-dialog.vue'
import { useRouter } from 'vue-router'
const router = useRouter()

onMounted(() => {
  getMyHouses()
})

/**
 * tab配置
 */
const tabList = ref([
  { label: 'My Houses', prop: 'houses' },
  { label: 'My Likes', prop: 'likes' },
  { label: 'My Appointments', prop: 'appointments' },
])
const type = ref('houses')

const onTabChange = () => {
  if (type.value === 'houses' || type.value === 'likes') {
    getMyHouses()
  } else if (type.value === 'appointments') {
    getMyAppointment()
  }
}

/**
 * 获取房屋列表
 */
const total = ref(0)
const houseList = ref([])
const houseLoading = ref(false)
async function getMyHouses() {
  try {
    houseLoading.value = true
    const res = await (type.value === 'houses' ? queryMyHouses() : queryMyLikes())
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
 * 获取我的预约
 */
const appointList = ref([])
const appointLoading = ref(false)
async function getMyAppointment() {
  try {
    appointLoading.value = true
    const res = await queryMyAppointment()
    if (res.status === 200) {
      appointList.value = (res.data || []).map((item) => {
        return {
          ...item,
          time: item.startTime ? `${item.startTime} ~ ${item.endTime.substr(11)}` : '--',
          agent: item.agentName ? `${item.agentName} / ${item.agentPhone}` : '--',
        }
      })
    } else {
      ElMessage.error(res.message)
    }
  } catch (e) {
    console.log(e)
  } finally {
    appointLoading.value = false
  }
}

/**
 * 取消预约
 */
const cancelLoading = ref(false)
async function cancelAppointmentFn(appoint) {
  const ok = await ElMessageBox.confirm('Confirm cancellation of appointment?', 'Warning', {
    confirmButtonText: 'OK',
    cancelButtonText: 'Cancel',
    type: 'warning',
  })
    .then(() => 1)
    .catch(() => 0)
  if (!ok) return

  try {
    cancelLoading.value = true
    const res = await cancelAppointment({
      appointId: appoint.id,
    })
    if (res.status === 200) {
      getMyAppointment()
      ElMessage.success('Appointment cancelled successfully！')
    } else {
      ElMessage.error(res.message || 'Appointment cancelled failed')
    }
  } catch (e) {
    console.log(e)
  } finally {
    cancelLoading.value = false
  }
}

/**
 * 修改预约
 */
const appointmentDialogRef = useTemplateRef('appointmentDialogRef')
function openCreateAppointDialog(appoint) {
  appointmentDialogRef.value.open(appoint.houseId, appoint)
}

function getLayoutTxt(layout) {
  return `${layout.toLowerCase()}-bedroom`
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
      width: 100%;
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

.house-title {
  color: #f76900;
  text-decoration: underline;
  cursor: pointer;
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
