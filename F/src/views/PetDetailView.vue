<template>
  <div class="pet-detail-container">
    <div class="pet-detail-background">
      <div class="bg-shapes">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
      </div>
    </div>
    <div class="container">
      <!-- 返回按钮 -->
      <div class="back-button-wrapper fade-in">
        <el-button type="text" @click="$router.go(-1)" class="back-button">
          <i class="el-icon-back"></i> 返回列表
        </el-button>
      </div>

      <!-- 宠物详情卡片 -->
      <div class="pet-detail-card fade-in">
        <!-- 宠物图片展示 -->
        <div class="pet-main-image">
          <img :src="pet.image_url" :alt="pet.name" class="main-image">
          <div class="pet-badge" :class="pet.status">
            <i class="el-icon-star-off"></i> {{ pet.status === '待领养' ? '立即领养' : pet.status === '已领养' ? '仅分享' : pet.status }}
          </div>
        </div>

        <!-- 宠物基本信息 -->
        <div class="pet-basic-info">
          <h1 class="pet-name">{{ pet.name }}</h1>
          <div class="pet-meta">
            <div class="meta-item">
              <span class="meta-label">类型</span>
              <span class="meta-value">{{ pet.type === 'dog' ? '🐕 狗狗' : pet.type === 'cat' ? '🐱 猫咪' : '🦄 其他' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">性别</span>
              <span class="meta-value">{{ pet.gender === 'male' ? '♂ 公' : pet.gender === 'female' ? '♀ 母' : '未知' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">年龄</span>
              <span class="meta-value">{{ pet.age }} 个月</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">地区</span>
              <span class="meta-value">
                <i class="el-icon-location"></i> {{ pet.location }}
              </span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="pet-actions">
            <el-button type="primary" size="large" class="primary-action-btn" @click="openApplyDialog">
              <i class="el-icon-plus"></i>
              立即申请领养
            </el-button>
            <el-button size="large" class="secondary-action-btn">
              <i class="el-icon-share"></i>
              分享给朋友
            </el-button>
            <el-button size="large" class="secondary-action-btn">
              <i class="el-icon-star-off"></i>
              收藏
            </el-button>
            <!-- 编辑宠物按钮 -->
            <el-button type="success" size="large" class="secondary-action-btn" @click="openEditDialog">
              <i class="el-icon-edit"></i>
              编辑宠物
            </el-button>
          </div>
        </div>
      </div>

      <div class="pet-detail-content">
        <!-- 宠物详情信息 -->
        <div class="pet-info-tabs fade-in" style="animation-delay: 0.2s">
          <el-tabs v-model="activeTab" class="info-tabs">
            <el-tab-pane name="basic">
  <template #label>
    <i class="el-icon-info"></i> 基本信息
  </template>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">品种</span>
                  <span class="info-value">{{ pet.breed || '未知' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">体重</span>
                  <span class="info-value">{{ pet.weight || 0 }} kg</span>
                </div>
                <div class="info-item">
                  <span class="info-label">颜色</span>
                  <span class="info-value">{{ pet.color || '未知' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">健康状况</span>
                  <span class="info-value">{{ pet.health_status || '健康' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">是否绝育</span>
                  <span class="info-value">{{ pet.neutered ? '已绝育' : '未绝育' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">是否免疫</span>
                  <span class="info-value">{{ pet.vaccinated ? '已免疫' : '未免疫' }}</span>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane name="description">
  <template #label>
    <i class="el-icon-document"></i> 详细描述
  </template>
              <div class="description-content">
                {{ pet.description || '暂无详细描述，欢迎咨询。' }}
              </div>
            </el-tab-pane>

            <el-tab-pane name="photos">
  <template #label>
    <i class="el-icon-picture-outline"></i> 更多照片
  </template>
              <div class="photo-grid">
                <div v-for="(photo, index) in pet.more_photos" :key="index" class="photo-item">
                  <img :src="photo" :alt="pet.name + '_photo_' + index" class="photo-image">
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

        <!-- 推荐相似宠物 -->
        <div class="related-pets-section fade-in" style="animation-delay: 0.4s">
          <h2 class="section-title">
            <i class="el-icon-document-copy"></i> 相似宠物推荐
          </h2>
          <div class="related-pets-grid">
            <div v-for="(relatedPet, index) in relatedPets" :key="relatedPet.id" class="related-pet-card" :style="{ animationDelay: index * 0.1 + 's' }">
              <div class="related-pet-image">
                <img :src="relatedPet.image_url" :alt="relatedPet.name" class="related-image">
              </div>
              <div class="related-pet-info">
                <h4 class="related-pet-name">{{ relatedPet.name }}</h4>
                <div class="related-pet-meta">
                  <span>{{ relatedPet.type === 'dog' ? '🐕 狗狗' : '🐱 猫咪' }}</span>
                  <span>{{ relatedPet.age }}个月</span>
                </div>
                <el-button type="primary" size="mini" @click="viewPet(relatedPet.id)">查看详情</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 领养申请对话框 -->
  <el-dialog
    v-model="showApplyDialog"
    title="申请领养"
    width="500px"
    center
  >
    <div class="apply-dialog-content">
      <el-form label-width="80px">
        <el-form-item label="宠物名称">
          <el-input v-model="pet.name" disabled />
        </el-form-item>
        <el-form-item label="申请理由" required>
          <el-input
            v-model="applyReason"
            type="textarea"
            :rows="4"
            placeholder="请详细说明您的领养理由、养宠经验、居住环境等信息，这将有助于我们评估您是否适合领养该宠物"
          />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showApplyDialog = false">取消</el-button>
        <el-button type="primary" @click="submitApply">提交申请</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 编辑宠物对话框 -->
  <el-dialog
    v-model="showEditDialog"
    title="编辑宠物信息"
    width="500px"
    center
  >
    <div class="edit-dialog-content">
      <el-form label-width="80px">
        <el-form-item label="宠物名称">
          <el-input v-model="editForm.name" placeholder="请输入宠物名称" />
        </el-form-item>
        
        <el-form-item label="宠物类型">
          <el-select v-model="editForm.type" placeholder="请选择宠物类型">
            <el-option label="狗狗" value="dog"></el-option>
            <el-option label="猫咪" value="cat"></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="品种">
          <el-input v-model="editForm.breed" placeholder="请输入宠物品种" />
        </el-form-item>
        
        <el-form-item label="年龄">
          <el-input-number
            v-model="editForm.age"
            :min="0"
            :max="360"
            placeholder="请输入宠物年龄（月）"
          />
        </el-form-item>
        
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender">
            <el-radio label="male">公</el-radio>
            <el-radio label="female">母</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="体重">
          <el-input-number
            v-model="editForm.weight"
            :min="0"
            :max="200"
            :step="0.1"
            placeholder="请输入宠物体重（kg）"
          />
        </el-form-item>
        
        <el-form-item label="颜色">
          <el-input v-model="editForm.color" placeholder="请输入宠物颜色" />
        </el-form-item>
        
        <el-form-item label="地区">
          <el-input v-model="editForm.location" placeholder="请输入宠物所在地区" />
        </el-form-item>
        
        <el-form-item label="健康状况">
          <el-input
            v-model="editForm.health_info"
            type="textarea"
            rows="2"
            placeholder="请输入宠物健康状况"
          />
        </el-form-item>
        
        <el-form-item label="绝育">
          <el-switch v-model="editForm.neutered" />
        </el-form-item>
        
        <el-form-item label="免疫">
          <el-switch v-model="editForm.vaccinated" />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="请选择宠物状态">
            <el-option label="待领养" value="待领养"></el-option>
            <el-option label="已领养" value="已领养"></el-option>
            <el-option label="已预定" value="已预定"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input
            v-model="editForm.description"
            type="textarea"
            rows="4"
            placeholder="请输入宠物详细描述"
          />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, onBeforeMount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores'
import api from '../api/index.js'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 当前宠物信息
const pet = ref({
  id: 1,
  name: '阿黄',
  type: 'dog',
  breed: '金毛寻回犬',
  age: 12,
  gender: 'male',
  weight: 15.5,
  color: '金黄色',
  location: '北京朝阳区',
  health_status: '健康',
  neutered: true,
  vaccinated: true,
  status: '待领养',
  description: '阿黄是一只非常活泼可爱的金毛幼犬，性格温顺，非常喜欢和小朋友一起玩耍。它已经完成了基础的疫苗接种和健康检查，完全可以健康快乐地陪伴您的家庭。阿黄很聪明，学习能力强，是理想的家庭宠物。',
  image_url: '/static/images/pet1_1.jpg',
  more_photos: [
    '/static/images/pet1_2.jpg',
    '/static/images/pet2_1.jpg',
    '/static/images/pet2_2.jpg'
  ],
  owner_id: null // 添加宠物主人ID
})

// 编辑宠物相关
const showEditDialog = ref(false)
const editForm = ref({
  name: '',
  type: '',
  breed: '',
  age: 0,
  gender: 'male',
  weight: 0,
  color: '',
  location: '',
  health_info: '',
  neutered: false,
  vaccinated: false,
  status: '待领养',
  description: ''
})

// 激活标签页
const activeTab = ref('basic')

// 相关宠物推荐
const relatedPets = ref([
  { id: 2, name: '旺财', type: 'dog', age: 36, image_url: '/static/images/pet2_1.jpg' },
  { id: 3, name: '可乐', type: 'dog', age: 12, image_url: '/static/images/pet3.jpg' },
  { id: 4, name: '咪咪', type: 'cat', age: 18, image_url: '/static/images/pet5_1.jpg' }
])

// 查看宠物详情
const viewPet = (id) => {
  router.push(`/pet/${id}`)
}

// 申请领养
const showApplyDialog = ref(false)
const applyReason = ref('')

// 打开申请领养对话框
const openApplyDialog = () => {
  const user = localStorage.getItem('user')
  if (!user) {
    ElMessage.warning('请先登录后再申请领养')
    router.push('/login')
    return
  }
  showApplyDialog.value = true
}

// 提交领养申请
const submitApply = async () => {
  try {
    if (!applyReason.value.trim()) {
      ElMessage.warning('请填写领养申请理由')
      return
    }
    
    const user = localStorage.getItem('user')
    const parsedUser = JSON.parse(user)
    
    await api.createAdoption({
      pet_id: pet.value.id,
      user_id: parsedUser.id,
      reason: applyReason.value
    })
    
    ElMessage.success('领养申请已提交，我们会尽快与您联系！')
    showApplyDialog.value = false
    applyReason.value = ''
  } catch (error) {
    console.error('申请领养失败:', error)
    ElMessage.error(error.response?.data?.error || '申请失败，请稍后重试')
  }
}

// 获取宠物信息
const fetchPet = async () => {
  try {
    const id = route.params.id
    console.log('获取宠物信息:', id)
    const data = await api.getPetById(id)
    console.log('获取到的宠物数据:', data)
    
    // 处理返回的数据结构，确保与前端期望的pet对象结构匹配
    // 从photos数组中找到主照片
    const mainPhoto = data.photos && data.photos.length > 0 
      ? data.photos.find(photo => photo.is_main) || data.photos[0]
      : null
    
    pet.value = {
      id: data.id,
      name: data.name,
      type: data.type,
      breed: data.breed,
      age: data.age,
      gender: data.gender,
      weight: data.weight || 0,
      color: data.color || '未知',
      location: data.location,
      health_status: data.health_info || '健康',
      neutered: data.neutered || false,
      vaccinated: data.vaccinated || false,
      status: data.status,
      description: data.description,
      image_url: mainPhoto ? mainPhoto.image_url : '/static/images/pet1_1.jpg',
      // 处理更多照片数据结构
      more_photos: data.photos ? data.photos.map(photo => photo.image_url) : [],
      owner_id: data.user_id || data.owner_id // 获取宠物主人ID
    }
  } catch (error) {
    console.error('获取宠物信息失败:', error)
    ElMessage.error('获取宠物信息失败，请稍后重试')
  }
}

// 检查当前用户是否是宠物主人
const isPetOwner = ref(false)

// 更新是否是宠物主人的状态
const updatePetOwnerStatus = () => {
  console.log('检查是否是宠物主人:')
  console.log('当前登录用户ID:', userStore.user?.id, typeof userStore.user?.id)
  console.log('宠物主人ID:', pet.value.owner_id, typeof pet.value.owner_id)
  
  if (userStore.user && userStore.user.id) {
    // 确保ID类型匹配
    isPetOwner.value = parseInt(userStore.user.id) === parseInt(pet.value.owner_id)
  } else {
    isPetOwner.value = false
  }
  
  console.log('判断结果:', isPetOwner.value)
}

// 打开编辑宠物对话框
const openEditDialog = () => {
  // 填充表单数据
  editForm.value = {
    name: pet.value.name,
    type: pet.value.type,
    breed: pet.value.breed || '',
    age: pet.value.age || 0,
    gender: pet.value.gender,
    weight: pet.value.weight || 0,
    color: pet.value.color || '',
    location: pet.value.location,
    health_info: pet.value.health_status || '',
    neutered: pet.value.neutered || false,
    vaccinated: pet.value.vaccinated || false,
    status: pet.value.status,
    description: pet.value.description || ''
  }
  showEditDialog.value = true
}

// 保存编辑的宠物信息
const saveEdit = async () => {
  try {
    if (!userStore.user || !userStore.user.id) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }
    
    // 构建请求数据
    const updateData = {
      ...editForm.value,
      user_id: userStore.user.id // 添加用户ID用于权限验证
    }
    
    // 调用API更新宠物信息
    await api.updatePet(pet.value.id, updateData)
    
    // 更新本地宠物数据
    await fetchPet()
    
    ElMessage.success('宠物信息更新成功')
    showEditDialog.value = false
  } catch (error) {
    console.error('更新宠物信息失败:', error)
    let errorMsg = '更新宠物信息失败'
    if (error.response?.data?.error) {
      errorMsg += ': ' + error.response.data.error
    }
    ElMessage.error(errorMsg)
  }
}

// 挂载时获取数据
onMounted(() => {
  // 初始化用户状态
  userStore.initializeUser()
  console.log('当前登录用户:', userStore.user)
  
  fetchPet().then(() => {
    console.log('宠物数据:', pet.value)
    // 获取宠物数据后检查是否是宠物主人
    updatePetOwnerStatus()
    console.log('是否是宠物主人:', isPetOwner.value)
  })
})
</script>

<style scoped>
.pet-detail-container {
  min-height: 100vh;
  position: relative;
  padding-bottom: 80px;
  overflow: hidden;
}

.pet-detail-background {
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

.back-button-wrapper {
  margin-bottom: 30px;
}

.back-button {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  transition: all 0.3s ease;
}

.back-button:hover {
  color: #fff;
  transform: translateX(-5px);
}

.pet-detail-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 40px;
  margin-bottom: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
  transition: all 0.3s ease;
}

.pet-detail-card:hover {
  box-shadow: 0 25px 70px rgba(0, 0, 0, 0.15);
}

.pet-main-image {
  flex: 1;
  min-width: 300px;
  max-width: 500px;
  position: relative;
  border-radius: 16px;
  overflow: hidden;
}

.main-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 16px;
}

.pet-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  color: white;
  padding: 10px 20px;
  border-radius: 20px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

/* 待领养状态样式 */
.pet-badge.待领养 {
  background: linear-gradient(135deg, #ff6b6b, #feca57);
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

/* 仅分享状态样式 */
.pet-badge.已领养 {
  background: linear-gradient(135deg, #4ecdc4, #45b7d1);
  box-shadow: 0 4px 15px rgba(78, 205, 196, 0.3);
}

.pet-basic-info {
  flex: 1;
  min-width: 300px;
}

.pet-name {
  font-size: 36px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
}

.pet-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin-bottom: 30px;
}

.meta-item {
  display: flex;
  flex-direction: column;
}

.meta-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.meta-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.pet-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.primary-action-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.primary-action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
}

.secondary-action-btn {
  border-radius: 12px;
  padding: 12px 25px;
  transition: all 0.3s ease;
}

.secondary-action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.pet-detail-content {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.pet-info-tabs {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
  margin-top: 30px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
  border-radius: 12px;
}

.info-label {
  color: #666;
  font-weight: 500;
}

.info-value {
  color: #333;
  font-weight: 600;
}

.description-content {
  padding: 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
  border-radius: 12px;
  line-height: 1.8;
  color: #555;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.photo-item {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.photo-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.15);
}

.photo-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.related-pets-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 30px;
}

.related-pets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 30px;
}

.related-pet-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.related-pet-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

.related-pet-image {
  height: 180px;
  overflow: hidden;
}

.related-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.related-pet-card:hover .related-image {
  transform: scale(1.1);
}

.related-pet-info {
  padding: 20px;
}

.related-pet-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.related-pet-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.related-pet-meta span {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
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
  .pet-detail-card {
    flex-direction: column;
    padding: 20px;
    gap: 20px;
  }

  .pet-main-image {
    max-width: 100%;
  }

  .pet-name {
    font-size: 28px;
  }

  .pet-meta {
    display: flex;
    justify-content: center;
    gap: 20px;
  }

  .pet-actions {
    flex-direction: column;
    align-items: center;
  }

  .pet-info-tabs,
  .related-pets-section {
    padding: 20px;
  }
}
</style>