<script setup>
import { ref, computed, onMounted, watchEffect, onActivated } from 'vue'
import { useRoute } from 'vue-router'
import {
  View,
  CollectionTag, Collection,
  Share,
  ChatDotRound,
  CircleClose,
  Delete
} from '@element-plus/icons-vue'
import api from '../api/index.js'
import { useUserStore, usePhotosStore } from '../stores/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const photosStore = usePhotosStore()
const route = useRoute()
userStore.initializeUser()

// 数据定义
const photos = computed(() => photosStore.sortedPhotos)
const loading = ref(false)
const error = ref(null)

// 评论相关
const comments = ref([])
const commentText = ref('') // 修改评论输入变量名以匹配模板
const replyingTo = ref(null) // 用于存储当前正在回复的评论对象

// 模态框状态
const selectedPhoto = ref(null)

// 加载照片数据
const loadPhotos = async () => {
  let photosData = []
  try {
    loading.value = true
    const response = await api.getPhotos()
    const data = response?.data || []
    
    // 将后端数据转换为前端所需格式
    photosData = data.map(photo => ({
      id: photo.id,
      title: photo.caption || `照片 ${photo.id}`,
      image_url: photo.image_url, // 保持与模板一致
      thumbnail: photo.thumbnail_url,
      liked: false, // 这需要从用户的点赞状态获取
      likes: photo.like_count || 0,
      views: photo.view_count || 0,
      collected: false, // 这需要从用户的收藏状态获取
      collections: photo.collection_count || 0, // 从后端获取正确的收藏数
      shares: 0, // 需要从分享表中获取
      comments: photo.comment_count || 0, // 从后端获取正确的评论数
      tags: [],
      uploaded_by: photo.user_id,
      created_at: photo.created_at || new Date().toISOString() // 添加发布时间字段
    }))
    
    // 如果用户已登录，获取用户的点赞和收藏状态
    if (userStore.user && userStore.user.id) {
      try {
        // 获取用户点赞的照片ID列表
        const likedPhotosResponse = await api.getUserLikedPhotos(userStore.user.id)
        const likedPhotoIds = likedPhotosResponse?.data?.liked_photo_ids || []
        
        // 获取用户收藏的照片ID列表
        const collectedPhotosResponse = await api.getUserCollectedPhotos(userStore.user.id)
        const collectedPhotoIds = collectedPhotosResponse?.data?.collected_photo_ids || []
        
        // 更新照片的liked和collected状态
        photosData = photosData.map(photo => ({
          ...photo,
          liked: likedPhotoIds.includes(photo.id),
          collected: collectedPhotoIds.includes(photo.id)
        }))
      } catch (error) {
        console.error('获取用户点赞/收藏状态失败:', error)
      }
    }
  } catch (err) {
    error.value = '加载照片失败'
    console.error('加载照片失败:', err)
  } finally {
    // 更新store中的数据
    photosStore.setPhotos(photosData)
    photosStore.setNeedRefresh(false)
    loading.value = false
  }
}



// 监听路由变化，重新加载照片数据
watchEffect(() => {
  console.log('路由变化:', route.path)
  // 当路由为/photos时，重新加载照片数据
  if (route.path === '/photos') {
    console.log('触发照片数据重新加载')
    loadPhotos()
  }
})

// 加载照片评论
const loadComments = async (photoId) => {
  try {
    const response = await api.getPhotoComments(photoId)
    console.log('加载的评论数据:', response)
    const commentsList = response?.data || []
    comments.value = buildNestedComments(commentsList)
    console.log('构建后的评论数据:', comments.value)
  } catch (err) {
    console.error('加载评论失败:', err)
  }
}

// 构建扁平化评论列表，按时间顺序排序
function buildNestedComments(commentsList) {
  return commentsList
    .map(comment => ({
      ...comment,
      user: comment.user,
      parent_user: comment.parent_user
    }))
    .sort((a, b) => new Date(a.created_at) - new Date(b.created_at))
}

// 格式化时间显示
function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 发布新评论
const postComment = async () => {
  if (!commentText.value.trim() || !selectedPhoto.value) return
  
  if (!userStore.user) {
    alert('请先登录后再发表评论')
    return
  }
  
  try {
    const commentData = {
      user_id: userStore.user.id,
      content: commentText.value.trim()
    }
    
    if (replyingTo.value) {
      commentData.parent_id = replyingTo.value.id
    }
    
    await api.addPhotoComment(selectedPhoto.value.id, commentData)
    commentText.value = ''
    replyingTo.value = null
    
    await loadComments(selectedPhoto.value.id)
    
    const updatedPhoto = photos.value.find(p => p.id === selectedPhoto.value.id)
    if (updatedPhoto) {
      updatedPhoto.comments += 1
      selectedPhoto.value.comments += 1
    }
  } catch (err) {
    console.error('发布评论失败:', err)
    alert('发布评论失败，请稍后重试')
  }
}

// 开始回复某个评论
const startReply = (comment) => {
  replyingTo.value = comment
  commentText.value = ''
  console.log('开始回复评论:', comment.id)
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  commentText.value = ''
}

// 切换点赞
async function toggleLike(photo) {
  if (!userStore.user) {
    ElMessage.warning('请先登录后再点赞')
    return
  }
  
  try {
    let response
    if (photo.liked) {
      // 取消点赞
      response = await api.unlikePhoto(photo.id, userStore.user.id)
    } else {
      // 点赞
      response = await api.likePhoto(photo.id, userStore.user.id)
    }
    
    // 更新前端状态
    const resultData = response.data || response
    photo.liked = !photo.liked
    photo.likes = resultData.like_count || (photo.liked ? photo.likes + 1 : photo.likes - 1)
    
    // 同时更新store中的数据
    photosStore.updatePhoto(photo.id, {
      liked: photo.liked,
      likes: photo.likes
    })
    
    ElMessage.success(response.message || '操作成功')
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('点赞操作失败，请稍后重试')
  }
}

// 切换收藏
async function toggleCollection(photo) {
  if (!userStore.user) {
    ElMessage.warning('请先登录后再收藏')
    return
  }
  
  try {
    let response
    if (photo.collected) {
      // 取消收藏
      response = await api.uncollectPhoto(photo.id, userStore.user.id)
    } else {
      // 收藏
      response = await api.collectPhoto(photo.id, userStore.user.id)
    }
    
    // 更新前端状态
    const resultData = response.data || response
    photo.collected = !photo.collected
    photo.collections = resultData.collection_count || (photo.collected ? photo.collections + 1 : photo.collections - 1)
    
    // 同时更新store中的数据
    photosStore.updatePhoto(photo.id, {
      collected: photo.collected,
      collections: photo.collections
    })
    
    ElMessage.success(response.message || '操作成功')
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('收藏操作失败，请稍后重试')
  }
}

// 打开模态框
function showModal(photo) {
  selectedPhoto.value = JSON.parse(JSON.stringify(photo))
  loadComments(photo.id)
}

// 关闭模态框
function closeModal() {
  selectedPhoto.value = null
  comments.value = []
  commentText.value = ''
  replyingTo.value = null
}

// 处理分享
function handleShare(photo) {
  console.log('分享照片:', photo)
}

// 为没有头像的用户生成颜色
const getAvatarColor = (userId) => {
  const colors = [
    '#409eff',
    '#67c23a',
    '#e6a23c',
    '#f56c6c',
    '#909399',
    '#722ed1',
    '#13c2c2',
    '#fa8c16'
  ]
  return colors[userId % colors.length]
}

// 删除照片确认对话框
const confirmDeletePhoto = async (photo) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这张照片吗？删除后无法恢复。',
      '删除照片',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deletePhoto(photo)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    }
  }
}

// 删除照片API调用
const deletePhoto = async (photo) => {
  try {
    await api.deletePhoto(photo.id)
    
    // 删除成功后更新UI
    const index = photos.value.findIndex(p => p.id === photo.id)
    if (index !== -1) {
      photos.value.splice(index, 1)
    }
    
    // 如果删除的是当前选中的照片，关闭模态框
    if (selectedPhoto.value && selectedPhoto.value.id === photo.id) {
      closeModal()
    }
    
    ElMessage.success('照片删除成功')
  } catch (error) {
    console.error('删除照片失败:', error)
    ElMessage.error('删除照片失败：' + (error.message || '未知错误'))
  }
}

// 删除评论确认对话框
const confirmDeleteComment = async (comment) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条评论吗？删除后无法恢复。',
      '删除评论',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deleteComment(comment)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    }
  }
}

// 删除评论API调用
const deleteComment = async (comment) => {
  try {
    await api.deleteComment(comment.id)
    
    // 删除成功后更新UI
    const index = comments.value.findIndex(c => c.id === comment.id)
    if (index !== -1) {
      comments.value.splice(index, 1)
    }
    
    // 更新评论计数
    if (selectedPhoto.value) {
      selectedPhoto.value.comments -= 1
    }
    
    ElMessage.success('评论删除成功')
  } catch (error) {
    console.error('删除评论失败:', error)
    ElMessage.error('删除评论失败，请稍后重试')
  }
}

// 组件挂载时加载照片
onMounted(async () => {
  await loadPhotos()
})

// 组件激活时重新加载数据（解决从详情页返回时数据不更新的问题）
onActivated(async () => {
  await loadPhotos()
})
</script>

<template>
  <div class="photos-container">
    <div class="photos-header">
      <h1>萌宠照片墙</h1>
      <div class="sort-options">
        <span>排序方式：</span>
        <select v-model="photosStore.sortBy" class="sort-select">
          <option value="created_at">按发布时间</option>
          <option value="likes">按点赞量</option>
        </select>
        <button @click="photosStore.toggleSortOrder" class="sort-order-btn">
          {{ photosStore.sortOrder === 'asc' ? '↑' : '↓' }}
        </button>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>
    
    <!-- 错误信息 -->
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 照片网格 -->
    <div v-if="!loading && !error" class="photos-grid">
      <div 
        v-for="photo in photos" 
        :key="photo.id"
        class="photo-item"
        @click="showModal(photo)"
      >
        <img :src="photo.thumbnail || photo.image_url" :alt="photo.title" class="photo-thumbnail">
        <!-- 仅当前用户可看到删除按钮 -->
        <button 
          v-if="userStore.user && photo.uploaded_by === userStore.user.id" 
          class="delete-btn" 
          @click.stop="confirmDeletePhoto(photo)"
          title="删除照片"
        >
          <CircleClose />
        </button>
        <div class="photo-info">
          <h3 class="photo-title">{{ photo.title }}</h3>
          <div class="photo-stats">
            <span class="stat-item" @click.stop="toggleLike(photo)">
              <span class="heart-icon" :class="{ 'filled': photo.liked }" style="cursor: pointer;">
                {{ photo.liked ? '♥' : '♡' }}
              </span>
              {{ photo.likes }}
            </span>
            <span class="stat-item" @click.stop="toggleCollection(photo)">
              <span class="collection-icon" style="cursor: pointer;">{{ photo.collected ? '★' : '☆' }}</span>
              {{ photo.collections }}
            </span>
            <!-- 分享图标已隐藏 -->
            <span class="stat-item">
              <span class="comment-icon">&#128172;</span>
              {{ photo.comments }}
            </span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 预览模态框 -->
    <div class="modal" v-if="selectedPhoto">
      <div class="modal-content">
        <div class="modal-header">
          <span class="close" @click="closeModal()">&times;</span>
        </div>
        <div class="modal-body">
          <!-- 图片区域 -->
          <div class="modal-image-container">
            <img :src="selectedPhoto.image_url" :alt="selectedPhoto.caption || '宠物照片'" class="modal-image">
            <!-- 图片操作按钮 -->
            <div class="modal-actions">
              <button class="modal-action-btn" @click="toggleLike(selectedPhoto)">
                <span class="heart-icon" :class="{ 'filled': selectedPhoto.liked }">
                  {{ selectedPhoto.liked ? '♥' : '♡' }}
                </span>
                <span class="action-count">{{ selectedPhoto.likes }}</span>
              </button>
              <button class="modal-action-btn" @click="toggleCollection(selectedPhoto)">
                <span class="collection-icon">{{ selectedPhoto.collected ? '★' : '☆' }}</span>
                <span class="action-count">{{ selectedPhoto.collections }}</span>
              </button>
              <!-- 分享图标已隐藏 -->
              <button class="modal-action-btn">
                <span class="comment-icon">&#128172;</span>
                <span class="action-count">{{ selectedPhoto.comments }}</span>
              </button>
              <!-- 仅当前用户可看到删除按钮 -->
              <button 
                v-if="userStore.user && selectedPhoto.uploaded_by === userStore.user.id" 
                class="modal-action-btn delete-btn" 
                @click="confirmDeletePhoto(selectedPhoto)"
                title="删除照片"
              >
                <span class="delete-icon">🗑️</span>
                <span class="action-count">删除</span>
              </button>
            </div>
          </div>
          
          <!-- 评论区域 -->
          <div class="comment-section">
            <h3>评论 ({{ comments.length }})</h3>
            
            <!-- 评论输入区域 -->
            <div class="comment-input-section">
              <div v-if="replyingTo" class="replying-indicator">
                <span class="replying-text">正在回复 {{ replyingTo.user?.username || '未知用户' }}</span>
                <button class="cancel-reply-btn" @click="cancelReply()">取消</button>
              </div>
              <div class="comment-input-wrapper">
                <input 
                  type="text" 
                  v-model="commentText" 
                  :placeholder="replyingTo ? `回复 ${replyingTo.user?.username || '未知用户'}...` : '写下你的评论...'"
                  @keyup.enter="postComment()"
                  class="comment-input"
                >
                <button @click="postComment()" class="post-button">发布</button>
              </div>
            </div>
            
            <!-- 评论列表 -->
            <div class="comments-list">
              <!-- 扁平化显示所有评论 -->
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-avatar">
                  <img v-if="comment.user?.avatar_url" :src="comment.user.avatar_url" alt="头像" class="avatar-image">
                  <div v-else :style="{ backgroundColor: getAvatarColor(comment.user?.id || comment.user_id) }" class="avatar-placeholder">
                    {{ comment.user?.username?.charAt(0).toUpperCase() || '用' }}
                  </div>
                </div>
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-username">{{ comment.user?.username || `用户${comment.user?.id || comment.user_id}` }}</span>
                    <span class="comment-time">{{ formatTime(comment.created_at) }}</span>
                  </div>
                  <div class="comment-text">
                    <div v-if="comment.parent_user" class="reply-indicator">回复 {{ comment.parent_user?.username || '未知用户' }}:</div>
                    <div :class="comment.parent_user ? 'reply-content' : ''">{{ comment.content }}</div>
                  </div>
                  <div class="comment-actions">
                    <button class="reply-button" @click="startReply(comment)">回复</button>
                    <!-- 只有评论创建者才能看到删除按钮 -->
                    <button 
                      v-if="userStore.user && (comment.user?.id || comment.user_id) === userStore.user.id" 
                      class="delete-button" 
                      @click="confirmDeleteComment(comment)"
                    >
                      删除
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.photos-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
}

.photos-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.photos-header h1 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #666;
}

.sort-select {
  padding: 6px 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.sort-select:hover {
  border-color: #c6e2ff;
}

.sort-select:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.sort-order-btn {
  padding: 6px 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sort-order-btn:hover {
  border-color: #409eff;
  color: #409eff;
}

.sort-order-btn:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}



.loading {
  text-align: center;
  padding: 100px 0;
  color: #666;
  font-size: 16px;
}

.error {
  text-align: center;
  padding: 100px 0;
  color: #f56c6c;
  font-size: 16px;
}

/* 照片网格 */
.photos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.photo-item {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.photo-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.photo-thumbnail {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.photo-info {
  padding: 12px;
}

.photo-title {
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.photo-stats {
  display: flex;
  gap: 15px;
  font-size: 13px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
}

.stat-item .heart-icon.filled {
  color: #f56c6c;
}

.stat-item .collection-icon {
  color: #e6a23c;
}

/* 模态框样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  max-width: 90%;
  max-height: 90%;
  width: 90%;
  height: 90%;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 10px 15px;
  background-color: #f0f0f0;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  font-size: 18px;
}

.modal-body {
  display: flex;
  flex-direction: column;
  height: calc(100% - 41px);
  overflow-y: auto;
}

.modal-image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  flex: 1;
  justify-content: center;
}

.modal-image {
  max-width: 100%;
  max-height: 70vh;
  width: auto;
  height: auto;
  object-fit: contain;
  border-radius: 8px;
}

.modal-actions {
  display: flex;
  gap: 20px;
  margin-top: 15px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.modal-action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  font-size: 14px;
  transition: color 0.3s ease;
}

.modal-action-btn:hover {
  color: #409eff;
}

/* 评论区域样式 */
.comment-section {
  border-top: 1px solid #eee;
  padding: 20px;
  background-color: #f9f9f9;
}

.comment-input-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.replying-indicator {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
}

.replying-text {
  color: #1890ff;
  font-size: 13px;
}

.cancel-reply-btn {
  padding: 4px 12px;
  background-color: white;
  color: #666;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.cancel-reply-btn:hover {
  color: #409eff;
  border-color: #409eff;
}

.comment-input-wrapper {
  display: flex;
  gap: 10px;
}

.comment-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.post-button {
  padding: 10px 20px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.comments-list {
  margin-top: 20px;
}

.comment-item {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  padding: 15px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 500;
  font-size: 16px;
  flex-shrink: 0;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 500;
  font-size: 16px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
  font-size: 13px;
}

.comment-username {
  font-weight: 600;
  color: #333;
}

.comment-text {
  margin-top: 6px;
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  white-space: pre-line; /* 保持换行符，让内容按行显示 */
  display: block;
}

.reply-indicator {
  color: #409eff;
  font-weight: 500;
  display: block;
  margin-bottom: 4px;
  width: 100%;
}

.reply-content {
  color: #666;
  line-height: 1.5;
  display: block;
  width: 100%;
}

.comment-actions {
  display: flex;
  gap: 8px;
  margin-top: 5px;
}

.reply-button {
  padding: 5px 10px;
  background-color: #f0f0f0;
  color: #666;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.delete-button {
  padding: 5px 10px;
  background-color: #fff0f0;
  color: #ff4d4f;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.delete-button:hover {
  background-color: #ffe6e6;
}

.close {
  cursor: pointer;
  font-size: 20px;
  color: #666;
  transition: color 0.3s;
}

.close:hover {
  color: #333;
}




</style>