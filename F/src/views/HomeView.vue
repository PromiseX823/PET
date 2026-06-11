<template>
  <div class="home">
    <div class="background-animation">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>
    
    <!-- 轮播图 -->
    <el-carousel height="580px" indicator-position="outside" arrow="always">
      <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
        <div class="carousel-slide">
          <div class="slide-content fade-in">
            <h1 class="slide-title">{{ item.title }}</h1>
            <p class="slide-subtitle">{{ item.subtitle }}</p>
            <el-button type="primary" size="large" class="cta-button">
              {{ item.cta }}
            </el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <div class="container">
      <!-- 功能入口 -->
      <section class="features">
        <h2 class="section-title fade-in">核心功能</h2>
        <p class="section-subtitle fade-in">选择你需要的服务，开始爱心之旅</p>
        <div class="features-grid">
          <div class="feature-item fade-in">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 2a14.5 14.5 0 0 0-10 17.9 14.5 14.5 0 0 0 18 0A14.5 14.5 0 0 0 12 2Z" />
                <path d="M15 9a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
                <path d="M13 15h1a3 3 0 1 0 0-6h-4a3 3 0 1 0 0 6h1" />
              </svg>
            </div>
            <h3 class="feature-title">浏览宠物</h3>
            <p class="feature-description">查看所有待领养的可爱宠物，找到你心仪的伙伴</p>
            <router-link to="/pets" class="feature-btn">开始浏览 <i class="el-icon-right"></i></router-link>
          </div>
          <div class="feature-item fade-in">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z" />
                <circle cx="12" cy="13" r="4" />
              </svg>
            </div>
            <h3 class="feature-title">分享照片</h3>
            <p class="feature-description">上传你的宠物照片，与社区用户分享美好瞬间</p>
            <router-link to="/upload" class="feature-btn">立即上传 <i class="el-icon-right"></i></router-link>
          </div>
          <div class="feature-item fade-in">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
              </svg>
            </div>
            <h3 class="feature-title">申请领养</h3>
            <p class="feature-description">给需要帮助的动物一个温暖的家，开启你们的美好旅程</p>
            <router-link to="/adopt" class="feature-btn">申请领养 <i class="el-icon-right"></i></router-link>
          </div>
          <div class="feature-item fade-in">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                <path d="M16 3.13a4 4 0 0 1 0 7.75" />
              </svg>
            </div>
            <h3 class="feature-title">爱心社区</h3>
            <p class="feature-description">加入我们的爱心社区，结识更多志同道合的朋友</p>
            <router-link to="/photos" class="feature-btn">加入社区 <i class="el-icon-right"></i></router-link>
          </div>
        </div>
      </section>

      <!-- 最新待领养宠物 -->
      <section class="latest-pets">
        <h2 class="section-title fade-in">最新待领养宠物</h2>
        <p class="section-subtitle fade-in">这些可爱的小家伙正在等待它们的新家庭</p>
        <div class="pets-grid">
          <div v-for="pet in latestPets" :key="pet.id" class="pet-card fade-in">
            <div class="pet-image-wrapper">
              <img :src="pet.image_url" :alt="pet.name" class="pet-image">
              <div class="pet-badge">待领养</div>
            </div>
            <div class="pet-info">
              <h3 class="pet-name">{{ pet.name }}</h3>
              <div class="pet-meta">
                <span class="pet-type">{{ pet.type === 'dog' ? '狗' : pet.type === 'cat' ? '猫' : '其他' }}</span>
                <span class="pet-age">{{ pet.age }}个月</span>
                <span class="pet-gender">{{ pet.gender === 'male' ? '公' : pet.gender === 'female' ? '母' : '未知' }}</span>
              </div>
              <div class="pet-actions">
                <el-button type="primary" size="small" class="action-btn primary" @click="viewPet(pet.id)">查看详情</el-button>
                <el-button type="default" size="small" class="action-btn default" @click="applyAdopt(pet.id)">申请领养</el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="view-all">
          <el-button type="primary" size="large" class="view-all-btn">
            <i class="el-icon-view"></i> 查看全部宠物
          </el-button>
        </div>
      </section>

      <!-- 热门照片 -->
      <section class="hot-photos">
        <h2 class="section-title fade-in">热门照片</h2>
        <p class="section-subtitle fade-in">来自爱心用户分享的精彩瞬间</p>
        <div class="photos-grid">
          <div v-for="photo in hotPhotos" :key="photo.id" class="photo-item fade-in">
            <div class="photo-image-wrapper">
              <img :src="photo.image_url" :alt="photo.caption" class="photo-image">
              <div class="photo-overlay">
                <div class="photo-likes">
                  <i class="el-icon-star-on"></i> {{ photo.like_count }}
                </div>
                <div class="photo-comments">
                  <i class="el-icon-chat-dot-round"></i> {{ photo.comment_count }}
                </div>
              </div>
            </div>
            <div class="photo-info">
              <div class="photo-caption">{{ photo.caption }}</div>
              <div class="photo-uploader">上传者：{{ photo.uploader }}</div>
            </div>
          </div>
        </div>
        <div class="view-all">
          <el-button type="primary" size="large" class="view-all-btn">
            <i class="el-icon-view"></i> 查看全部照片
          </el-button>
        </div>
      </section>

      <!-- 统计数据 -->
      <section class="stats">
        <div class="stats-container">
          <div class="stats-grid">
            <div class="stat-item fade-in">
              <div class="stat-icon">
              <el-icon><FolderOpened /></el-icon>
            </div>
              <div class="stat-number">{{ stats.pending_pets }}</div>
              <div class="stat-label">待领养宠物</div>
            </div>
            <div class="stat-item fade-in">
              <div class="stat-icon">
              <el-icon><Check /></el-icon>
            </div>
              <div class="stat-number">{{ stats.adopted_pets }}</div>
              <div class="stat-label">成功领养</div>
            </div>
            <div class="stat-item fade-in">
              <div class="stat-icon">
              <el-icon><Picture /></el-icon>
            </div>
              <div class="stat-number">{{ stats.photos }}</div>
              <div class="stat-label">分享照片</div>
            </div>
            <div class="stat-item fade-in">
              <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
              <div class="stat-number">{{ stats.users }}</div>
              <div class="stat-label">注册用户</div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { FolderOpened, Check, Picture, User } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()

// 轮播图数据
const carouselItems = ref([
  { title: '给流浪动物一个家', subtitle: '每一只小动物都值得被爱，领养代替购买', cta: '开始领养' },
  { title: '分享美好瞬间', subtitle: '记录你的宠物的精彩时刻，与大家分享', cta: '立即分享' },
  { title: '加入爱心社区', subtitle: '和同样热爱动物的朋友们一起，为它们创造更美好的生活', cta: '加入我们' }
])

// 最新待领养宠物数据
const latestPets = ref([])
const loadingPets = ref(false)

// 热门照片数据
const hotPhotos = ref([])
const loadingPhotos = ref(false)

// 统计数据
const stats = ref({
  pending_pets: 0,
  adopted_pets: 0,
  photos: 0,
  users: 0
})

// 获取热门照片
const fetchHotPhotos = async () => {
  try {
    loadingPhotos.value = true
    const response = await api.getHotPhotos(3)
    
    if (Array.isArray(response)) {
      hotPhotos.value = response.map(photo => ({
        id: photo.id,
        image_url: photo.image_url,
        caption: photo.caption,
        like_count: photo.like_count,
        comment_count: photo.comment_count,
        uploader: photo.user?.username || '未知用户'
      }))
    }
  } catch (error) {
    console.error('获取热门照片失败:', error)
  } finally {
    loadingPhotos.value = false
  }
}

// 获取最新待领养宠物
const fetchLatestPets = async () => {
  try {
    loadingPets.value = true
    const response = await api.getPets()
    
    if (response && response.pets) {
      // 过滤出待领养的宠物，并取前3个
      const pendingPets = response.pets
        .filter(pet => pet.status === '待领养')
        .slice(0, 3)
      
      // 转换数据格式
      latestPets.value = pendingPets.map(pet => ({
        id: pet.id,
        name: pet.name,
        type: pet.type,
        age: pet.age,
        gender: pet.gender,
        status: pet.status,
        image_url: pet.main_photo?.image_url || '/static/images/pet1_1.jpg'
      }))
    }
  } catch (error) {
    console.error('获取宠物列表失败:', error)
  } finally {
    loadingPets.value = false
  }
}

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await api.getStats()
    
    if (response && response.success && response.data) {
      const statsData = response.data
      stats.value = {
        pending_pets: statsData.pets.available,
        adopted_pets: statsData.adoptions.approved + statsData.adoptions.completed,
        photos: statsData.photos.total,
        users: statsData.users.total
      }
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 查看宠物详情
const viewPet = (id) => {
  router.push(`/pet/${id}`)
}

// 申请领养
const applyAdopt = (id) => {
  router.push(`/adopt?pet=${id}`)
}

// 页面加载时获取数据
onMounted(async () => {
  // 并行获取所有数据
  await Promise.all([
    fetchLatestPets(),
    fetchHotPhotos(),
    fetchStats()
  ])
})
</script>

<style scoped>
.home {
  background: linear-gradient(135deg, #fdfbfb 0%, #ebedee 100%);
  color: var(--text-color);
  position: relative;
  min-height: 100vh;
  overflow: hidden;
}

.background-animation {
  position: fixed;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 0;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  animation: float 20s infinite ease-in-out;
}

.shape-1 {
  width: 300px;
  height: 300px;
  background: var(--primary-color);
  top: 15%;
  right: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 200px;
  height: 200px;
  background: var(--secondary-color);
  bottom: 25%;
  left: 15%;
  animation-delay: 4s;
}

.shape-3 {
  width: 150px;
  height: 150px;
  background: var(--accent-color);
  top: 55%;
  left: 65%;
  animation-delay: 8s;
}

/* 轮播图 */
.carousel-slide {
  height: 580px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.carousel-slide::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
}

.slide-content {
  z-index: 1;
  animation: fadeInUp 1s ease;
}

.slide-title {
  font-size: 56px;
  margin-bottom: 24px;
  font-weight: bold;
  text-shadow: 0 2px 10px rgba(0,0,0,0.2);
}

.slide-subtitle {
  font-size: 26px;
  opacity: 0.95;
  margin-bottom: 40px;
  font-weight: 300;
}

.cta-button {
  background: white;
  color: var(--primary-color);
  border: none;
  padding: 16px 48px;
  font-size: 18px;
  font-weight: 500;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.2);
  transition: all var(--transition-base);
}

.cta-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.3);
}

/* 容器和通用样式 */
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 60px 24px;
  position: relative;
  z-index: 1;
}

.section-title {
  text-align: center;
  font-size: 42px;
  margin-bottom: 16px;
  color: var(--text-color-primary);
  font-weight: 600;
}

.section-subtitle {
  text-align: center;
  font-size: 18px;
  color: var(--text-color-secondary);
  margin-bottom: 50px;
}

/* 功能入口 */
.features {
  margin-bottom: 80px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 32px;
}

.feature-item {
  text-align: center;
  padding: 40px 32px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
}

.feature-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
  transform: scaleX(0);
  transition: transform var(--transition-base);
}

.feature-item:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12);
}

.feature-item:hover::before {
  transform: scaleX(1);
}

.feature-icon {
  font-size: 48px;
  color: var(--primary-color);
  margin-bottom: 24px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.1) 0%, rgba(255, 217, 61, 0.1) 100%);
  border-radius: 50%;
  transition: all var(--transition-base);
}

.feature-item:hover .feature-icon {
  transform: scale(1.1) rotate(5deg);
}

.feature-title {
  margin-bottom: 16px;
  color: var(--text-color-primary);
  font-size: var(--font-size-xl);
  font-weight: 600;
}

.feature-description {
  color: var(--text-color-secondary);
  margin-bottom: 24px;
  line-height: 1.7;
}

.feature-btn {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all var(--transition-base);
}

.feature-btn:hover {
  gap: 12px;
}

/* 宠物卡片 */
.latest-pets {
  margin-bottom: 80px;
}

.pets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 32px;
}

.pet-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all var(--transition-base);
}

.pet-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12);
}

.pet-image-wrapper {
  position: relative;
  height: 240px;
  overflow: hidden;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-base);
}

.pet-card:hover .pet-image {
  transform: scale(1.1);
}

.pet-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  background: var(--primary-color);
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.pet-info {
  padding: 24px;
}

.pet-name {
  margin-bottom: 12px;
  color: var(--text-color-primary);
  font-size: var(--font-size-xl);
  font-weight: 600;
}

.pet-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  font-size: 14px;
  flex-wrap: wrap;
}

.pet-type,
.pet-age,
.pet-gender {
  padding: 4px 12px;
  background: var(--bg-color-light);
  border-radius: 12px;
  color: var(--text-color-secondary);
}

.pet-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  flex: 1;
}

/* 热门照片 */
.hot-photos {
  margin-bottom: 80px;
}

.photos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.photo-item {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all var(--transition-base);
}

.photo-item:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12);
}

.photo-image-wrapper {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.photo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-base);
}

.photo-item:hover .photo-image {
  transform: scale(1.1);
}

.photo-overlay {
  position: absolute;
  bottom: 16px;
  left: 16px;
  right: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.photo-likes,
.photo-comments {
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 14px;
  backdrop-filter: blur(10px);
}

.photo-info {
  padding: 20px;
}

.photo-caption {
  font-size: 16px;
  color: var(--text-color-primary);
  margin-bottom: 8px;
  font-weight: 500;
}

.photo-uploader {
  font-size: 14px;
  color: var(--text-color-secondary);
}

/* 查看全部按钮 */
.view-all {
  text-align: center;
  margin-top: 40px;
}

.view-all-btn {
  border-radius: 50px;
  padding: 14px 40px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transition: all var(--transition-base);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  border: none;
}

.view-all-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}

/* 统计数据 */
.stats {
  margin: 60px 0;
}

.stats-container {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  padding: 60px 0;
  border-radius: 20px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 40px;
}

.stat-item {
  text-align: center;
  color: white;
  transition: transform var(--transition-base);
}

.stat-item:hover {
  transform: translateY(-5px);
}

.stat-icon {
  font-size: 32px;
  margin-bottom: 12px;
  opacity: 0.8;
}

.stat-number {
  font-size: 56px;
  font-weight: bold;
  margin-bottom: 12px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.2);
}

.stat-label {
  font-size: 18px;
  opacity: 0.9;
}

/* 动画 */
@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(20px, -20px) scale(1.05);
  }
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

.fade-in {
  animation: fadeInUp 0.6s ease backwards;
}

.fade-in:nth-child(1) { animation-delay: 0.2s; }
.fade-in:nth-child(2) { animation-delay: 0.4s; }
.fade-in:nth-child(3) { animation-delay: 0.6s; }
.fade-in:nth-child(4) { animation-delay: 0.8s; }

/* 响应式设计 */
@media (max-width: 768px) {
  .carousel-slide {
    height: 400px;
  }

  .slide-title {
    font-size: 32px;
  }

  .slide-subtitle {
    font-size: 18px;
    margin-bottom: 24px;
  }

  .section-title {
    font-size: 32px;
  }

  .section-subtitle {
    font-size: 16px;
  }

  .container {
    padding: 40px 16px;
  }

  .features-grid,
  .pets-grid,
  .photos-grid,
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }
}
</style>