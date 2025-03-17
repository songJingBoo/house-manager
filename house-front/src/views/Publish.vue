<template>
  <div class="publish-page">
    <div class="publish-page__title">Publish property information</div>

    <el-form
      ref="publishFormRef"
      class="publish-page__form"
      :model="formData"
      :rules="rules"
      label-position="left"
      label-width="140"
      style="max-width: 600px"
    >
      <el-form-item label="Rent/Sale" prop="intention">
        <el-radio-group v-model="formData.intention">
          <el-radio value="RENT" size="large">Rent</el-radio>
          <el-radio value="SALE" size="large">Sale</el-radio>
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
      <!-- <el-form-item label="Rent/Sale" prop="intention">
        <el-select v-model="formData.intention" placeholder="Please select publication type">
          <el-option label="Rent" value="RENT" />
          <el-option label="Sale" value="SALE" />
        </el-select>
      </el-form-item> -->
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
      <!-- <el-form-item label="Floor" prop="floor">
        <el-input v-model="formData.floor" placeholder="Please enter house floor" />
      </el-form-item>   -->
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
    </el-form>

    <el-button
      type="primary"
      :loading="submitLoading"
      class="publish-page__submit-btn"
      @click="submitForm()"
      >Confirm publish</el-button
    >
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { publishHouse } from '@/api/house'
import { cityList } from '@/config/city'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
const router = useRouter()

onMounted(() => {})

const formData = ref({
  city: '',
  community: '', // 小区
  address: '',
  intention: 'RENT', // 意愿类型（出租 RENT、出售 SALE）
  layout: '', // 户型
  area: '', // 面积
  floor: '', // 层高
  expectPrice: '', // 期望售价
  name: '', // 姓名
  phone: '', // 联系电话
  // remarks: '',
})
const rules = reactive({})
// const rules = reactive({
//   city: [{ required: true, message: 'Please select city' }],
//   community: [
//     { required: true, message: 'Please input community name' },
//     { max: 32, message: 'Maximum input of 32 characters' },
//   ],
//   address: [
//     { required: true, message: 'Please input community name' },
//     { max: 64, message: 'Maximum input of 64 characters' },
//   ],
//   type: [{ required: true, message: 'Please select intention of house' }],
//   layout: [{ required: true, message: 'Please select the housing layout' }],
//   area: [{ required: true, message: 'Please input the area of the house' }],
//   floor: [{ required: true, message: 'Please input the floor of the house' }],
//   expectPrice: [{ required: true, message: 'Please input your expected selling price' }],
//   name: [{ required: true, message: 'Please input your name' }],
//   phone: [{ required: true, message: 'Please input your phone' }],
//   // remarks: [{ required: false }, { max: 100, message: 'Maximum input of 100 characters' }],
// })

const submitLoading = ref(false)
const publishFormRef = ref()
const submitForm = async () => {
  if (!validates()) return

  try {
    submitLoading.value = true
    const res = await publishHouse(formData.value)
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

function validates() {
  const { intention, city, community, address, layout, area, expectPrice, name, phone } =
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
  }
  .publish-page__form {
    width: 540px;
    padding-top: 30px;
  }
  .publish-page__submit-btn {
    width: 160px;
    height: 50px;
    margin-left: auto;
    margin-right: 420px;
  }
}
</style>

<style lang="scss">
.publish-page {
  .el-form-item {
    margin-bottom: 18px;
    padding-bottom: 18px;
    border-bottom: 1px solid #eee;
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
  .publish-page__form {
  }
}
</style>
