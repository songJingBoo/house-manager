<template>
  <div class="house-detail-page common-page">
    <h1 class="page-title">{{ houseDetail.title }}</h1>
    <div class="detail-wrap">
      <div class="image-wrap">
        <div class="image-preview">
          <img v-if="currentUrl" :src="currentUrl" lazy />
        </div>
        <div class="image-list">
          <img v-for="url in houseDetail.imgList" :key="url" :src="url" lazy />
          <ArrowLeftBold class="image-control left" />
          <ArrowRightBold class="image-control right" />
        </div>
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

    <!-- 评论区 -->
    <div class="comment-section">
      <h2>房屋评论</h2>
      <!-- 发表评论输入框 -->
      <div class="comment-send-wrap">
        <el-input v-model="newComment" class="comment-send-wrap__input" type="textarea" :rows="3" placeholder="发表评论" />
        <el-button type="primary" class="comment-send-wrap__btn" @click="submitComment()">发表评论</el-button>
      </div>
      <!-- 评论列表 -->
      <div v-for="comment in commentList" :key="comment.id" class="comment-item">
        <div class="comment-author"><User class="comment-icon author" />{{ comment.userId }}</div>
        <div class="comment-content">{{ comment.content }}</div>
        <div class="comment-time">
          <Clock class="comment-icon time" />{{ comment.createTime }}
          <span class="chat-wrap" @click="toggleReplyInput(comment.id)"><ChatDotSquare class="comment-icon chat" />{{ comment.replies.length }}</span>
        </div>
        <!-- 回复列表 -->
        <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
          <div class="reply-author"><User class="comment-icon author" />{{ reply.userId }}</div>
          <div class="reply-content">{{ reply.content }}</div>
          <div class="reply-time">
            <Clock class="comment-icon time" />{{ reply.createTime }}
            <span class="chat-wrap" @click="toggleReplyInput(comment.id)"><ChatDotSquare class="comment-icon chat" />回复</span>
          </div>
        </div>
        <!-- 回复评论输入框 -->
        <el-input v-model="newReply[comment.id]" placeholder="回复评论" @keyup.enter="submitComment(comment.id)" style="display: none" :ref="replyInputRef[comment.id]" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { queryHouseDetail, queryCommentList, submitNewComment } from '@/api/house'
import { MapLocation, OfficeBuilding, Message, Clock, ArrowLeftBold, ArrowRightBold, User, ChatDotSquare } from '@element-plus/icons-vue'
import 'vue3-carousel/carousel.css'
import { useRoute } from 'vue-router'
const route = useRoute()

const houseId = ref('')
const houseDetail = ref({})
const currentUrl = ref('') // 当前图片的url

onMounted(() => {
  houseId.value = route.params.id
  getHouseDetail()
  getCommentList()
})

function getLayoutTxt(layout) {
  return `${layout?.toLowerCase()}-bedroom`
}

/**
 * 获取房屋详情
 */
async function getHouseDetail() {
  try {
    const res = await queryHouseDetail({ id: houseId.value })
    if (res.status === 200) {
      houseDetail.value = res.data || {}
      houseDetail.value.imgList = res.data.images.split(',')
      currentUrl.value = houseDetail.value.imgList[0]
    } else {
      this.$message.error(res.message)
    }
  } catch (e) {
    console.log(e)
  }
}

const commentList = ref([])
const newComment = ref('')
const newReply = ref({})
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
        commentMap[comment.id] = comment
      })

      // 关联评论和回复
      comments.forEach((comment) => {
        if (comment.parentId) {
          const parentComment = commentMap[comment.parentId]
          if (parentComment) {
            parentComment.replies.push(comment)
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
async function submitComment(commentId) {
  if (newComment.value) {
    try {
      const res = await submitNewComment({ houseId: houseId.value, content: newComment.value, commentId })
      if (res.status === 200) {
        newComment.value = ''
        getCommentList()
      } else {
        console.error(res.message)
      }
    } catch (e) {
      console.log(e)
    }
  }
}

/**
 * 预约看房
 */
function bookView() {}
const replyInputRef = ref({})
const showReplyInput = ref({})

function toggleReplyInput(commentId) {
  showReplyInput.value[commentId] = !showReplyInput.value[commentId]
  if (showReplyInput.value[commentId]) {
    setTimeout(() => {
      replyInputRef.value[commentId].focus()
    })
  }
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
      .image-list {
        width: 640px;
        height: 150px;
        margin-top: 12px;
        overflow-y: auto;
        border: 1px solid #eee;
        position: relative;
        img {
          width: 220px;
          height: 100%;
          margin-right: 12px;
          object-fit: cover;
          cursor: pointer;
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

    .comment-send-wrap {
      position: relative;
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

    .comment-item {
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #eee;

      .comment-author {
        font-weight: bold;
        color: #333;
      }

      .comment-content {
        margin-top: 5px;
        color: #666;
      }

      .comment-time {
        margin-top: 5px;
        font-size: 12px;
        color: #999;
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
}
.comment-item {
  .item-info__icon {
    cursor: pointer;
  }
}
</style>
