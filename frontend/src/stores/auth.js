
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(null)

  const isAuthenticated = computed(() => !!token.value)

  async function login(username, password) {
    try {
      const response = await axios.post('/api/auth/login', { username, password })
      const { accessToken, user: userData } = response.data.data
      
      token.value = accessToken
      user.value = userData
      localStorage.setItem('token', accessToken)
      axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
      
      return true
    } catch (error) {
      throw error.response?.data?.message || '登录失败'
    }
  }

  async function register(username, email, password, phone) {
    try {
      const response = await axios.post('/api/auth/register', { 
        username, 
        email, 
        password,
        phone 
      })
      const { accessToken, user: userData } = response.data.data
      
      token.value = accessToken
      user.value = userData
      localStorage.setItem('token', accessToken)
      axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
      
      return true
    } catch (error) {
      throw error.response?.data?.message || '注册失败'
    }
  }

  async function logout() {
    try {
      await axios.post('/api/auth/logout')
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      token.value = null
      user.value = null
      localStorage.removeItem('token')
      delete axios.defaults.headers.common['Authorization']
    }
  }

  async function fetchUser() {
    if (!token.value) return
    
    try {
      const response = await axios.get('/api/auth/me')
      user.value = response.data.data
    } catch (error) {
      console.error('Fetch user error:', error)
      logout()
    }
  }

  function initialize() {
    if (token.value) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
      fetchUser()
    }
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    register,
    logout,
    fetchUser,
    initialize
  }
})
