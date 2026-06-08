<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getMockRuleList,
  createMockRule,
  updateMockRule,
  deleteMockRule,
  type MockRule,
  type MockRulePageParams,
  type MockRuleCreateData,
  type MockRuleUpdateData,
} from '../api/mockRule'
import { getInterfaceList, type ApiInterface } from '../api/interface'
import { getProjectList, type Project } from '../api/project'

const rules = ref<MockRule[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const searchName = ref('')

const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const dialogTitle = ref('新建规则')

const allInterfaces = ref<ApiInterface[]>([])
const dialogInterfaces = ref<ApiInterface[]>([])
const interfaceLoading = ref(false)
const projects = ref<Project[]>([])
const selectedProjectId = ref<number | null>(null)

const formData = ref<MockRuleCreateData>({
  apiInterfaceId: null,
  name: '',
  matchType: 'exact',
  matchRule: '',
  responseBody: '',
  responseStatusCode: 200,
  responseHeaders: '',
  delayMs: 0,
})

function getInterfaceName(id: number): string {
  const api = allInterfaces.value.find((i) => i.id === id)
  return api ? api.name : `#${id}`
}

async function loadAllInterfaces() {
  try {
    const result = await getInterfaceList({ page: 1, size: 999 })
    allInterfaces.value = result.list
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

async function loadProjects() {
  try {
    projects.value = await getProjectList()
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

async function onProjectChange(projectId: number | null) {
  selectedProjectId.value = projectId
  formData.value.apiInterfaceId = null
  if (!projectId) {
    dialogInterfaces.value = []
    return
  }
  interfaceLoading.value = true
  try {
    const result = await getInterfaceList({ projectId, size: 999 })
    dialogInterfaces.value = result.list
  } catch {
    // axios interceptor shows ElMessage.error
  } finally {
    interfaceLoading.value = false
  }
}

async function loadList() {
  try {
    const params: MockRulePageParams = { page: currentPage.value, size: pageSize }
    if (searchName.value) params.name = searchName.value
    const result = await getMockRuleList(params)
    rules.value = result.list
    total.value = result.total
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

function openCreate() {
  editingId.value = null
  dialogTitle.value = '新建规则'
  selectedProjectId.value = null
  dialogInterfaces.value = []
  formData.value = {
    apiInterfaceId: null,
    name: '',
    matchType: 'exact',
    matchRule: '',
    responseBody: '',
    responseStatusCode: 200,
    responseHeaders: '',
    delayMs: 0,
  }
  dialogVisible.value = true
}

async function openEdit(row: MockRule) {
  editingId.value = row.id
  dialogTitle.value = '编辑规则'
  const api = allInterfaces.value.find((i) => i.id === row.apiInterfaceId)
  selectedProjectId.value = api ? api.projectId : null
  if (selectedProjectId.value) {
    await onProjectChange(selectedProjectId.value)
  }
  formData.value = {
    apiInterfaceId: row.apiInterfaceId,
    name: row.name,
    matchType: row.matchType,
    matchRule: row.matchRule,
    responseBody: row.responseBody,
    responseStatusCode: row.responseStatusCode,
    responseHeaders: row.responseHeaders,
    delayMs: row.delayMs,
  }
  dialogVisible.value = true
}

async function handleSave() {
  try {
    if (editingId.value) {
      await updateMockRule(editingId.value, formData.value as MockRuleUpdateData)
      ElMessage.success('规则已更新')
    } else {
      await createMockRule(formData.value)
      ElMessage.success('规则已创建')
    }
    dialogVisible.value = false
    await loadList()
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

async function handleDelete(row: MockRule) {
  try {
    await ElMessageBox.confirm(`确定要删除规则「${row.name}」吗？`, '删除确认', {
      type: 'warning',
    })
    await deleteMockRule(row.id)
    ElMessage.success('规则已删除')
    await loadList()
  } catch {
    // cancel or error
  }
}

async function handleToggle(row: MockRule, val: boolean) {
  try {
    await updateMockRule(row.id, {
      apiInterfaceId: row.apiInterfaceId,
      name: row.name,
      matchType: row.matchType,
      matchRule: row.matchRule,
      enabled: val ? 1 : 0,
    })
    row.enabled = val ? 1 : 0
    ElMessage.success(val ? '已启用' : '已停用')
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

function handleSearch() {
  currentPage.value = 1
  loadList()
}

function formatDate(_row: unknown, _col: unknown, val: string) {
  if (!val) return ''
  return val.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  loadAllInterfaces()
  loadProjects()
  loadList()
})
</script>

<template>
  <div class="rule-list">
    <div class="toolbar">
      <el-input
        v-model="searchName"
        placeholder="搜索规则名称"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="openCreate">新建规则</el-button>
    </div>

    <el-table :data="rules" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="规则名称" min-width="140" />
      <el-table-column label="关联接口" min-width="140">
        <template #default="{ row }">
          {{ getInterfaceName(row.apiInterfaceId) }}
        </template>
      </el-table-column>
      <el-table-column prop="matchType" label="匹配类型" width="90">
        <template #default="{ row }">
          <el-tag
            :type="row.matchType === 'exact' ? 'success' : row.matchType === 'wildcard' ? 'warning' : 'info'"
            size="small"
          >
            {{ row.matchType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="matchRule" label="匹配规则" min-width="150" show-overflow-tooltip />
      <el-table-column prop="responseStatusCode" label="状态码" width="80" />
      <el-table-column prop="delayMs" label="延迟(ms)" width="90" />
      <el-table-column label="启用" width="80">
        <template #default="{ row }">
          <el-switch
            :model-value="row.enabled === 1"
            size="small"
            @change="(val: boolean) => handleToggle(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" :formatter="formatDate" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next, total"
      class="pagination"
      @current-change="loadList"
    />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="90px">
        <el-form-item label="所属项目" required>
          <el-select
            v-model="selectedProjectId"
            placeholder="请先选择项目"
            style="width: 100%"
            :disabled="!!editingId"
            @change="onProjectChange"
          >
            <el-option
              v-for="p in projects"
              :key="p.id"
              :label="p.name"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关联接口" required>
          <el-select
            v-model="formData.apiInterfaceId"
            placeholder="请先选择项目"
            filterable
            :loading="interfaceLoading"
            :disabled="!selectedProjectId || !!editingId"
            style="width: 100%"
          >
            <el-option
              v-for="api in dialogInterfaces"
              :key="api.id"
              :label="`${api.name} (${api.method} ${api.path})`"
              :value="api.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规则名称" required>
          <el-input v-model="formData.name" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="匹配类型" required>
          <el-select v-model="formData.matchType" style="width: 100%">
            <el-option label="精确匹配 (exact)" value="exact" />
            <el-option label="通配匹配 (wildcard)" value="wildcard" />
            <el-option label="正则匹配 (regex)" value="regex" />
          </el-select>
        </el-form-item>
        <el-form-item label="匹配规则" required>
          <el-input v-model="formData.matchRule" placeholder="如 /api/users、/api/* 或 ^/api/.*" />
        </el-form-item>
        <el-form-item label="响应体">
          <el-input
            v-model="formData.responseBody"
            type="textarea"
            :rows="5"
            placeholder="JSON 响应"
          />
        </el-form-item>
        <el-form-item label="响应头">
          <el-input v-model="formData.responseHeaders" placeholder='JSON 格式，如 {"X-Custom":"value"}' />
        </el-form-item>
        <el-form-item label="状态码">
          <el-input-number v-model="formData.responseStatusCode" :min="100" :max="599" />
        </el-form-item>
        <el-form-item label="延迟(ms)">
          <el-input-number v-model="formData.delayMs" :min="0" :max="60000" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :disabled="!formData.name || !formData.matchRule || !formData.apiInterfaceId"
          @click="handleSave"
        >
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.rule-list {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.toolbar .el-button--success {
  margin-left: auto;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
