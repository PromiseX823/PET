<template>
  <div class="upload-container">
    <!-- 背景装饰 -->
    <div class="upload-background">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>
    
    <div class="container">
      <h1 class="page-title">
        <i class="el-icon-upload"></i> 上传照片
      </h1>
      <p class="page-subtitle">分享您和宠物的美好瞬间，记录每一个温馨时刻</p>

      <div class="upload-wrapper">
        <el-form :model="form" label-width="80px" class="upload-form">
          <el-form-item label="选择宠物">
            <el-select v-model="form.pet_id" placeholder="请选择宠物" class="form-select">
              <el-option value="">请选择宠物</el-option>
              <el-option v-for="pet in myPets" :key="pet.id" :label="pet.name" :value="pet.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="上传照片">
            <el-upload
              class="upload-demo"
              drag
              action=""
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleFileChange"
            >
              <ElIcon class="el-icon--upload">
                <Upload />
              </ElIcon>
              <div class="el-upload__text">将文件拖到此处，或点击选择文件</div>
              <div class="el-upload__tip" slot="tip">仅支持 jpg/png 格式且文件大小不超过 20MB</div>
            </el-upload>
          </el-form-item>
          <el-form-item v-if="previewUrl" label="预览">
            <div class="preview-wrapper">
              <img :src="previewUrl" alt="预览" class="preview-image" />
            </div>
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="form.description" type="textarea" placeholder="请输入照片描述" rows="4" class="form-input" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleUpload" :loading="loading" class="upload-button">
              <ElIcon v-if="!loading">
                <Upload />
              </ElIcon>
              <span v-if="loading">上传中...</span>
              <span v-else>上传照片</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Upload } from '@element-plus/icons-vue'
import { ElIcon } from 'element-plus'
import { ElMessage } from 'element-plus'
import api from '../api/index.js'
import { useUserStore } from '../stores/index.js'


const router = useRouter()
const form = ref({ pet_id: '', description: '' })
const myPets = ref([])
const previewUrl = ref('')
const loading = ref(false)
const selectedFile = ref(null)

const userStore = useUserStore()
userStore.initializeUser()

// 获取用户宠物列表
const loadUserPets = async () => {
  if (!userStore.user) {
    myPets.value = []
    return
  }
  
  try {
    const response = await api.getUserPets(userStore.user.id)
    console.log('getUserPets API响应:', response)
    
    // 后端API返回ApiResponse格式，数据在response.data中
    const data = response.data || []
    myPets.value = Array.isArray(data) ? data : []
    
    if (myPets.value.length === 0) {
      ElMessage.warning('您还没有宠物，请先添加宠物后再上传照片')
    }
  } catch (err) {
    console.error('加载用户宠物失败:', err)
    myPets.value = []
    ElMessage.error('加载宠物列表失败，请稍后重试')
  }
}

const handleFileChange = (file) => {
  // 验证文件类型
  const allowedTypes = ['image/png', 'image/jpeg', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.raw.type)) {
    ElMessage.error('不支持的文件类型，仅支持: png, jpg, jpeg, gif, webp')
    return
  }
  
  // 验证文件大小（最大20MB）
  const maxSize = 20 * 1024 * 1024 // 20MB
  if (file.raw.size > maxSize) {
    ElMessage.error('文件大小超过限制（最大20MB）')
    return
  }
  
  selectedFile.value = file.raw
  try {
    if (window.URL) {
      previewUrl.value = window.URL.createObjectURL(file.raw)
    } else {
      // 降级处理：如果window.URL不可用，不显示预览
      previewUrl.value = ''
    }
  } catch (error) {
    console.error('生成预览URL失败:', error)
    previewUrl.value = ''
  }
}

const handleUpload = async () => {
    // 验证表单
    if (!selectedFile.value) {
      ElMessage.error('请选择要上传的照片')
      return
    }
    
    if (!userStore.user) {
      ElMessage.error('请先登录后再上传照片')
      return
    }
    
    if (!form.value.pet_id) {
      ElMessage.error('请选择宠物')
      return
    }
    
    loading.value = true
    
    try {
      console.log('开始上传文件:', selectedFile.value.name)
      console.log('用户ID:', userStore.user.id)
      
      // 1. 上传文件到服务器
      const formData = new FormData()
      formData.append('file', selectedFile.value)
      
      console.log('FormData创建成功，准备调用上传API')
      console.log('API实例:', api)
      console.log('上传API调用开始')
      let uploadResponse
      try {
        uploadResponse = await api.uploadPhoto(formData)
        console.log('上传API响应:', uploadResponse)
      } catch (error) {
        console.error('上传API错误详情:', {
          message: error.message,
          response: error.response,
          status: error.response?.status,
          statusText: error.response?.statusText,
          headers: error.response?.headers,
          data: error.response?.data,
          config: error.config
        })
        throw error
      }
      
      if (!uploadResponse || !uploadResponse.file_url) {
        throw new Error('文件上传失败，返回数据格式错误')
      }
      
      console.log('文件上传成功，file_url:', uploadResponse.file_url)
      
      // 2. 创建照片记录
      const photoData = {
        pet_id: form.value.pet_id ? parseInt(form.value.pet_id) : null,
        user_id: userStore.user.id,
        image_url: uploadResponse.file_url,
        thumbnail_url: uploadResponse.file_url,
        caption: form.value.description,
        is_main: false
      }
      
      console.log('准备创建照片记录，数据:', photoData)
      const createResponse = await api.createPhoto(photoData)
      console.log('创建照片记录API响应:', createResponse)
      
      if (!createResponse) {
        throw new Error('创建照片记录失败')
      }
      
      // 3. 上传成功处理
      ElMessage.success('照片上传成功！')
      
      // 4. 导航回照片墙页面
      router.push('/photos')
    } catch (err) {
      console.error('上传失败详情:', {
        message: err.message,
        response: err.response,
        status: err.response?.status,
        data: err.response?.data,
        config: err.config
      })
      let errorMessage = '上传失败，请稍后重试'
      if (err.response) {
        if (err.response.data && err.response.data.error) {
          errorMessage = err.response.data.error
        } else if (err.response.status === 401) {
          errorMessage = '登录已过期，请重新登录'
        } else if (err.response.status === 403) {
          errorMessage = '您没有权限执行此操作'
        } else if (err.response.status >= 500) {
          errorMessage = '服务器错误，请稍后重试'
        } else {
          errorMessage = `请求失败 (${err.response.status})`
        }
      } else if (err.message) {
        errorMessage = err.message
      }
      ElMessage.error(errorMessage)
    } finally {
      loading.value = false
    }
  }

// 组件挂载时加载用户宠物
onMounted(async () => {
  await loadUserPets()
})
</script>

<style scoped>
.upload-container {
  min-height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #fff9f9 0%, #fef3f3 50%, #f0f9ff 100%);
  padding: 60px 0 80px 0;
  overflow: hidden;
}

.upload-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at 20% 20%, rgba(255, 107, 107, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(78, 205, 196, 0.15) 0%, transparent 50%);
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.3;
  animation: float 20s infinite ease-in-out;
}

.shape-1 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 250px;
  height: 250px;
  background: linear-gradient(135deg, #4ecdc4 0%, #44a3d0 100%);
  bottom: 10%;
  right: 10%;
  animation-delay: 4s;
}

.shape-3 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #96ceb4 0%, #45b7d1 100%);
  top: 50%;
  left: 30%;
  animation-delay: 8s;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.page-title {
  text-align: center;
  font-size: 42px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 12px;
  animation: fadeInUp 0.8s ease;
}

.page-subtitle {
  text-align: center;
  color: var(--text-color-secondary);
  font-size: 16px;
  margin-bottom: 60px;
  animation: fadeInUp 0.8s ease 0.2s backwards;
}

.upload-wrapper {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 50px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  animation: fadeInUp 0.8s ease 0.4s backwards;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.upload-wrapper:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
}

.upload-form {
  max-width: 700px;
  margin: 0 auto;
}

.form-select,
.form-input {
  width: 100%;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.form-select:focus-within,
.form-input:focus-within {
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
}

.upload-demo {
  margin: 10px 0;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.upload-demo:hover {
  border-color: var(--primary-color);
}

.el-upload-dragger {
  border-radius: 12px;
  transition: all 0.3s ease;
}

.el-upload-dragger:hover {
  transform: scale(1.02);
}

.preview-wrapper {
  margin-top: 10px;
}

.preview-image {
  max-width: 300px;
  max-height: 300px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.preview-image:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.15);
}

.upload-button {
  width: 200px;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  border: none;
  border-radius: 12px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.upload-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.upload-button:hover::before {
  left: 100%;
}

.upload-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(255, 107, 107, 0.3);
}

.upload-button:active {
  transform: translateY(0);
}

/* 动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .upload-container {
    padding: 40px 0 60px 0;
  }
  
  .page-title {
    font-size: 32px;
  }
  
  .upload-wrapper {
    padding: 30px 20px;
  }
  
  .preview-image {
    max-width: 250px;
    max-height: 250px;
  }
}
</style>