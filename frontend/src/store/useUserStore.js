import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi, getCurrentUser } from '../api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    playlistVersion: 0
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.userInfo?.role === 'ADMIN'
  },

  actions: {
    async login(username, password) {
      const data = await loginApi(username, password)
      this.token = data.token
      localStorage.setItem('token', data.token)
      await this.fetchUserInfo()
    },

    async register(username, password, nickname) {
      await registerApi(username, password, nickname)
    },

    async fetchUserInfo() {
      const user = await getCurrentUser()
      this.userInfo = user
      localStorage.setItem('userInfo', JSON.stringify(user))
    },

    refreshPlaylists() {
      this.playlistVersion++
    },

    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
