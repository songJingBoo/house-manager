<template>
  <el-dialog v-model="visible" :title="title" width="500" :before-close="cancel">
    <div class="common-dialog-content">
      <form autocomplete="off" style="display: none">
        <input type="text" name="fake-username" />
        <input type="password" name="fake-password" />
      </form>
      <el-form ref="managerRef" :model="formData" :rules="rules" label-width="auto" style="max-width: 600px">
        <el-form-item label="Username" prop="username">
          <el-input v-model="formData.username" />
        </el-form-item>
        <el-form-item label="Phone" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="Role" prop="role">
          <el-select v-model="formData.role" placeholder="Select">
            <el-option label="Agent" value="Agent" />
            <el-option label="Reviewer" value="Reviewer" />
            <el-option label="Admin" value="Admin" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!formData.id" label="Password" prop="password">
          <el-input v-model="formData.password" type="password" autocomplete="new-password" />
        </el-form-item>
        <el-form-item v-if="formData.id" label="Password" prop="newPassword">
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
import { saveManager } from '@/api/back/manager'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['submit'])
const title = computed(() => {
  return formData.id ? 'Edit administrator' : 'Add administrator'
})
const formData = reactive({
  id: '',
  username: '',
  phone: '',
  role: '',
  password: '',
  newPassword: '',
})
const rules = reactive({
  username: [
    { required: true, message: 'Please input username', trigger: 'blur' },
    { min: 3, max: 8, message: 'Length should be 3 to 8', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: 'Please input phone', trigger: 'blur' },
    { min: 11, max: 11, message: 'Please input valid phone number', trigger: 'blur' },
  ],
  role: [{ required: true, message: 'Please select role' }],
  password: [{ required: true, message: 'Please select role' }],
})

// 打开弹窗
const visible = defineModel(false)
function open(data) {
  visible.value = true
  if (data) {
    formData.id = data.id
    formData.userId = data.userId
    formData.username = data.username
    formData.phone = data.phone
    formData.role = data.role
  }
}
defineExpose({ open }) // 指定要暴露open方法出去

// 关闭弹窗
function cancel() {
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
      const res = await saveManager(formData)
      if (res.status === 200) {
        emit('submit')
        cancel()
        ElMessage.success('Successfully saved')
      } else {
        ElMessage.error(res.message || 'Save failed')
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
