<template>
  <div class="photo-detail-container">
    <!-- 导航栏 -->
    <div class="navbar">
      <el-button type="primary" icon="ArrowLeft" @click="goBack">返回</el-button>
      <h1>照片详情</h1>
      <div class="nav-actions"></div>
    </div>

    <!-- 照片详情内容 -->
    <div v-if="photo" class="photo-detail-content">
      <div class="photo-main">
        <el-image
          :src="photo.url"
          fit="contain"
          class="detail-photo-image"
        />
      </div>
      
      <div class="photo-info">
        <div class="photo-meta">
          <div class="meta-item">
            <span class="meta-label">上传时间：</span>
            <span class="meta-value">{{ formatDate(photo.createdAt) }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">点赞数：</span>
            <span class="meta-value">{{ photo.likes || 0 }}</span>
          </div>
        </div>
        
        <div class="photo-caption">
          <h2>{{ photo.caption || '无描述' }}</h2>
        </div>
        
        <div class="photo-actions">
          <el-button 
            type="primary" 
            size="large"
            @click="likePhoto(photo.id)"
          >
            {{ isLiked ? '取消点赞' : '点赞' }}
            <el-icon v-if="isLiked"><StarFilled /></el-icon>
            <el-icon v-else><Star /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
    
    <div v-else class="loading-state">
      <el-skeleton animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElSkeleton } from 'element-plus'
import { ArrowLeft, Star, StarFilled } from '@element-plus/icons-vue'
import api from '@/api'
import { useUserStore, usePhotosStore } from '@/stores'

const route = useRoute()
const router = useRouter()

const userStore = useUserStore()
const photosStore = usePhotosStore()

// 照片详情数据
const photo = ref(null)
const isLiked = ref(false)

// 返回上一页
function goBack() {
  router.push('/photos')
}

// 格式化日期
function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 点赞/取消点赞
async function likePhoto(photoId) {
  try {
    const userStore = useUserStore()
    
    // 检查用户是否登录并获取用户ID
    if (!userStore.isLoggedIn || !userStore.user?.id) {
      console.error('点赞失败: 用户未登录或用户信息缺失')
      ElMessage.error('请先登录')
      return
    }
    
    const userId = userStore.user.id
    
    console.log('=== 开始点赞操作 ===')
    console.log('点赞操作参数:', { photoId, userId, isLiked: isLiked.value })
    console.log('用户信息:', userStore.user)
    console.log('用户ID类型:', typeof userId)
    console.log('用户ID值:', userId)
    console.log('登录状态:', userStore.isLoggedIn)
    
    let response
    if (isLiked.value) {
      console.log('调用取消点赞API')
      try {
        response = await api.unlikePhoto(photoId, userId)
        console.log('取消点赞响应:', response)
        
        // 更新本地状态
        if (photo.value) {
          photo.value.likes = response.like_count
          // 更新store中的照片数据
          photosStore.updatePhoto(photo.value.id, {
            likes: response.like_count,
            liked: false
          })
        }
        isLiked.value = false
        ElMessage.success(response.message || '取消点赞成功')
      } catch (unlikeError) {
        console.error('取消点赞API调用失败:', unlikeError)
        throw unlikeError
      }
    } else {
      console.log('调用点赞API')
      try {
        response = await api.likePhoto(photoId, userId)
        console.log('点赞响应:', response)
        
        // 更新本地状态
        if (photo.value) {
          photo.value.likes = response.like_count
          // 更新store中的照片数据
          photosStore.updatePhoto(photo.value.id, {
            likes: response.like_count,
            liked: true
          })
        }
        isLiked.value = true
        ElMessage.success(response.message || '点赞成功')
      } catch (likeError) {
        console.error('点赞API调用失败:', likeError)
        throw likeError
      }
    }
    
    console.log('=== 点赞操作完成 ===')
  } catch (error) {
    console.error('点赞失败:', error)
    console.error('错误详情:', error.response ? error.response : error)
    if (error.response) {
      console.error('响应状态:', error.response.status)
      console.error('响应数据:', error.response.data)
      console.error('响应头:', error.response.headers)
    }
    if (error.request) {
      console.error('请求数据:', error.request)
    }
    ElMessage.error('操作失败，请稍后再试')
  }
}

// 获取照片详情
async function getPhotoDetail() {
  try {
    const photoId = route.params.id
    const response = await api.getPhotoById(photoId)
    // 转换后端返回的数据字段，使其与模板中使用的字段名称一致
    photo.value = {
      id: response.id,
      url: response.image_url,
      createdAt: response.upload_time,
      likes: response.like_count || 0,
      caption: response.caption,
      isLiked: false // 暂时默认未点赞，后续需要后端支持
    }
    isLiked.value = false // 暂时默认未点赞，后续需要后端支持
    
    // 检查用户是否已经点赞（需要后端API支持）
    // 这里暂时使用模拟逻辑，实际应该调用后端API获取点赞状态
  } catch (error) {
    console.error('获取照片详情失败:', error)
    console.error('错误详情:', error.response ? error.response : error)
    if (error.response) {
      console.error('响应状态:', error.response.status)
      console.error('响应数据:', error.response.data)
    }
    ElMessage.error('加载照片详情失败')
  }
}

// 初始化
onMounted(() => {
  getPhotoDetail()
})
</script>

<style scoped>
.photo-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.navbar h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
}

.photo-detail-content {
  display: flex;
  gap: 30px;
}

.photo-main {
  flex: 1;
  max-width: 800px;
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.detail-photo-image {
  width: 100%;
  height: auto;
  max-height: 600px;
  object-fit: contain;
}

.photo-info {
  flex: 0 0 350px;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.photo-meta {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.meta-item {
  display: flex;
  margin-bottom: 8px;
}

.meta-label {
  font-weight: 500;
  margin-right: 10px;
  color: #606266;
}

.photo-caption h2 {
  font-size: 20px;
  font-weight: 500;
  margin-top: 0;
  margin-bottom: 20px;
}

.photo-actions {
  margin-top: 20px;
}

.loading-state {
  text-align: center;
  padding: 50px;
}

@media (max-width: 768px) {
  .photo-detail-content {
    flex-direction: column;
  }
  
  .photo-info {
    flex: 1;
  }
}
</style>