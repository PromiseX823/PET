import { defineStore } from 'pinia'

function cleanImageUrl(url) {
  if (!url) return url
  return url.replace('http://localhost:5000', '')
            .replace('http://localhost:8080', '')
            .replace('https://localhost:5000', '')
            .replace('https://localhost:8080', '')
}

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    isLoggedIn: false
  }),
  actions: {
    login(user) {
      this.user = this.cleanUserUrls(user)
      this.isLoggedIn = true
      localStorage.setItem('user', JSON.stringify(this.user))
    },
    logout() {
      this.user = null
      this.isLoggedIn = false
      localStorage.removeItem('user')
    },
    initializeUser() {
      const storedUser = localStorage.getItem('user')
      if (storedUser) {
        this.user = this.cleanUserUrls(JSON.parse(storedUser))
        this.isLoggedIn = true
      }
    },
    setUser(user) {
      this.user = this.cleanUserUrls(user)
      localStorage.setItem('user', JSON.stringify(this.user))
    },
    cleanUserUrls(user) {
      if (!user) return user
      if (user.avatar) {
        user.avatar = cleanImageUrl(user.avatar)
      }
      if (user.avatar_url) {
        user.avatar_url = cleanImageUrl(user.avatar_url)
      }
      if (user.avatarUrl) {
        user.avatarUrl = cleanImageUrl(user.avatarUrl)
      }
      return user
    }
  }
})

export const usePetsStore = defineStore('pets', {
  state: () => ({
    pets: [],
    currentPet: null
  }),
  actions: {
    setPets(pets) {
      this.pets = pets
    },
    setCurrentPet(pet) {
      this.currentPet = pet
    }
  }
})

export const usePhotosStore = defineStore('photos', {
  state: () => ({
    photos: [],
    needRefresh: false, // 用于标记是否需要刷新数据
    sortBy: 'created_at', // 排序字段：created_at(发布时间), likes(点赞量)
    sortOrder: 'desc' // 排序方向：asc(升序), desc(降序)
  }),
  actions: {
    setPhotos(photos) {
      this.photos = photos
    },
    setNeedRefresh(value) {
      this.needRefresh = value
    },
    updatePhoto(photoId, updates) {
      const index = this.photos.findIndex(photo => photo.id === photoId)
      if (index !== -1) {
        this.photos[index] = { ...this.photos[index], ...updates }
      }
    },
    setSortBy(sortBy) {
      this.sortBy = sortBy
    },
    setSortOrder(sortOrder) {
      this.sortOrder = sortOrder
    },
    toggleSortOrder() {
      this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc'
    }
  },
  getters: {
    sortedPhotos: (state) => {
      return [...state.photos].sort((a, b) => {
        if (state.sortBy === 'created_at') {
          const dateA = new Date(a.created_at).getTime()
          const dateB = new Date(b.created_at).getTime()
          return state.sortOrder === 'asc' ? dateA - dateB : dateB - dateA
        } else if (state.sortBy === 'likes') {
          return state.sortOrder === 'asc' ? a.likes - b.likes : b.likes - a.likes
        }
        return 0
      })
    }
  }
})