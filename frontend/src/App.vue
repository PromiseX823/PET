
<template>
  <div class="min-h-screen bg-gray-50">
    <nav class="bg-white shadow-sm">
      <div class="max-w-6xl mx-auto px-4 py-4 flex justify-between items-center">
        <div class="flex items-center space-x-2">
          <svg class="w-8 h-8 text-primary-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <span class="text-xl font-bold text-gray-800">Pet Adoption</span>
        </div>
        <div class="flex items-center space-x-4">
          <router-link to="/" class="text-gray-600 hover:text-primary-500 transition-colors">
            首页
          </router-link>
          <template v-if="authStore.isAuthenticated">
            <router-link to="/profile" class="text-gray-600 hover:text-primary-500 transition-colors">
              个人中心
            </router-link>
            <button 
              @click="handleLogout"
              class="btn btn-secondary"
            >
              退出登录
            </button>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-outline">
              登录
            </router-link>
            <router-link to="/register" class="btn btn-primary">
              注册
            </router-link>
          </template>
        </div>
      </div>
    </nav>
    <main class="max-w-6xl mx-auto px-4 py-8">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

onMounted(() => {
  authStore.initialize()
})

function handleLogout() {
  authStore.logout()
}
</script>
