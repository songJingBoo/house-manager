<template>
  <el-dialog v-model="visible" title="Audit Result" width="500" :before-close="cancel">
    <div class="common-dialog-content">
      <el-form ref="auditFormRef" :model="formData" :rules="rules" label-width="auto" style="max-width: 600px">
        <el-form-item label="status" prop="status">
          <el-select v-model="formData.status" placeholder="Select">
            <el-option label="available" value="AVAILABLE" />
            <el-option label="unavailable" value="UNAVAILABLE" />
          </el-select>
        </el-form-item>
        <el-form-item label="remarks" prop="remarks">
          <el-input v-model="formData.remarks" :rows="3" type="textarea" maxlength="100" show-word-limit />
        </el-form-item>
      </el-form>
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
import { auditRecord } from '@/api/back/audit'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['submit'])
const formData = reactive({
  id: '',
  status: '',
  remarks: '',
})
const rules = reactive({
  status: [{ required: true, message: 'Please select audit status' }],
  remarks: [{ required: false }, { max: 1000 }],
})

// 打开弹窗
const visible = defineModel(false)
function open(data) {
  visible.value = true
  if (data) {
    formData.id = data.id
  }
}
defineExpose({ open }) // 指定要暴露open方法出去

// 关闭弹窗
function cancel() {
  visible.value = false
}

// 提交
const submitLoading = ref(false)
const auditFormRef = ref()
async function submit(formEl) {
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      submitLoading.value = true
      const res = await auditRecord(formData)
      if (res.status === 200) {
        emit('submit')
        cancel()
        ElMessage.success('Successfully audit')
      } else {
        ElMessage.error(res.message || 'Audit failed')
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
