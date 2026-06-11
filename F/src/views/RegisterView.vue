<template>
  <div class="register-container">
    <div class="register-background">
      <div class="bg-pattern"></div>
    </div>
    <div class="register-content">
      <div class="register-form-wrapper">
        <div class="form-header">
          <div class="logo-section">
            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 2a14.5 14.5 0 0 0-10 17.9 14.5 14.5 0 0 0 18 0A14.5 14.5 0 0 0 12 2Z" />
              <path d="M15 9a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
              <path d="M13 15h1a3 3 0 1 0 0-6h-4a3 3 0 1 0 0 6h1" />
            </svg>
            <h1 class="logo-text">宠物之家</h1>
          </div>
          <h2 class="form-title">加入我们的爱心社区</h2>
          <p class="form-subtitle">创建您的账户，开启与宠物相伴的美好时光</p>
        </div>
        <el-form ref="registerForm" :model="formData" :rules="rules" label-width="0px">
          <el-form-item prop="username">
            <el-input 
              v-model="formData.username" 
              placeholder="用户名"
              class="form-input"
            >
              <template #prefix>
                <i class="el-icon-user"></i>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="email">
            <el-input 
              v-model="formData.email" 
              placeholder="电子邮箱"
              class="form-input"
            >
              <template #prefix>
                <i class="el-icon-message"></i>
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
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="formData.confirmPassword" 
              type="password" 
              placeholder="确认密码"
              class="form-input"
            >
              <template #prefix>
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" block class="register-button" @click="handleRegister">
              <i class="el-icon-user-plus"></i> 创建账户
            </el-button>
          </el-form-item>
        </el-form>
        <div class="form-footer">
          <span class="footer-text">已有账户？</span>
          <router-link to="/login" class="login-link">立即登录，继续您的爱心之旅</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api/index'

const router = useRouter()
const registerForm = ref(null)

const formData = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value !== formData.value.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

const handleRegister = () => {
  registerForm.value.validate((valid) => {
    if (valid) {
      // 调用后端注册API
      api.register(formData.value.username, formData.value.email, formData.value.password)
        .then(response => {
          console.log('注册API响应:', response)
          if (response.success && response.user) {
            ElMessage.success('注册成功，现在跳转到登录页面')
            setTimeout(() => {
              router.push('/login')
            }, 1500)
          } else {
            ElMessage.error('注册失败：' + (response.error || '未知错误'))
          }
        })
        .catch(error => {
          console.error('注册请求失败:', error)
          ElMessage.error('注册失败，请稍后重试')
        })
    }
  })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.register-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
}

.bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at 30% 40%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 70% 70%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 50% 20%, rgba(255, 255, 255, 0.05) 0%, transparent 50%);
  animation: backgroundFloat 20s ease-in-out infinite;
}

@keyframes backgroundFloat {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(-20px, 20px) rotate(-1deg);
  }
  66% {
    transform: translate(10px, -10px) rotate(1deg);
  }
}

.register-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  padding: 0 24px;
  animation: fadeInUp 1s ease;
}

.register-form-wrapper {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  padding: 60px 40px;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  transition: all var(--transition-base);
}

.register-form-wrapper:hover {
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

.register-button {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  border: none;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(255, 107, 107, 0.3);
  transition: all var(--transition-base);
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(255, 107, 107, 0.4);
}

.register-button:active {
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

.login-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  margin-left: 8px;
  transition: all var(--transition-base);
}

.login-link:hover {
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
  .register-form-wrapper {
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