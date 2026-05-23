<template>
  <div class="my-favorites">
    <h2 class="page-title">我的收藏</h2>
    <el-empty v-if="!songs.length && !loading" description="还没有收藏歌曲" />
    <div class="song-grid" v-if="songs.length">
      <div v-for="song in songs" :key="song.id" class="song-card">
        <img :src="song.coverUrl || '/default-cover.png'" class="card-cover"
          @click="playSong(song)" />
        <div class="card-info" @click="playSong(song)">
          <div class="card-title">{{ song.title }}</div>
          <div class="card-artist">{{ song.artist }}</div>
        </div>
        <el-button icon="Close" circle size="small" class="unlike-btn"
          title="取消收藏" @click.stop="handleUnlike(song.id)" />
      </div>
    </div>
    <el-pagination v-if="total > limit" layout="prev, pager, next" :total="total"
      :page-size="limit" :current-page="page" @current-change="load"
      class="pagination" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyLikes, toggleLike } from '../api/user'
import { usePlayerStore } from '../store/usePlayerStore'

const playerStore = usePlayerStore()
const songs = ref([])
const page = ref(1)
const limit = 10
const total = ref(0)
const loading = ref(false)

async function load(p = 1) {
  loading.value = true
  page.value = p
  try {
    const data = await getMyLikes(p, limit)
    songs.value = data.records || []
    total.value = data.total || 0
  } catch (e) { /* no data */ }
  loading.value = false
}

async function handleUnlike(songId) {
  await toggleLike(songId)
  await load(page.value)
}

function playSong(song) {
  playerStore.play(song, songs.value)
}

onMounted(() => load())
</script>

<style scoped>
.my-favorites { max-width: 1280px; margin: 0 auto; }
.page-title { font-size: 22px; font-weight: 700; margin: 24px 0 16px; color: var(--text-primary); letter-spacing: -0.5px; }
.song-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 20px; }
.song-card {
  background: var(--card-bg); border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); border: 1px solid var(--border);
}
.song-card:hover { transform: translateY(-4px); box-shadow: var(--card-hover-shadow); }
.card-cover { width: 100%; aspect-ratio: 1; object-fit: cover; display: block; }
.card-info { padding: 10px 12px 12px; }
.card-title { font-size: 13px; font-weight: 600; color: var(--text-secondary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-artist { font-size: 11px; color: var(--text-dim); margin-top: 3px; }
.unlike-btn { position: absolute; top: 6px; right: 6px; opacity: 0; transition: opacity 0.15s; }
.song-card:hover .unlike-btn { opacity: 1; }
.pagination { margin-top: 24px; justify-content: center; display: flex; }
</style>
