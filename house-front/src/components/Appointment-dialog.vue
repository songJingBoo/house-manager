<template>
  <el-dialog v-model="visible" title="Appointment" width="1000" :before-close="cancel">
    <div class="common-dialog-content">
      <el-form ref="auditFormRef" :model="formData" :rules="rules" :inline="true" label-width="auto">
        <el-form-item label="Date" prop="status">
          <el-date-picker v-model="formData.date" type="date" placeholder="Pick a day" :disabled-date="disabledDate" value-format="YYYY-MM-DD" format="YYYY-MM-DD" @change="onDateChange" />
        </el-form-item>
        <el-form-item label="Time Range" prop="timeRange">
          <el-input v-model="formData.timeRange" disabled />
        </el-form-item>
      </el-form>

      <div class="range-wrap">
        <div class="range-item" v-for="item in timeRangeConfig" :key="item.label">
          <div class="range-item__label">{{ item.label }}</div>
          <el-popover placement="top-start" :width="120" :hide-after="0" trigger="hover" :content="item.hover">
            <template #reference>
              <div
                class="range-item__area"
                :class="{
                  'range-item__area--actived': item.actived,
                  'range-item__area--selected': item.selected || (formData.timeRangeArr.length === 2 && item.index > formData.timeRangeArr[0].index && item.index < formData.timeRangeArr[1].index),
                }"
                @click="onTimeRangeClick(item)"
              ></div>
            </template>
          </el-popover>
        </div>
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancel">Cancel</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submit(auditFormRef)">Submit</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { queryHouseDate, createAppointment } from '@/api/house'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['submit'])
const formData = reactive({
  date: new Date().toISOString().slice(0, 10),
  timeRange: '',
  timeRangeArr: [],
})
const rules = reactive({
  date: [{ required: true, message: 'Select date' }],
  timeRange: [{ required: true, message: 'Select time range' }],
})

// 打开弹窗
const visible = defineModel(false)
const houseId = ref('')
const appintment = ref({
  // 编辑预约
  id: '',
  date: '',
  startTime: '',
  endTime: '',
})
function open(hId, ap) {
  visible.value = true
  houseId.value = hId
  if (ap) {
    appintment.value = ap
    const { startTime, endTime } = ap
    const startIndex = range.value.indexOf(startTime.split(' ')[1])
    const endIndex = range.value.indexOf(endTime.split(' ')[1])
    formData.date = ap.startTime.split(' ')[0]
    formData.timeRange = `${ap.startTime.split(' ')[1]} ~ ${ap.endTime.split(' ')[1]}`
    formData.timeRangeArr = [
      {
        index: startIndex,
        label: startTime.split(' ')[1],
        selected: true, // 当前已勾选
      },
      {
        index: endIndex,
        label: endTime.split(' ')[1],
        selected: true, // 当前已勾选
      },
    ]
  }
  getHouseDate()
}
defineExpose({ open }) // 指定要暴露open方法出去

// 关闭弹窗
function cancel() {
  formData.timeRange = ''
  formData.timeRangeArr = []
  visible.value = false
}

function onDateChange() {
  formData.timeRange = ''
  formData.timeRangeArr = []
  getHouseDate()
}

/**
 * 获取房屋当日已预约时段
 */
async function getHouseDate() {
  timeRangeConfig.value.forEach((jtem) => {
    jtem.selected = false
    jtem.actived = false
  })

  try {
    const res = await queryHouseDate({
      houseId: houseId.value,
      date: formData.date,
    })
    if (res.status === 200) {
      ;(res.data || []).forEach((item) => {
        const { id, startTime, endTime } = item
        if (appintment.value.id && id === appintment.value.id) {
          return
        }
        const startIndex = range.value.indexOf(startTime.split(' ')[1])
        const endIndex = range.value.indexOf(endTime.split(' ')[1])
        timeRangeConfig.value.forEach((jtem, j) => {
          if (j >= startIndex && j <= endIndex) {
            jtem.actived = true
          }
        })
      })
    }
  } catch (e) {
    console.log(e)
  }
}
// 日期选择器禁止选择昨日及之前
const disabledDate = (date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0) // 固定为当日 00:00:00
  return date.getTime() < today.getTime() // 禁用早于今日的日期
}

// 时间区间
const range = ref([
  '08:00',
  '08:30',
  '09:00',
  '09:30',
  '10:00',
  '10:30',
  '11:00',
  '11:30',
  '12:00',
  '12:30',
  '13:00',
  '13:30',
  '14:00',
  '14:30',
  '15:00',
  '15:30',
  '16:00',
  '16:30',
  '17:00',
  '17:30',
  '18:00',
])
function createRange() {
  const timeRange = []
  for (let i = 0, len = range.value.length - 1; i < len; i++) {
    timeRange.push({
      index: i,
      label: range.value[i],
      hover: `${range.value[i]} ~ ${range.value[i + 1]}`,
      actived: false, // 已被预约
      selected: false, // 当前已勾选
    })
  }
  return timeRange
}
const timeRangeConfig = ref(createRange())

// 选中时间区间
function onTimeRangeClick(timeRange) {
  if (timeRange.actived) return
  if (timeRange.selected) {
    timeRange.selected = false
    const index = formData.timeRangeArr.indexOf(timeRange)
    if (index > -1) {
      formData.timeRangeArr.splice(index, 1)
    }
    return
  }

  timeRange.selected = true
  if (!formData.timeRangeArr.length) {
    formData.timeRangeArr.push(timeRange)
  } else if (formData.timeRangeArr.length === 1) {
    if (timeRange.index > formData.timeRangeArr[0].index) {
      formData.timeRangeArr.push(timeRange)
    } else {
      formData.timeRangeArr.unshift(timeRange)
    }
  } else {
    if (timeRange.index < formData.timeRangeArr[0].index) {
      const tail = formData.timeRangeArr.pop()
      tail.selected = false
      formData.timeRangeArr.unshift(timeRange)
    } else {
      const head = formData.timeRangeArr.shift()
      head.selected = false
      formData.timeRangeArr.push(timeRange)
    }
  }

  formData.timeRange = formData.timeRangeArr.length === 2 ? `${formData.timeRangeArr[0].label} ~ ${formData.timeRangeArr[1].label}` : ''
}

// 提交
const submitLoading = ref(false)
const auditFormRef = ref()
async function submit(formEl) {
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      submitLoading.value = true
      const res = await createAppointment({
        houseId: houseId.value,
        startTime: `${formData.date} ${formData.timeRangeArr[0].label}`,
        endTime: `${formData.date} ${formData.timeRangeArr[1].label}`,
      })
      if (res.status === 200) {
        emit('submit')
        cancel()
        ElMessage.success('Appointment successful！')
      } else {
        ElMessage.error(res.message || 'Appointment failed')
      }
    } catch (e) {
      console.log(e)
    } finally {
      submitLoading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.range-wrap {
  display: flex;
  .range-item {
    .range-item__label {
      font-size: 12px;
    }
    .range-item__area {
      border: 1px solid #eee;
      width: 42px;
      height: 42px;
      cursor: pointer;
      &:hover {
        background: #eee;
      }
    }
    .range-item__area--selected {
      background: #a5f0b5 !important;
    }
    .range-item__area--actived {
      background: #d493c8 !important;
    }
  }
}
</style>

<style lang="scss"></style>
