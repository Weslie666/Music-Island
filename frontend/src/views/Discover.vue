<template>
  <div class="discover mi-page">
    <section class="explore-hero mi-card">
      <div>
        <div class="mi-kicker">Explore Library</div>
        <h1>发现音乐</h1>
        <p>按风格浏览曲库，用播放、收藏和推荐算法找到更适合你的歌。</p>
      </div>
      <div class="genre-panel">
        <button
          v-for="g in genres"
          :key="g"
          :class="['genre-tag', { active: activeGenre === g }]"
          @click="selectGenre(g)"
        >
          {{ g }}
        </button>
      </div>
    </section>

    <div class="section-row">
      <div>
        <div class="mi-kicker">Current Genre</div>
        <h2 class="mi-title">{{ activeGenre || '全部音乐' }}</h2>
      </div>
      <span class="mi-pill">{{ total }} 首歌曲</span>
    </div>

    <div class="mi-song-grid" v-if="songs.length">
      <article v-for="song in songs" :key="song.id" class="mi-song-card" @click="playSong(song)">
        <div class="mi-cover-wrap">
          <img :src="song.coverUrl || '/default-cover.png'" class="mi-cover" />
          <button class="mi-play-fab" title="播放"><el-icon><VideoPlay /></el-icon></button>
          <span class="song-genre">{{ song.genre }}</span>
        </div>
        <div class="mi-card-info">
          <div class="mi-card-title">{{ song.title }}</div>
          <div class="mi-card-artist">{{ song.artist }}</div>
          <div class="song-meta">
            <span>{{ formatCount(song.playCount || 0) }} 播放</span>
            <span>{{ formatCount(song.likeCount || 0) }} 收藏</span>
          </div>
        </div>
      </article>
    </div>
    <el-empty v-else description="该分类暂无歌曲" />

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
import { VideoPlay } from '@element-plus/icons-vue'
import { getSongs, getGenres } from '../api/song'
import { usePlayerStore } from '../store/usePlayerStore'
import { formatCount } from '../utils/format'

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
.explore-hero {
  min-height: 210px;
  padding: 34px;
  display: grid;
  grid-template-columns: minmax(320px, 0.8fr) 1.2fr;
  gap: 28px;
  align-items: center;
}
.explore-hero h1 {
  margin-top: 10px;
  color: var(--text-primary);
  font-size: 48px;
  line-height: 1;
  font-weight: 900;
}
.explore-hero p {
  margin-top: 14px;
  color: var(--text-muted);
  font-size: 16px;
  line-height: 1.75;
  max-width: 520px;
}
.genre-panel {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
}
.genre-tag {
  border: 1px solid var(--border);
  background: var(--glass-bg);
  color: var(--text-muted);
  border-radius: 999px;
  min-height: 40px;
  padding: 0 18px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 700;
  transition: all 0.22s ease;
}
.genre-tag:hover { color: var(--text-primary); transform: translateY(-2px); }
.genre-tag.active {
  color: #fff;
  background: linear-gradient(135deg, var(--accent), var(--gradient-2));
  border-color: transparent;
  box-shadow: 0 14px 30px var(--accent-glow);
}
.section-row {
  display: flex;
  justify-content: space-between;
  align-items: end;
  margin: 32px 0 18px;
}
.song-genre {
  position: absolute;
  top: 10px;
  left: 10px;
  max-width: calc(100% - 20px);
  padding: 5px 9px;
  border-radius: 999px;
  background: rgba(0,0,0,0.52);
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  backdrop-filter: blur(10px);
}
.song-meta {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-top: 12px;
  color: var(--text-dim);
  font-size: 11px;
}
.pagination { display: flex; justify-content: center; margin-top: 28px; }

@media (max-width: 980px) {
  .explore-hero { grid-template-columns: 1fr; }
  .genre-panel { justify-content: flex-start; }
}
</style>
