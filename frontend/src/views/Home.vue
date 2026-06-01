<template>
  <div class="home mi-page">
    <section class="hero-stage mi-card">
      <div class="hero-copy">
        <div class="mi-kicker">Music Island Recommendation System</div>
        <h1>音乐岛</h1>
        <p>把收藏、播放历史和歌曲风格变成你的专属推荐，让下一首好歌自然浮现。</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" round @click="$router.push('/discover')">
            开始探索
          </el-button>
          <el-button size="large" round class="ghost-btn" @click="playFirst">
            播放热门
          </el-button>
        </div>
        <div class="hero-metrics">
          <div>
            <strong>{{ popularSongs.length || 0 }}</strong>
            <span>热门推荐</span>
          </div>
          <div>
            <strong>{{ latestSongs.length || 0 }}</strong>
            <span>最新上线</span>
          </div>
          <div>
            <strong>{{ userStore.isLoggedIn ? 'AI' : 'Hot' }}</strong>
            <span>{{ userStore.isLoggedIn ? '个性推荐' : '冷启动推荐' }}</span>
          </div>
        </div>
      </div>

      <div class="cover-collage">
        <div
          v-for="(song, i) in heroSongs"
          :key="song.id || i"
          class="cover-tile"
          :class="'tile-' + i"
          @click="playSong(song, heroSongs)"
        >
          <img :src="song.coverUrl || '/default-cover.png'" :alt="song.title" />
          <div class="tile-caption">
            <span>{{ song.title }}</span>
            <small>{{ song.artist }}</small>
          </div>
        </div>
      </div>
    </section>

    <SongShelf
      v-if="userStore.isLoggedIn"
      title="为你推荐"
      kicker="AI Selected"
      :songs="personalSongs"
      empty="多听几首歌后，这里会为你推荐"
      @play="playSong"
    />

    <SongShelf
      title="热门推荐"
      kicker="Popular Now"
      :songs="popularSongs"
      link="/discover"
      @play="playSong"
    />

    <SongShelf
      title="最新上线"
      kicker="Fresh Arrivals"
      :songs="latestSongs"
      @play="playSong"
    />
  </div>
</template>

<script setup>
import { computed, defineComponent, h, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { VideoPlay, ArrowRight } from '@element-plus/icons-vue'
import { getSongs } from '../api/song'
import { getPopularRecommendations, getPersonalRecommendations } from '../api/recommend'
import { usePlayerStore } from '../store/usePlayerStore'
import { useUserStore } from '../store/useUserStore'

const playerStore = usePlayerStore()
const userStore = useUserStore()
const personalSongs = ref([])
const popularSongs = ref([])
const latestSongs = ref([])

const heroSongs = computed(() => {
  const merged = [...personalSongs.value, ...popularSongs.value, ...latestSongs.value]
  const seen = new Set()
  return merged.filter(song => {
    if (!song?.id || seen.has(song.id)) return false
    seen.add(song.id)
    return true
  }).slice(0, 6)
})

onMounted(async () => {
  if (userStore.isLoggedIn) {
    try { personalSongs.value = await getPersonalRecommendations(12) } catch (e) {}
  }
  try { popularSongs.value = await getPopularRecommendations(12) || [] } catch (e) {
    try { const f = await getSongs({ sort: 'popular', limit: 12 }); popularSongs.value = f?.records || f || [] } catch (e2) {}
  }
  try { const d = await getSongs({ sort: 'latest', limit: 10 }); latestSongs.value = d?.records || d || [] } catch (e) {}
})

function playSong(song, queue) { playerStore.play(song, queue || [song]) }
function playFirst() {
  const queue = popularSongs.value.length ? popularSongs.value : latestSongs.value
  if (queue.length) playSong(queue[0], queue)
}

const SongShelf = defineComponent({
  props: {
    title: String,
    kicker: String,
    songs: { type: Array, default: () => [] },
    link: String,
    empty: { type: String, default: '暂无歌曲' }
  },
  emits: ['play'],
  setup(props, { emit }) {
    return () => h('section', { class: 'song-shelf' }, [
      h('div', { class: 'shelf-header' }, [
        h('div', [
          h('div', { class: 'mi-kicker' }, props.kicker),
          h('h2', { class: 'mi-title' }, props.title)
        ]),
        props.link ? h(RouterLink, { to: props.link, class: 'shelf-link' }, () => ['浏览全部 ', h(ArrowRight, { class: 'link-icon' })]) : null
      ]),
      props.songs.length
        ? h('div', { class: 'shelf-track' }, props.songs.map(song =>
            h('article', { class: 'mi-song-card shelf-card', key: song.id, onClick: () => emit('play', song, props.songs) }, [
              h('div', { class: 'mi-cover-wrap' }, [
                h('img', { class: 'mi-cover', src: song.coverUrl || '/default-cover.png', alt: song.title }),
                h('button', { class: 'mi-play-fab', title: '播放' }, [h(VideoPlay)])
              ]),
              h('div', { class: 'mi-card-info' }, [
                h('div', { class: 'mi-card-title' }, song.title),
                h('div', { class: 'mi-card-artist' }, song.artist)
              ])
            ])
          ))
        : h('div', { class: 'empty-panel' }, props.empty)
    ])
  }
})
</script>

<style scoped>
.home { padding-bottom: 8px; }

.hero-stage {
  min-height: 360px;
  padding: 42px;
  display: grid;
  grid-template-columns: minmax(420px, 0.92fr) minmax(460px, 1.08fr);
  gap: 42px;
  align-items: center;
  overflow: hidden;
  position: relative;
}
.hero-stage::before {
  content: "";
  position: absolute;
  inset: -35% -15% auto auto;
  width: 620px;
  height: 620px;
  background: radial-gradient(circle, var(--accent-glow), transparent 64%);
  pointer-events: none;
}
.hero-copy { position: relative; z-index: 1; }
.hero-copy h1 {
  margin-top: 12px;
  color: var(--hero-ink);
  font-size: 70px;
  line-height: 0.98;
  font-weight: 900;
  letter-spacing: 0;
}
.hero-copy p {
  max-width: 560px;
  margin-top: 18px;
  color: var(--hero-muted);
  font-size: 18px;
  line-height: 1.8;
}
.hero-actions { display: flex; gap: 12px; margin-top: 28px; }
.ghost-btn { background: var(--glass-bg); border-color: var(--border); color: var(--text-primary); }
.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  max-width: 520px;
  margin-top: 34px;
}
.hero-metrics div {
  padding: 14px 16px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: var(--glass-bg);
}
.hero-metrics strong { display: block; color: var(--text-primary); font-size: 24px; line-height: 1; }
.hero-metrics span { display: block; margin-top: 7px; color: var(--text-muted); font-size: 12px; }

.cover-collage {
  position: relative;
  height: 330px;
  z-index: 1;
}
.cover-tile {
  position: absolute;
  overflow: hidden;
  border-radius: 20px;
  border: 1px solid rgba(255,255,255,0.3);
  box-shadow: 0 22px 54px rgba(0,0,0,0.25);
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.cover-tile:hover { transform: translateY(-8px) scale(1.02); box-shadow: 0 30px 74px rgba(0,0,0,0.32); }
.cover-tile img { width: 100%; height: 100%; object-fit: cover; display: block; }
.tile-caption {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 34px 14px 12px;
  background: linear-gradient(transparent, rgba(0,0,0,0.72));
  color: #fff;
}
.tile-caption span, .tile-caption small {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.tile-caption span { font-size: 13px; font-weight: 800; }
.tile-caption small { margin-top: 3px; color: rgba(255,255,255,0.72); font-size: 11px; }
.tile-0 { width: 190px; height: 190px; left: 12px; top: 60px; }
.tile-1 { width: 220px; height: 220px; left: 176px; top: 10px; z-index: 2; }
.tile-2 { width: 164px; height: 164px; right: 56px; top: 42px; }
.tile-3 { width: 154px; height: 154px; left: 112px; bottom: 4px; }
.tile-4 { width: 190px; height: 190px; right: 132px; bottom: 0; z-index: 3; }
.tile-5 { width: 118px; height: 118px; right: 16px; bottom: 46px; }

.song-shelf { margin-top: 36px; }
.shelf-header {
  display: flex;
  align-items: end;
  justify-content: space-between;
  margin-bottom: 18px;
}
.shelf-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--text-muted);
  font-size: 13px;
  font-weight: 700;
}
.shelf-link:hover { color: var(--accent); }
.link-icon { width: 15px; height: 15px; }
:deep(.shelf-header) {
  display: flex;
  align-items: end;
  justify-content: space-between;
  margin-bottom: 18px;
}
:deep(.shelf-link) {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--text-muted);
  font-size: 13px;
  font-weight: 700;
}
:deep(.shelf-link:hover) { color: var(--accent); }
:deep(.link-icon) { width: 15px; height: 15px; }
:deep(.shelf-track) {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(165px, 1fr));
  gap: 18px;
}
:deep(.shelf-card) {
  width: 100%;
  max-width: none;
}
:deep(.shelf-card .mi-play-fab) {
  width: 42px;
  height: 42px;
  right: 12px;
  bottom: 12px;
}
:deep(.empty-panel) {
  padding: 28px;
  border-radius: var(--radius-md);
  background: var(--glass-bg);
  border: 1px solid var(--border);
  color: var(--text-muted);
}

@media (max-width: 1280px) {
  .hero-stage { grid-template-columns: 1fr 1fr; padding: 34px; }
  .hero-copy h1 { font-size: 58px; }
  :deep(.shelf-track) { gap: 16px; }
}
@media (max-width: 980px) {
  .hero-stage { grid-template-columns: 1fr; }
  .cover-collage { display: none; }
  :deep(.shelf-track) { grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 14px; }
}
@media (max-width: 680px) {
  :deep(.shelf-track) { grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); gap: 12px; }
}
</style>
