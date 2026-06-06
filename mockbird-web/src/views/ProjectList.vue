<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getProjectList, type Project } from '../api/project'

const projects = ref<Project[]>([])
const currentPage = ref(1)
const pageSize = 10
const total = computed(() => projects.value.length)

const pagedProjects = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return projects.value.slice(start, start + pageSize)
})

onMounted(async () => {
  try {
    projects.value = await getProjectList()
  } catch {
    // axios interceptor already shows ElMessage.error
  }
})
</script>

<template>
  <div class="project-list">
    <el-table :data="pagedProjects" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="项目名称" min-width="160" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="upstreamUrl" label="上游地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
    </el-table>
    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next, total"
      class="pagination"
    />
  </div>
</template>

<style scoped>
.project-list {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
