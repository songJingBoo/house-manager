<template>
  <div class="publish-page">
    <div class="publish-page__title">Publish property information</div>

    <div class="publish-page__content">
      <el-scrollbar>
        <el-form
          ref="publishFormRef"
          class="publish-form"
          :model="formData"
          :rules="rules"
          label-position="left"
          label-width="140"
          style="max-width: 600px"
        >
          <el-form-item label="Rent/Sale" prop="intention">
            <el-radio-group v-model="formData.intention">
              <el-radio value="SALE" size="large">Sale</el-radio>
              <el-radio value="RENT" size="large">Rent</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="City" prop="city">
            <el-select v-model="formData.city" placeholder="Please select city">
              <el-option v-for="item in cityList" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="Community" prop="community">
            <el-input v-model="formData.community" placeholder="Please enter the community" />
          </el-form-item>
          <el-form-item label="Address" prop="address">
            <el-input v-model="formData.address" placeholder="Please enter the address" />
          </el-form-item>
          <el-form-item label="Layout" prop="layout">
            <el-select v-model="formData.layout" placeholder="Please select house layout">
              <el-option label="one-bedroom" value="ONE" />
              <el-option label="two-bedroom" value="TWO" />
              <el-option label="three-bedroom" value="THREE" />
              <el-option label="four-bedroom" value="FOUR" />
            </el-select>
          </el-form-item>
          <el-form-item label="Area" prop="area">
            <el-input v-model="formData.area" placeholder="Please enter house area">
              <template #suffix>
                <span>㎡</span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="Floor" prop="floor">
            <el-input v-model="formData.floor" placeholder="Please enter house floor" />
          </el-form-item>  
          <el-form-item
            :label="formData.intention === 'RENT' ? 'Expected rent' : 'Expected price'"
            prop="expectPrice"
          >
            <el-input
              v-model="formData.expectPrice"
              :placeholder="
                formData.intention === 'RENT'
                  ? 'Please enter your expected rent'
                  : 'Please enter your expected selling price'
              "
            >
              <template #suffix>
                <span>{{ formData.intention === 'RENT' ? '¥ / month' : '¥ 10K' }}</span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="Name" prop="name">
            <el-input v-model="formData.name" placeholder="Please enter your name" />
          </el-form-item>
          <el-form-item label="Phone" prop="phone">
            <el-input
              v-model="formData.phone"
              intention="number"
              placeholder="Please enter your phone"
            />
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
            </el-upload>
          </el-form-item>
        </el-form>
      </el-scrollbar>
    </div>

    <div class="publish-page__btn">
      <el-button
        type="primary"
        :loading="submitLoading"
        class="submit-btn"
        @click="submitForm()"
      >Publish</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { publishHouse } from '@/api/house'
import { upload } from '@/api/file'
import { cityList } from '@/config/city'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
const router = useRouter()

onMounted(() => {})

const formData = ref({
  city: '',
  community: '', // 小区
  address: '',
  intention: 'SALE', // 意愿类型（出租 RENT、出售 SALE）
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
  layout: [{ required: true, message: 'Please select the housing layout' }],
  area: [{ required: true, message: 'Please input the area of the house' }],
  floor: [{ required: true, message: 'Please input the floor of the house' }],
  expectPrice: [{ required: true, message: 'Please input your expected selling price' }],
  name: [{ required: true, message: 'Please input your name' }],
  phone: [{ required: true, message: 'Please input your phone' }],
  files: [
    { type: 'array', required: true, message: 'Please upload images', trigger: 'change' },
    { type: 'array', min: 4, message: 'Minimum 4 images required', trigger: 'change' },
  ]
  // remarks: [{ required: false }, { max: 100, message: 'Maximum input of 100 characters' }],
})

const submitLoading = ref(false)
const publishFormRef = ref()
const submitForm = async () => {
  if (!validates()) return

  try {
    submitLoading.value = true
    const res = await publishHouse({
      ...formData.value,
      files: formData.value.files.map((item) => {
        return {
          path: item.path,
          isCover: item.isCover ? 1 : 0,
        }
      }),
    })
    if (res.status === 200) {
      router.push({ name: 'HouseList' })
      ElMessage.success('Successfully published')
    } else {
      ElMessage.error(res.message || 'publish failed')
    }
  } catch (e) {
    console.log(e)
  } finally {
    submitLoading.value = false
  }
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

function validates() {
  const { intention, city, community, address, layout, area, expectPrice, name, phone, files } =
    formData.value

  let errTip = ''
  if (!city) {
    errTip = 'Please select city'
  } else if (!community) {
    errTip = 'Please enter the community'
  } else if (!address) {
    errTip = 'Please enter the address'
  } else if (!layout) {
    errTip = 'Please select house layout'
  } else if (!area) {
    errTip = 'Please enter house area'
  } else if (!expectPrice) {
    errTip =
      intention === 'RENT'
        ? 'Please enter your expected rent'
        : 'Please enter your expected selling price'
  } else if (!name) {
    errTip = 'Please enter your name'
  } else if (!phone) {
    errTip = 'Please enter your phone'
  } else if (!files.length) {
    errTip = 'Please upload images'
  } else if (files.length < 4) {
    errTip = 'Minimum 4 images required'
  }

  if (errTip) {
    ElMessage.info(errTip)
    return false
  }
  return true
}
</script>

<style lang="scss" scoped>
.publish-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  .publish-page__title {
    background: #f5f5f6;
    width: 100%;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 20px;
    font-size: 26px;
    font-weight: bold;
    border-bottom: 1px solid #eee;
  }
  .publish-page__content {
    width: 100%;
    height: calc(100% - 240px);
    .publish-form {
      width: 540px;
      margin: 0 auto;
      padding-top: 30px;
      padding-right: 20px;
    }
  }
  
  .publish-page__btn {
    width: 100%;
    height: 110px;
    margin-top: 10px;
    padding-top: 20px;
    border-top: 1px solid #eee;
    background: #f5f5f6;
    text-align: center;
    .submit-btn {
      width: 160px;
      height: 50px;
      margin-right: -338px;
    }
  }
}
</style>

<style lang="scss">
.publish-page {
  .el-form-item {
    margin-bottom: 18px;
    padding-bottom: 18px;
    &:not(:last-child) {
      border-bottom: 1px solid #eee;
    }
    .el-form-item__label {
      font-size: 16px;
      font-weight: bold;
    }
    .el-input__wrapper,
    .el-select__wrapper {
      box-shadow: none;
    }
    .el-input-number {
      width: 400px;
      .el-input-number__decrease,
      .el-input-number__increase {
        display: none;
      }
      .el-input__wrapper {
        padding: 0 11px;
      }
    }
  }
  .el-upload-list {
    display: flex;
    flex-wrap: wrap;
    .el-upload-list__item {
      width: 180px;
      padding: 8px;
      &:nth-child(2n) {
        margin-left: 18px;
      }
      .el-upload-list__item-file-name {
        display: none;
      }
      .el-upload-list__item-thumbnail {
        width: 148px;
        height: 97px;
      }
    }
  }
}
</style>
