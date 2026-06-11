<template>
  <div class="admin-view">
    <div class="background-animation">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>
    
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title fade-in">管理后台</h1>
        <p class="page-subtitle fade-in">全面管理网站的各项数据和功能</p>
      </div>

      <div class="admin-content">
        <!-- 侧边导航 -->
        <div class="sidebar fade-in">
          <div class="admin-header">
            <div class="admin-logo">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="logo-icon">
                <path d="M12 2a14.5 14.5 0 0 0-10 17.9 14.5 14.5 0 0 0 18 0A14.5 14.5 0 0 0 12 2Z" />
                <path d="M15 9a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
                <path d="M13 15h1a3 3 0 1 0 0-6h-4a3 3 0 1 0 0 6h1" />
              </svg>
              <h3 class="admin-title">宠物领养平台</h3>
            </div>
          </div>

          <div class="nav-menu">
            <div
              v-for="menu in adminMenus"
              :key="menu.value"
              class="nav-item"
              :class="{ active: activeTab === menu.value }"
              @click="activeTab = menu.value"
            >
              <i :class="menu.icon" class="nav-icon"></i>
              <span class="nav-label">{{ menu.label }}</span>
            </div>
          </div>

          <div class="system-info">
            <div class="info-item">
              <div class="info-label">系统状态</div>
              <div class="info-value">
                <span class="status-dot online"></span>
                运行正常
              </div>
            </div>
            <div class="info-item">
              <div class="info-label"> 管理员账户</div>
              <div class="info-value">admin@petadopt.com</div>
            </div>
          </div>
        </div>

        <!-- 主内容区域 -->
        <div class="main-content">
          <!-- 数据统计 -->
          <div v-if="activeTab === 'dashboard'" class="tab-content fade-in">
            <div class="stats-grid">
              <div class="stat-card fade-in">
                <div class="stat-header">
                  <div class="stat-icon primary">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                      <circle cx="9" cy="7" r="4" />
                      <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                      <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                    </svg>
                  </div>
                </div>
                <div class="stat-body">
                  <div class="stat-number">{{ stats.users }}</div>
                  <div class="stat-label">注册用户</div>
                </div>
                <div class="stat-footer">
                  <span class="stat-change positive">+2.5% 较上周</span>
                </div>
              </div>

              <div class="stat-card fade-in">
                <div class="stat-header">
                  <div class="stat-icon success">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M12 2a14.5 14.5 0 0 0-10 17.9 14.5 14.5 0 0 0 18 0A14.5 14.5 0 0 0 12 2Z" />
                      <path d="M15 9a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
                      <path d="M13 15h1a3 3 0 1 0 0-6h-4a3 3 0 1 0 0 6h1" />
                    </svg>
                  </div>
                </div>
                <div class="stat-body">
                  <div class="stat-number">{{ stats.pets }}</div>
                  <div class="stat-label">待领养宠物</div>
                </div>
                <div class="stat-footer">
                  <span class="stat-change positive">+15 本周新增</span>
                </div>
              </div>

              <div class="stat-card fade-in">
                <div class="stat-header">
                  <div class="stat-icon warning">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
                      <polyline points="22 4 12 14.01 9 11.01" />
                    </svg>
                  </div>
                </div>
                <div class="stat-body">
                  <div class="stat-number">{{ stats.adoptions }}</div>
                  <div class="stat-label">成功领养</div>
                </div>
                <div class="stat-footer">
                  <span class="stat-change positive">+5 本周完成</span>
                </div>
              </div>

              <div class="stat-card fade-in">
                <div class="stat-header">
                  <div class="stat-icon secondary">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                      <circle cx="8.5" cy="8.5" r="1.5" />
                      <path d="m21 15-5-5L5 21" />
                    </svg>
                  </div>
                </div>
                <div class="stat-body">
                  <div class="stat-number">{{ stats.photos }}</div>
                  <div class="stat-label">分享照片</div>
                </div>
                <div class="stat-footer">
                  <span class="stat-change positive">+32 最新上传</span>
                </div>
              </div>
            </div>

            <div class="stats-details">
              <div class="card">
                <div class="card-header">
                  <h2 class="card-title">系统概览</h2>
                </div>
                <div class="card-body">
                  <div class="overview-grid">
                    <div class="overview-item">
                      <div class="overview-label">今日访问量</div>
                      <div class="overview-value">{{ stats.todayVisits }}</div>
                    </div>
                    <div class="overview-item">
                      <div class="overview-label">待审核申请</div>
                      <div class="overview-value pending">{{ stats.pendingApplications }}</div>
                    </div>
                    <div class="overview-item">
                      <div class="overview-label">今日新增用户</div>
                      <div class="overview-value positive">{{ stats.newUsersToday }}</div>
                    </div>
                    <div class="overview-item">
                      <div class="overview-label">活跃领养信息</div>
                      <div class="overview-value">{{ stats.activeAdoptions }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 用户管理 -->
          <div v-if="activeTab === 'users'" class="tab-content fade-in">
            <div class="card">
              <div class="card-header">
                <h2 class="card-title">用户管理</h2>
                <div class="card-actions">
                  <el-input placeholder="搜索用户名或邮箱" v-model="userSearch" prefix-icon="Search" class="search-input"></el-input>
                </div>
              </div>
              <div class="card-body">
                <el-table :data="paginatedUsers" border stripe class="admin-table">
                  <el-table-column prop="id" label="ID" min-width="80" />
                  <el-table-column prop="name" label="用户名" min-width="120" />
                  <el-table-column prop="email" label="邮箱" min-width="150" />
                  <el-table-column prop="phone" label="手机号" min-width="120" />
                  <el-table-column prop="register_time" label="注册时间" min-width="150" />
                  <el-table-column prop="status" label="状态" min-width="100">
                    <template #default="{row}">
                      <el-tag :type="row.status === 'active' ? 'success' : 'danger'">{{ row.status === 'active' ? '正常' : '禁用' }}</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" min-width="150" fixed="right">
                    <template #default="{row}">
                      <el-button type="primary" size="small" class="action-btn">编辑</el-button>
                      <el-button type="danger" size="small" class="action-btn" @click="toggleUserStatus(row)">{{ row.status === 'active' ? '禁用' : '启用' }}</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-pagination
                  class="admin-pagination"
                  background
                  @size-change="handleUserSizeChange"
                  @current-change="handleUserCurrentChange"
                  :current-page="userCurrentPage"
                  :page-sizes="[10, 20, 50, 100]"
                  :page-size="userPageSize"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="userTotal"
                ></el-pagination>
              </div>
            </div>
          </div>

          <!-- 宠物管理 -->
          <div v-if="activeTab === 'pets'" class="tab-content fade-in">
            <div class="card">
              <div class="card-header">
                <h2 class="card-title">宠物管理</h2>
                <div class="card-actions">
                  <el-input placeholder="搜索宠物名称" v-model="petSearch" prefix-icon="Search" class="search-input"></el-input>
                </div>
              </div>
              <div class="card-body">
                <el-table :data="paginatedPets" border stripe class="admin-table">
                  <el-table-column prop="id" label="ID" min-width="80" />
                  <el-table-column prop="name" label="宠物名称" min-width="120" />
                  <el-table-column prop="type" label="类型" min-width="100">
                    <template #default="{row}">
                      <el-tag type="primary">{{ row.type === 'dog' ? '狗' : row.type === 'cat' ? '猫' : '其他' }}</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="age" label="年龄" min-width="100" />
                  <el-table-column prop="breed" label="品种" min-width="120" />
                  <el-table-column prop="status" label="状态" min-width="100">
                    <template #default="{row}">
                      <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="submitter" label="上传者" min-width="120" />
                  <el-table-column prop="submit_time" label="上传时间" min-width="150" />
                  <el-table-column label="操作" min-width="180" fixed="right">
                    <template #default="{row}">
                      <el-button type="primary" size="small" class="action-btn" @click="viewPet(row)">查看</el-button>
                      <el-button type="success" size="small" class="action-btn" v-if="row.status === 'pending'" @click="approvePet(row)">通过</el-button>
                      <el-button type="danger" size="small" class="action-btn" @click="rejectPet(row)">拒绝</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-pagination
                  class="admin-pagination"
                  background
                  @size-change="handlePetSizeChange"
                  @current-change="handlePetCurrentChange"
                  :current-page="petCurrentPage"
                  :page-sizes="[10, 20, 50, 100]"
                  :page-size="petPageSize"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="petTotal"
                ></el-pagination>
              </div>
            </div>
          </div>

          <!-- 领养管理 -->
          <div v-if="activeTab === 'adoptions'" class="tab-content fade-in">
            <div class="card">
              <div class="card-header">
                <h2 class="card-title">领养管理</h2>
              </div>
              <div class="card-body">
                <el-table :data="paginatedAdoptions" border stripe class="admin-table">
                  <el-table-column prop="id" label="ID" min-width="80" />
                  <el-table-column prop="user_name" label="申请人" min-width="100" />
                  <el-table-column prop="pet_name" label="宠物名称" min-width="100" />
                  <el-table-column prop="pet_type" label="宠物类型" min-width="80" />
                  <el-table-column prop="application_date" label="申请日期" min-width="140" />
                  <el-table-column prop="status" label="状态" min-width="80">
                    <template #default="{row}">
                      <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="description" label="申请理由" min-width="180" />
                  <el-table-column label="操作" min-width="180" fixed="right">
                    <template #default="{row}">
                      <el-button type="primary" size="small" class="action-btn" @click="viewAdoptionDetail(row)">查看详情</el-button>
                      <el-button type="success" size="small" class="action-btn" v-if="row.status === 'pending'" @click="quickApproveAdoption(row)">通过申请</el-button>
                      <el-button type="danger" size="small" class="action-btn" v-if="row.status === 'pending'" @click="quickRejectAdoption(row)">拒绝申请</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-pagination
                  class="admin-pagination"
                  background
                  @size-change="handleAdoptionSizeChange"
                  @current-change="handleAdoptionCurrentChange"
                  :current-page="adoptionCurrentPage"
                  :page-sizes="[10, 20, 50, 100]"
                  :page-size="adoptionPageSize"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="adoptionTotal"
                ></el-pagination>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 领养申请详情对话框 -->
    <el-dialog
      v-model="adoptionDialogVisible"
      title="领养申请详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentAdoption" class="adoption-detail">
        <div class="detail-section">
          <h4>申请人信息</h4>
          <div class="detail-row">
            <span class="label">用户名：</span>
            <span class="value">{{ currentAdoption.user ? currentAdoption.user.username : '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="label">邮箱：</span>
            <span class="value">{{ currentAdoption.user ? currentAdoption.user.email : '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="label">手机号：</span>
            <span class="value">{{ currentAdoption.user ? currentAdoption.user.phone : '-' }}</span>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>宠物信息</h4>
          <div class="detail-row">
            <span class="label">宠物名称：</span>
            <span class="value">{{ currentAdoption.pet ? currentAdoption.pet.name : '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="label">宠物类型：</span>
            <span class="value">{{ currentAdoption.pet ? currentAdoption.pet.type : '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="label">品种：</span>
            <span class="value">{{ currentAdoption.pet ? currentAdoption.pet.breed : '-' }}</span>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>申请信息</h4>
          <div class="detail-row">
            <span class="label">申请日期：</span>
            <span class="value">{{ currentAdoption.apply_time }}</span>
          </div>
          <div class="detail-row">
            <span class="label">申请状态：</span>
            <el-tag :type="getStatusType(currentAdoption.status)">
              {{ getStatusText(currentAdoption.status) }}
            </el-tag>
          </div>
          <div class="detail-row">
            <span class="label">申请理由：</span>
            <span class="value">{{ currentAdoption.applicant_note || '-' }}</span>
          </div>
        </div>
        
        <div class="detail-section" v-if="currentAdoption.status === 'pending'">
          <h4>审核意见</h4>
          <el-input
            v-model="reviewComment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见（可选）"
          />
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="adoptionDialogVisible = false">取消</el-button>
          <el-button
            v-if="currentAdoption && currentAdoption.status === 'pending'"
            type="success"
            @click="approveAdoption"
          >
            批准申请
          </el-button>
          <el-button
            v-if="currentAdoption && currentAdoption.status === 'pending'"
            type="danger"
            @click="rejectAdoption"
          >
            拒绝申请
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()

// 活跃标签页
const activeTab = ref('dashboard')

// 管理员菜单
const adminMenus = ref([
  { value: 'dashboard', label: '数据概览', icon: 'el-icon-data-board' },
  { value: 'users', label: '用户管理', icon: 'el-icon-user' },
  { value: 'pets', label: '宠物管理', icon: 'el-icon-folder-opened' },
  { value: 'adoptions', label: '领养管理', icon: 'el-icon-document' }
])

// 统计数据
const stats = ref({
  users: 0,
  pets: 0,
  adoptions: 0,
  photos: 0,
  todayVisits: 0,
  pendingApplications: 0,
  newUsersToday: 0,
  activeAdoptions: 0
})

// 用户数据
const users = ref([])

// 宠物数据
const pets = ref([])

// 领养数据
const adoptions = ref([])

// 搜索关键词
const userSearch = ref('')
const petSearch = ref('')

// 分页相关数据
// 用户列表分页
const userCurrentPage = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)

// 宠物列表分页
const petCurrentPage = ref(1)
const petPageSize = ref(10)
const petTotal = ref(0)

// 领养列表分页
const adoptionCurrentPage = ref(1)
const adoptionPageSize = ref(10)
const adoptionTotal = ref(0)

// 加载管理员数据
const loadAdminData = async () => {
  try {
    // 并行获取数据
    const [statsResponse, petsResponse, adoptionsResponse, usersResponse, photosResponse] = await Promise.all([
      api.getStats(), // 获取统计数据
      api.getPets({ sort_by: 'id', sort_order: 'asc', page: 1, page_size: 1000 }),
      api.getAdoptions(),
      api.getUsers({ sort_by: 'id', sort_order: 'asc', page: 1, page_size: 1000 }),
      api.getPhotos()
    ])
    
    // 处理统计数据
    if (statsResponse && statsResponse.success && statsResponse.data) {
      const statsData = statsResponse.data
      stats.value.users = statsData.users.total
      stats.value.pets = statsData.pets.available
      stats.value.adoptions = statsData.adoptions.approved + statsData.adoptions.completed
      stats.value.photos = statsData.photos.total
      stats.value.newUsersToday = statsData.users.today
      stats.value.todayVisits = 0 // 暂时没有实现访问量统计
      stats.value.pendingApplications = statsData.adoptions.pending
      stats.value.activeAdoptions = statsData.adoptions.approved + statsData.adoptions.pending
    }
    
    // 处理宠物数据
    if (petsResponse && petsResponse.pets) {
      pets.value = petsResponse.pets.map(pet => ({
        id: pet.id,
        name: pet.name,
        type: pet.type,
        age: pet.age,
        breed: pet.breed,
        status: pet.status === '待领养' ? 'approved' : pet.status === '已领养' ? 'adopted' : 'pending',
        submitter: pet.owner_id,
        submit_time: pet.created_at
      }))
      petTotal.value = pets.value.length
    }
    
    // 处理领养数据
    if (adoptionsResponse && adoptionsResponse.adoptions && Array.isArray(adoptionsResponse.adoptions)) {
      adoptions.value = adoptionsResponse.adoptions.map(adoption => ({
        id: adoption.id,
        pet_id: adoption.pet_id,
        user_name: adoption.user ? adoption.user.username : adoption.applicant_id,
        pet_name: adoption.pet ? adoption.pet.name : adoption.pet_id,
        pet_type: adoption.pet ? adoption.pet.type : '',
        application_date: adoption.apply_time,
        status: adoption.status,
        description: adoption.applicant_note,
        applicant_note: adoption.applicant_note,
        admin_note: adoption.admin_note,
        user: adoption.user,
        pet: adoption.pet
      }))
      adoptionTotal.value = adoptions.value.length
    }
    
    // 处理用户数据
    if (usersResponse && usersResponse.users) {
      users.value = usersResponse.users.map(user => ({
        id: user.id,
        name: user.username,
        email: user.email,
        phone: user.phone || '-',
        register_time: user.created_at ? user.created_at.split(' ')[0] : '-',
        status: 'active'
      }))
      userTotal.value = users.value.length
    }
    
  } catch (error) {
    console.error('加载管理员数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

// 分页后的用户列表
const paginatedUsers = computed(() => {
  // 先过滤搜索结果
  const filtered = users.value.filter(user => 
    user.name.includes(userSearch.value) || 
    user.email.includes(userSearch.value)
  )
  // 更新总条数
  userTotal.value = filtered.length
  // 再分页
  const start = (userCurrentPage.value - 1) * userPageSize.value
  const end = start + userPageSize.value
  return filtered.slice(start, end)
})

// 分页后的宠物列表
const paginatedPets = computed(() => {
  // 先过滤搜索结果
  const filtered = pets.value.filter(pet => 
    pet.name.includes(petSearch.value)
  )
  // 更新总条数
  petTotal.value = filtered.length
  // 再分页
  const start = (petCurrentPage.value - 1) * petPageSize.value
  const end = start + petPageSize.value
  return filtered.slice(start, end)
})

// 分页后的领养列表
const paginatedAdoptions = computed(() => {
  const start = (adoptionCurrentPage.value - 1) * adoptionPageSize.value
  const end = start + adoptionPageSize.value
  return adoptions.value.slice(start, end)
})

// 组件挂载时加载数据
onMounted(() => {
  loadAdminData()
})

// 分页处理 - 用户管理
const handleUserSizeChange = (val) => {
  userPageSize.value = val
  userCurrentPage.value = 1 // 切换每页大小时重置到第一页
}

const handleUserCurrentChange = (val) => {
  userCurrentPage.value = val
}

// 分页处理 - 宠物管理
const handlePetSizeChange = (val) => {
  petPageSize.value = val
  petCurrentPage.value = 1 // 切换每页大小时重置到第一页
}

const handlePetCurrentChange = (val) => {
  petCurrentPage.value = val
}

// 分页处理 - 领养管理
const handleAdoptionSizeChange = (val) => {
  adoptionPageSize.value = val
  adoptionCurrentPage.value = 1 // 切换每页大小时重置到第一页
}

const handleAdoptionCurrentChange = (val) => {
  adoptionCurrentPage.value = val
}

// 获取状态类型
const getStatusType = (status) => {
  switch (status) {
    case 'pending': return 'warning'
    case 'approved': return 'success'
    case 'rejected': return 'danger'
    case 'adopted': return 'info'
    default: return ''
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'pending': return '待审核'
    case 'approved': return '已通过'
    case 'rejected': return '已拒绝'
    case 'adopted': return '已领养'
    default: return ''
  }
}

// 切换用户状态
const toggleUserStatus = (user) => {
  user.status = user.status === 'active' ? 'disabled' : 'active'
  ElMessage.success(`用户 ${user.name} ${user.status === 'active' ? '启用' : '禁用'}成功`)
}

// 查看宠物
const viewPet = (pet) => {
  router.push(`/pet/${pet.id}`)
}

// 通过宠物审核
const approvePet = (pet) => {
  pet.status = 'approved'
  ElMessage.success(`宠物 ${pet.name} 审核通过`)
}

// 拒绝宠物审核
const rejectPet = (pet) => {
  pet.status = 'rejected'
  ElMessage.success(`宠物 ${pet.name} 已拒绝`)
}

// 领养申请对话框
const adoptionDialogVisible = ref(false)
const currentAdoption = ref(null)
const reviewComment = ref('')
const reviewerId = ref(1) // 管理员ID，实际应该从登录用户信息中获取

// 查看领养申请详情
const viewAdoptionDetail = async (adoption) => {
  try {
    // 获取所有领养申请数据
    const allAdoptions = await api.getAdoptions()
    const adoptionDetail = allAdoptions.adoptions.find(a => a.id === adoption.id)
    
    if (adoptionDetail) {
      currentAdoption.value = adoptionDetail
      reviewComment.value = adoptionDetail.admin_note || ''
      adoptionDialogVisible.value = true
    } else {
      ElMessage.error('未找到领养申请详情')
    }
  } catch (error) {
    console.error('获取领养申请详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 批准领养申请
const approveAdoption = async () => {
  if (!currentAdoption.value) return
  
  try {
    await api.approveAdoption(currentAdoption.value.id, {
      reviewer_id: reviewerId.value,
      review_comment: reviewComment.value
    })
    
    // 更新本地数据
    const index = adoptions.value.findIndex(a => a.id === currentAdoption.value.id)
    if (index !== -1) {
      adoptions.value[index].status = 'approved'
      adoptions.value[index].admin_note = reviewComment.value
    }
    
    // 更新统计数据
    stats.value.pendingApplications = adoptions.value.filter(a => a.status === 'pending').length
    
    adoptionDialogVisible.value = false
    ElMessage.success('领养申请已批准')
    
    // 重新加载数据
    await loadAdminData()
  } catch (error) {
    console.error('批准领养申请失败:', error)
    ElMessage.error('批准失败')
  }
}

// 拒绝领养申请
const rejectAdoption = async () => {
  if (!currentAdoption.value) return
  
  try {
    await api.rejectAdoption(currentAdoption.value.id, {
      reviewer_id: reviewerId.value,
      review_comment: reviewComment.value
    })
    
    // 更新本地数据
    const index = adoptions.value.findIndex(a => a.id === currentAdoption.value.id)
    if (index !== -1) {
      adoptions.value[index].status = 'rejected'
      adoptions.value[index].admin_note = reviewComment.value
    }
    
    // 更新统计数据
    stats.value.pendingApplications = adoptions.value.filter(a => a.status === 'pending').length
    
    adoptionDialogVisible.value = false
    ElMessage.success('领养申请已拒绝')
    
    // 重新加载数据
    await loadAdminData()
  } catch (error) {
    console.error('拒绝领养申请失败:', error)
    ElMessage.error('拒绝失败')
  }
}

// 快速批准领养申请（直接从列表操作）
const quickApproveAdoption = async (adoption) => {
  try {
    await api.approveAdoption(adoption.id, {
      reviewer_id: reviewerId.value,
      review_comment: ''
    })
    
    // 更新本地数据
    const index = adoptions.value.findIndex(a => a.id === adoption.id)
    if (index !== -1) {
      adoptions.value[index].status = 'approved'
    }
    
    // 更新统计数据
    stats.value.pendingApplications = adoptions.value.filter(a => a.status === 'pending').length
    
    ElMessage.success('领养申请已批准')
    
    // 重新加载数据
    await loadAdminData()
  } catch (error) {
    console.error('批准领养申请失败:', error)
    ElMessage.error('批准失败')
  }
}

// 快速拒绝领养申请（直接从列表操作）
const quickRejectAdoption = async (adoption) => {
  try {
    await api.rejectAdoption(adoption.id, {
      reviewer_id: reviewerId.value,
      review_comment: ''
    })
    
    // 更新本地数据
    const index = adoptions.value.findIndex(a => a.id === adoption.id)
    if (index !== -1) {
      adoptions.value[index].status = 'rejected'
    }
    
    // 更新统计数据
    stats.value.pendingApplications = adoptions.value.filter(a => a.status === 'pending').length
    
    ElMessage.success('领养申请已拒绝')
    
    // 重新加载数据
    await loadAdminData()
  } catch (error) {
    console.error('拒绝领养申请失败:', error)
    ElMessage.error('拒绝失败')
  }
}
</script>

<style scoped>
.admin-view {
  background: linear-gradient(135deg, #f0f4f8 0%, #d9e2ec 100%);
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  color: var(--text-color);
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

.container {
  max-width: 1600px;
  margin: 0 auto;
  padding: 40px 20px;
  position: relative;
  z-index: 1;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 36px;
  color: var(--text-color-primary);
  margin-bottom: 12px;
  font-weight: 600;
}

.page-subtitle {
  font-size: 16px;
  color: var(--text-color-secondary);
}

/* 管理内容布局 */
.admin-content {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

/* 侧边栏 */
.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.admin-header {
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid var(--border-color);
}

.admin-logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  color: var(--primary-color);
}

.admin-title {
  font-size: var(--font-size-lg);
  color: var(--text-color-primary);
  font-weight: 600;
}

/* 导航菜单 */
.nav-menu {
  margin-bottom: 32px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  margin-bottom: 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all var(--transition-base);
  color: var(--text-color-secondary);
}

.nav-item:hover {
  background: var(--bg-color-light);
  color: var(--text-color-primary);
}

.nav-item.active {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

.nav-icon {
  font-size: 18px;
}

.nav-label {
  font-weight: 500;
}

/* 系统信息 */
.system-info {
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
}

.system-info .info-item {
  margin-bottom: 16px;
}

.system-info .info-label {
  font-size: 12px;
  color: var(--text-color-secondary);
  margin-bottom: 6px;
}

.system-info .info-value {
  font-size: 14px;
  color: var(--text-color-primary);
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.online {
  background: var(--success-color);
}

/* 主内容区域 */
.main-content {
  flex: 1;
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all var(--transition-base);
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
}

.stat-header {
  margin-bottom: 16px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.primary {
  background: linear-gradient(135deg, var(--primary-color) 0%, #ff9a9e 100%);
}

.stat-icon.success {
  background: linear-gradient(135deg, var(--success-color) 0%, #a8edea 100%);
}

.stat-icon.warning {
  background: linear-gradient(135deg, var(--warning-color) 0%, #ffecd2 100%);
}

.stat-icon.secondary {
  background: linear-gradient(135deg, var(--secondary-color) 0%, #c7ceea 100%);
}

.stat-body {
  margin-bottom: 12px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: var(--text-color-primary);
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: var(--text-color-secondary);
}

.stat-footer {
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
}

.stat-change {
  font-size: 12px;
}

.stat-change.positive {
  color: var(--success-color);
}

.stat-change.negative {
  color: var(--danger-color);
}

/* 卡片样式 */
.card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.02) 0%, rgba(255, 217, 61, 0.02) 100%);
}

.card-title {
  font-size: 20px;
  color: var(--text-color-primary);
  font-weight: 600;
}

.card-actions {
  display: flex;
  gap: 12px;
}

.search-input {
  width: 200px;
}

.card-body {
  padding: 24px;
  overflow-x: auto;
  white-space: nowrap;
}

/* 表格 */
.admin-table {
  margin-bottom: 32px;
  border-radius: 8px;
  overflow: hidden;
  min-width: 800px;
}

.admin-table >>> .el-table {
  background: transparent;
  width: 100%;
}

.admin-table >>> .el-table__header-wrapper {
  background: linear-gradient(135deg, var(--bg-color-light) 0%, rgba(255, 255, 255, 0.5) 100%);
}

/* 概览网格 */
.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.overview-item {
  text-align: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  transition: all var(--transition-base);
}

.overview-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.overview-label {
  font-size: 13px;
  color: var(--text-color-secondary);
  margin-bottom: 8px;
}

.overview-value {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-color-primary);
}

.overview-value.pending {
  color: var(--warning-color);
}

.overview-value.positive {
  color: var(--success-color);
}

/* 表格 */
.admin-table {
  margin-bottom: 32px;
  border-radius: 8px;
  overflow: hidden;
}

.admin-table >>> .el-table {
  background: transparent;
}

.admin-table >>> .el-table__header-wrapper {
  background: linear-gradient(135deg, var(--bg-color-light) 0%, rgba(255, 255, 255, 0.5) 100%);
}

/* 分页 */
.admin-pagination {
  text-align: right;
  margin-top: 32px;
}

/* 操作按钮 */
.action-btn {
  margin: 0 4px;
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .overview-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .admin-content {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .overview-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .search-input {
    width: 100%;
  }

  .page-title {
    font-size: 28px;
  }

  .card-body {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .overview-grid {
    grid-template-columns: 1fr;
  }
}

/* 领养申请详情对话框样式 */
.adoption-detail {
  padding: 8px 0;
}

.detail-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
}

.detail-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.detail-section h4 {
  font-size: 16px;
  color: var(--text-color-primary);
  font-weight: 600;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-section h4::before {
  content: '';
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  border-radius: 2px;
}

.detail-row {
  display: flex;
  margin-bottom: 12px;
  align-items: flex-start;
}

.detail-row .label {
  min-width: 100px;
  font-size: 14px;
  color: var(--text-color-secondary);
  font-weight: 500;
}

.detail-row .value {
  flex: 1;
  font-size: 14px;
  color: var(--text-color-primary);
  line-height: 1.6;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 - 领养详情对话框 */
@media (max-width: 768px) {
  .detail-row {
    flex-direction: column;
    gap: 4px;
  }
  
  .detail-row .label {
    min-width: auto;
  }
}
</style>