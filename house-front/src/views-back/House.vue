<template>
  <BasePage title="House Manager">
    <template #header>
      <el-tabs v-model="search.status" class="demo-tabs" @tab-click="getListFn()">
        <el-tab-pane label="Pending" name="UNPUBLISHED" />
        <el-tab-pane label="Published" name="PUBLISHED" />
        <el-tab-pane label="Finished" name="FINISHED" />
        <el-tab-pane label="Removed" name="REMOVED" />
      </el-tabs>
      <el-form :inline="true" :model="search" class="demo-form-inline">
        <el-form-item label="Intention" prop="intention" @change="getListFn()">
          <el-select v-model="search.intention" placeholder="Select" style="width: 120px">
            <el-option label="Sale" value="SALE" />
            <el-option label="Rent" value="RENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="City" prop="city">
          <el-select v-model="search.city" placeholder="Select" style="width: 120px">
            <el-option v-for="item in cityList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="Community" prop="community">
          <el-input v-model="search.community" placeholder="Enter community" clearable />
        </el-form-item>
        <el-form-item label="Phone" prop="phone">
          <el-input v-model="search.phone" placeholder="Enter phone" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getListFn">Search</el-button>
          <el-button v-if="store.user.role === 'Admin'" type="primary" @click="filterFn">Filter Config</el-button>
        </el-form-item>
      </el-form>
    </template>

    <div class="common-table">
      <el-table v-loading="loading" class="common-table__table" :data="tableData" style="width: 100%">
        <el-table-column label="Title" prop="titleTxt" width="180" />
        <el-table-column label="Layout" prop="layoutTxt" width="180" />
        <el-table-column label="Area" prop="areaTxt" width="180" />
        <el-table-column label="Expected price" prop="expectPriceTxt" width="180" />
        <el-table-column v-if="search.status === 'FINISHED'" label="Final price" prop="finalPriceTxt" width="180" />
        <el-table-column label="Floor" prop="floor" width="180" />
        <el-table-column label="City" prop="city" width="180" />
        <el-table-column label="Community" prop="community" width="180" />
        <el-table-column label="Address" prop="address" width="180" />
        <el-table-column label="Name" prop="name" width="180" />
        <el-table-column label="Phone" prop="phone" width="180" />
        <el-table-column label="Publish Time" prop="createTime" width="180" />
        <el-table-column v-if="search.status === 'UNPUBLISHED' || search.status === 'PUBLISHED'" label="operate" prop="Operate" fixed="right" width="280">
          <template #default="scope">
            <el-button type="primary" small @click="editFn(scope.row)">Edit</el-button>
            <el-button v-if="scope.row.status === 'UNPUBLISHED'" type="primary" small @click="changeStatusFn(scope.row, 'PUBLISHED')">Publish</el-button>
            <el-button v-if="scope.row.status === 'PUBLISHED'" type="primary" small @click="toSoldFn(scope.row)">{{ scope.row.intention === 'RENT' ? 'To Rented' : 'To Sold' }}</el-button>
            <el-button type="primary" small @click="changeStatusFn(scope.row, 'REMOVED')">Remove</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="common-table__pager" background layout="total, prev, pager, next" :page-size="20" :total="page.total" />
    </div>

    <!-- 信息修改 -->
    <EditDialog ref="editRef" @submit="getListFn" />
    <!-- 过滤项配置 -->
    <FilterDialog ref="filterRef" @submit="getListFn" />
    <!-- 售出 -->
    <SoldDialog ref="soldRef" @submit="getListFn" />
  </BasePage>
</template>

<script setup>
import { ref, onMounted, useTemplateRef } from 'vue'
import BasePage from '@/components/back/Base-page.vue'
import EditDialog from './audit/edit-dialog.vue'
import FilterDialog from './house/filter-dialog.vue'
import SoldDialog from './house/sold-dialog.vue'
import { queryHouseList, changeStatus } from '@/api/back/house-manage'
import { cityList } from '@/config/city'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useInfoStore } from '@/stores/userInfo'
const store = useInfoStore()

onMounted(() => {
  getListFn()
})

const search = ref({
  status: 'UNPUBLISHED',
  intention: '',
  city: '',
  community: '',
  phone: '',
})
const page = ref({
  pageNum: 1,
  total: 0,
})

// 查询房屋
const loading = ref(false)
const tableData = ref([])
async function getListFn(isInit = true) {
  if (isInit) {
    page.value.pageNum = 1
  }

  try {
    loading.value = true
    const res = await queryHouseList({
      ...search.value,
      intention: search.value.intention || undefined,
    })
    if (res.status === 200) {
      tableData.value = (res.data || []).map((item) => {
        return {
          ...item,
          titleTxt: item.title || '--',
          layoutTxt: `${item.layout.toLowerCase()}-bedroom`,
          areaTxt: `${item.area}㎡`,
          expectPriceTxt: `${item.expectPrice}￥10k`,
          finalPriceTxt: `${item.finalPrice}￥10k`,
        }
      })
    }
  } catch (e) {
    console.log(e)
  } finally {
    loading.value = false
  }
}

/**
 * 发布/下架房产
 */
const statusLoading = ref(false)
async function changeStatusFn(house, status) {
  const ok = await ElMessageBox.confirm('Confirm to change the property status?', 'Warning', {
    confirmButtonText: 'OK',
    cancelButtonText: 'Cancel',
    type: 'warning',
  })
    .then(() => 1)
    .catch(() => 0)
  if (!ok) return

  try {
    statusLoading.value = true
    const res = await changeStatus({
      houseId: house.houseId,
      status,
    })
    if (res.status === 200) {
      getListFn()
      ElMessage.success('Property status updated successfully!')
    } else {
      ElMessage.error(res.message || 'Property status updated failed')
    }
  } catch (e) {
    console.log(e)
  } finally {
    statusLoading.value = false
  }
}

// 打开新增/编辑弹窗
const editRef = useTemplateRef('editRef')
function editFn(data) {
  editRef.value.open(data)
}

// 打开售出/租出弹窗
const soldRef = useTemplateRef('soldRef')
function toSoldFn(data) {
  soldRef.value.open(data)
}

// 打开过滤项配置弹窗
const filterRef = useTemplateRef('filterRef')
function filterFn() {
  filterRef.value.open()
}
</script>

<style lang="scss" scoped></style>
