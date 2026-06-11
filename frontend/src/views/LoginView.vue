
<template>
  <div class="max-w-md mx-auto">
    <div class="card">
      <div class="text-center mb-6">
        <svg class="w-16 h-16 mx-auto text-primary-500 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
        </svg>
        <h2 class="text-2xl font-bold text-gray-800">欢迎回来</h2>
        <p class="text-gray-500 mt-2">请登录您的账号</p>
      </div>
      
      <form @submit.prevent="handleSubmit">
        <div class="mb-4">
          <label class="form-label">用户名</label>
          <input 
            v-model="username"
            type="text" 
            class="form-input"
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="mb-6">
          <label class="form-label">密码</label>
          <input 
            v-model="password"
            type="password" 
            class="form-input"
            placeholder="请输入密码"
            required
          />
        </div>
        
        <button 
          type="submit" 
          class="btn btn-primary w-full"
          :disabled="loading"
        >
          <span v-if="loading" class="animate-spin inline-block w-5 h-5 border-2 border-white border-t-transparent rounded-full mr-2"></span>
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <div class="mt-6 text-center">
        <p class="text-gray-500">
          还没有账号？
          <router-link to="/register" class="text-primary-500 hover:underline">立即注册</router-link>
        </p>
      </div>
      
      <div v-if="error" class="mt-4 p-3 bg-red-50 text-red-600 rounded-lg text-center">
        {{ error }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function handleSubmit() {
  loading.value = true
  error.value = ''
  
  try {
    await authStore.login(username.value, password.value)
    router.push('/profile')
  } catch (err) {
    error.value = err
  } finally {
    loading.value = false
  }
}
</script>
