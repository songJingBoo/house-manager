<template>
  <el-dialog v-model="visible" title="Edit Property" width="500" :before-close="cancel">
    <div class="common-dialog-content" style="max-height: 520px; overflow-y: auto">
      <el-form ref="publishFormRef" class="publish-page__form" :model="formData" :rules="rules" label-width="auto" style="max-width: 600px">
        <el-form-item label="Title" prop="title">
          <el-input v-model="formData.title" />
        </el-form-item>
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
          <el-input-number v-model="formData.expectPrice" :step="1" :max="500" controls-position="right">
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
        <el-form-item label="Files">
          <el-upload
            ref="uploadRef"
            action="/api/file/upload"
            :multiple="true"
            :show-file-list="true"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :before-upload="beforeUpload"
            :http-request="customUploadRequest"
            list-type="picture"
            :file-list="formData.files"
          >
            <template #trigger>
              <el-button type="primary">Select Files</el-button>
            </template>
            <template #file="{ file }">
              <div class="custom-file-item">
                <el-radio v-model="file.isCover" :label="true" @change="handleCover(file)"></el-radio>
                <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
                <!-- <div class="el-upload-list__item-actions"> -->
                <div class="custom-file-item__delete" @click="handleRemove(file)">
                  <el-icon><Delete /></el-icon>
                </div>
                <!-- </div> -->
              </div>
            </template>
          </el-upload>
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
import { upload } from '@/api/file'
import { ElMessage } from 'element-plus'
import { Delete, Download } from '@element-plus/icons-vue'
import { cityList } from '@/config/city'

const emit = defineEmits(['submit'])
const formData = ref({
  title: '', // 标题
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
  files: [], // 上传的文件
  // remarks: '',
})
const rules = reactive({
  title: [{ required: true, message: 'Please input title' }],
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
  console.log(formData.value.files)
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      submitLoading.value = true
      const res = await editInformation({
        ...formData.value,
        files: formData.value.files.map((item) => {
          return {
            path: item.path,
            isCover: item.isCover ? 1 : 0,
          }
        }),
      })
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
    // 图片回显
    formData.value.files =
      (data.images || []).map((item) => {
        return {
          path: item.imageUrl,
          url: `${import.meta.env.VITE_FILE_DOMAIN}${item.imageUrl}`,
          isCover: item.isCover === 1,
        }
      }) || []
  }
}
defineExpose({ open }) // 指定要暴露open方法出去

// 关闭弹窗
function cancel() {
  visible.value = false
}

/**
 * 上传功能
 */
// 上传成功
const handleUploadSuccess = (response, file, fileList) => {
  if (response) {
    console.log('Upload success:', response)
    formData.value.files.push({
      path: response[0],
      url: `${import.meta.env.VITE_FILE_DOMAIN}${response[0]}`,
      isCover: false,
    })
  }
}
// 移除
const handleRemove = (file, fileList) => {
  console.log('Remove file:', file)
  formData.value.files = formData.value.files.filter((item) => item.url !== file.url)
}
// 设置首页图
const handleCover = (file) => {
  formData.value.files.forEach((item) => {
    item.isCover = false
  })
  file.isCover = true
}
// 上传前
const beforeUpload = (file) => {
  // 可以在这里添加文件验证逻辑
  console.log(file.size)
  return true
}
// 自定义上传请求
const customUploadRequest = async (options) => {
  const formData = new FormData()
  formData.append('files', options.file)
  const res = await upload(formData)
  if (res.status === 200) {
    options.onSuccess(res.data)
  }
}
</script>

<style lang="scss" scoped>
.custom-file-item {
  display: flex;
  align-items: center;
  .el-radio__label {
    display: none;
  }
}
</style>

<style lang="scss">
.common-dialog-content {
  .el-upload-list__item-file-name {
    max-width: 172px;
  }

  .el-upload-list {
    display: flex;
    flex-wrap: wrap;
    width: 280px;
    .el-upload-list__item {
      width: 130px;
      position: relative;
      &:nth-child(2n) {
        margin-left: 18px;
      }
      .custom-file-item__delete {
        margin-left: 5px;
        cursor: pointer;
        &:hover {
          color: red;
        }
      }
    }
    .custom-file-item {
      .el-radio {
        margin-right: 10px;
        .el-radio__label {
          display: none;
        }
      }
    }
  }
}
</style>
