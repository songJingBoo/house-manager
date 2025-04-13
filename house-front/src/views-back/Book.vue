<template>
  <BasePage title="Appointment List">
    <template #header>
      <el-tabs v-model="search.status" class="demo-tabs" @tab-click="getListFn()">
        <el-tab-pane label="Pending" name="PENDING" />
        <el-tab-pane label="Confirmed" name="CONFIRMED" />
        <el-tab-pane label="Cancelled" name="CANCELLED" />
        <el-tab-pane label="Finished" name="FINISHED" />
      </el-tabs>
      <el-form :inline="true" :model="search" class="demo-form-inline">
        <el-form-item label="Title" prop="title">
          <el-input v-model="search.title" placeholder="Enter title" clearable />
        </el-form-item>
        <el-form-item label="Start Time" prop="startTime">
          <el-input v-model="search.startTime" placeholder="Select time" clearable />
        </el-form-item>
        <el-form-item label="End Time" prop="endTime">
          <el-input v-model="search.endTime" placeholder="Select time" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getListFn">Search</el-button>
        </el-form-item>
      </el-form>
    </template>

    <div class="common-table">
      <el-table v-loading="loading" class="common-table__table" :data="tableData" style="width: 100%">
        <el-table-column label="Title" prop="title" width="180">
          <template #default="scope">
            <div class="text-over" :title="scope.row.title">{{ scope.row.title || '--' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="Start Time" prop="startTime" width="180">
          <template #default="scope">
            <div class="text-over">{{ scope.row.startTime || '--' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="End Time" prop="endTime" width="180">
          <template #default="scope">
            <div class="text-over">{{ scope.row.endTime || '--' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="Address" prop="address" width="180">
          <template #default="scope">
            <div class="text-over" :title="scope.row.address">{{ scope.row.address || '--' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="Phone" prop="phone" width="180" />
        <el-table-column label="Creator" prop="username" width="180" />
        <el-table-column label="Create Time" prop="createTime" width="180" />
        <el-table-column label="operate" prop="Operate" fixed="right" width="180">
          <template #default="scope">
            <el-button type="primary" small @click="openEditAppointDialog(scope.row)">Edit</el-button>
            <el-button type="primary" small @click="openAuditAppointDialog(scope.row)">Audit</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="common-table__pager" background layout="total, prev, pager, next" :page-size="20" :total="page.total" />
    </div>

    <!-- 修改预约 -->
    <AppointmentDialog ref="appointmentDialogRef" @submit="getListFn" />
    <!-- 审核预约 -->
    <AuditAppointDialog ref="auditAppointRef" @submit="getListFn" />
  </BasePage>
</template>

<script setup>
import { ref, onMounted, useTemplateRef } from 'vue'
import BasePage from '@/components/back/Base-page.vue'
import AppointmentDialog from '@/components/Appointment-dialog.vue'
import AuditAppointDialog from './book/audit-appoint-dialog.vue'
import { queryAppointmentList } from '@/api/back/appointment'

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

/**
 * 查询预约列表
 */
const loading = ref(false)
const tableData = ref([])
async function getListFn(isInit = true) {
  if (isInit) {
    page.value.pageNum = 1
  }

  try {
    loading.value = true
    const res = await queryAppointmentList(search.value)
    if (res.status === 200) {
      tableData.value = (res.data || []).map((item) => {
        return {
          ...item,
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
 * 编辑预约时间
 */
const appointmentDialogRef = useTemplateRef('appointmentDialogRef')
function openEditAppointDialog(appointment) {
  appointmentDialogRef.value.open(appointment.houseId, appointment)
}
/**
 * 审核预约
 */
const auditAppointRef = useTemplateRef('auditAppointRef')
function openAuditAppointDialog(appointment) {
  auditAppointRef.value.open(appointment)
}
</script>

<style lang="scss" scoped></style>
