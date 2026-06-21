<template>
  <div class="profile-container">
    <!-- 加载状态骨架屏 -->
    <el-skeleton :loading="loading" animated>
      <!-- 个人信息卡片 -->
      <div class="profile-card">
      <div class="profile-header">
        <div class="avatar-wrapper">
          <el-avatar :size="80" :src="user.avatar || defaultAvatar">
            {{ user.nickname?.charAt(0) || 'U' }}
          </el-avatar>
          <el-button
            type="primary"
            size="small"
            class="edit-avatar-btn"
            @click="editProfile"
          >
            更换头像
          </el-button>
        </div>
        <div class="profile-info">
          <h2 class="nickname">{{ user.nickname || '用户' }}</h2>
          <p class="username">@{{ user.username }}</p>
          <div class="user-stats">
            <div class="stat-item clickable" @click="showFollowList('following')">
              <span class="stat-value">{{ user.followingCount || 0 }}</span>
              <span class="stat-label">关注</span>
            </div>
            <div class="stat-item clickable" @click="showFollowList('followers')">
              <span class="stat-value">{{ user.followerCount || 0 }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ user.petCount || 0 }}</span>
              <span class="stat-label">宠物</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ user.photoCount || 0 }}</span>
              <span class="stat-label">照片</span>
            </div>
          </div>
        </div>
      </div>
      <div class="profile-description">
        <p>{{ user.description || '暂无个人简介' }}</p>
        <p v-if="user.phone" class="profile-info-item">
          <span class="info-label">电话号码：</span>{{ user.phone }}
        </p>
        <p v-if="user.area" class="profile-info-item">
          <span class="info-label">所在地区：</span>{{ user.area }}
        </p>
        <div class="profile-actions">
          <el-button
            type="primary"
            size="small"
            class="edit-btn"
            @click="editProfile"
          >
            编辑资料
          </el-button>
          <el-button
            type="danger"
            size="small"
            class="delete-btn"
            @click="showDeleteAccountConfirm"
          >
            注销账号
          </el-button>
        </div>
      </div>
    </div>

    <!-- 宠物列表 -->
    <div class="section">
      <div class="section-header">
        <h3>我的宠物</h3>
        <el-button
          type="primary"
          size="small"
          class="add-pet-btn"
          @click="addPet"
        >
          <Plus /> 添加宠物
        </el-button>
      </div>
      <div class="pets-grid" v-if="user.pets && user.pets.length > 0">
        <div
          v-for="pet in user.pets"
          :key="pet.id"
          class="pet-item"
        >
          <div @click="viewPetDetail(pet.id)" class="pet-item-content">
            <div class="pet-image-wrapper">
              <el-image
                :src="pet.avatar || defaultPetAvatar"
                fit="cover"
                class="pet-image"
              />
            </div>
            <div class="pet-info">
              <h4 class="pet-name">{{ pet.name }}</h4>
              <p class="pet-breed">{{ pet.breed }}</p>
            </div>
          </div>
          <el-button
            type="danger"
            size="small"
            class="delete-pet-btn"
            @click.stop="showDeleteConfirm(pet.id)"
          >
            删除
          </el-button>
        </div>
      </div>
      <div v-else class="empty-state">
        <p>暂无宠物，点击"添加宠物"开始</p>
      </div>
    </div>

    <!-- 照片墙 -->
    <div class="section">
      <div class="section-header">
        <h3>我的照片</h3>
        <el-button
          type="primary"
          size="small"
          class="upload-photo-btn"
          @click="uploadPhoto"
        >
          <Plus /> 上传照片
        </el-button>
      </div>
      <div class="photos-grid" v-if="user.photos && user.photos.length > 0">
        <div
          v-for="photo in user.photos"
          :key="photo.id"
          class="photo-item"
          @click="viewPhotoDetail(photo)"
        >
          <div class="photo-image-wrapper">
            <el-image
              :src="photo.url"
              fit="cover"
              class="photo-image"
            />
            <div class="photo-overlay">
              <el-button
                size="small"
                type="danger"
                class="overlay-btn"
                icon="Delete"
                @click.stop="deletePhoto(photo.id)"
              >
                删除
              </el-button>
            </div>
          </div>
          <div class="photo-caption">{{ photo.caption || '无描述' }}</div>
        </div>
      </div>
      <div v-else class="empty-state">
        <p>暂无照片，点击"上传照片"开始</p>
      </div>
    </div>

    <!-- 我的收藏 -->
    <div class="section">
      <div class="section-header">
        <h3>我的收藏</h3>
      </div>
      <div class="pets-grid" v-if="myFavorites && myFavorites.length > 0">
        <div
          v-for="pet in myFavorites"
          :key="pet.id"
          class="pet-item"
        >
          <div class="pet-item-content" @click="viewPetDetail(pet.id)">
            <div class="pet-image-wrapper">
              <el-image
                :src="pet.mainPhoto?.imageUrl || defaultPetAvatar"
                fit="cover"
                class="pet-image"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="pet-info">
              <div class="pet-name">{{ pet.name }}</div>
              <div class="pet-breed">{{ pet.breed || '未知品种' }}</div>
            </div>
          </div>
          <div class="pet-actions">
            <el-button size="small" type="danger" plain @click.stop="removeFromFavorites(pet.id)">
              取消收藏
            </el-button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <p>暂无收藏，去收藏一些可爱的宠物吧</p>
      </div>
    </div>

    <!-- 申请记录 -->
    <div class="section">
      <div class="section-header">
        <h3>申请记录</h3>
      </div>
      <div class="applications-list" v-if="user.applications && user.applications.length > 0">
        <div
          v-for="app in user.applications"
          :key="app.id"
          class="application-item"
        >
          <div class="app-info">
            <img
              :src="app.pet.avatar || defaultPetAvatar"
              alt="宠物头像"
              class="app-pet-image"
            />
            <div class="app-details">
              <div class="app-pet-name">{{ app.pet.name }}</div>
              <div class="app-date">申请日期：{{ formatDate(app.createdAt) }}</div>
              <div class="app-description">{{ app.description }}</div>
            </div>
          </div>
          <div class="app-status">
            <el-tag
              :type="getApplicationStatusType(app.status)"
              size="small"
            >
              {{ getApplicationStatusText(app.status) }}
            </el-tag>
          </div>
          <div class="app-actions">
            <el-button
              type="primary"
              size="small"
              @click="editAdoption(app)"
            >
              <i class="el-icon-edit"></i> 编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="deleteAdoption(app.id)"
            >
              <i class="el-icon-delete"></i> 删除
            </el-button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <p>暂无申请记录</p>
      </div>
    </div>

    <!-- 评论记录 -->
    <div class="section">
      <div class="section-header">
        <h3>我的评论</h3>
      </div>
      <div class="comments-list" v-if="user.comments && user.comments.length > 0">
        <div
          v-for="comment in user.comments"
          :key="comment.id"
          class="comment-item"
        >
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-meta">
            <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
            <el-button
              size="small"
              type="text"
              class="reply-btn"
              @click="replyToComment(comment)"
            >
              回复
            </el-button>
            <el-button
              size="small"
              type="text"
              class="delete-comment-btn"
              @click="deleteComment(comment.id)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <p>暂无评论记录</p>
      </div>
    </div>
  </el-skeleton>
  </div>

  <!-- 编辑个人资料弹窗 -->
  <el-dialog
    v-model="dialogVisible.editProfile"
    title="编辑个人资料"
    width="500px"
    destroy-on-close
  >
    <el-form :model="editForm" :rules="editFormRules" label-width="80px" size="default" ref="editFormRef">
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="头像">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :auto-upload="false"
          :on-change="handleAvatarChange"
          :before-upload="beforeAvatarUpload"
        >
          <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar" @error="editForm.avatar = ''" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item label="简介" prop="description">
        <el-input
          v-model="editForm.description"
          type="textarea"
          rows="3"
          placeholder="请输入个人简介"
        />
      </el-form-item>
      <el-form-item label="电话号码" prop="phone">
        <el-input v-model="editForm.phone" placeholder="请输入电话号码" />
      </el-form-item>
      <el-form-item label="所在地区" prop="area">
        <el-input v-model="editForm.area" placeholder="请输入所在地区" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible.editProfile = false">取消</el-button>
        <el-button type="primary" @click="saveProfile">保存</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 添加宠物弹窗 -->
  <el-dialog
    v-model="dialogVisible.addPet"
    title="添加宠物"
    width="600px"
    destroy-on-close
  >
    <el-form :model="addPetForm" label-width="100px" size="default">
      <el-form-item label="宠物名字">
        <el-input v-model="addPetForm.name" placeholder="请输入宠物名字" />
      </el-form-item>
      <el-form-item label="宠物类型">
        <el-select v-model="addPetForm.type" placeholder="请选择宠物类型">
          <el-option label="猫咪" value="cat" />
          <el-option label="狗狗" value="dog" />
          <el-option label="鸟类" value="bird" />
          <el-option label="兔兔" value="rabbit" />
          <el-option label="其他" value="other" />
        </el-select>
      </el-form-item>
      <el-form-item label="宠物品种">
        <el-input v-model="addPetForm.breed" placeholder="请输入宠物品种" />
      </el-form-item>
      <el-form-item label="宠物年龄">
        <el-input-number
          v-model="addPetForm.age"
          :min="0"
          :max="20"
          placeholder="请输入宠物年龄"
        />
      </el-form-item>
      <el-form-item label="宠物性别">
        <el-radio-group v-model="addPetForm.gender">
          <el-radio label="male">公</el-radio>
          <el-radio label="female">母</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="宠物颜色">
        <el-input v-model="addPetForm.color" placeholder="请输入宠物颜色" />
      </el-form-item>
      <el-form-item label="宠物体重">
        <el-input-number
          v-model="addPetForm.weight"
          :min="0"
          :max="200"
          :step="0.1"
          placeholder="请输入宠物体重（kg）"
        />
      </el-form-item>
      <el-form-item label="所在位置">
        <el-input v-model="addPetForm.location" placeholder="请输入宠物所在位置" />
      </el-form-item>
      <el-form-item label="宠物描述">
        <el-input
          v-model="addPetForm.description"
          type="textarea"
          rows="3"
          placeholder="请输入宠物描述"
        />
      </el-form-item>
      <el-form-item label="健康信息">
        <el-input
          v-model="addPetForm.health_info"
          type="textarea"
          rows="2"
          placeholder="请输入宠物健康信息"
        />
      </el-form-item>
      <el-form-item label="绝育状态">
        <el-radio-group v-model="addPetForm.neutered">
          <el-radio :label="true">已绝育</el-radio>
          <el-radio :label="false">未绝育</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="疫苗接种">
        <el-radio-group v-model="addPetForm.vaccinated">
          <el-radio :label="true">已接种</el-radio>
          <el-radio :label="false">未接种</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="宠物状态">
        <el-radio-group v-model="addPetForm.status">
          <el-radio label="adoption">可领养</el-radio>
          <el-radio label="share">仅分享</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="宠物头像">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :auto-upload="false"
          :on-change="handlePetAvatarChange"
          :before-upload="beforeAvatarUpload"
        >
          <img v-if="addPetForm.avatar" :src="addPetForm.avatar" class="avatar" @error="addPetForm.avatar = ''" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible.addPet = false">取消</el-button>
        <el-button type="primary" @click="savePet">保存</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 照片详情弹窗（使用Element Plus Dialog替代自定义模态框） -->
  <el-dialog
    v-model="dialogVisible.photoDetail"
    :title="currentPhoto.caption || '照片详情'"
    width="800px"
    destroy-on-close
  >
    <div class="photo-detail-content">
      <el-image
        :src="currentPhoto.url"
        fit="contain"
        class="detail-photo-image"
        :preview-src-list="[currentPhoto.url]"
      />
      <div class="photo-meta">
        <div class="meta-item">
          <span class="meta-label">上传时间：</span>
          <span class="meta-value">{{ formatDate(currentPhoto.createdAt) }}</span>
        </div>
        <div class="meta-item">
          <span class="meta-label">点赞数：</span>
          <span class="meta-value">{{ currentPhoto.likes || 0 }}</span>
        </div>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible.photoDetail = false">关闭</el-button>
        <el-button 
          type="primary" 
          @click="likePhoto(currentPhoto.id)"
        >
          {{ isLiked ? '取消点赞' : '点赞' }}
        </el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 删除宠物确认对话框 -->
  <el-dialog
    v-model="dialogVisible.deletePet"
    title="确认删除"
    width="400px"
    destroy-on-close
  >
    <p>确定要删除该宠物吗？此操作不可恢复。</p>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible.deletePet = false">取消</el-button>
        <el-button type="danger" @click="confirmDeletePet">删除</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 注销账号确认对话框 -->
  <el-dialog
    v-model="dialogVisible.deleteAccount"
    title="确认注销账号"
    width="400px"
    destroy-on-close
  >
    <p>确定要注销您的账号吗？此操作不可恢复，所有与您相关的数据都将被永久删除。</p>
    <p style="color: #f56c6c; margin-top: 12px;">请输入您的密码以确认：</p>
    <el-input
      v-model="deletePassword"
      type="password"
      placeholder="请输入密码"
      style="margin-top: 8px;"
    />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible.deleteAccount = false">取消</el-button>
        <el-button type="danger" @click="deleteAccount">确认注销</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 编辑领养申请对话框 -->
  <el-dialog
    v-model="dialogVisible.editAdoption"
    title="编辑领养申请"
    width="500px"
    destroy-on-close
  >
    <el-form :model="editAdoptionForm" label-width="80px" size="default">
      <el-form-item label="宠物名称">
        <el-input v-model="currentAdoption.pet.name" disabled />
      </el-form-item>
      <el-form-item label="申请理由" required>
        <el-input
          v-model="editAdoptionForm.description"
          type="textarea"
          rows="4"
          placeholder="请详细说明您的领养理由、养宠经验、居住环境等信息，这将有助于我们评估您是否适合领养该宠物"
        />
      </el-form-item>
      <el-form-item label="联系电话">
        <el-input
          v-model="editAdoptionForm.phone"
          placeholder="请输入您的联系电话"
        />
      </el-form-item>
      <el-form-item label="居住地址">
        <el-input
          v-model="editAdoptionForm.address"
          placeholder="请输入您的居住地址"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible.editAdoption = false">取消</el-button>
        <el-button type="primary" @click="saveAdoption">保存</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 删除领养申请确认对话框 -->
  <el-dialog
    v-model="dialogVisible.deleteAdoption"
    title="确认删除"
    width="400px"
    destroy-on-close
  >
    <p>确定要删除该领养申请吗？此操作不可恢复。</p>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible.deleteAdoption = false">取消</el-button>
        <el-button type="danger" @click="confirmDeleteAdoption">删除</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 关注/粉丝列表弹窗 -->
  <el-dialog
    v-model="dialogVisible.followList"
    :title="followListType === 'following' ? '我的关注' : '我的粉丝'"
    width="500px"
    class="follow-list-dialog"
  >
    <div v-if="followList.length === 0" class="empty-follow">
      <p>{{ followListType === 'following' ? '还没有关注任何人' : '还没有粉丝' }}</p>
    </div>
    <div v-else class="follow-list">
      <div
        v-for="item in followList"
        :key="item.id"
        class="follow-item"
      >
        <img
          :src="(followListType === 'following' ? item.followingAvatar : item.followerAvatar) || defaultAvatar"
          class="follow-avatar"
          alt="头像"
        />
        <div class="follow-info">
          <div class="follow-username">
            {{ followListType === 'following' ? item.followingUsername : item.followerUsername }}
          </div>
          <div class="follow-time">{{ formatDate(item.createdAt) }}</div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, unref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore, usePetsStore } from '@/stores'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()
const petsStore = usePetsStore()

// 从userStore获取用户信息
const user = computed(() => userStore.user || {
  id: 1,
  username: 'user123',
  nickname: '用户123',
  avatar: '',
  description: '',
  phone: '',
  area: '',
  pets: [],
  photos: [],
  applications: [],
  comments: [],
  followingCount: 0,
  followerCount: 0,
  petCount: 0,
  photoCount: 0
})

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const defaultPetAvatar = 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png'

// 弹窗状态管理
const dialogVisible = ref({
  editProfile: false,
  addPet: false,
  photoDetail: false,
  deletePet: false,
  deleteAccount: false,
  editAdoption: false,
  deleteAdoption: false,
  followList: false
})

// 关注/粉丝列表
const followListType = ref('following') // 'following' 或 'followers'
const followList = ref([])

// 要删除的宠物ID
const petToDeleteId = ref(null)
// 要编辑的领养申请
const currentAdoption = ref(null)
// 要删除的领养申请ID
const adoptionToDeleteId = ref(null)

// 表单数据
const editForm = ref({
  nickname: '',
  avatar: '',
  description: '',
  phone: '',
  area: ''
})

// 编辑资料表单验证规则
const editFormRules = ref({
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '个人简介不能超过 200 个字符', trigger: 'blur' }
  ],
  phone: [
    { max: 15, message: '电话号码不能超过 15 个字符', trigger: 'blur' }
  ],
  area: [
    { max: 50, message: '所在地区不能超过 50 个字符', trigger: 'blur' }
  ]
})

const addPetForm = ref({
  name: '',
  type: '',
  breed: '',
  age: 0,
  gender: 'male',
  avatar: '',
  description: '',
  health_info: '',
  location: '',
  weight: 0,
  color: '',
  neutered: false,
  vaccinated: false,
  status: 'adoption' // 'adoption'表示可领养，'share'表示仅分享
})

// 当前查看的照片
const currentPhoto = ref({})
const isLiked = ref(false)

// 加载状态
const loading = ref(false)

// 表单引用
const editFormRef = ref(null)

// 注销账号密码
const deletePassword = ref('')

// 收藏列表 - 从共享 store 获取
const myFavorites = computed(() => petsStore.myFavorites)

const editAdoptionForm = ref({
  description: '',
  phone: '',
  address: ''
})

// 初始化
onMounted(async () => {
  console.log('ProfileView onMounted: userStore.user =', JSON.stringify(userStore.user))
  console.log('ProfileView onMounted: localStorage.user =', localStorage.getItem('user'))
  
  // 检查userStore.user是否存在以及是否有id字段
  console.log('ProfileView onMounted: userStore.user存在吗？', !!userStore.user)
  console.log('ProfileView onMounted: userStore.user.id =', userStore.user?.id)
  
  // 如果用户未登录，跳转到登录页
  if (!userStore.user) {
    console.log('ProfileView: 用户未登录，跳转到登录页')
    router.push('/login')
    return
  }
  
  // 如果用户没有id字段，尝试从本地存储重新加载
  if (!userStore.user.id) {
    console.log('ProfileView: 用户没有id字段，尝试从本地存储重新加载')
    const localUser = localStorage.getItem('user')
    if (localUser) {
      try {
        const parsedUser = JSON.parse(localUser)
        console.log('ProfileView: 从本地存储加载的用户信息:', parsedUser)
        if (parsedUser.id) {
          userStore.setUser(parsedUser)
          console.log('ProfileView: 已更新userStore.user:', userStore.user)
        } else {
          // 如果本地存储也没有id字段，尝试重新登录
          console.log('ProfileView: 本地存储的用户信息也没有id字段，尝试重新登录')
          router.push('/login')
          return
        }
      } catch (e) {
        console.error('ProfileView: 解析本地存储用户信息失败:', e)
        router.push('/login')
        return
      }
    } else {
      // 如果本地存储没有用户信息，跳转到登录页
      console.log('ProfileView: 本地存储没有用户信息，跳转到登录页')
      router.push('/login')
      return
    }
  }
  
  // 加载用户完整信息
  console.log('ProfileView: 开始加载用户完整信息')
  await loadUserDetail()
  console.log('ProfileView: 用户完整信息加载完成，userStore.user =', JSON.stringify(userStore.user))
})

// 加载用户完整信息
async function loadUserDetail() {
  loading.value = true
  try {
    // 检查用户ID是否存在
    if (!userStore.user?.id) {
      throw new Error('用户ID不存在')
    }
    
    const result = await api.getUserById(userStore.user.id)
    const response = result.data || result

    if (!response) {
      throw new Error('API返回的数据为空')
    }

    const completeUser = {
      ...userStore.user,
      ...response,
      id: userStore.user.id,
      description: response.description || userStore.user.description || '',
      phone: response.phone || userStore.user.phone || '',
      area: response.area || userStore.user.area || '',
      pets: response.pets ? response.pets.map(pet => ({
        ...pet,
        avatar: pet.main_photo?.image_url || pet.photos?.[0]?.image_url || ''
      })) : [],
      photos: response.photos ? response.photos.map(photo => ({
        ...photo,
        url: photo.image_url
      })) : [],
      applications: response.applications || [],
      comments: response.comments || [],
      followingCount: response.followingCount || 0,
      followerCount: response.followerCount || 0,
      petCount: response.petCount || 0,
      photoCount: response.photoCount || 0
    }
    userStore.setUser(completeUser)
    
    // 获取收藏列表
    try {
      const favResult = await api.getMyFavorites()
      petsStore.setMyFavorites(favResult.data || []) // 同步到共享 store
    } catch (e) {
      console.error('获取收藏列表失败:', e)
      petsStore.setMyFavorites([])
    }
  } catch (error) {
    let errorMessage = '加载用户详细信息失败，使用本地存储的用户信息'
    
    if (error.response) {
      // 服务器返回了错误状态码
      const status = error.response.status
      if (status === 404) {
        errorMessage = '用户信息不存在'
      } else if (status === 500) {
        errorMessage = '服务器错误，请稍后再试'
      } else if (status === 401) {
        errorMessage = '登录已过期，请重新登录'
        // 跳转到登录页
        setTimeout(() => router.push('/login'), 1500)
      } else {
        errorMessage = `服务器错误 (${status})`
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      errorMessage = '网络错误，请检查您的网络连接'
    } else {
      // 其他错误
      errorMessage = error.message || '加载失败'
    }
    
    console.error('加载用户信息失败:', error)
    ElMessage.error(errorMessage)
    
    // 确保本地存储的用户信息包含所有必要字段
    if (userStore.user) {
      const completeUser = {
        ...userStore.user,
        pets: userStore.user.pets || [],
        photos: userStore.user.photos || [],
        applications: userStore.user.applications || [],
        comments: userStore.user.comments || [],
        followingCount: userStore.user.followingCount || 0,
        followerCount: userStore.user.followerCount || 0,
        petCount: userStore.user.petCount || 0,
        photoCount: userStore.user.photoCount || 0
      }
      userStore.setUser(completeUser)
    }
  } finally {
    loading.value = false
  }
}

// 查看宠物详情
function viewPetDetail(petId) {
  router.push(`/pet/${petId}`)
}

// 编辑个人资料
function editProfile() {
  // 重置表单数据
  editForm.value = {
    nickname: user.value.nickname,
    avatar: user.value.avatar,
    description: user.value.description,
    phone: user.value.phone,
    area: user.value.area
  }
  dialogVisible.value.editProfile = true
}

// 保存个人资料
async function saveProfile() {
  // 表单验证
  const formRef = unref(editFormRef)
  if (!formRef) return
  
  try {
    await formRef.validate()
    
    console.log('发送到服务器的用户数据:', JSON.stringify(editForm.value))
    const response = await api.updateUser(userStore.user.id, editForm.value)
    console.log('服务器返回的用户数据:', JSON.stringify(response))
    // 确保更新后的用户对象包含所有必要字段，特别是id字段
    // 确保本地编辑的字段都被保留，即使服务器不返回这些字段
    const updatedUser = {
      ...userStore.user, // 基础用户数据
      ...editForm.value, // 本地编辑的数据（覆盖服务器返回的可能不完整的数据）
      ...response, // 服务器返回的数据（可能包含其他更新的信息）
      id: userStore.user.id // 明确保留id字段，防止服务器不返回
    }
    userStore.setUser(updatedUser)
    dialogVisible.value.editProfile = false
    ElMessage.success('个人资料更新成功')
  } catch (error) {
    // 如果是表单验证错误，不需要额外处理
    if (error === false) return
    
    let errorMessage = '更新个人资料失败'
    
    if (error.response) {
      const status = error.response.status
      if (status === 400) {
        errorMessage = '提交的数据格式错误'
      } else if (status === 401) {
        errorMessage = '登录已过期，请重新登录'
        setTimeout(() => router.push('/login'), 1500)
      } else if (status === 500) {
        errorMessage = '服务器错误，请稍后再试'
      } else {
        errorMessage = `更新失败 (${status})`
      }
    } else if (error.request) {
      errorMessage = '网络错误，请检查您的网络连接'
    }
    
    ElMessage.error(errorMessage)
    console.error('更新个人资料失败:', error)
  }
}

// 头像上传处理
async function handleAvatarChange(file) {
  try {
    // 先进行格式验证
    if (!beforeAvatarUpload(file.raw)) {
      return
    }
    
    // 创建FormData对象
    const formData = new FormData()
    formData.append('file', file.raw)
    // 添加用户ID
    formData.append('user_id', userStore.user.id)
    
    // 上传头像到服务器
    const response = await api.uploadAvatar(formData)
    
    // 设置表单的头像URL - 只使用服务器返回的URL
    if (response && response.data && response.data.image_url) {
      editForm.value.avatar = response.data.image_url
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.warning('头像上传失败，请重试')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  }
}

// 头像上传前验证
function beforeAvatarUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传头像只能是图片格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像大小不能超过 2MB!')
  }
  return isImage && isLt2M
}

// 添加宠物
function addPet() {
  // 重置表单数据
  addPetForm.value = {
    name: '',
    type: '',
    breed: '',
    age: 0,
    gender: 'male',
    avatar: '',
    description: '',
    health_info: '',
    location: '',
    weight: 0,
    color: '',
    neutered: false,
    vaccinated: false,
    status: '待领养' // '待领养'表示可领养，'share'表示仅分享
  }
  dialogVisible.value.addPet = true
}

// 保存宠物信息
async function savePet() {
  try {
    // 转换数据格式以匹配后端要求
    const petData = {
      name: addPetForm.value.name,
      type: addPetForm.value.type,
      breed: addPetForm.value.breed,
      age: addPetForm.value.age || 0,
      gender: addPetForm.value.gender,
      description: addPetForm.value.description,
      healthInfo: addPetForm.value.health_info,
      location: addPetForm.value.location,
      weight: addPetForm.value.weight || 0,
      color: addPetForm.value.color,
      neutered: addPetForm.value.neutered,
      vaccinated: addPetForm.value.vaccinated,
      status: addPetForm.value.status,
      ownerId: userStore.user.id,
      photos: addPetForm.value.avatar ? [
        {
          imageUrl: addPetForm.value.avatar,
          caption: '',
          isMain: true
        }
      ] : []
    }
    
    const response = await api.createPet(petData)
    // 更新用户的宠物列表
    const updatedUser = {
      ...user.value,
      pets: [...(user.value.pets || []), response.data],
      petCount: (user.value.petCount || 0) + 1
    }
    userStore.setUser(updatedUser)
    dialogVisible.value.addPet = false
    ElMessage.success('宠物添加成功')
  } catch (error) {
    let errorMessage = '添加宠物失败'
    
    if (error.response) {
      const status = error.response.status
      if (status === 400) {
        errorMessage = error.response.data?.error || '提交的数据格式错误'
      } else if (status === 401) {
        errorMessage = '登录已过期，请重新登录'
        setTimeout(() => router.push('/login'), 1500)
      } else if (status === 500) {
        errorMessage = '服务器错误，请稍后再试'
      } else {
        errorMessage = `添加失败 (${status})`
      }
    } else if (error.request) {
      errorMessage = '网络错误，请检查您的网络连接'
    }
    
    ElMessage.error(errorMessage)
    console.error('添加宠物失败:', error)
  }
}

// 宠物头像上传处理
async function handlePetAvatarChange(file) {
  try {
    // 先进行格式验证
    if (!beforeAvatarUpload(file.raw)) {
      return
    }
    
    // 创建FormData对象
    const formData = new FormData()
    formData.append('file', file.raw)
    formData.append('user_id', userStore.user.id) // 添加用户ID
    
    // 上传头像到服务器
    const response = await api.uploadPetAvatar(formData)
    
    // 设置表单的头像URL - 只使用服务器返回的URL
    if (response && response.data && response.data.image_url) {
      addPetForm.value.avatar = response.data.image_url
      ElMessage.success('宠物头像上传成功')
    } else {
      ElMessage.warning('头像上传失败，请重试')
    }
  } catch (error) {
    console.error('宠物头像上传失败:', error)
    ElMessage.error('宠物头像上传失败')
  }
}

// 上传照片
function uploadPhoto() {
  router.push('/upload')
}

// 查看照片详情
function viewPhotoDetail(photo) {
  currentPhoto.value = photo
  isLiked.value = photo.isLiked
  dialogVisible.value.photoDetail = true
}

// 显示删除宠物确认对话框
function showDeleteConfirm(petId) {
  petToDeleteId.value = petId
  dialogVisible.value.deletePet = true
}

// 确认删除宠物
async function confirmDeletePet() {
  try {
    if (!petToDeleteId.value) return
    
    await api.deletePet(petToDeleteId.value)
    
    // 更新本地用户数据
    if (userStore.user && userStore.user.pets) {
      userStore.user.pets = userStore.user.pets.filter(pet => pet.id !== petToDeleteId.value)
    }
    
    ElMessage.success('宠物删除成功')
    dialogVisible.value.deletePet = false
    petToDeleteId.value = null
  } catch (error) {
    console.error('删除宠物失败:', error)
    ElMessage.error('删除宠物失败，请稍后重试')
  }
}

// 编辑领养申请
function editAdoption(app) {
  currentAdoption.value = app
  editAdoptionForm.value = {
    description: app.description || '',
    phone: app.user?.phone || '',
    address: app.user?.address || ''
  }
  dialogVisible.value.editAdoption = true
}

// 取消收藏宠物
async function removeFromFavorites(petId) {
  try {
    await api.unfavoritePet(petId, userStore.user.id)
    petsStore.removeFavorite(petId)
    ElMessage.success('已取消收藏')
  } catch (error) {
    console.error('取消收藏失败:', error)
    ElMessage.error('取消收藏失败')
  }
}

// 保存领养申请
async function saveAdoption() {
  try {
    if (!currentAdoption.value) return
    
    const response = await api.updateAdoption(currentAdoption.value.id, editAdoptionForm.value)
    
    // 更新本地用户数据
    if (userStore.user && userStore.user.applications) {
      const index = userStore.user.applications.findIndex(app => app.id === currentAdoption.value.id)
      if (index !== -1) {
        userStore.user.applications[index] = {
          ...userStore.user.applications[index],
          ...response
        }
      }
    }
    
    ElMessage.success('领养申请更新成功')
    dialogVisible.value.editAdoption = false
    currentAdoption.value = null
  } catch (error) {
    console.error('更新领养申请失败:', error)
    ElMessage.error('更新领养申请失败，请稍后重试')
  }
}

// 删除领养申请
function deleteAdoption(appId) {
  adoptionToDeleteId.value = appId
  dialogVisible.value.deleteAdoption = true
}

// 确认删除领养申请
async function confirmDeleteAdoption() {
  try {
    if (!adoptionToDeleteId.value) return
    
    // 使用取消领养的API，而不是直接删除
    await api.cancelAdoption(adoptionToDeleteId.value)
    
    // 更新本地用户数据
    if (userStore.user && userStore.user.applications) {
      userStore.user.applications = userStore.user.applications.filter(app => app.id !== adoptionToDeleteId.value)
    }
    
    ElMessage.success('领养申请已取消')
    dialogVisible.value.deleteAdoption = false
    adoptionToDeleteId.value = null
  } catch (error) {
    console.error('取消领养申请失败:', error)
    ElMessage.error('取消领养申请失败，请稍后重试')
  }
}

// 删除照片
async function deletePhoto(photoId) {
  try {
    await ElMessageBox.confirm('确定要删除这张照片吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await api.deletePhoto(photoId)
    // 更新用户的照片列表
    const updatedUser = {
      ...user.value,
      photos: (user.value.photos || []).filter(p => p.id !== photoId),
      photoCount: Math.max(0, (user.value.photoCount || 0) - 1)
    }
    userStore.setUser(updatedUser)
    ElMessage.success('照片删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除照片失败')
      console.error('删除照片失败:', error)
    }
  }
}

// 点赞照片
async function likePhoto(photoId) {
  try {
    if (isLiked.value) {
      // 取消点赞
      await api.unlikePhoto(photoId)
      currentPhoto.value.likes--
    } else {
      // 点赞
      await api.likePhoto(photoId)
      currentPhoto.value.likes++
    }
    isLiked.value = !isLiked.value
    
    // 更新用户照片列表中的点赞状态
    const updatedPhotos = user.value.photos.map(p => {
      if (p.id === photoId) {
        return {
          ...p,
          likes: currentPhoto.value.likes,
          isLiked: isLiked.value
        }
      }
      return p
    })
    
    // 更新用户信息
    userStore.setUser({
      ...user.value,
      photos: updatedPhotos
    })
    
    ElMessage.success(isLiked.value ? '点赞成功' : '取消点赞成功')
  } catch (error) {
    // 还原状态
    if (isLiked.value) {
      currentPhoto.value.likes++
    } else {
      currentPhoto.value.likes--
    }
    isLiked.value = !isLiked.value
    
    let errorMessage = isLiked.value ? '点赞失败' : '取消点赞失败'
    if (error.response?.status === 401) {
      errorMessage = '登录已过期，请重新登录'
      setTimeout(() => router.push('/login'), 1500)
    }
    
    ElMessage.error(errorMessage)
    console.error('点赞操作失败:', error)
  }
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

// 获取申请状态类型
function getApplicationStatusType(status) {
  const statusMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取申请状态文本
function getApplicationStatusText(status) {
  const statusMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return statusMap[status] || '未知状态'
}

// 回复评论
function replyToComment(comment) {
  ElMessage.info('回复功能开发中...')
}

// 删除评论
async function deleteComment(commentId) {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 调用API删除评论
    await api.deleteComment(commentId)
    
    // 更新用户的评论列表
    const updatedUser = {
      ...user.value,
      comments: (user.value.comments || []).filter(c => c.id !== commentId)
    }
    userStore.setUser(updatedUser)
    ElMessage.success('评论删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      let errorMessage = '删除评论失败'
      if (error.response?.status === 401) {
        errorMessage = '登录已过期，请重新登录'
        setTimeout(() => router.push('/login'), 1500)
      } else if (error.response?.status === 404) {
        errorMessage = '评论不存在'
      }
      
      ElMessage.error(errorMessage)
      console.error('删除评论失败:', error)
    }
  }
}

// 显示注销账号确认对话框
function showDeleteAccountConfirm() {
  dialogVisible.value.deleteAccount = true
}

// 注销账号
async function deleteAccount() {
  try {
    await api.deleteAccount(deletePassword.value)
    
    // 清除用户信息
    userStore.logout()
    
    // 跳转到登录页
    router.push('/login')
    
    ElMessage.success('账号注销成功')
  } catch (error) {
    let errorMessage = '账号注销失败'
    if (error.response?.status === 401) {
      errorMessage = '登录已过期，请重新登录'
      setTimeout(() => router.push('/login'), 1500)
    } else if (error.response?.status === 403) {
      errorMessage = '密码错误，请重试'
    }
    
    ElMessage.error(errorMessage)
    console.error('账号注销失败:', error)
  } finally {
    dialogVisible.value.deleteAccount = false
    deletePassword.value = ''
  }
}

// 显示关注/粉丝列表
async function showFollowList(type) {
  if (!userStore.user || !userStore.user.id) return
  followListType.value = type
  dialogVisible.value.followList = true
  try {
    const response = type === 'following'
      ? await api.getFollowingList(userStore.user.id)
      : await api.getFollowerList(userStore.user.id)
    if (response && response.data && response.data.data) {
      followList.value = response.data.data
    } else if (response && response.data) {
      followList.value = response.data
    } else {
      followList.value = []
    }
  } catch (error) {
    console.error('获取' + (type === 'following' ? '关注' : '粉丝') + '列表失败:', error)
    ElMessage.error('获取列表失败')
    followList.value = []
  }
}
</script>

<style scoped>
/* 主容器样式 */
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

/* 优化整体页面的背景色和字体 */
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  color: #333;
  background-color: #f8f9fa;
}

/* 个人资料卡片 */
.profile-card {
  background-color: #fff;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s ease;
}

.profile-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.avatar-wrapper {
  position: relative;
  margin-right: 32px;
}

.edit-avatar-btn {
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(255, 255, 255, 0.9);
  color: #409eff;
  border: 1px solid #409eff;
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 13px;
  cursor: pointer;
  opacity: 0;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.edit-avatar-btn:hover {
  background-color: #409eff;
  color: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.avatar-wrapper:hover .edit-avatar-btn {
  opacity: 1;
}

.profile-info {
  flex: 1;
  min-width: 0;
}

.nickname {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.username {
  font-size: 15px;
  color: #7f8c8d;
  margin: 0 0 20px 0;
}

.user-stats {
  display: flex;
  gap: 40px;
  padding: 16px 0;
  background-color: #f8f9fa;
  border-radius: 12px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: transform 0.2s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 4px;
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: #95a5a6;
  font-weight: 500;
}

.profile-description {
  padding-top: 28px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.profile-description p {
  margin: 0;
  font-size: 16px;
  color: #5c6bc0;
  line-height: 1.8;
  flex: 1;
  min-width: 0;
  background-color: #f3f4f6;
  padding: 16px 20px;
  border-radius: 12px;
  border-left: 4px solid #409eff;
}

.profile-info-item {
  background-color: #e8f5e9 !important;
  border-left-color: #67c23a !important;
  color: #2e7d32 !important;
}

.info-label {
  font-weight: 600;
  margin-right: 8px;
}

/* 通用区块样式 */
.section {
  background-color: #fff;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 28px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s ease;
}

.section:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f0f0;
}

.section-header h3 {
  margin: 0;
  font-size: 20px;
  color: #2c3e50;
  font-weight: 700;
  letter-spacing: -0.3px;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 50px 30px;
  color: #7f8c8d;
  background-color: #f8f9fa;
  border-radius: 12px;
  border: 2px dashed #e0e0e0;
  transition: all 0.3s ease;
}

.empty-state:hover {
  border-color: #409eff;
  background-color: #f0f8ff;
}

/* 宠物列表样式 */
.pets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 24px;
}

.pet-item {
  background-color: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.pet-item:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.pet-item-content {
  cursor: pointer;
}

.pet-actions {
  padding: 12px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: center;
}

.pet-image-wrapper {
  height: 220px;
  overflow: hidden;
  position: relative;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.pet-item:hover .pet-image {
  transform: scale(1.1);
}

.pet-item-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  cursor: pointer;
}

.pet-info {
  padding: 20px;
  text-align: center;
  background-color: #f8f9fa;
  flex-grow: 1;
}

.delete-pet-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 10;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 20px;
  padding: 4px 12px;
  font-size: 12px;
}

.pet-item:hover .delete-pet-btn {
  opacity: 1;
}

.pet-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
}

.pet-breed {
  margin: 0;
  font-size: 15px;
  color: #7f8c8d;
}

/* 照片墙样式 */
.photos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.photo-item {
  border-radius: 16px;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.photo-item:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
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
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.photo-item:hover .photo-image {
  transform: scale(1.1);
}

.photo-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  opacity: 0;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 20px;
}

.photo-item:hover .photo-overlay {
  opacity: 1;
}

.overlay-btn {
  opacity: 0.9;
  transition: all 0.3s ease;
  border-radius: 20px !important;
}

.overlay-btn:hover {
  opacity: 1;
  transform: scale(1.05);
}

.photo-caption {
  padding: 16px;
  font-size: 15px;
  color: #2c3e50;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  background-color: #f8f9fa;
}

/* 申请记录样式 */
.applications-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.application-item {
  background-color: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-left: 4px solid #409eff;
}

.application-item:hover {
  background-color: #f0f8ff;
  transform: translateX(8px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.app-info {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
  min-width: 0;
}

.app-pet-image {
  width: 90px;
  height: 90px;
  border-radius: 12px;
  object-fit: cover;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.app-details {
  flex: 1;
  min-width: 0;
}

.app-pet-name {
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.app-date {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 8px;
  font-weight: 500;
}

.app-description {
  font-size: 15px;
  color: #607d8b;
  line-height: 1.8;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  overflow: hidden;
}

.app-status {
  flex-shrink: 0;
}

.app-actions {
  display: flex;
  gap: 10px;
  margin-left: 20px;
  flex-shrink: 0;
}

.app-status .el-tag {
  font-size: 14px !important;
  padding: 6px 12px !important;
  border-radius: 20px !important;
  font-weight: 600 !important;
}

/* 评论记录样式 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  background-color: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-left: 4px solid #9c27b0;
  position: relative;
}

.comment-item:hover {
  background-color: #faf5ff;
  transform: translateX(4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.comment-content {
  font-size: 16px;
  color: #2c3e50;
  line-height: 1.8;
  margin-bottom: 16px;
  word-break: break-word;
  padding-right: 80px;
}

.comment-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #7f8c8d;
  font-size: 14px;
}

.comment-time {
  margin-right: 20px;
  font-weight: 500;
}

.comment-meta .el-button {
  font-size: 13px;
  padding: 4px 12px;
  border-radius: 16px !important;
  font-weight: 600;
}

.comment-meta .el-button:hover {
  transform: scale(1.05);
}

/* 照片详情样式 */
.photo-detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-photo-image {
  width: 100%;
  max-height: 500px;
  object-fit: contain;
  border-radius: 8px;
  background-color: #f5f5f5;
}

.photo-meta {
  display: flex;
  gap: 32px;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.meta-value {
  font-size: 14px;
  color: #303133;
}

/* 头像上传样式 */
.avatar-uploader {
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar {
  width: 178px;
  height: 178px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.avatar:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  background-color: #f8f9fa;
  border: 2px dashed #dcdfe6;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.avatar-uploader-icon:hover {
  border-color: #409eff;
  color: #409eff;
  background-color: #f0f8ff;
}

/* 统一按钮样式 */
.el-button {
  border-radius: 20px !important;
  font-weight: 600 !important;
  transition: all 0.3s ease !important;
  padding: 8px 20px !important;
}

.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.el-button--primary {
  background-color: #409eff !important;
  border-color: #409eff !important;
}

.el-button--small {
  padding: 6px 16px !important;
  font-size: 13px !important;
}

.el-button--text {
  color: #606266 !important;
}

.el-button--text:hover {
  color: #409eff !important;
  background-color: rgba(64, 158, 255, 0.1) !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    padding: 12px;
  }

  .profile-card {
    padding: 20px;
    border-radius: 12px;
  }

  .profile-header {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .avatar-wrapper {
    margin-right: 0;
  }

  .user-stats {
    gap: 20px;
    flex-wrap: wrap;
    justify-content: center;
  }

  .section {
    padding: 20px;
    border-radius: 12px;
  }

  .pets-grid, .photos-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 16px;
  }

  .pet-image-wrapper, .photo-image-wrapper {
    height: 150px;
  }

  .application-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .app-info {
    gap: 16px;
  }
  
  .app-actions {
    margin-left: 0;
    margin-top: 10px;
    width: 100%;
    justify-content: flex-end;
  }

  .app-pet-image {
    width: 70px;
    height: 70px;
  }

  .comment-content {
    padding-right: 0;
  }

  .comment-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .photo-meta {
    flex-direction: column;
    gap: 12px;
  }
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  background-color: #fafafa;
}

/* 弹窗样式覆盖 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .avatar-wrapper {
    margin-right: 0;
    margin-bottom: 16px;
  }
  
  .user-stats {
    justify-content: center;
  }
  
  .profile-description {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .pets-grid,
  .photos-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
  
  .application-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .app-info {
    width: 100%;
  }
  
  .photo-meta {
    flex-direction: column;
    gap: 12px;
  }
}

/* 关注/粉丝列表样式 */
.clickable {
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.clickable:hover {
  background-color: rgba(64, 158, 255, 0.1);
}

.empty-follow {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.follow-list {
  max-height: 400px;
  overflow-y: auto;
}

.follow-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
  margin-bottom: 8px;
}

.follow-item:hover {
  background-color: #f5f7fa;
}

.follow-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
  border: 2px solid #f0f0f0;
}

.follow-info {
  flex: 1;
  min-width: 0;
}

.follow-username {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.follow-time {
  font-size: 12px;
  color: #909399;
}
</style>