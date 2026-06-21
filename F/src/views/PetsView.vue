<template>
  <div class="pets-container">
    <div class="pets-background">
      <div class="bg-shapes">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
      </div>
    </div>
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header fade-in">
        <h1 class="page-title">
          <i class="el-icon-pets"></i> 寻找您的新伙伴
        </h1>
        <p class="page-subtitle">
          在我们的平台上，有许多可爱的宠物正在等待您的爱与关怀
        </p>
      </div>

      <!-- 筛选栏 --<!-- 使用 float 让卡片并排布局，并用 margin-right 来控制间距，确保卡片在筛选栏下方正确排列 -->
      <div class="filter-section fade-in" style="animation-delay: 0.2s">
        <div class="filter-card">
          <div class="filter-group">
            <label class="filter-label">宠物状态</label>
            <el-select v-model="selectedStatus" placeholder="选择宠物状态" class="filter-select" size="large">
              <el-option label="全部" value="all"></el-option>
              <el-option label="可领养" value="待领养"></el-option>
              <el-option label="仅分享" value="已领养"></el-option>
            </el-select>
          </div>

          <div class="filter-group">
            <label class="filter-label">宠物类型</label>
            <el-select v-model="selectedType" placeholder="选择类型" class="filter-select">
              <el-option label="所有宠物" value="all"></el-option>
              <el-option label="狗狗" value="dog"></el-option>
              <el-option label="猫咪" value="cat"></el-option>
              <el-option label="其他" value="other"></el-option>
            </el-select>
          </div>

          <div class="filter-group">
            <label class="filter-label">性别</label>
            <el-select v-model="selectedGender" placeholder="选择性别" class="filter-select">
              <el-option label="全部" value="all"></el-option>
              <el-option label="公" value="male"></el-option>
              <el-option label="母" value="female"></el-option>
            </el-select>
          </div>

          <div class="filter-group">
            <label class="filter-label">年龄段</label>
            <el-select v-model="selectedAge" placeholder="选择年龄段" class="filter-select">
              <el-option label="全部" value="all"></el-option>
              <el-option label="幼年期" value="young"></el-option>
              <el-option label="青年期" value="adult"></el-option>
              <el-option label="成年期" value="mature"></el-option>
            </el-select>
          </div>

          <div class="filter-group">
            <label class="filter-label">地区</label>
            <el-input 
              v-model="selectedLocation" 
              placeholder="搜索地区" 
              class="filter-select"
              clearable
            ></el-input>
          </div>

          <div class="filter-actions">
            <el-button type="primary" @click="applyFilters" class="filter-button primary">
              <i class="el-icon-search"></i> 搜索
            </el-button>
            <el-button @click="resetFilters" class="filter-button secondary">
              <i class="el-icon-refresh"></i> 重置
            </el-button>
          </div>
        </div>
      </div>

      <!-- 宠物列表 -->
      <div class="pets-list-section fade-in" style="animation-delay: 0.4s">
        <div class="results-info">
          <span>找到 {{ filteredPets.length }} 只宠物</span>
        </div>

        <div class="pets-grid">
          <div 
            v-for="(pet, index) in filteredPets" 
            :key="pet.id" 
            class="pet-card"
            :style="{ animationDelay: index * 0.1 + 's' }"
          >
            <div class="pet-image-container">
              <img :src="pet.image_url" :alt="pet.name" class="pet-image">
              <div v-if="pet.status === '待领养'" class="pet-status-badge">待领养</div>
              <div v-else-if="pet.status === 'adopted' || pet.status === '已领养'" class="pet-status-badge adopted-badge">已领养</div>
              <div v-else class="pet-status-badge share-badge">仅分享</div>
            </div>
            <div class="pet-info">
              <h3 class="pet-name">{{ pet.name }}</h3>
              <div class="pet-meta">
                <span class="meta-item">{{ pet.type === 'dog' ? '🐕 狗狗' : pet.type === 'cat' ? '🐱 猫咪' : '🦄 其他' }}</span>
                <span class="meta-item">{{ pet.age }}个月</span>
                <span class="meta-item">{{ pet.gender === 'male' ? '♂ 公' : '♀ 母' }}</span>
              </div>
              <div class="pet-description">
                {{ pet.description }}
              </div>
              <div class="pet-actions">
                <el-button type="primary" size="small" @click="viewPet(pet.id)">
                  <i class="el-icon-eye"></i> 查看详情
                </el-button>
                <el-button size="small" @click="addToFavorite(pet.id)">
                  <i class="el-icon-star-off"></i> 收藏
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="filteredPets.length === 0" class="empty-state">
          <i class="el-icon-folder-opened"></i>
          <h3>暂未找到符合条件的宠物</h3>
          <p>请尝试调整筛选条件或稍后再试</p>
        </div>

        <!-- 分页 -->
        <div v-if="filteredPets.length > 0" class="pagination-wrapper">
          <el-pagination
            background
            layout="prev, pager, next, total"
            :total="totalPets"
            :page-size="pageSize"
            v-model:current-page="currentPage"
            @current-change="handleCurrentChange"
          ></el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api/index.js'

const router = useRouter()

// 筛选条件
const selectedType = ref('all')
const selectedGender = ref('all')
const selectedAge = ref('all')
const selectedLocation = ref('')
const selectedStatus = ref('all')

// 分页
const currentPage = ref(1)
const pageSize = ref(12)

// 宠物数据
const pets = ref([])
const totalPets = ref(0)
const totalPages = ref(1)
const loading = ref(false)

// 从后端获取宠物数据
const fetchPets = async () => {
  loading.value = true
  try {
    // 构建筛选参数
    const filters = {
      page: currentPage.value,
      page_size: pageSize.value
    }
    if (selectedStatus.value !== 'all') {
      filters.status = selectedStatus.value
    }
    if (selectedType.value !== 'all') {
      filters.type = selectedType.value
    }
    if (selectedGender.value !== 'all') {
      filters.gender = selectedGender.value
    }
    if (selectedLocation.value) {
      filters.location = selectedLocation.value
    }
    
    const response = await api.getPets(filters)
    const data = response?.data || {}
    // 保存总宠物数和总页数
    totalPets.value = data.total || 0
    totalPages.value = data.total_pages || 1
    // 处理数据格式，确保与前端期望一致
    pets.value = (data.pets || []).map(pet => ({
      ...pet,
      // 确保status字段有合理的值
      status: pet.status && pet.status.trim() ? pet.status.trim() : '已领养',
      image_url: pet.main_photo ? pet.main_photo.image_url : '/static/images/default_pet.jpg'
      }))
  } catch (error) {
    ElMessage.error('获取宠物列表失败，请稍后再试')
    console.error('获取宠物列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 筛选后的宠物列表
const filteredPets = computed(() => {
  return pets.value
})

// 查看宠物详情
const viewPet = (id) => {
  router.push(`/pet/${id}`)
}

// 添加到收藏
const addToFavorite = async (id) => {
  const user = localStorage.getItem('user')
  if (!user) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  const parsedUser = JSON.parse(user)
  if (!parsedUser.id) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await api.favoritePet(id, parsedUser.id)
    ElMessage.success('已添加到收藏')
  } catch (error) {
    console.error('收藏失败:', error)
    let msg = '收藏失败'
    if (error.response?.data?.message) {
      msg = error.response.data.message
    } else if (error.message) {
      msg = error.message
    }
    ElMessage.error(msg)
  }
}

// 应用筛选
const applyFilters = () => {
  currentPage.value = 1
  fetchPets() // 调用fetchPets获取筛选后的数据
  ElMessage.success('筛选条件已应用')
}

// 重置筛选
const resetFilters = () => {
  selectedType.value = 'all'
  selectedGender.value = 'all'
  selectedAge.value = 'all'
  selectedLocation.value = ''
  selectedStatus.value = 'all'
  currentPage.value = 1
  fetchPets() // 调用fetchPets获取全部数据
  ElMessage.success('筛选条件已重置')
}

// 分页变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchPets()
}

// 挂载时获取数据
onMounted(() => {
  console.log('宠物列表页面已加载')
  fetchPets()
})
</script>

<style scoped>
.pets-container {
  min-height: 100vh;
  position: relative;
  padding-bottom: 80px;
  overflow: hidden;
}

.pets-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  z-index: -1;
}

.bg-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  animation: float 20s ease-in-out infinite;
}

.shape-1 {
  width: 300px;
  height: 300px;
  background: #ff6b6b;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 200px;
  height: 200px;
  background: #4ecdc4;
  top: 60%;
  right: 10%;
  animation-delay: 5s;
}

.shape-3 {
  width: 250px;
  height: 250px;
  background: #ffe66d;
  bottom: 20%;
  left: 50%;
  animation-delay: 10s;
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

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 20px;
  position: relative;
  z-index: 1;
}

.page-header {
  text-align: center;
  margin-bottom: 60px;
  color: white;
}

.page-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 15px;
  background: linear-gradient(135deg, #fff, #f0f0f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-subtitle {
  font-size: 18px;
  opacity: 0.9;
  color: rgba(255, 255, 255, 0.85);
}

.filter-section {
  margin-bottom: 40px;
}

.filter-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 30px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  align-items: flex-end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 160px;
}

.filter-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.filter-select {
  width: 100%;
  min-width: 160px;
}

.filter-actions {
  display: flex;
  gap: 12px;
}

.filter-button {
  border-radius: 12px;
  padding: 10px 25px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.filter-button.primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
}

.filter-button.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
}

.filter-button.secondary {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border: 1px solid rgba(102, 126, 234, 0.3);
}

.filter-button.secondary:hover {
  background: rgba(102, 126, 234, 0.2);
}

.pets-list-section {
  margin-top: 40px;
}

.results-info {
  margin-bottom: 30px;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  color: #666;
  font-size: 14px;
}

.pets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.pet-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  animation: fadeInUp 1s ease backwards;
}

.pet-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
}

.pet-image-container {
  position: relative;
  height: 250px;
  overflow: hidden;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.pet-card:hover .pet-image {
  transform: scale(1.1);
}

.pet-status-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: linear-gradient(135deg, #ff6b6b, #feca57);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

/* 仅分享徽章样式 */
.pet-status-badge.share-badge {
  background: linear-gradient(135deg, #4ecdc4, #45b7d1);
  box-shadow: 0 4px 12px rgba(78, 205, 196, 0.3);
}

/* 已领养徽章样式 */
.pet-status-badge.adopted-badge {
  background: linear-gradient(135deg, #ff6b6b, #ee5a5a);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.pet-info {
  padding: 25px;
}

.pet-name {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #333;
}

.pet-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.meta-item {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  padding: 5px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.pet-description {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.pet-actions {
  display: flex;
  gap: 10px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: rgba(255, 255, 255, 0.7);
}

.empty-state i {
  font-size: 64px;
  margin-bottom: 20px;
  color: rgba(255, 255, 255, 0.5);
}

.empty-state h3 {
  font-size: 24px;
  margin-bottom: 10px;
  color: white;
}

.empty-state p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.fade-in {
  animation: fadeInUp 1s ease;
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
  .container {
    padding: 40px 16px;
  }

  .page-title {
    font-size: 32px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .filter-card {
    padding: 20px;
    flex-direction: column;
    align-items: stretch;
  }

  .filter-group {
    min-width: 100%;
  }

  .filter-select {
    min-width: 100%;
  }

  .filter-actions {
    width: 100%;
    justify-content: center;
  }

  .pets-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .pet-card {
    max-width: 100%;
  }
}
</style>