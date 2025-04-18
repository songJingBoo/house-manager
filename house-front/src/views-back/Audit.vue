<template>
  <BasePage title="Audit">
    <template #header>
      <el-tabs v-model="search.status" class="demo-tabs" @tab-click="getListFn()">
        <el-tab-pane label="Pending" name="PENDING" />
        <el-tab-pane label="Approved" name="APPROVED" />
        <el-tab-pane label="Rejected" name="REJECTED" />
      </el-tabs>
      <el-form :inline="true" :model="search" class="demo-form-inline">
        <el-form-item label="City" prop="city">
          <el-select v-model="search.city" placeholder="Select" style="width: 120px">
            <el-option v-for="item in cityList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="Phone" prop="phone">
          <el-input v-model="search.phone" placeholder="Enter phone" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getListFn">Search</el-button>
        </el-form-item>
      </el-form>
    </template>

    <div class="common-table">
      <el-table v-loading="loading" class="common-table__table" :data="tableData" style="width: 100%">
        <el-table-column label="Title" prop="titleTxt" width="180" />
        <el-table-column label="Layout" prop="layoutTxt" width="180" />
        <el-table-column label="Area" prop="areaTxt" width="180" />
        <el-table-column label="Expected price" prop="expectPriceTxt" width="180" />
        <el-table-column label="Floor" prop="floor" width="180" />
        <el-table-column label="City" prop="city" width="180" />
        <el-table-column label="Community" prop="community" width="180" />
        <el-table-column label="Address" prop="address" width="180" />
        <el-table-column label="Name" prop="name" width="180" />
        <el-table-column label="Phone" prop="phone" width="180" />
        <el-table-column label="Publish Time" prop="createTime" width="180" />
        <el-table-column v-if="search.status === 'PENDING'" label="operate" prop="Operate" fixed="right" width="180">
          <template #default="scope">
            <el-button type="primary" small @click="editFn(scope.row)">Edit</el-button>
            <el-button type="primary" small @click="auditFn(scope.row)">Audit</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="common-table__pager" background layout="total, prev, pager, next" :page-size="20" :total="page.total" />
    </div>

    <!-- 信息修改 -->
    <EditDialog ref="editRef" @submit="getListFn" />
    <!-- 审核 -->
    <AuditDialog ref="auditRef" @submit="getListFn" />
  </BasePage>
</template>

<script setup>
import { ref, onMounted, useTemplateRef } from 'vue'
import BasePage from '@/components/back/Base-page.vue'
import EditDialog from './audit/edit-dialog.vue'
import AuditDialog from './audit/audit-dialog.vue'
import { queryAuditList } from '@/api/back/audit'
import { cityList } from '@/config/city'

onMounted(() => {
  getListFn()
})

const search = ref({
  status: 'PENDING',
  city: '',
  phone: '',
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
    const res = await queryAuditList(search.value)
    if (res.status === 200) {
      tableData.value = (res.data || []).map((item) => {
        return {
          ...item,
          titleTxt: item.title || '--',
          layoutTxt: `${item.layout.toLowerCase()}-bedroom`,
          areaTxt: `${item.area}㎡`,
          expectPriceTxt: `${item.area}￥10k`,
        }
      })
    }
  } catch (e) {
    console.log(e)
  } finally {
    loading.value = false
  }
}

// 打开审核弹窗
const auditRef = useTemplateRef('auditRef')
function auditFn(data) {
  auditRef.value.open(data)
}

// 打开新增/编辑弹窗
const editRef = useTemplateRef('editRef')
function editFn(data) {
  editRef.value.open(data)
}
</script>

<style lang="scss" scoped></style>
