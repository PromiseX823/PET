
<template>
  <div class="text-center py-16">
    <div class="mb-8">
      <svg class="w-24 h-24 mx-auto text-primary-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
      </svg>
    </div>
    <h1 class="text-4xl font-bold text-gray-800 mb-4">欢迎来到宠物领养平台</h1>
    <p class="text-gray-600 mb-8 max-w-2xl mx-auto">
      寻找您的完美宠物伴侣，给它们一个温暖的家。
    </p>
    <div class="flex justify-center space-x-4">
      <router-link to="/register" class="btn btn-primary">
        立即注册
      </router-link>
      <button @click="checkHealth" class="btn btn-secondary">
        检查服务状态
      </button>
    </div>
    
    <div v-if="healthStatus" class="mt-8 p-4 bg-white rounded-lg shadow-md inline-block">
      <h3 class="text-lg font-semibold text-gray-800 mb-2">服务状态</h3>
      <div class="text-left">
        <p>Backend: <span :class="healthStatus.status === 'UP' ? 'text-green-500' : 'text-red-500'">{{ healthStatus.status }}</span></p>
        <p>Redis: <span :class="healthStatus.redis === 'UP' ? 'text-green-500' : 'text-red-500'">{{ healthStatus.redis }}</span></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from '@/api'

const healthStatus = ref(null)

async function checkHealth() {
  try {
    const response = await axios.get('/api/public/health')
    healthStatus.value = response.data.data
  } catch (error) {
    healthStatus.value = { status: 'DOWN', redis: 'DOWN' }
    alert('服务连接失败')
  }
}
</script>
