<template>
  <div class="home">
    <!-- Hero Banner -->
    <div class="hero-banner">
      <div class="hero-text">
        <h1 class="hero-title">发现你的音乐世界</h1>
        <p class="hero-sub">智能推荐，为你找到下一首心动旋律</p>
      </div>
      <div class="hero-visual">
        <div class="hero-disc" v-for="i in 3" :key="i"
          :style="{ animationDelay: i * 1.5 + 's', left: 40 + i * 30 + '%', top: 10 + i * 15 + '%' }">
          <div class="disc-inner"></div>
        </div>
      </div>
    </div>

    <!-- Personalized -->
    <template v-if="userStore.isLoggedIn">
      <div class="section-header">
        <h2 class="section-title">为你推荐</h2>
        <span class="section-badge">AI 精选</span>
      </div>
      <div class="song-grid" v-if="personalSongs.length">
        <div v-for="(song, i) in personalSongs" :key="song.id"
          class="song-card song-card-hover" :style="{ animationDelay: i * 0.04 + 's' }"
          @click="playSong(song, personalSongs)">
          <div class="card-img-wrap">
            <img :src="song.coverUrl || '/default-cover.png'" class="card-cover" />
            <div class="card-overlay"></div>
            <div class="card-play-btn">▶</div>
          </div>
          <div class="card-info">
            <div class="card-title">{{ song.title }}</div>
            <div class="card-artist">{{ song.artist }}</div>
          </div>
        </div>
      </div>
      <el-empty v-else description="多听几首歌后，这里会为你推荐" />
    </template>

    <!-- Popular -->
    <div class="section-header">
      <h2 class="section-title">热门推荐</h2>
      <router-link to="/discover" class="section-link">浏览全部 →</router-link>
    </div>
    <div class="song-grid" v-if="popularSongs.length">
      <div v-for="(song, i) in popularSongs" :key="song.id"
        class="song-card song-card-hover" :style="{ animationDelay: i * 0.04 + 's' }"
        @click="playSong(song, popularSongs)">
        <div class="card-img-wrap">
          <img :src="song.coverUrl || '/default-cover.png'" class="card-cover" />
          <div class="card-overlay"></div>
          <div class="card-play-btn">▶</div>
        </div>
        <div class="card-info">
          <div class="card-title">{{ song.title }}</div>
          <div class="card-artist">{{ song.artist }}</div>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无歌曲" />

    <!-- Latest -->
    <div class="section-header">
      <h2 class="section-title">最新上线</h2>
    </div>
    <div class="song-grid" v-if="latestSongs.length">
      <div v-for="(song, i) in latestSongs" :key="song.id"
        class="song-card song-card-hover" :style="{ animationDelay: i * 0.04 + 's' }"
        @click="playSong(song, latestSongs)">
        <div class="card-img-wrap">
          <img :src="song.coverUrl || '/default-cover.png'" class="card-cover" />
          <div class="card-overlay"></div>
          <div class="card-play-btn">▶</div>
        </div>
        <div class="card-info">
          <div class="card-title">{{ song.title }}</div>
          <div class="card-artist">{{ song.artist }}</div>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无歌曲" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSongs } from '../api/song'
import { getPopularRecommendations, getPersonalRecommendations } from '../api/recommend'
import { usePlayerStore } from '../store/usePlayerStore'
import { useUserStore } from '../store/useUserStore'

const playerStore = usePlayerStore()
const userStore = useUserStore()
const personalSongs = ref([])
const popularSongs = ref([])
const latestSongs = ref([])

onMounted(async () => {
  if (userStore.isLoggedIn) {
    try { personalSongs.value = await getPersonalRecommendations(15) } catch (e) {}
  }
  try { popularSongs.value = await getPopularRecommendations(15) || [] } catch (e) {
    try { const f = await getSongs({ sort: 'popular', limit: 15 }); popularSongs.value = f?.records || f || [] } catch (e2) {}
  }
  try { const d = await getSongs({ sort: 'latest', limit: 10 }); latestSongs.value = d?.records || d || [] } catch (e) {}
})

function playSong(song, queue) { playerStore.play(song, queue) }
</script>

<style scoped>
.home { max-width: 1320px; margin: 0 auto; }

/* Hero Banner */
.hero-banner {
  background: linear-gradient(135deg, #1a1a2e 0%, #2d1b3d 50%, #1a1a2e 100%);
  border-radius: 20px; padding: 48px 56px;
  margin-bottom: 40px; position: relative; overflow: hidden;
  min-height: 200px; display: flex; align-items: center;
  border: 1px solid rgba(255,255,255,0.06);
}
.hero-text { position: relative; z-index: 2; }
.hero-title {
  font-size: 36px; font-weight: 800; color: #fff;
  letter-spacing: -1.5px; line-height: 1.2; margin-bottom: 8px;
  background: linear-gradient(135deg, #fff 30%, #e94560 100%);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
}
.hero-sub { font-size: 16px; color: rgba(255,255,255,0.5); font-weight: 400; }
.hero-visual { position: absolute; right: 0; top: 0; width: 50%; height: 100%; pointer-events: none; }
.hero-disc {
  position: absolute; width: 180px; height: 180px; border-radius: 50%;
  background: linear-gradient(135deg, rgba(233,69,96,0.3), rgba(255,107,107,0.1));
  animation: floatDisc 6s ease-in-out infinite;
  border: 2px solid rgba(255,255,255,0.08);
}
.disc-inner {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
  width: 40px; height: 40px; border-radius: 50%;
  background: rgba(255,255,255,0.2);
}
@keyframes floatDisc {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* Section headers */
.section-header { display: flex; align-items: baseline; gap: 16px; margin: 36px 0 18px; }
.section-title {
  font-size: 24px; font-weight: 700; color: var(--text-primary);
  letter-spacing: -0.5px;
}
.section-badge {
  font-size: 11px; font-weight: 600; text-transform: uppercase;
  padding: 3px 10px; border-radius: 20px;
  background: linear-gradient(135deg, #e94560, #ff6b6b);
  color: #fff; letter-spacing: 1px;
}
.section-link { font-size: 13px; color: var(--text-dim); font-weight: 500; transition: color 0.2s; }
.section-link:hover { color: var(--accent); }

/* Song Grid */
.song-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 20px; }
.song-card {
  background: var(--card-bg); border-radius: 12px; overflow: hidden;
  cursor: pointer; transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border);
  animation: cardIn 0.5s ease backwards;
}
.song-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--card-hover-shadow);
  border-color: transparent;
}
@keyframes cardIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Card Image */
.card-img-wrap { position: relative; overflow: hidden; }
.card-cover {
  width: 100%; aspect-ratio: 1; object-fit: cover; display: block;
  transition: transform 0.5s ease;
}
.song-card:hover .card-cover { transform: scale(1.08); }
.card-overlay {
  position: absolute; inset: 0;
  background: linear-gradient(transparent 50%, rgba(0,0,0,0.5));
  opacity: 0; transition: opacity 0.35s;
}
.song-card:hover .card-overlay { opacity: 1; }

/* Play button on hover */
.card-play-btn {
  position: absolute; bottom: 12px; right: 12px; z-index: 3;
  width: 48px; height: 48px; border-radius: 50%;
  background: #1db954; color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px;
  opacity: 0; transform: translateY(12px) scale(0.9);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.song-card:hover .card-play-btn {
  opacity: 1;
  transform: translateY(0) scale(1);
}
.card-play-btn:hover { background: #1ed760; transform: scale(1.08) !important; }

/* Card Info */
.card-info { padding: 12px 14px 14px; }
.card-title {
  font-size: 13px; font-weight: 600; color: var(--text-secondary);
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
  line-height: 1.3;
}
.card-artist { font-size: 12px; color: var(--text-dim); margin-top: 4px; font-weight: 400; }

[data-theme="dark"] .hero-banner { background: linear-gradient(135deg, #0a0a1a 0%, #1a0a2e 50%, #0a0a1a 100%); }
</style>
