<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { invokeInterface, type InvokeResponse } from '../api/invoke'
import { getInterfaceList, type ApiInterface } from '../api/interface'
import { getProjectList, type Project } from '../api/project'

const projects = ref<Project[]>([])
const dialogInterfaces = ref<ApiInterface[]>([])
const selectedProjectId = ref<number | null>(null)
const selectedInterfaceId = ref<number | null>(null)
const interfaceLoading = ref(false)

interface KvRow {
  key: string
  value: string
}

const paramRows = ref<KvRow[]>([{ key: '', value: '' }])
const headerRows = ref<KvRow[]>([{ key: '', value: '' }])
const bodyText = ref('')
const sending = ref(false)
const response = ref<InvokeResponse | null>(null)
const responseError = ref('')

const selectedInterface = ref<ApiInterface | null>(null)

async function loadProjects() {
  try {
    projects.value = await getProjectList()
  } catch {
    // axios interceptor shows ElMessage.error
  }
}

async function onProjectChange(projectId: number | null) {
  selectedProjectId.value = projectId
  selectedInterfaceId.value = null
  selectedInterface.value = null
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

function onInterfaceChange(id: number | null) {
  selectedInterfaceId.value = id
  selectedInterface.value = dialogInterfaces.value.find((i) => i.id === id) || null
  response.value = null
  responseError.value = ''
}

function addParamRow() {
  paramRows.value.push({ key: '', value: '' })
}

function removeParamRow(index: number) {
  paramRows.value.splice(index, 1)
}

function addHeaderRow() {
  headerRows.value.push({ key: '', value: '' })
}

function removeHeaderRow(index: number) {
  headerRows.value.splice(index, 1)
}

function buildRecord(rows: KvRow[]): Record<string, string> {
  const record: Record<string, string> = {}
  for (const row of rows) {
    if (row.key.trim()) {
      record[row.key.trim()] = row.value
    }
  }
  return record
}

async function handleSend() {
  if (!selectedInterfaceId.value) return
  sending.value = true
  response.value = null
  responseError.value = ''
  try {
    const queryParams = buildRecord(paramRows.value)
    const headers = buildRecord(headerRows.value)
    const data: { queryParams?: Record<string, string>; headers?: Record<string, string>; body?: string } = {}
    if (Object.keys(queryParams).length) data.queryParams = queryParams
    if (Object.keys(headers).length) data.headers = headers
    if (bodyText.value.trim()) data.body = bodyText.value
    response.value = await invokeInterface(selectedInterfaceId.value, data)
  } catch {
    responseError.value = '请求失败，请确认上游地址已配置且可访问'
  } finally {
    sending.value = false
  }
}

function formatJson(raw: string): string {
  try {
    return JSON.stringify(JSON.parse(raw), null, 2)
  } catch {
    return raw
  }
}

onMounted(() => {
  loadProjects()
})
</script>

<template>
  <div class="online-test">
    <div class="target-bar">
      <el-select
        v-model="selectedProjectId"
        placeholder="选择项目"
        style="width: 200px"
        @change="onProjectChange"
      >
        <el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" />
      </el-select>
      <el-select
        v-model="selectedInterfaceId"
        placeholder="选择接口"
        filterable
        :loading="interfaceLoading"
        :disabled="!selectedProjectId"
        style="width: 280px"
        @change="onInterfaceChange"
      >
        <el-option
          v-for="api in dialogInterfaces"
          :key="api.id"
          :label="`${api.name} (${api.method} ${api.path})`"
          :value="api.id"
        />
      </el-select>
      <el-tag v-if="selectedInterface" type="info" size="small" class="method-tag">
        {{ selectedInterface.method }}
      </el-tag>
      <el-button
        type="primary"
        :disabled="!selectedInterfaceId"
        :loading="sending"
        @click="handleSend"
      >
        发送请求
      </el-button>
    </div>

    <div v-if="selectedInterfaceId" class="main-area">
      <div class="request-panel">
        <div class="section">
          <div class="section-title">
            <span>Query Params</span>
            <el-button size="small" text @click="addParamRow">+ 添加</el-button>
          </div>
          <div v-for="(row, i) in paramRows" :key="i" class="kv-row">
            <el-input v-model="row.key" placeholder="key" size="small" style="width: 160px" />
            <el-input v-model="row.value" placeholder="value" size="small" style="width: 200px" />
            <el-button
              size="small"
              type="danger"
              text
              :disabled="paramRows.length === 1"
              @click="removeParamRow(i)"
            >
              删除
            </el-button>
          </div>
        </div>

        <div class="section">
          <div class="section-title">
            <span>Headers</span>
            <el-button size="small" text @click="addHeaderRow">+ 添加</el-button>
          </div>
          <div v-for="(row, i) in headerRows" :key="i" class="kv-row">
            <el-input v-model="row.key" placeholder="key" size="small" style="width: 160px" />
            <el-input v-model="row.value" placeholder="value" size="small" style="width: 200px" />
            <el-button
              size="small"
              type="danger"
              text
              :disabled="headerRows.length === 1"
              @click="removeHeaderRow(i)"
            >
              删除
            </el-button>
          </div>
        </div>

        <div class="section">
          <div class="section-title">Body</div>
          <el-input
            v-model="bodyText"
            type="textarea"
            :rows="6"
            placeholder='{"key": "value"}'
          />
        </div>
      </div>

      <div class="response-panel">
        <div class="section-title">响应</div>
        <div v-if="response" class="response-content">
          <div class="response-meta">
            <el-tag :type="response.statusCode < 400 ? 'success' : 'danger'" size="small">
              {{ response.statusCode }}
            </el-tag>
            <span class="duration">{{ response.durationMs }}ms</span>
          </div>
          <pre class="response-body">{{ formatJson(response.body) }}</pre>
        </div>
        <div v-else-if="responseError" class="response-error">
          <el-alert :title="responseError" type="error" :closable="false" />
        </div>
        <div v-else class="response-placeholder">
          点击「发送请求」查看响应
        </div>
      </div>
    </div>

    <div v-else class="empty-hint">
      请先选择项目与接口
    </div>
  </div>
</template>

<style scoped>
.online-test {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  min-height: calc(100vh - 120px);
}
.target-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}
.method-tag {
  flex-shrink: 0;
}
.main-area {
  display: flex;
  gap: 20px;
  min-height: 400px;
}
.request-panel {
  flex: 1;
  min-width: 0;
}
.response-panel {
  flex: 1;
  min-width: 0;
  border-left: 1px solid #ebeef5;
  padding-left: 20px;
}
.section {
  margin-bottom: 16px;
}
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}
.kv-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.response-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.duration {
  font-size: 13px;
  color: #909399;
}
.response-body {
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 12px;
  font-family: monospace;
  font-size: 13px;
  line-height: 1.6;
  max-height: 500px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
}
.response-placeholder {
  color: #c0c4cc;
  font-size: 14px;
  padding: 40px 0;
  text-align: center;
}
.response-error {
  margin-top: 8px;
}
.empty-hint {
  color: #c0c4cc;
  font-size: 16px;
  text-align: center;
  padding: 80px 0;
}
</style>
