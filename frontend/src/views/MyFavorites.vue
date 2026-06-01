<template>
  <div class="my-favorites mi-page">
    <div class="page-head mi-card">
      <div>
        <div class="mi-kicker">My Library</div>
        <h1>我的收藏</h1>
      </div>
      <span class="mi-pill">{{ total }} 首歌曲</span>
    </div>
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
.page-head { display: flex; justify-content: space-between; align-items: end; padding: 28px 32px; margin-bottom: 24px; }
.page-head h1 { margin-top: 8px; color: var(--text-primary); font-size: 36px; font-weight: 900; }
.song-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(165px, 1fr)); gap: 18px; }
.song-card {
  position: relative; background: var(--card-bg); border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); border: 1px solid var(--border); box-shadow: var(--card-shadow);
}
.song-card:hover { transform: translateY(-4px); box-shadow: var(--card-hover-shadow); }
.card-cover { width: 100%; aspect-ratio: 1; object-fit: cover; display: block; }
.card-info { padding: 10px 12px 12px; }
.card-title { font-size: 13px; font-weight: 600; color: var(--text-secondary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-artist { font-size: 11px; color: var(--text-dim); margin-top: 3px; }
.unlike-btn { position: absolute; top: 6px; right: 6px; opacity: 0; transition: opacity 0.15s; }
.song-card:hover .unlike-btn { opacity: 1; }
.pagination { margin-top: 24px; justify-content: center; display: flex; }

@media (max-width: 1280px) {
  .song-grid { gap: 16px; }
}
@media (max-width: 980px) {
  .song-grid { grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 14px; }
}
@media (max-width: 680px) {
  .song-grid { grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); gap: 12px; }
}
</style>
