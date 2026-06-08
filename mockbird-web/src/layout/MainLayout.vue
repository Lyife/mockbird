<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeMenu = ref('/projects')

function handleSelect(index: string) {
  router.push(index)
}
</script>

<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">MockBird</div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        @select="handleSelect"
      >
        <el-menu-item index="/projects">
          <span>项目管理</span>
        </el-menu-item>
        <el-menu-item index="/interfaces">
          <span>接口管理</span>
        </el-menu-item>
        <el-menu-item index="/mock-rules">
          <span>Mock 规则</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <span class="header-title">Mock 服务平台</span>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
}
.layout-aside {
  background-color: #304156;
  overflow: hidden;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  background-color: #2b3a4a;
}
.layout-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
}
.header-title {
  font-size: 16px;
  color: #303133;
}
.layout-main {
  background: #f0f2f5;
  min-height: 0;
}
</style>
