import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(
  config => {
    const user = localStorage.getItem('user')
    if (user) {
      const parsedUser = JSON.parse(user)
      if (parsedUser.token) {
        config.headers.Authorization = `Bearer ${parsedUser.token}`
      }
    }
    
    if ((config.method === 'post' || config.method === 'put') && !(config.data instanceof FormData)) {
      config.headers['Content-Type'] = 'application/json'
    }
    
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  response => {
    const result = response.data
    if (result && result.code === 200) {
      return result
    }
    return result
  },
  error => {
    console.error('API请求错误:', error)
    return Promise.reject(error)
  }
)

export { api }

export default {
  getPets(params = {}) {
    return api.get('/pets', { params })
  },
  
  getPetById(id) {
    return api.get(`/pets/${id}`)
  },
  
  createPet(petData) {
    return api.post('/pets', petData)
  },
  
  updatePet(petId, petData) {
    return api.put(`/pets/${petId}`, petData)
  },
  
  deletePet(petId) {
    return api.delete(`/pets/${petId}`)
  },
  
  uploadFile(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/upload/image', formData)
  },
  
  uploadAvatar(formData) {
    return api.post('/upload/image', formData)
  },
  
  uploadPetAvatar(formData) {
    return api.post('/upload/image', formData)
  },
  
  getPhotos() {
    return api.get('/photos')
  },
  
  getHotPhotos(limit = 3) {
    return api.get('/photos/hot', { params: { limit } })
  },
  
  getPhotoById(id) {
    return api.get(`/photos/${id}`)
  },
  
  getPhotoComments(photoId) {
    return api.get(`/comments/photos/${photoId}`)
  },
  
  addPhotoComment(photoId, commentData) {
    return api.post(`/comments/photos/${photoId}`, commentData)
  },
  
  uploadPhoto(formData) {
    return api.post('/upload/image', formData)
  },
  
  createPhoto(photoData) {
    return api.post('/photos', photoData)
  },
  
  deletePhoto(photoId) {
    return api.delete(`/photos/${photoId}`)
  },
  
  likePhoto(photoId, userId) {
    const requestData = { user_id: userId }
    return api.post(`/photos/${photoId}/like`, requestData)
  },
  
  unlikePhoto(photoId, userId) {
    const requestData = { user_id: userId }
    return api.post(`/photos/${photoId}/unlike`, requestData)
  },
  
  getPhotoLikeStatus(photoId, userId) {
    return api.get(`/photos/${photoId}/like-status`, { params: { user_id: userId } })
  },
  
  getUserLikedPhotos(userId) {
    return api.get(`/users/${userId}/liked-photos`)
  },
  
  collectPhoto(photoId, userId) {
    return api.post(`/photos/${photoId}/collect`, { user_id: userId })
  },
  
  uncollectPhoto(photoId, userId) {
    return api.post(`/photos/${photoId}/uncollect`, { user_id: userId })
  },
  
  getPhotoCollectionStatus(photoId, userId) {
    return api.get(`/photos/${photoId}/collection-status`, { params: { user_id: userId } })
  },
  
  getUserCollectedPhotos(userId) {
    return api.get(`/users/${userId}/collected-photos`)
  },
  
  getUserById(id) {
    return api.get(`/users/${id}`)
  },
  
  getUsers(params = {}) {
    return api.get('/users', { params })
  },
  
  updateUser(id, userData) {
    return api.put(`/users/${id}`, userData)
  },
  
  getUserPets(userId) {
    return api.get(`/users/${userId}/pets`)
  },
  
  getUserAdoptions(userId) {
    return api.get(`/adoptions/users/${userId}`)
  },
  
  getUserPhotos(userId) {
    return api.get('/photos')
  },
  
  login(username, password) {
    return api.post('/auth/login', { username, password })
  },
  
  register(username, email, password) {
    return api.post('/auth/register', { username, email, password })
  },
  
  logout() {
    return api.post('/auth/logout')
  },
  
  getAdoptions() {
    return api.get('/adoptions')
  },
  
  createAdoption(adoptionData) {
    return api.post('/adoptions', adoptionData)
  },
  
  approveAdoption(adoptionId, data) {
    return api.post(`/adoptions/${adoptionId}/approve`, data)
  },
  
  rejectAdoption(adoptionId, data) {
    return api.post(`/adoptions/${adoptionId}/reject`, data)
  },
  
  cancelAdoption(adoptionId) {
    return api.post(`/adoptions/${adoptionId}/cancel`)
  },
  
  updateAdoption(adoptionId, adoptionData) {
    return api.put(`/adoptions/${adoptionId}`, adoptionData)
  },
  
  completeAdoption(adoptionId) {
    return api.post(`/adoptions/${adoptionId}/complete`)
  },
  
  getPetAdoptions(petId) {
    return api.get(`/adoptions/pets/${petId}`)
  },
  
  deleteComment(commentId) {
    return api.delete(`/comments/${commentId}`)
  },
  
  deleteAccount(password) {
    const user = localStorage.getItem('user')
    const parsedUser = user ? JSON.parse(user) : null
    const userId = parsedUser ? parsedUser.id : null
    
    return api.post('/users/self/delete', { user_id: userId, password })
  },
  
  getStats() {
    return api.get('/stats')
  },
  
  getPetTypeStats() {
    return api.get('/stats/pets/type')
  },
  
  getPetStatusStats() {
    return api.get('/stats/pets/status')
  },
  
  getUserPetStats() {
    return api.get('/stats/users/pets')
  },
  
  getNotifications(userId) {
    return api.get(`/notifications/users/${userId}`)
  },
  
  markAllAsRead(userId) {
    return api.post(`/notifications/users/${userId}/read-all`)
  },
  
  getUnreadCount(userId) {
    return api.get(`/notifications/users/${userId}/unread-count`)
  },
  
  favoritePet(petId) {
    return api.post(`/favorites/pets/${petId}`)
  },
  
  unfavoritePet(petId, userId) {
    return api.delete(`/favorites/pets/${petId}`, { params: { user_id: userId } })
  },
  
  getFavoriteStatus(petId, userId) {
    return api.get(`/favorites/pets/${petId}/status`, { params: { user_id: userId } })
  },
  
  aiChat(messages) {
    return api.post('/ai/qwen', { messages })
  },
  
  aiGeneratePetDescription(petInfo) {
    return api.post('/ai/pet-description', { petInfo })
  },
  
  aiAnalyzeAdoption(applicationInfo) {
    return api.post('/ai/adoption-analysis', { applicationInfo })
  },
  
  aiAnswerQuestion(question) {
    return api.post('/ai/pet-question', { question })
  },
  
  // 关注相关 API
  followUser(userId) {
    return api.post(`/follows/${userId}`)
  },
  
  unfollowUser(userId) {
    return api.delete(`/follows/${userId}`)
  },
  
  toggleFollow(userId) {
    return api.post(`/follows/toggle/${userId}`)
  },
  
  getFollowingList(userId) {
    return api.get(`/follows/following/${userId}`)
  },
  
  getFollowerList(userId) {
    return api.get(`/follows/followers/${userId}`)
  },
  
  getFollowStats(userId) {
    return api.get(`/follows/stats/${userId}`)
  },
  
  checkFollowing(userId) {
    return api.get(`/follows/check/${userId}`)
  }
}
