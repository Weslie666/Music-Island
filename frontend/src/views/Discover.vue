<template>
  <div class="discover">
    <h2 class="page-title">发现音乐</h2>
    <div class="genre-tabs">
      <span
        v-for="g in genres"
        :key="g"
        :class="['genre-tag', { active: activeGenre === g }]"
        @click="selectGenre(g)"
      >{{ g }}</span>
    </div>
    <div class="song-grid">
      <div v-for="song in songs" :key="song.id" class="song-card" @click="playSong(song)">
        <img :src="song.coverUrl || '/default-cover.png'" class="card-cover" />
        <div class="card-info">
          <div class="card-title">{{ song.title }}</div>
          <div class="card-artist">{{ song.artist }}</div>
        </div>
      </div>
      <el-empty v-if="!songs.length" description="该分类暂无歌曲" />
    </div>
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
import { ref, onMounted } from 'vue'
import { getSongs, getGenres } from '../api/song'
import { usePlayerStore } from '../store/usePlayerStore'

const playerStore = usePlayerStore()
const genres = ref([])
const activeGenre = ref('')
const songs = ref([])
const page = ref(1)
const total = ref(0)
const limit = 10

onMounted(async () => {
  try {
    genres.value = await getGenres()
    if (genres.value.length) {
      activeGenre.value = genres.value[0]
      await loadSongs()
    }
  } catch (e) { /* no data yet */ }
})

async function loadSongs() {
  try {
    const data = await getSongs({ genre: activeGenre.value, page: page.value, limit })
    songs.value = data.records || []
    total.value = data.total || 0
  } catch (e) { songs.value = [] }
}

async function selectGenre(g) {
  activeGenre.value = g
  page.value = 1
  await loadSongs()
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
.discover { max-width: 1200px; margin: 0 auto; }
.page-title { font-size: 22px; margin-bottom: 20px; color: var(--text-primary); }
.genre-tabs { display: flex; gap: 12px; margin-bottom: 24px; flex-wrap: wrap; }
.genre-tag {
  padding: 6px 20px; border-radius: 20px; background: var(--bg-hover);
  cursor: pointer; font-size: 14px; transition: all 0.2s;
}
.genre-tag:hover { background: var(--accent-light); }
.genre-tag.active { background: #e94560; color: var(--text-primary); }
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
