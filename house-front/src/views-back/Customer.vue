<template>
  <BasePage title="Customer">
    <template #header>
      <el-form :inline="true" :model="search" class="demo-form-inline">
        <el-form-item label="Username">
          <el-input v-model="search.keyword" placeholder="Enter username/phone" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getListFn">Search</el-button>
        </el-form-item>
      </el-form>
    </template>

    <div class="common-table">
      <el-table v-loading="loading" class="common-table__table" :data="tableData" style="width: 100%">
        <el-table-column prop="username" label="Username" width="180" />
        <el-table-column prop="phone" label="Phone" width="180" />
        <el-table-column prop="createTime" label="Create Time" />
        <el-table-column prop="updateTime" label="Update Time" />
        <el-table-column prop="operate" label="Operate">
          <template #default="scope">
            <el-button type="primary" small @click="editCustomerFn(scope.row)">Edit</el-button>
            <!-- <el-button type="primary" small @click="deleteManagerFn(scope.row)">Delete</el-button> -->
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="common-table__pager" background layout="total, prev, pager, next" :page-size="20" :total="page.total" />
    </div>

    <!-- 新增/修改 -->
    <AddDialog ref="addRef" @submit="getListFn" />
  </BasePage>
</template>

<script setup>
import { ref, onMounted, useTemplateRef } from 'vue'
import BasePage from '@/components/back/Base-page.vue'
import AddDialog from './manager/add-dialog.vue'
import { queryCustomerList } from '@/api/back/customer'
import { ElMessage, ElMessageBox } from 'element-plus'

onMounted(() => {
  getListFn()
})

const search = ref({
  keyword: '',
})
const page = ref({
  pageNum: 1,
  total: 0,
})

// 查询管理员
const loading = ref(false)
const tableData = ref([])
async function getListFn(isInit = true) {
  if (isInit) {
    page.value.pageNum = 1
  }

  try {
    loading.value = true
    const res = await queryCustomerList(search.value)
    if (res.status === 200) {
      // const { records = [], total = 0 } = res.data || {}
      tableData.value = res.data || []
      // page.value.total = total
    }
  } catch (e) {
    console.log(e)
  } finally {
    loading.value = false
  }
}

// 打开新增/编辑弹窗
const addRef = useTemplateRef('addRef')
function editCustomerFn(data) {
  addRef.value.open(data)
}
</script>

<style lang="scss" scoped></style>
