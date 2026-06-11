<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <Header />
    <!-- 主要内容区域 -->
    <main class="main-content">
      <router-view />
    </main>
    <!-- 底部页脚 -->
    <Footer />
  </div>
</template>

<script setup>
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import { useUserStore } from '@/stores'

// 初始化用户信息
const userStore = useUserStore()
userStore.initializeUser()
</script>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}
</style>