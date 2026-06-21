import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import PetsView from '../views/PetsView.vue'
import PetDetailView from '../views/PetDetailView.vue'
import PhotosView from '../views/PhotosView.vue'
import PhotoDetailView from '../views/PhotoDetailView.vue'
import UploadView from '../views/UploadView.vue'
import AdoptView from '../views/AdoptView.vue'
import ProfileView from '../views/ProfileView.vue'
import AdminView from '../views/AdminView.vue'
import AiChatView from '../views/AiChatView.vue'
import { useUserStore } from '@/stores'

const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/register', name: 'register', component: RegisterView },
  { path: '/pets', name: 'pets', component: PetsView },
  { path: '/pet/:id', name: 'petDetail', component: PetDetailView },
  { path: '/photos', name: 'photos', component: PhotosView },
  { path: '/photos/:id', name: 'photoDetail', component: PhotoDetailView },
  { path: '/upload', name: 'upload', component: UploadView },
  { path: '/adopt', name: 'adopt', component: AdoptView },
  { path: '/profile', name: 'profile', component: ProfileView },
  { 
    path: '/admin', 
    name: 'admin', 
    component: AdminView,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  { path: '/ai-chat', name: 'aiChat', component: AiChatView }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
    return
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin && userStore.user.role !== 'admin') {
    next('/')
    return
  }
  
  next()
})

export default router