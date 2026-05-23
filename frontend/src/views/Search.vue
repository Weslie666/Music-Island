<template>
  <div class="search-page">
    <h2 class="page-title">搜索结果：{{ keyword }}</h2>
    <div class="song-grid" v-if="songs.length">
      <div v-for="song in songs" :key="song.id" class="song-card" @click="playSong(song)">
        <img :src="song.coverUrl || '/default-cover.png'" class="card-cover" />
        <div class="card-info">
          <div class="card-title">{{ song.title }}</div>
          <div class="card-artist">{{ song.artist }}</div>
        </div>
      </div>
    </div>
    <el-empty v-else description="未找到相关歌曲" />
    <div class="pagination" v-if="total > limit">
      <el-pagination
        layout="prev, pager, next"
        :page-size="limit"
        :total="total"
        :current-page="page"
        @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getSongs } from '../api/song'
import { usePlayerStore } from '../store/usePlayerStore'

const route = useRoute()
const playerStore = usePlayerStore()
const keyword = ref('')
const songs = ref([])
const page = ref(1)
const total = ref(0)
const limit = 10

watch(() => route.query.q, async (q) => {
  if (q) {
    keyword.value = q
    page.value = 1
    await loadSongs()
  }
}, { immediate: true })

async function loadSongs() {
  try {
    const data = await getSongs({ keyword: keyword.value, page: page.value, limit })
    songs.value = data.records || []
    total.value = data.total || 0
  } catch (e) { songs.value = [] }
}

async function onPageChange(p) {
  page.value = p
  await loadSongs()
}

function playSong(song) {
  playerStore.play(song, songs.value)
}
</script>

<style scoped>
.search-page { max-width: 1200px; margin: 0 auto; }
.page-title { font-size: 22px; margin-bottom: 20px; color: var(--text-primary); }
.song-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; }
.song-card {
  background: #fff; border-radius: 8px; overflow: hidden; cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.song-card:hover { transform: translateY(-4px); box-shadow: var(--card-hover-shadow); }
.card-cover { width: 100%; aspect-ratio: 1; object-fit: cover; display: block; transition: transform 0.4s ease; }
.song-card:hover .card-cover { transform: scale(1.06); }
.card-info { padding: 10px; }
.card-title { font-size: 14px; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-artist { font-size: 12px; color: var(--text-dim); margin-top: 4px; }
.pagination { display: flex; justify-content: center; margin-top: 24px; }
</style>
