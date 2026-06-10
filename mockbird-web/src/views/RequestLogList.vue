<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  getRequestLogList,
  getRequestLogDetail,
  type RequestLog,
  type RequestLogPageParams,
} from '../api/requestLog'
import { getProjectList, type Project } from '../api/project'

const logs = ref<RequestLog[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const projects = ref<Project[]>([])
const filterProjectId = ref<number | null>(null)

const detailVisible = ref(false)
const detailLog = ref<RequestLog | null>(null)

async function loadProjects() {
  try {
    projects.value = await getProjectList()
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

async function loadList() {
  try {
    const params: RequestLogPageParams = { page: currentPage.value, size: pageSize }
    if (filterProjectId.value) params.projectId = filterProjectId.value
    const result = await getRequestLogList(params)
    logs.value = result.list
    total.value = result.total
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

function handleSearch() {
  currentPage.value = 1
  loadList()
}

async function openDetail(row: RequestLog) {
  try {
    detailLog.value = await getRequestLogDetail(row.id)
    detailVisible.value = true
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

function formatDate(_row: unknown, _col: unknown, val: string): string {
  if (!val) return ''
  return val.replace('T', ' ').substring(0, 19)
}

function formatJson(val: string): string {
  if (!val) return '-'
  try {
    return JSON.stringify(JSON.parse(val), null, 2)
  } catch {
    return val
  }
}

function methodTagType(method: string): string {
  if (method === 'GET') return 'success'
  if (method === 'POST') return 'primary'
  if (method === 'PUT') return 'warning'
  return 'danger'
}

function statusTagType(code: number): string {
  if (code >= 200 && code < 300) return 'success'
  if (code >= 400 && code < 500) return 'warning'
  if (code >= 500) return 'danger'
  return 'info'
}

onMounted(() => {
  loadProjects()
  loadList()
})
</script>

<template>
  <div class="request-log-list">
    <div class="toolbar">
      <el-select
        v-model="filterProjectId"
        placeholder="按项目筛选"
        clearable
        style="width: 200px"
        @change="handleSearch"
        @clear="handleSearch"
      >
        <el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <el-table :data="logs" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="requestMethod" label="方法" width="90">
        <template #default="{ row }">
          <el-tag :type="methodTagType(row.requestMethod)" size="small">
            {{ row.requestMethod }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="requestPath" label="请求路径" min-width="180" show-overflow-tooltip />
      <el-table-column prop="responseStatusCode" label="状态码" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.responseStatusCode)" size="small">
            {{ row.responseStatusCode }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="durationMs" label="耗时(ms)" width="90" />
      <el-table-column prop="createdAt" label="请求时间" width="180" :formatter="formatDate" />
      <el-table-column label="操作" width="80" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">详情</el-button>
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

    <el-dialog v-model="detailVisible" title="请求日志详情" width="700px">
      <el-descriptions v-if="detailLog" :column="2" border>
        <el-descriptions-item label="ID">{{ detailLog.id }}</el-descriptions-item>
        <el-descriptions-item label="项目ID">{{ detailLog.projectId }}</el-descriptions-item>
        <el-descriptions-item label="接口ID">{{ detailLog.apiInterfaceId }}</el-descriptions-item>
        <el-descriptions-item label="命中规则ID">{{ detailLog.mockRuleId ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">
          <el-tag :type="methodTagType(detailLog.requestMethod)" size="small">
            {{ detailLog.requestMethod }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求路径">{{ detailLog.requestPath }}</el-descriptions-item>
        <el-descriptions-item label="响应状态码">
          <el-tag :type="statusTagType(detailLog.responseStatusCode)" size="small">
            {{ detailLog.responseStatusCode }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="耗时">{{ detailLog.durationMs }} ms</el-descriptions-item>
        <el-descriptions-item label="请求头" :span="2">
          <pre class="json-block">{{ formatJson(detailLog.requestHeaders) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="请求体" :span="2">
          <pre class="json-block">{{ detailLog.requestBody || '-' }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应体" :span="2">
          <pre class="json-block">{{ formatJson(detailLog.responseBody) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="记录时间" :span="2">
          {{ formatDate(null, null, detailLog.createdAt) }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.request-log-list {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
.json-block {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  font-size: 12px;
  max-height: 200px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
}
</style>
