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
    return response.data
  },
  error => {
    console.error('API请求错误:', error)
    return Promise.reject(error)
  }
)

export { api }

export default {
  getPets(params = {}) {
    return api.get('/api/pets', { params })
  },
  
  getPetById(id) {
    return api.get(`/api/pets/${id}`)
  },
  
  createPet(petData) {
    return api.post('/api/pets', petData)
  },
  
  updatePet(petId, petData) {
    return api.put(`/api/pets/${petId}`, petData)
  },
  
  deletePet(petId) {
    return api.delete(`/api/pets/${petId}`)
  },
  
  uploadFile(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/api/upload/image', formData)
  },
  
  uploadAvatar(formData) {
    return api.post('/api/upload/image', formData)
  },
  
  uploadPetAvatar(formData) {
    return api.post('/api/upload/image', formData)
  },
  
  getPhotos() {
    return api.get('/api/photos')
  },
  
  getHotPhotos(limit = 3) {
    return api.get('/api/photos/hot', { params: { limit } })
  },
  
  getPhotoById(id) {
    return api.get(`/api/photos/${id}`)
  },
  
  getPhotoComments(photoId) {
    return api.get(`/api/comments/photos/${photoId}`)
  },
  
  addPhotoComment(photoId, commentData) {
    return api.post(`/api/comments/photos/${photoId}`, commentData)
  },
  
  uploadPhoto(formData) {
    return api.post('/api/upload/image', formData)
  },
  
  createPhoto(photoData) {
    return api.post('/api/photos', photoData)
  },
  
  deletePhoto(photoId) {
    return api.delete(`/api/photos/${photoId}`)
  },
  
  likePhoto(photoId, userId) {
    const requestData = { user_id: userId }
    return api.post(`/api/photos/${photoId}/like`, requestData)
  },
  
  unlikePhoto(photoId, userId) {
    const requestData = { user_id: userId }
    return api.post(`/api/photos/${photoId}/unlike`, requestData)
  },
  
  getPhotoLikeStatus(photoId, userId) {
    return api.get(`/api/photos/${photoId}/like-status`, { params: { user_id: userId } })
  },
  
  getUserLikedPhotos(userId) {
    return api.get(`/api/users/${userId}/liked-photos`)
  },
  
  collectPhoto(photoId, userId) {
    return api.post(`/api/photos/${photoId}/collect`, { user_id: userId })
  },
  
  uncollectPhoto(photoId, userId) {
    return api.post(`/api/photos/${photoId}/uncollect`, { user_id: userId })
  },
  
  getPhotoCollectionStatus(photoId, userId) {
    return api.get(`/api/photos/${photoId}/collection-status`, { params: { user_id: userId } })
  },
  
  getUserCollectedPhotos(userId) {
    return api.get(`/api/users/${userId}/collected-photos`)
  },
  
  getUserById(id) {
    return api.get(`/api/users/${id}`)
  },
  
  getUsers(params = {}) {
    return api.get('/api/users', { params })
  },
  
  updateUser(id, userData) {
    return api.put(`/api/users/${id}`, userData)
  },
  
  getUserPets(userId) {
    return api.get(`/api/users/${userId}/pets`)
  },
  
  getUserAdoptions(userId) {
    return api.get(`/api/adoptions/users/${userId}`)
  },
  
  getUserPhotos(userId) {
    return api.get('/api/photos')
  },
  
  login(username, password) {
    return api.post('/api/auth/login', { username, password })
  },
  
  register(username, email, password) {
    return api.post('/api/auth/register', { username, email, password })
  },
  
  logout() {
    return api.post('/api/auth/logout')
  },
  
  getAdoptions() {
    return api.get('/api/adoptions')
  },
  
  createAdoption(adoptionData) {
    return api.post('/api/adoptions', adoptionData)
  },
  
  approveAdoption(adoptionId, data) {
    return api.post(`/api/adoptions/${adoptionId}/approve`, data)
  },
  
  rejectAdoption(adoptionId, data) {
    return api.post(`/api/adoptions/${adoptionId}/reject`, data)
  },
  
  cancelAdoption(adoptionId) {
    return api.post(`/api/adoptions/${adoptionId}/cancel`)
  },
  
  updateAdoption(adoptionId, adoptionData) {
    return api.put(`/api/adoptions/${adoptionId}`, adoptionData)
  },
  
  completeAdoption(adoptionId) {
    return api.post(`/api/adoptions/${adoptionId}/complete`)
  },
  
  getPetAdoptions(petId) {
    return api.get(`/api/adoptions/pets/${petId}`)
  },
  
  deleteComment(commentId) {
    return api.delete(`/api/comments/${commentId}`)
  },
  
  deleteAccount(password) {
    const user = localStorage.getItem('user')
    const parsedUser = user ? JSON.parse(user) : null
    const userId = parsedUser ? parsedUser.id : null
    
    return api.post('/api/users/self/delete', { user_id: userId, password })
  },
  
  getStats() {
    return api.get('/api/stats')
  },
  
  getPetTypeStats() {
    return api.get('/api/stats/pets/type')
  },
  
  getPetStatusStats() {
    return api.get('/api/stats/pets/status')
  },
  
  getUserPetStats() {
    return api.get('/api/stats/users/pets')
  },
  
  getNotifications(userId) {
    return api.get(`/api/notifications/users/${userId}`)
  },
  
  markAllAsRead(userId) {
    return api.post(`/api/notifications/users/${userId}/read-all`)
  },
  
  getUnreadCount(userId) {
    return api.get(`/api/notifications/users/${userId}/unread-count`)
  },
  
  favoritePet(petId, userId) {
    return api.post(`/api/favorites/pets/${petId}`, { user_id: userId })
  },
  
  unfavoritePet(petId, userId) {
    return api.delete(`/api/favorites/pets/${petId}`, { params: { user_id: userId } })
  },
  
  getFavoriteStatus(petId, userId) {
    return api.get(`/api/favorites/pets/${petId}/status`, { params: { user_id: userId } })
  }
}
