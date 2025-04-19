<template>
  <div class="house-detail-page common-page">
    <h1 class="page-title">{{ houseDetail.title }}</h1>
    <div class="detail-wrap">
      <div class="image-wrap">
        <div class="image-preview">
          <img v-if="houseDetail.imgList" :src="houseDetail.imgList[currentIndex].url" lazy />
        </div>
        <div class="image-list-wrap">
          <div id="image-list" class="image-list">
            <img v-for="img in houseDetail.imgList" :key="img.url" :src="img.url" lazy />
          </div>
          <ArrowLeftBold class="image-control left" @click="onSwitch(-1)" />
          <ArrowRightBold class="image-control right" @click="onSwitch(1)" />
        </div>
      </div>

      <div class="house-overview">
        <div class="detail-price">
          <div class="total-price">{{ isFinished ? houseDetail.finalPrice : houseDetail.expectPrice }}万</div>
          <!-- <div class="unit-price">{{ houseDetail.unitPrice }}元/平</div> -->
        </div>
        <!-- <div class="house-prop">
          <HomeFilled class="small-icon" />
          {{ houseDetail.model }} -- {{ houseDetail.area }}平米
        </div> -->
        <div><MapLocation class="item-info__icon" />{{ houseDetail.address }}</div>
        <div><OfficeBuilding class="item-info__icon" />Layout：{{ getLayoutTxt(houseDetail.layout) }}</div>
        <div><Message class="item-info__icon" />Area: {{ houseDetail.area }} ㎡</div>
        <div><Message class="item-info__icon" />Agent: {{ houseDetail.agentName }} / {{ houseDetail.agentPhone || '--' }}</div>
        <div><Clock class="item-info__icon" />{{ houseDetail.updateTime }}</div>

        <template v-if="!isFinished">
          <el-button v-if="!appintedTimeRange" type="primary" @click="openCreateAppointDialog">预约看房</el-button>
          <el-button v-if="appintedTimeRange" type="success" @click="cancelAppointmentFn">取消预约</el-button>
          <span v-if="appintedTimeRange" class="appointed-time">{{ appintedTimeRange }}</span>
        </template>
        <template v-else>
          <el-button type="primary" disabled>已完成交易</el-button>
        </template>
      </div>
    </div>

    <!-- 评论区 -->
    <div class="comment-section">
      <h2>房屋评论</h2>
      <!-- 发表评论输入框 -->
      <div class="comment-send-wrap">
        <el-input v-model="newComment" class="comment-send-wrap__input" type="textarea" :rows="3" placeholder="发表评论" />
        <el-button type="primary" class="comment-send-wrap__btn" @click="submitComment()">发表评论</el-button>
      </div>
      <!-- 评论列表 -->
      <div class="comment-list">
        <div v-for="comment in commentList" :key="comment.id" class="comment-item">
          <div class="comment-author">
            <User class="comment-icon author" />{{ comment.username }}
            <span v-if="comment.replyName">回复 {{ comment.replyName }}</span>
          </div>
          <div class="comment-content-warp">
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-time">
              <Clock class="comment-icon time" />{{ comment.createTime }}
              <span class="chat-wrap" @click="toggleReplyInput(comment)"><ChatDotSquare class="comment-icon chat" />{{ comment.replies.length }}</span>
            </div>
            <div v-if="replyObj.id === comment.id" class="comment-send-wrap">
              <el-input v-model="replyObj.content" class="comment-send-wrap__input" type="textarea" :rows="3" placeholder="回复评论" />
              <el-button type="primary" class="comment-send-wrap__btn" @click="submitComment(1)">回复评论</el-button>
            </div>
          </div>
          <!-- 回复列表 -->
          <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
            <div class="reply-author">
              <User class="comment-icon author" />{{ reply.username }}
              <span v-if="reply.replyName">回复 {{ reply.replyName }}</span>
            </div>
            <div class="reply-content-warp">
              <div class="reply-content">{{ reply.content }}</div>
              <div class="reply-time">
                <Clock class="comment-icon time" />{{ reply.createTime }}
                <span class="chat-wrap" @click="toggleReplyInput(reply)"><ChatDotSquare class="comment-icon chat" />回复</span>
              </div>
              <div v-if="replyObj.id === reply.id" class="comment-send-wrap">
                <el-input v-model="replyObj.content" class="comment-send-wrap__input" type="textarea" :rows="3" placeholder="回复评论" />
                <el-button type="primary" class="comment-send-wrap__btn" @click="submitComment(1)">回复评论</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 预约 -->
    <AppointmentDialog ref="appointmentDialogRef" @submit="getHouseDetail" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, useTemplateRef } from 'vue'
import { queryHouseDetail, queryCommentList, submitNewComment, cancelAppointment } from '@/api/house'
import { MapLocation, OfficeBuilding, Message, Clock, ArrowLeftBold, ArrowRightBold, User, ChatDotSquare } from '@element-plus/icons-vue'
import AppointmentDialog from './detail/Appointment-dialog.vue'
import 'vue3-carousel/carousel.css'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
const route = useRoute()

const houseId = ref('')
const houseDetail = ref({})
const currentUrl = ref('') // 当前图片的url
const currentIndex = ref(0) // 当前图片下标

onMounted(() => {
  houseId.value = route.params.id
  getHouseDetail()
  getCommentList()
})

const isFinished = computed(() => {
  return houseDetail.value.status === 'FINISHED'
})
const isRemoved = computed(() => {
  return houseDetail.value.status === 'REMOVED'
})

// 是否已预约
const isAppointed = computed(() => {
  return !!(houseDetail.value.appointStartTime && houseDetail.value.appointEndTime)
})
// 已预约时段
const appintedTimeRange = computed(() => {
  if (houseDetail.value.appointStartTime && houseDetail.value.appointEndTime) {
    const date = houseDetail.value.appointStartTime.split(' ')[0]
    const startTime = houseDetail.value.appointStartTime.split(' ')[1]
    const endTime = houseDetail.value.appointEndTime.split(' ')[1]
    return `${date} ${startTime} ~ ${endTime}`
  }
  return ''
})

function getLayoutTxt(layout) {
  return `${layout?.toLowerCase()}-bedroom`
}

/**
 * 取消预约
 */
const cancelLoading = ref(false)
async function cancelAppointmentFn() {
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
      appointId: houseDetail.value.appointId,
    })
    if (res.status === 200) {
      getHouseDetail()
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
 * 获取房屋详情
 */
async function getHouseDetail() {
  try {
    const res = await queryHouseDetail({ id: houseId.value })
    if (res.status === 200) {
      houseDetail.value = res.data || {}
      const images = (res.data.images || []).map((item) => {
        return {
          ...item,
          url: `${import.meta.env.VITE_FILE_DOMAIN}${item.imageUrl}`,
        }
      })
      images.sort((a, b) => b.isCover - a.isCover)
      houseDetail.value.imgList = images
    } else {
      this.$message.error(res.message)
    }
  } catch (e) {
    console.log(e)
  }
}

const commentList = ref([])
const newComment = ref('')
// 获取评论列表
async function getCommentList() {
  try {
    const res = await queryCommentList({ id: houseId.value })
    if (res.status === 200) {
      // 处理评论数据，根据parentId关联评论和回复
      const comments = res.data || []
      const commentMap = {}
      const rootComments = []

      // 构建评论映射
      comments.forEach((comment) => {
        comment.replies = []
        comment.showInput = false
        commentMap[comment.id] = comment
      })

      // 关联评论和回复
      comments.forEach((comment) => {
        comment.showInput = false
        if (comment.parentId) {
          const parentComment = commentMap[comment.parentId]
          if (parentComment) {
            const gradParentComment = commentMap[parentComment.parentId]
            if (gradParentComment) {
              gradParentComment.replies.push(comment)
            } else {
              parentComment.replies.push(comment)
            }
          }
        } else {
          rootComments.push(comment)
        }
      })

      commentList.value = rootComments
    } else {
      console.error(res.message)
    }
  } catch (e) {
    console.log(e)
  }
}

// 提交评论
async function submitComment(isReply = 0) {
  let params
  if (isReply) {
    params = {
      houseId: houseId.value,
      content: replyObj.value.content,
      parentId: replyObj.value.id,
    }
  } else {
    params = {
      houseId: houseId.value,
      content: newComment.value,
    }
  }
  try {
    const res = await submitNewComment(params)
    if (res.status === 200) {
      if (isReply) {
        replyObj.value = {}
      } else {
        newComment.value = ''
      }
      getCommentList()
    } else {
      console.error(res.message)
    }
  } catch (e) {
    console.log(e)
  }
}

/**
 * 预约看房
 */
const appointmentDialogRef = useTemplateRef('appointmentDialogRef')
function openCreateAppointDialog() {
  appointmentDialogRef.value.open(houseId.value)
}

/**
 * 打开回复框
 */
const replyObj = ref({})
function toggleReplyInput(comment) {
  if (replyObj.value.id === comment.id) {
    replyObj.value = {}
    return
  }
  replyObj.value = {
    id: comment.id,
    content: '',
    showInput: true,
  }
}

/**
 * 图片左右切换
 */
function onSwitch(dir) {
  const imgListEl = document.getElementById('image-list')
  currentIndex.value += dir
  imgListEl.style.transform = `translateX(${-1 * currentIndex.value * 220}px)`
}
</script>

<style lang="scss" scoped>
.house-detail-page {
  padding: 30px 0 100px 0;

  .page-title {
    margin-bottom: 20px;
  }
  .detail-wrap {
    display: flex;
    .image-wrap {
      width: 640px;
      display: flex;
      flex-direction: column;
      .image-preview {
        width: 640px;
        height: 400px;
        margin-right: 30px;
        border: 1px solid #eee;
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
      .image-list-wrap {
        overflow: hidden;
        width: 640px;
        height: 150px;
        margin-top: 12px;
        border: 1px solid #eee;
        position: relative;
        .image-list {
          display: flex;
          transition: all ease 0.3s;
          img {
            width: 220px;
            height: 100%;
            margin-right: 12px;
            object-fit: cover;
            cursor: pointer;
          }
        }
        .image-control {
          width: 40px;
          height: 40px;
          position: absolute;
          top: 50%;
          transform: translateY(-50%);
          cursor: pointer;
          &.left {
            left: 0;
          }
          &.right {
            right: 0;
          }
        }
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

  .comment-section {
    margin-top: 30px;
    padding: 20px;
    background-color: #f9f9f9;
    border: 1px solid #eee;
    border-radius: 4px;

    h2 {
      margin-bottom: 15px;
      font-size: 20px;
      font-weight: bold;
    }

    .comment-icon {
      width: 16px;
      height: 16px;
      margin-right: 6px;
      vertical-align: sub;
      &.chat {
        margin-left: 20px;
      }
    }

    .chat-wrap {
      cursor: pointer;
      &:hover {
        color: var(--theme-color);
      }
    }

    .comment-list {
      margin-top: 20px;
      .comment-item {
        margin-bottom: 20px;
        padding-bottom: 20px;
        border-bottom: 1px solid #eee;

        .comment-author {
          font-weight: bold;
          color: #333;
        }

        .comment-content-warp {
          margin-left: 20px;
          .comment-content {
            margin-top: 5px;
            color: #666;
          }

          .comment-time {
            margin-top: 5px;
            font-size: 12px;
            color: #999;
          }
        }

        .reply-item {
          margin-top: 15px;
          margin-left: 20px;
          padding-left: 10px;
          border-left: 2px solid #eee;

          .reply-author {
            font-weight: bold;
            color: #333;
          }

          .reply-content-warp {
            margin-left: 20px;
            .reply-content {
              margin-top: 5px;
              color: #666;
            }

            .reply-time {
              margin-top: 5px;
              font-size: 12px;
              color: #999;
            }
          }
        }

        input {
          width: 100%;
          margin-top: 10px;
          padding: 8px 12px;
          border: 1px solid #ccc;
          border-radius: 4px;
          box-sizing: border-box;
        }
      }
    }

    .comment-send-wrap {
      position: relative;
      margin-top: 15px;
      .comment-send-wrap__input {
        :deep(.el-textarea__inner) {
          padding-right: 108px;
        }
      }
      .comment-send-wrap__btn {
        position: absolute;
        bottom: 6px;
        right: 10px;
      }
    }
  }
}
.comment-item {
  .item-info__icon {
    cursor: pointer;
  }
}

.appointed-time {
  font-size: 14px;
  color: #a6a6a6;
  line-height: 20px;
  margin-left: 12px;
}
</style>
