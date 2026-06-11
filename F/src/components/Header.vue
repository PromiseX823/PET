<template>
  <header class="app-header">
    <div class="header-background"></div>
    <div class="container">
      <div class="logo">
        <router-link to="/" class="logo-link">
          <el-icon><House /></el-icon>
          <span class="logo-text">宠物之家</span>
        </router-link>
      </div>
      <nav class="nav-menu">
        <router-link to="/" class="nav-item">
          <el-icon><House /></el-icon> 首页
        </router-link>
        <router-link to="/pets" class="nav-item">
          <el-icon><Tools /></el-icon> 宠物列表
        </router-link>
        <router-link to="/photos" class="nav-item">
          <el-icon><Picture /></el-icon> 照片墙
        </router-link>
        <router-link to="/adopt" class="nav-item">
          <el-icon><User /></el-icon> 我要领养
        </router-link>
        <router-link to="/upload" class="nav-item">
          <el-icon><Upload /></el-icon> 上传照片
        </router-link>
      </nav>
      <div class="user-area">
        <!-- 通知图标 - 始终显示 -->
        <el-dropdown trigger="click" @command="handleNotificationCommand">
          <div class="notification-icon-container">
            <el-icon><Bell /></el-icon>
            <span v-if="unreadCount > 0 && userStore.isLoggedIn" class="notification-count">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <div v-if="userStore.isLoggedIn">
                <div class="notification-header">
                  <span>通知</span>
                  <el-button type="text" size="small" @click="markAllAsRead">全部已读</el-button>
                </div>
                <div class="notification-list" v-if="notifications.length > 0">
                  <el-dropdown-item
                    v-for="notification in notifications"
                    :key="notification.id"
                    :command="{ type: 'notification', id: notification.id }"
                    class="notification-item"
                    :class="{ 'unread': !notification.is_read }"
                  >
                    <div class="notification-content">
                      <div class="notification-title">{{ notification.title }}</div>
                      <div class="notification-body">{{ notification.content }}</div>
                      <div class="notification-time">{{ formatTime(notification.created_at) }}</div>
                    </div>
                    <el-icon @click.stop="deleteNotification(notification.id)"><Delete /></el-icon>
                  </el-dropdown-item>
                </div>
                <div class="notification-empty" v-else>
                  <el-dropdown-item disabled>
                    <div class="empty-content">
                      <el-icon><Bell /></el-icon>
                      <span>暂无通知</span>
                    </div>
                  </el-dropdown-item>
                </div>
              </div>
              <div v-else>
                <el-dropdown-item disabled>
                  <div class="empty-content">
                    <el-icon><User /></el-icon>
                    <span>请先登录查看通知</span>
                  </div>
                </el-dropdown-item>
              </div>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <!-- 用户头像或登录/注册按钮 -->
        <template v-if="userStore.isLoggedIn">
          <el-dropdown trigger="click">
            <span class="user-avatar">
              <el-avatar :src="avatarUrl" size="40">
                {{ userStore.user.username?.charAt(0) || 'U' }}
              </el-avatar>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <el-icon><User /></el-icon>
                  <router-link to="/profile">个人中心</router-link>
                </el-dropdown-item>
                <el-dropdown-item v-if="userStore.user.role === 'admin'">
                  <el-icon><Setting /></el-icon>
                  <router-link to="/admin">管理后台</router-link>
                </el-dropdown-item>
                <el-dropdown-item divided @click="userStore.logout()">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="btn-login">
          <el-icon><User /></el-icon> 登录
        </router-link>
        <router-link to="/register" class="btn-register">
          <el-icon><Plus /></el-icon> 注册
        </router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useUserStore } from '@/stores'
import { ElMessage } from 'element-plus'
import { computed, ref, onMounted, watch } from 'vue'
import { api } from '@/api'
import { Bell, House, User, Tools, Picture, Upload } from '@element-plus/icons-vue'

const userStore = useUserStore()

// 通知相关数据
const notifications = ref([])
const unreadCount = ref(0)

// 计算属性：确保头像URL是完整的绝对路径
const avatarUrl = computed(() => {
  const url = userStore.user?.avatar
  if (!url) {
    return null
  }
  // 如果已经是完整URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 如果是相对路径，直接返回（前端代理会处理）
  if (url.startsWith('/')) {
    return url
  }
  // 其他情况，返回null（会显示用户名首字母）
  return null
})

// 获取用户通知
const fetchNotifications = async () => {
  if (!userStore.isLoggedIn || !userStore.user.id) return
  
  try {
    const response = await api.get(`/api/notifications?user_id=${userStore.user.id}&limit=10`)
    if (response.success) {
      // 确保通知ID是数字类型
      notifications.value = response.data.map(notification => ({
        ...notification,
        id: Number(notification.id),
        user_id: Number(notification.user_id)
      }))
      unreadCount.value = notifications.value.filter(n => !n.is_read).length
    }
  } catch (error) {
    console.error('获取通知失败:', error)
  }
}

// 标记单个通知为已读
const markNotificationAsRead = async (notificationId) => {
  // 确保通知ID是数字类型
  const id = Number(notificationId)
  
  try {
    // 传递user_id参数以验证用户权限
    await api.post(`/api/notifications/${id}/read`, {
      user_id: userStore.user.id
    })
    // 从列表中移除该通知
    const index = notifications.value.findIndex(n => n.id === id)
    if (index > -1) {
      notifications.value.splice(index, 1)
      // 更新未读计数
      unreadCount.value = notifications.value.filter(n => !n.is_read).length
    }
    ElMessage.success('通知已标记为已读并移除')
  } catch (error) {
    console.error('标记通知为已读失败:', error)
    
    // 详细记录错误信息
    console.error('错误完整信息:', JSON.stringify(error, null, 2))
    
    // 处理不同格式的错误信息
    let errorMessage = '标记已读失败'
    let isNotificationNotFound = false
    
    if (error.response) {
      // 服务器返回了错误响应
      const responseData = error.response.data
      console.error('响应数据:', responseData)
      
      if (typeof responseData === 'string') {
        // 错误信息是字符串
        errorMessage = responseData
        if (responseData.includes('通知不存在')) {
          isNotificationNotFound = true
        }
      } else if (responseData.error) {
        // 错误信息在error字段中
        errorMessage = responseData.error
        if (errorMessage === '通知不存在') {
          isNotificationNotFound = true
        }
      } else {
        // 其他格式的错误信息
        errorMessage = JSON.stringify(responseData)
      }
    } else if (error.message) {
      // 没有响应，只有错误消息
      errorMessage = error.message
      if (error.message.includes('通知不存在')) {
        isNotificationNotFound = true
      }
    }
    
    console.error('处理后的错误信息:', errorMessage)
    console.error('是否为通知不存在:', isNotificationNotFound)
    
    // 如果通知不存在，重新获取最新的通知列表
    if (isNotificationNotFound) {
      await fetchNotifications()
      ElMessage.info('通知已不存在，已更新通知列表')
    } else {
      ElMessage.error(errorMessage)
    }
  }
}

// 标记所有通知为已读
const markAllAsRead = async () => {
  if (!userStore.isLoggedIn || !userStore.user.id) return
  
  try {
    await api.post('/api/notifications/read-all', { user_id: userStore.user.id })
    // 直接重新获取最新的通知列表，确保数据同步
    await fetchNotifications()
    ElMessage.success('所有通知已标记为已读')
  } catch (error) {
    console.error('标记所有通知为已读失败:', error)
    console.error('错误详情:', error.response?.data || error.message)
    ElMessage.error('标记所有已读失败')
  }
}

// 删除通知
const deleteNotification = async (notificationId) => {
  // 确保通知ID是数字类型
  const id = Number(notificationId)
  
  try {
    // 传递user_id参数以验证用户权限
    await api.delete(`/api/notifications/${id}`, {
      data: {
        user_id: userStore.user.id
      }
    })
    // 直接从列表中移除，不需要重新获取整个列表
    notifications.value = notifications.value.filter(n => n.id !== id)
    unreadCount.value = notifications.value.filter(n => !n.is_read).length
    ElMessage.success('通知已删除')
  } catch (error) {
    console.error('删除通知失败:', error)
    
    // 处理不同格式的错误信息
    let errorMessage = '删除通知失败'
    let isNotificationNotFound = false
    
    if (error.response) {
      // 服务器返回了错误响应
      const responseData = error.response.data
      console.error('响应数据:', responseData)
      
      if (typeof responseData === 'string') {
        // 错误信息是字符串
        errorMessage = responseData
        if (responseData.includes('通知不存在')) {
          isNotificationNotFound = true
        }
      } else if (responseData.error) {
        // 错误信息在error字段中
        errorMessage = responseData.error
        if (errorMessage === '通知不存在') {
          isNotificationNotFound = true
        }
      } else {
        // 其他格式的错误信息
        errorMessage = JSON.stringify(responseData)
      }
    } else if (error.message) {
      // 没有响应，只有错误消息
      errorMessage = error.message
      if (error.message.includes('通知不存在')) {
        isNotificationNotFound = true
      }
    }
    
    console.error('处理后的错误信息:', errorMessage)
    
    // 如果通知不存在，重新获取最新的通知列表
    if (isNotificationNotFound) {
      await fetchNotifications()
      ElMessage.info('通知已不存在，已更新通知列表')
    } else {
      ElMessage.error(errorMessage)
    }
  }
}

// 处理通知点击事件
const handleNotificationCommand = (command) => {
  if (command.type === 'notification') {
    // 确保通知ID是数字类型
    markNotificationAsRead(Number(command.id))
    // 可以根据related_id跳转到对应的页面
  }
}

// 格式化时间
const formatTime = (timeStr) => {
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now - time
  
  if (diff < 60 * 1000) {
    return '刚刚'
  } else if (diff < 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 1000))}分钟前`
  } else if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 60 * 1000))}小时前`
  } else if (diff < 7 * 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`
  } else {
    return time.toLocaleDateString()
  }
}

// 监听登录状态变化，重新获取通知
watch(() => userStore.isLoggedIn, (newVal) => {
  if (newVal) {
    fetchNotifications()
  } else {
    notifications.value = []
    unreadCount.value = 0
  }
})

// 组件挂载时获取通知
onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchNotifications()
  }
})
</script>

<style scoped>
.app-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 4px 32px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
  border-bottom: 1px solid rgba(255, 255, 255, 0.8);
}

.header-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(78, 205, 196, 0.05) 0%, rgba(255, 107, 107, 0.05) 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px;
  position: relative;
  z-index: 1;
}

.logo-link {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all var(--transition-base);
}

.logo-link:hover {
  transform: translateY(-2px);
}

.nav-menu {
  display: flex;
  gap: 15px;
}

.nav-item {
  text-decoration: none;
  color: var(--text-color-primary);
  font-weight: 500;
  padding: 10px 20px;
  border-radius: 12px;
  transition: all var(--transition-base);
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-item:hover {
  color: var(--primary-color);
  background: rgba(78, 205, 196, 0.1);
  transform: translateY(-2px);
}

.nav-item.router-link-active {
  color: white;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  box-shadow: 0 4px 16px rgba(78, 205, 196, 0.3);
}

.user-area {
  display: flex;
  align-items: center;
  gap: 15px;
}

.btn-login, .btn-register {
  padding: 10px 24px;
  text-decoration: none;
  border-radius: 12px;
  transition: all var(--transition-base);
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.btn-login {
  color: var(--text-color-primary);
}

.btn-login:hover {
  color: var(--primary-color);
  background: rgba(78, 205, 196, 0.1);
}

.btn-register {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  color: white;
}

.btn-register:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(78, 205, 196, 0.3);
}

.user-avatar {
  cursor: pointer;
  transition: all var(--transition-base);
}

.user-avatar:hover {
  transform: scale(1.1);
}

/* 通知样式 */
.notification-icon-container {
  position: relative;
  font-size: 24px;
  cursor: pointer;
  color: var(--text-color-primary);
  transition: all 0.3s ease;
  padding: 10px;
  border-radius: 50%;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-icon-container:hover {
  color: var(--primary-color);
  background: rgba(78, 205, 196, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(78, 205, 196, 0.2);
}

.notification-count {
  position: absolute;
  top: 5px;
  right: 5px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  color: white;
  font-size: 12px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 12px;
  min-width: 24px;
  text-align: center;
  line-height: 1.2;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
  border: 2px solid white;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  background: white;
}

.notification-header span {
  font-weight: 700;
  font-size: 16px;
  color: var(--text-color-primary);
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
  padding: 8px 0;
  background: white;
}

.notification-item {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.notification-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 3px;
  background: transparent;
  transition: background 0.3s ease;
}

.notification-item:hover {
  background: #fafafa;
  transform: translateX(4px);
}

.notification-item:hover::before {
  background: var(--primary-color);
}

.notification-item.unread {
  background: linear-gradient(135deg, rgba(78, 205, 196, 0.08) 0%, rgba(78, 205, 196, 0.02) 100%);
  font-weight: 500;
}

.notification-item.unread::before {
  background: var(--primary-color);
}

.notification-content {
  flex: 1;
  margin-right: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.notification-title {
  color: var(--text-color-primary);
  font-weight: 600;
  font-size: 15px;
  padding-bottom: 4px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.notification-body {
  color: var(--text-color-secondary);
  font-size: 14px;
  line-height: 1.5;
  padding: 4px 0;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 4px;
  padding: 8px 12px;
}

.notification-time {
  color: #999;
  font-size: 12px;
  font-style: italic;
  text-align: right;
  padding-top: 4px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.notification-item .el-icon {
  color: #999;
  font-size: 18px;
  opacity: 0.6;
  transition: all 0.3s ease;
  align-self: center;
  padding: 4px;
  border-radius: 4px;
}

.notification-item:hover .el-icon {
  color: var(--danger-color);
  opacity: 1;
  background: rgba(255, 107, 107, 0.1);
}

/* 美化滚动条 */
.notification-list::-webkit-scrollbar {
  width: 6px;
}

.notification-list::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.notification-list::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.notification-list::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}

.notification-empty {
  padding: 20px;
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-color-placeholder);
  font-size: 14px;
}

.empty-content i {
  font-size: 32px;
  margin-bottom: 8px;
  opacity: 0.5;
}

@media (max-width: 768px) {
  .container {
    padding: 0 16px;
    height: 60px;
  }

  .nav-menu {
    gap: 8px;
    font-size: 14px;
  }

  .nav-item {
    padding: 8px 12px;
    font-size: 14px;
  }

  .logo-link {
    font-size: 22px;
  }
}
</style>