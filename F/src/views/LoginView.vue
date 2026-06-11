<template>
  <div class="login-container">
    <div class="login-background">
      <div class="bg-pattern"></div>
    </div>
    <div class="login-content">
      <div class="login-form-wrapper">
        <div class="form-header">
          <div class="logo-section">
            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 2a14.5 14.5 0 0 0-10 17.9 14.5 14.5 0 0 0 18 0A14.5 14.5 0 0 0 12 2Z" />
              <path d="M15 9a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
              <path d="M13 15h1a3 3 0 1 0 0-6h-4a3 3 0 1 0 0 6h1" />
            </svg>
            <h1 class="logo-text">宠物之家</h1>
          </div>
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">请登录您的账户，继续您的爱心之旅</p>
        </div>
        <el-form ref="loginForm" :model="formData" :rules="rules" label-width="0px">
          <el-form-item prop="username">
            <el-input 
              v-model="formData.username" 
              placeholder="用户名或邮箱"
              class="form-input"
            >
              <template #prefix>
                <i class="el-icon-user"></i>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="formData.password" 
              type="password" 
              placeholder="密码"
              class="form-input"
            >
              <template #prefix>
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" block class="login-button" @click="handleLogin">
              <i class="el-icon-s-promotion"></i> 立即登录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="form-footer">
          <span class="footer-text">还没有账户？</span>
          <router-link to="/register" class="register-link">立即注册，开始爱心之旅</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores'
import { ElMessage } from 'element-plus'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()
const loginForm = ref(null)

const formData = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  loginForm.value.validate((valid) => {
    if (valid) {
      // 调用后端登录API
      api.login(formData.value.username, formData.value.password)
        .then(response => {
          console.log('登录API响应:', response)
          if (response.success && response.user) {
            console.log('准备登录的用户数据:', response.user)
            // 映射后端返回的字段到前端期望的格式
            const formattedUser = {
              ...response.user,
              avatar: response.user.avatar_url, // 将avatar_url映射为avatar
              description: response.user.bio, // 将bio映射为description
              nickname: response.user.username // 将username映射为nickname
            }
            userStore.login(formattedUser)
            console.log('登录后用户存储:', userStore)
            ElMessage.success('登录成功')
            router.push('/')
          } else {
            ElMessage.error('登录失败：' + (response.error || '未知错误'))
          }
        })
        .catch(error => {
          console.error('登录请求失败:', error)
          ElMessage.error('登录失败，请检查用户名和密码')
        })
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 50%, #f8b500 100%);
}

.bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 20%, rgba(255, 255, 255, 0.05) 0%, transparent 50%);
  animation: backgroundFloat 20s ease-in-out infinite;
}

@keyframes backgroundFloat {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(20px, -20px) rotate(1deg);
  }
  66% {
    transform: translate(-10px, 10px) rotate(-1deg);
  }
}

.login-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  padding: 0 24px;
  animation: fadeInUp 1s ease;
}

.login-form-wrapper {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  padding: 60px 40px;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  transition: all var(--transition-base);
}

.login-form-wrapper:hover {
  box-shadow: 0 25px 70px rgba(0, 0, 0, 0.3);
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
}

.logo-section svg {
  color: var(--primary-color);
  margin-bottom: 12px;
  animation: logoPulse 3s ease-in-out infinite;
}

@keyframes logoPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.95;
  }
}

.logo-text {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.form-title {
  font-size: 26px;
  color: var(--text-color);
  margin-bottom: 8px;
  font-weight: 600;
}

.form-subtitle {
  font-size: 14px;
  color: var(--text-color-secondary);
  margin: 0;
}

.form-input {
  border-radius: 12px;
  margin-bottom: 20px;
  border-color: var(--border-color);
  transition: all var(--transition-base);
}

.form-input:hover {
  border-color: var(--primary-color);
}

.form-input:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(255, 107, 107, 0.1);
}

.login-button {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  border: none;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(255, 107, 107, 0.3);
  transition: all var(--transition-base);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(255, 107, 107, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.form-footer {
  text-align: center;
  margin-top: 32px;
}

.footer-text {
  color: var(--text-color-secondary);
  font-size: 14px;
}

.register-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  margin-left: 8px;
  transition: all var(--transition-base);
}

.register-link:hover {
  color: var(--secondary-color);
  text-decoration: underline;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .login-form-wrapper {
    padding: 40px 30px;
    margin: 0 16px;
  }

  .form-title {
    font-size: 22px;
  }

  .logo-text {
    font-size: 24px;
  }
}
</style>