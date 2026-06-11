
<template>
  <div class="max-w-2xl mx-auto">
    <div class="card">
      <div class="text-center mb-8">
        <div class="w-24 h-24 mx-auto bg-primary-100 rounded-full flex items-center justify-center mb-4">
          <svg class="w-12 h-12 text-primary-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
          </svg>
        </div>
        <h2 class="text-2xl font-bold text-gray-800">{{ user?.username }}</h2>
        <p class="text-gray-500">{{ user?.role }}</p>
      </div>
      
      <div class="space-y-4">
        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
          <span class="text-gray-600">用户名</span>
          <span class="font-medium">{{ user?.username }}</span>
        </div>
        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
          <span class="text-gray-600">邮箱</span>
          <span class="font-medium">{{ user?.email }}</span>
        </div>
        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
          <span class="text-gray-600">手机号</span>
          <span class="font-medium">{{ user?.phone || '未设置' }}</span>
        </div>
        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
          <span class="text-gray-600">注册时间</span>
          <span class="font-medium">{{ formatDate(user?.createdAt) }}</span>
        </div>
      </div>
      
      <div class="mt-8">
        <button @click="handleLogout" class="btn btn-secondary w-full">
          退出登录
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const user = computed(() => authStore.user)

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
