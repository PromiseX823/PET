
<template>
  <div class="max-w-md mx-auto">
    <div class="card">
      <div class="text-center mb-6">
        <svg class="w-16 h-16 mx-auto text-primary-500 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"></path>
        </svg>
        <h2 class="text-2xl font-bold text-gray-800">创建账号</h2>
        <p class="text-gray-500 mt-2">加入我们，开始您的宠物领养之旅</p>
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
        <div class="mb-4">
          <label class="form-label">邮箱</label>
          <input 
            v-model="email"
            type="email" 
            class="form-input"
            placeholder="请输入邮箱"
            required
          />
        </div>
        <div class="mb-4">
          <label class="form-label">手机号</label>
          <input 
            v-model="phone"
            type="tel" 
            class="form-input"
            placeholder="请输入手机号（可选）"
          />
        </div>
        <div class="mb-6">
          <label class="form-label">密码</label>
          <input 
            v-model="password"
            type="password" 
            class="form-input"
            placeholder="请输入密码（至少6位）"
            required
          />
        </div>
        
        <button 
          type="submit" 
          class="btn btn-primary w-full"
          :disabled="loading"
        >
          <span v-if="loading" class="animate-spin inline-block w-5 h-5 border-2 border-white border-t-transparent rounded-full mr-2"></span>
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      
      <div class="mt-6 text-center">
        <p class="text-gray-500">
          已有账号？
          <router-link to="/login" class="text-primary-500 hover:underline">立即登录</router-link>
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
const email = ref('')
const phone = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function handleSubmit() {
  loading.value = true
  error.value = ''
  
  try {
    await authStore.register(username.value, email.value, password.value, phone.value)
    router.push('/profile')
  } catch (err) {
    error.value = err
  } finally {
    loading.value = false
  }
}
</script>
