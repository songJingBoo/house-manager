<template>
  <el-dialog v-model="visible" :title="formData.intention === 'RENT' ? 'To RENT' : 'To Sold'" width="500" :before-close="cancel">
    <div class="common-dialog-content">
      <el-form ref="soldFormRef" :model="formData" :rules="rules" label-width="auto" style="max-width: 600px">
        <el-form-item label="Final Price" prop="finalPrice">
          <el-input v-model="formData.finalPrice" placeholder="Please enter your final price" >
            <template #suffix>
              <span>{{ formData.intention === 'RENT' ? '¥ / month' : '¥ 10K' }}</span>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancel">Cancel</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submit(soldFormRef)">Submit</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { dealHouse } from '@/api/back/house-manage'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['submit'])
const formData = reactive({
  houseId: '',
  finalPrice: '',
})
const rules = reactive({
  finalPrice: [{ required: true, message: 'Please input final price' }],
})

// 打开弹窗
const visible = defineModel(false)
function open(data) {
  visible.value = true
  if (data) {
    formData.houseId = data.houseId
  }
}
defineExpose({ open }) // 指定要暴露open方法出去

// 关闭弹窗
function cancel() {
  visible.value = false
}

// 提交
const submitLoading = ref(false)
const soldFormRef = ref()
async function submit(formEl) {
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      submitLoading.value = true
      const res = await dealHouse(formData)
      if (res.status === 200) {
        emit('submit')
        cancel()
        ElMessage.success('Successfully')
      } else {
        ElMessage.error(res.message || 'Failed')
      }
    } catch (e) {
      console.log(e)
    } finally {
      submitLoading.value = false
    }
  })
}
</script>

<style lang="scss" scoped></style>

<style lang="scss"></style>
