<template>
  <div class="search-page mi-page">
    <div class="search-header mi-card">
      <div>
        <div class="mi-kicker">Search Results</div>
        <h1>搜索：{{ keyword }}</h1>
      </div>
      <span class="mi-pill">{{ total }} 个结果</span>
    </div>
    <div class="mi-song-grid" v-if="songs.length">
      <article v-for="song in songs" :key="song.id" class="mi-song-card" @click="playSong(song)">
        <div class="mi-cover-wrap">
          <img :src="song.coverUrl || '/default-cover.png'" class="mi-cover" />
          <button class="mi-play-fab" title="播放"><el-icon><VideoPlay /></el-icon></button>
        </div>
        <div class="mi-card-info">
          <div class="mi-card-title">{{ song.title }}</div>
          <div class="mi-card-artist">{{ song.artist }}</div>
        </div>
      </article>
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
import { VideoPlay } from '@element-plus/icons-vue'
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
.search-header {
  margin-bottom: 24px;
  padding: 28px 32px;
  display: flex;
  justify-content: space-between;
  align-items: end;
}
.search-header h1 {
  margin-top: 8px;
  color: var(--text-primary);
  font-size: 36px;
  font-weight: 900;
}
.pagination { display: flex; justify-content: center; margin-top: 24px; }
</style>
