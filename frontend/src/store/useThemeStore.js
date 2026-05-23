import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    mode: localStorage.getItem('theme') || 'light'
  }),
  getters: {
    isDark: (state) => state.mode === 'dark'
  },
  actions: {
    toggle() {
      this.mode = this.mode === 'dark' ? 'light' : 'dark'
      localStorage.setItem('theme', this.mode)
    },
    applyTheme() {
      document.documentElement.setAttribute('data-theme', this.mode)
    }
  }
})
