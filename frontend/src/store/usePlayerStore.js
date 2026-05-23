import { defineStore } from 'pinia'
import { getStreamUrl } from '../api/song'

export const usePlayerStore = defineStore('player', {
  state: () => ({
    currentSong: null,
    isPlaying: false,
    currentTime: 0,
    duration: 0,
    volume: 70,
    queue: [],
    queueIndex: -1,
    playMode: 'list' // list | random | single
  }),

  getters: {
    hasSong: (state) => !!state.currentSong,
    streamUrl: (state) => {
      if (!state.currentSong) return ''
      return getStreamUrl(state.currentSong.id)
    }
  },

  actions: {
    play(song, queue = null) {
      if (queue) {
        this.queue = queue
        this.queueIndex = queue.findIndex(s => s.id === song.id)
      }
      this.currentSong = song
      this.isPlaying = true
    },

    togglePlay() {
      this.isPlaying = !this.isPlaying
    },

    pause() {
      this.isPlaying = false
    },

    resume() {
      this.isPlaying = true
    },

    next() {
      if (this.queue.length === 0) return
      let nextIndex
      if (this.playMode === 'random') {
        nextIndex = Math.floor(Math.random() * this.queue.length)
      } else if (this.playMode === 'single') {
        nextIndex = this.queueIndex
      } else {
        nextIndex = (this.queueIndex + 1) % this.queue.length
      }
      this.queueIndex = nextIndex
      this.currentSong = this.queue[nextIndex]
      this.isPlaying = true
    },

    prev() {
      if (this.queue.length === 0) return
      let prevIndex
      if (this.playMode === 'random') {
        prevIndex = Math.floor(Math.random() * this.queue.length)
      } else {
        prevIndex = (this.queueIndex - 1 + this.queue.length) % this.queue.length
      }
      this.queueIndex = prevIndex
      this.currentSong = this.queue[prevIndex]
      this.isPlaying = true
    },

    setPlayMode(mode) {
      this.playMode = mode
    },

    updateProgress(time, dur) {
      this.currentTime = time
      this.duration = dur
    },

    seekTo(time) {
      this.currentTime = time
    }
  }
})
