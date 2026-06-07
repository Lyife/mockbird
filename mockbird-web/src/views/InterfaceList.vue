<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getInterfaceList,
  createInterface,
  updateInterface,
  deleteInterface,
  type ApiInterface,
  type InterfacePageParams,
  type InterfaceCreateData,
  type InterfaceUpdateData,
} from '../api/interface'
import { getProjectList, type Project } from '../api/project'

const interfaces = ref<ApiInterface[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const searchName = ref('')
const filterMethod = ref('')

const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const dialogTitle = ref('新建接口')
const projects = ref<Project[]>([])
const formData = ref<InterfaceCreateData>({
  projectId: 0,
  name: '',
  path: '',
  method: 'GET',
  upstreamUrl: '',
})

async function loadProjects() {
  try {
    projects.value = await getProjectList()
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

async function loadList() {
  try {
    const params: InterfacePageParams = {
      page: currentPage.value,
      size: pageSize,
    }
    if (searchName.value) params.name = searchName.value
    if (filterMethod.value) params.method = filterMethod.value
    const result = await getInterfaceList(params)
    interfaces.value = result.list
    total.value = result.total
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

function openCreate() {
  editingId.value = null
  dialogTitle.value = '新建接口'
  formData.value = { projectId: 0, name: '', path: '', method: 'GET', upstreamUrl: '' }
  dialogVisible.value = true
}

function openEdit(row: ApiInterface) {
  editingId.value = row.id
  dialogTitle.value = '编辑接口'
  formData.value = {
    projectId: row.projectId,
    name: row.name,
    path: row.path,
    method: row.method,
    upstreamUrl: row.upstreamUrl,
  }
  dialogVisible.value = true
}

async function handleSave() {
  try {
    if (editingId.value) {
      await updateInterface(editingId.value, formData.value as InterfaceUpdateData)
      ElMessage.success('接口已更新')
    } else {
      await createInterface(formData.value)
      ElMessage.success('接口已创建')
    }
    dialogVisible.value = false
    await loadList()
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

async function handleDelete(row: ApiInterface) {
  try {
    await ElMessageBox.confirm(`确定要删除接口「${row.name}」吗？`, '删除确认', {
      type: 'warning',
    })
    await deleteInterface(row.id)
    ElMessage.success('接口已删除')
    await loadList()
  } catch {
    // cancel or error
  }
}

function handleSearch() {
  currentPage.value = 1
  loadList()
}

onMounted(() => {
  loadProjects()
  loadList()
})
</script>

<template>
  <div class="interface-list">
    <div class="toolbar">
      <el-input
        v-model="searchName"
        placeholder="搜索接口名称"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-select
        v-model="filterMethod"
        placeholder="请求方法"
        clearable
        style="width: 140px"
        @change="handleSearch"
        @clear="handleSearch"
      >
        <el-option label="GET" value="GET" />
        <el-option label="POST" value="POST" />
        <el-option label="PUT" value="PUT" />
        <el-option label="DELETE" value="DELETE" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="openCreate">新建接口</el-button>
    </div>

    <el-table :data="interfaces" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="接口名称" min-width="160" />
      <el-table-column prop="projectName" label="所属项目" min-width="120" />
      <el-table-column prop="path" label="路径" min-width="160" />
      <el-table-column prop="method" label="方法" width="90">
        <template #default="{ row }">
          <el-tag :type="row.method === 'GET' ? 'success' : row.method === 'POST' ? 'primary' : row.method === 'PUT' ? 'warning' : 'danger'" size="small">
            {{ row.method }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="upstreamUrl" label="上游地址" min-width="180" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="所属项目" required>
          <el-select v-model="formData.projectId" placeholder="请选择项目" style="width: 100%">
            <el-option
              v-for="p in projects"
              :key="p.id"
              :label="p.name"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" required>
          <el-input v-model="formData.name" placeholder="请输入接口名称" />
        </el-form-item>
        <el-form-item label="路径" required>
          <el-input v-model="formData.path" placeholder="如 /api/users" />
        </el-form-item>
        <el-form-item label="方法" required>
          <el-select v-model="formData.method" style="width: 100%">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="上游地址">
          <el-input v-model="formData.upstreamUrl" placeholder="可选，留空则使用项目级上游" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :disabled="!formData.name || !formData.path || !formData.projectId"
          @click="handleSave"
        >
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.interface-list {
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
