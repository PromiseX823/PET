<template>
  <div class="adopt-container">
    <!-- 背景装饰 -->
    <div class="adopt-background">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>
    
    <div class="container">
      <h1 class="page-title">
        <i class="el-icon-heart-fill"></i> 申请领养
      </h1>
      <p class="page-subtitle">给需要温暖的毛孩子一个家，用爱陪伴它们成长</p>

      <div class="adopt-wrapper">
        <section class="adopt-info">
          <h2 class="section-title"><i class="el-icon-document"></i> 领养流程和注意事项</h2>
          <div class="notice-list">
            <el-descriptions :column="descriptionColumns" border class="notice-descriptions">
              <el-descriptions-item label="流程">
                选择喜欢的宠物，填写申请信息，等待审核
              </el-descriptions-item>
              <el-descriptions-item label="要求">
                有稳定的收入和居所，接受回访
              </el-descriptions-item>
              <el-descriptions-item label="审核">
                一般 1-3 个工作日内会有结果
              </el-descriptions-item>
              <el-descriptions-item label="责任">
                领养后请善待宠物，不得遗弃
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </section>

        <section class="adopt-form-section">
          <el-form :model="form" label-width="100px" class="adopt-form">
            <el-form-item label="选择宠物">
              <el-select v-model="form.pet_id" placeholder="选择要领养的宠物" @change="onPetSelect" class="form-select" :loading="loading">
                <el-option v-for="pet in availablePets" :key="pet.id" :label="`${pet.name} - ${pet.type}`" :value="pet.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="申请理由">
              <el-input v-model="form.reason" type="textarea" placeholder="请说明您领养此宠物的原因" rows="6" class="form-input" />
            </el-form-item>
            <el-form-item label="联系方式">
              <el-input v-model="form.phone" placeholder="请输入您的联系电话" class="form-input" />
            </el-form-item>
            <el-form-item label="居住地址">
              <el-input v-model="form.address" placeholder="请输入您的详细地址" class="form-input" />
            </el-form-item>
            <el-form-item label="">
              <el-button type="primary" @click="submitAdoption" class="adopt-button">
                <i class="el-icon-check"></i> 提交领养申请
              </el-button>
            </el-form-item>
          </el-form>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api/index.js'
import { useUserStore } from '../stores/index.js'

const userStore = useUserStore()
userStore.initializeUser()

const form = ref({ pet_id: '', reason: '', phone: '', address: '' })
const availablePets = ref([])
const loading = ref(false)
const descriptionColumns = ref(2)

// 响应式控制描述列表的列数
watchEffect(() => {
  const updateColumns = () => {
    descriptionColumns.value = window.innerWidth <= 768 ? 1 : 2
  }
  
  // 初始设置
  updateColumns()
  
  // 监听窗口大小变化
  window.addEventListener('resize', updateColumns)
  
  // 清理函数
  return () => window.removeEventListener('resize', updateColumns)
})

const loadAvailablePets = async () => {
  try {
    loading.value = true
    const response = await api.getPets()
    const data = response?.data || {}
    availablePets.value = (data.pets || []).filter(pet => pet.status === '待领养')
  } catch (error) {
    console.error('加载宠物列表失败:', error)
    ElMessage.error('加载宠物列表失败')
  } finally {
    loading.value = false
  }
}

const onPetSelect = (id) => {
  console.log('选择宠物 ID:', id)
}

const submitAdoption = async () => {
  if (!userStore.user) {
    ElMessage.error('请先登录后再申请领养')
    return
  }
  
  if (!form.value.pet_id) {
    ElMessage.error('请选择要领养的宠物')
    return
  }
  
  if (!form.value.reason) {
    ElMessage.error('请填写申请理由')
    return
  }
  
  if (!form.value.phone) {
    ElMessage.error('请填写联系方式')
    return
  }
  
  if (!form.value.address) {
    ElMessage.error('请填写居住地址')
    return
  }
  
  try {
    const adoptionData = {
      pet_id: form.value.pet_id,
      user_id: userStore.user.id,
      reason: form.value.reason,
      phone: form.value.phone,
      address: form.value.address
    }
    
    await api.createAdoption(adoptionData)
    ElMessage.success('领养申请已提交！我们会尽快与您联系。')
    
    form.value = { pet_id: '', reason: '', phone: '', address: '' }
  } catch (error) {
    console.error('提交申请失败:', error)
    ElMessage.error('提交申请失败，请稍后重试')
  }
}

onMounted(() => {
  loadAvailablePets()
})
</script>

<style scoped>
.adopt-container {
  min-height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 50%, #fef3f3 100%);
  padding: 60px 0 80px 0;
  overflow: hidden;
}

.adopt-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at 30% 30%, rgba(78, 205, 196, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(255, 107, 107, 0.15) 0%, transparent 50%);
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
  background: linear-gradient(135deg, #4ecdc4 0%, #44a3d0 100%);
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 250px;
  height: 250px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  bottom: 10%;
  right: 10%;
  animation-delay: 3s;
}

.shape-3 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #45b7d1 0%, #96ceb4 100%);
  top: 50%;
  right: 30%;
  animation-delay: 6s;
}

.container {
  max-width: 1000px;
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

.adopt-wrapper {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 50px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  animation: fadeInUp 0.8s ease 0.4s backwards;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.adopt-wrapper:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 25px;
  padding-left: 15px;
  border-left: 4px solid var(--primary-color);
  position: relative;
}

.section-title::before {
  content: '';
  position: absolute;
  top: 50%;
  left: -15px;
  width: 4px;
  height: 60%;
  background: linear-gradient(to bottom, var(--primary-color), var(--secondary-color));
  transform: translateY(-50%);
}

.adopt-info {
  margin-bottom: 50px;
}

.notice-list {
  margin-top: 20px;
}

.notice-descriptions {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.notice-descriptions:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.el-descriptions__label {
  font-weight: 600;
  color: var(--text-color);
  background: rgba(255, 107, 107, 0.05);
  padding: 15px;
}

.el-descriptions__content {
  color: var(--text-color-secondary);
  padding: 15px;
}

.adopt-form-section {
  margin-top: 50px;
}

.adopt-form {
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
  box-shadow: 0 0 0 3px rgba(78, 205, 196, 0.1);
}

.adopt-button {
  width: 220px;
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

.adopt-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.adopt-button:hover::before {
  left: 100%;
}

.adopt-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(78, 205, 196, 0.3);
}

.adopt-button:active {
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
  .adopt-container {
    padding: 40px 0 60px 0;
  }
  
  .page-title {
    font-size: 32px;
  }
  
  .adopt-wrapper {
    padding: 30px 20px;
  }
  
  .section-title {
    font-size: 20px;
  }
}
</style>