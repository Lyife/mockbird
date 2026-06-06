<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function goProjects() {
  router.push('/projects')
}
</script>

<template>
  <div class="home">
    <h1>MockBird</h1>
    <p class="subtitle">接口 Mock 服务平台</p>
    <p class="desc">
      前端/客户端团队在等待真实后端接口开发期间，<br />
      用 MockBird 先伪造出接口的响应数据来联调，不被后端进度卡住。
    </p>
    <el-button type="primary" size="large" @click="goProjects">
      开始使用
    </el-button>

    <el-row :gutter="24" class="feature-row">
      <el-col :span="8">
        <el-card shadow="hover">
          <h3>项目管理</h3>
          <p>多项目隔离，项目级 upstream_url 默认上游地址</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <h3>Mock 规则</h3>
          <p>精确/通配匹配，响应模板支持动态占位符，可配延迟</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <h3>请求日志</h3>
          <p>记录每次 Mock 调用的完整请求/响应/耗时</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.home {
  text-align: center;
  padding: 80px 20px;
}
h1 {
  font-size: 48px;
  color: #303133;
  margin-bottom: 12px;
}
.subtitle {
  font-size: 20px;
  color: #409eff;
  margin-bottom: 16px;
}
.desc {
  color: #909399;
  line-height: 1.8;
  margin-bottom: 32px;
}
.feature-row {
  margin-top: 60px;
  text-align: left;
}
.feature-row h3 {
  color: #303133;
  margin-bottom: 8px;
}
.feature-row p {
  color: #909399;
  font-size: 14px;
}
</style>
