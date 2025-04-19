<template>
  <el-dialog v-model="visible" title="Reset Password" width="500" :before-close="cancel">
    <div class="common-dialog-content">
      <form autocomplete="off" style="display: none">
        <input type="text" name="fake-username" />
        <input type="password" name="fake-password" />
      </form>
      <el-form ref="managerRef" :model="formData" :rules="rules" label-width="auto" style="max-width: 600px">
        <el-form-item label="Password" prop="password">
          <el-input v-model="formData.password" type="password" autocomplete="new-password" placeholder="******" />
        </el-form-item>
        <el-form-item label="New Password" prop="newPassword">
          <el-input v-model="formData.newPassword" type="password" autocomplete="new-password" placeholder="******" />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancel">Cancel</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submit(managerRef)">Submit</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { resetPassword } from '@/api/login'
import { ElMessage } from 'element-plus'
import md5 from 'md5'
import { useRouter } from 'vue-router'
const router = useRouter()

const emit = defineEmits(['submit'])
const formData = reactive({
  password: '',
  newPassword: '',
})
const rules = reactive({
  password: [{ required: true, message: 'Please enter password' }],
  newPassword: [{ required: true, message: 'Please enter new password' }],
})

// 打开弹窗
const visible = defineModel(false)
function open() {
  visible.value = true
  formData.password = ''
  formData.newPassword = ''
}
defineExpose({ open }) // 指定要暴露open方法出去

// 关闭弹窗
function cancel() {
  managerRef.value.clearValidate()
  visible.value = false
}

// 提交
const submitLoading = ref(false)
const managerRef = ref()
async function submit(formEl) {
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      submitLoading.value = true
      const res = await resetPassword({
        password: md5(formData.password),
        newPassword: md5(formData.newPassword),
      })
      if (res.status === 200) {
        emit("submit")
        cancel()
        ElMessage.success('Password reset successful')
      } else {
        ElMessage.error(res.message || 'Password reset failed')
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
