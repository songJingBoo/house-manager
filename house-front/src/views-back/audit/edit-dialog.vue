<template>
  <el-dialog v-model="visible" title="Edit Property" width="500" :before-close="cancel">
    <div class="common-dialog-content">
      <el-form ref="publishFormRef" class="publish-page__form" :model="formData" :rules="rules" label-width="auto" style="max-width: 600px">
        <el-form-item label="City" prop="city">
          <el-select v-model="formData.city" placeholder="Select">
            <el-option v-for="item in cityList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="Community" prop="community">
          <el-input v-model="formData.community" />
        </el-form-item>
        <el-form-item label="Address" prop="address">
          <el-input v-model="formData.address" />
        </el-form-item>
        <!-- <el-form-item label="Type" prop="type">
          <el-select v-model="formData.type" placeholder="Select">
            <el-option label="Residential" :value="0" />
            <el-option label="Commercial" :value="1" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="Layout" prop="layout">
          <el-select v-model="formData.layout" placeholder="Select">
            <el-option label="one-bedroom" :value="1" />
            <el-option label="two-bedroom" :value="2" />
            <el-option label="three-bedroom" :value="3" />
            <el-option label="four-bedroom" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="Area" prop="area">
          <el-input-number v-model="formData.area" :precision="2" :step="0.01" :max="500" controls-position="right" />
        </el-form-item>
        <el-form-item label="Floor" prop="floor">
          <el-input-number v-model="formData.floor" :step="1" :max="50" controls-position="right" />
        </el-form-item>
        <el-form-item label="Expected price" prop="expectPrice">
          <el-input-number v-model="formData.expectPrice" :step="1" :max="50" controls-position="right">
            <template #suffix>
              <span>¥10K</span>
            </template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="Name" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="Phone" prop="phone">
          <el-input v-model="formData.phone" type="number" />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancel">Cancel</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm(publishFormRef)">Submit</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { editInformation } from '@/api/back/audit'
import { ElMessage } from 'element-plus'
import { cityList } from '@/config/city'

const emit = defineEmits(['submit'])
const formData = ref({
  city: '',
  community: '', // 小区
  address: '',
  // type: '', // 房屋属性（0 住宅 Residential / 1 商业 Commercial）
  layout: '', // 户型
  area: '', // 面积
  floor: '', // 层高
  expectPrice: '', // 期望售价
  name: '', // 姓名
  phone: '', // 联系电话
  // remarks: '',
})
const rules = reactive({
  city: [{ required: true, message: 'Please select city' }],
  community: [
    { required: true, message: 'Please input community name' },
    { max: 32, message: 'Maximum input of 32 characters' },
  ],
  address: [
    { required: true, message: 'Please input community name' },
    { max: 64, message: 'Maximum input of 64 characters' },
  ],
  // type: [{ required: true, message: 'Please select type of house' }],
  layout: [{ required: true, message: 'Please select the housing layout' }],
  area: [{ required: true, message: 'Please input the area of the house' }],
  floor: [{ required: true, message: 'Please input the floor of the house' }],
  expectPrice: [{ required: true, message: 'Please input your expected selling price' }],
  name: [{ required: true, message: 'Please input your name' }],
  phone: [{ required: true, message: 'Please input your phone' }],
  // remarks: [{ required: false }, { max: 100, message: 'Maximum input of 100 characters' }],
})

const submitLoading = ref(false)
const publishFormRef = ref()
const submitForm = async (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      submitLoading.value = true
      const res = await editInformation(formData.value)
      if (res.status === 200) {
        emit('submit')
        cancel()
        ElMessage.success('Successfully edit')
      } else {
        ElMessage.error(res.message || 'edit failed')
      }
    } catch (e) {
      console.log(e)
    } finally {
      submitLoading.value = false
    }
  })
}

// 打开弹窗
const visible = defineModel(false)
function open(data) {
  visible.value = true
  if (data) {
    formData.value = data
  }
}
defineExpose({ open }) // 指定要暴露open方法出去

// 关闭弹窗
function cancel() {
  visible.value = false
}
</script>

<style lang="scss" scoped></style>

<style lang="scss"></style>
