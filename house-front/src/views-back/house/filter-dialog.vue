<template>
  <el-dialog v-model="visible" title="Filter Config" width="700" :before-close="cancel">
    <div class="common-dialog-content filter-dialog">
      <div class="config-item" v-for="(item, index) in configList" :key="item.code">
        <div class="config-item__name">
          <el-checkbox v-model="item.status" class="config-checkbox" :true-value="1" :false-value="0" />
          <el-input v-model="item.name" placeholder="请输入过滤项名称" clearable :disabled="!item.status" @clear="resetItemName(item)" style="width: 80px; margin-right: 10px" />
          <span>{{ item.suffix }}</span>
        </div>
        <div class="config-item__content">
          <div v-for="(option, optionIndex) in item.config" :key="optionIndex" class="option-item">
            <el-input v-model="option.min" class="config-input" placeholder="最小值" clearable :disabled="!item.status" @clear="resetOptionMin(option)"></el-input>
            <el-input v-model="option.eq" class="config-input" placeholder="等于" clearable :disabled="!item.status" @clear="resetOptionEq(option)"></el-input>
            <el-input v-model="option.max" class="config-input" placeholder="最大值" clearable :disabled="!item.status" @clear="resetOptionMax(option)"></el-input>
            <el-button @click="deleteOption(item, optionIndex)" type="danger" :disabled="!item.status">删除</el-button>
          </div>
          <el-button @click="addOption(item)" type="primary" :disabled="!item.status">添加选项</el-button>
        </div>
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm()">提交</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { queryHouseFilter, saveHouseFilter } from '@/api/back/house-manage'
import { ElMessage } from 'element-plus'

/**
 * 固定过滤项
 */
const configList = ref([
  {
    name: '单价',
    code: 'unitPrice',
    suffix: '元/㎡',
    status: 0,
    config: [],
  },
  {
    name: '总价',
    code: 'totalPrice',
    suffix: '元',
    status: 0,
    config: [],
  },
  {
    name: '面积',
    code: 'area',
    suffix: '㎡',
    status: 0,
    config: [],
  },
  {
    name: '户型',
    code: 'layout',
    suffix: '居',
    status: 0,
    config: [],
  },
])

// 打开弹窗
const visible = defineModel(false)
function open() {
  visible.value = true
  getHouseFilter()
}
defineExpose({ open }) // 指定要暴露open方法出去

// 关闭弹窗
function cancel() {
  visible.value = false
}

// 添加选项
function addOption(item) {
  item.config.push({})
}

// 删除选项
function deleteOption(item, optionIndex) {
  item.config.splice(optionIndex, 1)
}

// 重置过滤项
function resetItemName(item) {
  item.name = ''
}
function resetOptionMin(option) {
  option.min = ''
}
function resetOptionMax(option) {
  option.max = ''
}
function resetOptionEq(option) {
  option.eq = ''
}

/**
 * 获取过滤项
 */
async function getHouseFilter() {
  const res = await queryHouseFilter()
  if (res.status === 200) {
    configList.value = (res.data || []).map((item) => {
      return {
        ...item,
        config: JSON.parse(item.config) || [],
      }
    })
  }
}

/**
 * 保存
 */
async function submitForm() {
  const params = configList.value.map((item) => {
    return {
      ...item,
      config: JSON.stringify(item.config),
    }
  })
  const res = await saveHouseFilter(params)
  if (res.status === 200) {
    ElMessage.success('保存成功')
    cancel()
  }
}
</script>

<style lang="scss" scoped>
.filter-dialog {
  height: auto;
  max-height: 500px;
  overflow-y: auto;

  .config-item {
    display: flex;
    align-items: flex-start;
    line-height: 20px;
    margin-bottom: 10px;
  }

  .config-item__name {
    width: 160px;
    margin-right: 10px;
    display: flex;
    align-items: center;
    .config-checkbox {
      margin-right: 12px;
    }
  }

  .config-item__content {
    flex: 1;
    .config-input {
      width: 100px;
      margin-right: 10px;
    }
  }

  .option-item {
    display: flex;
    align-items: center;
    margin-bottom: 5px;
  }

  .option-item input {
    margin-right: 10px;
  }
}
</style>

<style lang="scss">
.common-dialog-content {
  .el-upload-list__item-file-name {
    max-width: 172px;
  }
}
</style>
