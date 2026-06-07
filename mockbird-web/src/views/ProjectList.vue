<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getProjectList,
  createProject,
  updateProject,
  deleteProject,
  type Project,
  type ProjectCreateData,
  type ProjectUpdateData,
} from '../api/project'

const projects = ref<Project[]>([])
const currentPage = ref(1)
const pageSize = 10
const total = computed(() => projects.value.length)

const pagedProjects = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return projects.value.slice(start, start + pageSize)
})

const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const dialogTitle = computed(() => (editingId.value ? '编辑项目' : '新建项目'))
const formData = ref<ProjectCreateData>({
  name: '',
  description: '',
  upstreamUrl: '',
})

function openCreate() {
  editingId.value = null
  formData.value = { name: '', description: '', upstreamUrl: '' }
  dialogVisible.value = true
}

function openEdit(project: Project) {
  editingId.value = project.id
  formData.value = {
    name: project.name,
    description: project.description,
    upstreamUrl: project.upstreamUrl,
  }
  dialogVisible.value = true
}

async function handleSave() {
  try {
    if (editingId.value) {
      await updateProject(editingId.value, formData.value as ProjectUpdateData)
      ElMessage.success('项目已更新')
    } else {
      await createProject(formData.value)
      ElMessage.success('项目已创建')
    }
    dialogVisible.value = false
    await loadProjects()
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

async function handleDelete(project: Project) {
  try {
    await ElMessageBox.confirm(`确定要删除项目「${project.name}」吗？`, '删除确认', {
      type: 'warning',
    })
    await deleteProject(project.id)
    ElMessage.success('项目已删除')
    await loadProjects()
  } catch {
    // cancel or error
  }
}

async function loadProjects() {
  try {
    projects.value = await getProjectList()
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

onMounted(() => {
  loadProjects()
})
</script>

<template>
  <div class="project-list">
    <div class="toolbar">
      <el-button type="primary" @click="openCreate">新建项目</el-button>
    </div>

    <el-table :data="pagedProjects" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="项目名称" min-width="160" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="upstreamUrl" label="上游地址" min-width="200" show-overflow-tooltip />
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
    />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="formData.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="上游地址">
          <el-input v-model="formData.upstreamUrl" placeholder="如 http://localhost:8081" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!formData.name" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.project-list {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}
.toolbar {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
