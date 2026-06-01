<template>
  <div class="song-detail mi-page" v-if="song">
    <section class="detail-hero mi-card">
      <div class="ambient" :style="ambientStyle"></div>
      <div class="cover-wrap">
        <img :src="song.coverUrl || '/default-cover.png'" class="detail-cover" />
      </div>
      <div class="detail-info">
        <span class="mi-pill" v-if="song.genre">{{ song.genre }}</span>
        <h1>{{ song.title }}</h1>
        <p class="artist">{{ song.artist }}</p>
        <div class="meta-line">
          <span v-if="song.album">{{ song.album }}</span>
          <span v-if="song.duration">{{ formatTime(song.duration) }}</span>
        </div>
        <div class="actions">
          <el-button type="primary" size="large" round class="btn-play" @click="play">
            <el-icon><VideoPlay /></el-icon> 播放
          </el-button>
          <el-button size="large" round @click="handleToggleLike" :class="{ 'btn-liked': liked }">
            <el-icon><StarFilled v-if="liked" /><Star v-else /></el-icon>
            {{ liked ? '已收藏' : '收藏' }}
          </el-button>
          <el-button size="large" round @click="showPlaylistDialog = true">
            <el-icon><FolderAdd /></el-icon> 歌单
          </el-button>
        </div>
        <div class="stats">
          <div><strong>{{ formatCount(song.playCount || 0) }}</strong><span>播放次数</span></div>
          <div><strong>{{ formatCount(song.likeCount || 0) }}</strong><span>收藏次数</span></div>
          <div><strong>{{ similarSongs.length }}</strong><span>相似推荐</span></div>
        </div>
      </div>
    </section>

    <section class="similar-section" v-if="similarSongs.length">
      <div class="section-head">
        <div>
          <div class="mi-kicker">Because You Like This</div>
          <h2 class="mi-title">相似歌曲</h2>
        </div>
      </div>
      <div class="mi-song-grid similar-grid">
        <article v-for="s in similarSongs" :key="s.id" class="mi-song-card" @click="goToSong(s.id)">
          <div class="mi-cover-wrap">
            <img :src="s.coverUrl || '/default-cover.png'" class="mi-cover" />
            <button class="mi-play-fab" title="查看"><el-icon><ArrowRight /></el-icon></button>
          </div>
          <div class="mi-card-info">
            <div class="mi-card-title">{{ s.title }}</div>
            <div class="mi-card-artist">{{ s.artist }}</div>
          </div>
        </article>
      </div>
    </section>

    <section class="comments-shell mi-card">
      <CommentSection v-if="song" :songId="song.id" />
    </section>

    <el-dialog v-model="showPlaylistDialog" title="添加到歌单" width="400px">
      <el-empty v-if="!playlists.length" description="暂无歌单" />
      <div v-for="pl in playlists" :key="pl.id" class="pl-option" @click="handleAddToPlaylist(pl.id)">{{ pl.name }}</div>
      <template #footer>
        <el-input v-model="newPlName" placeholder="新建歌单名称" style="width: 180px" />
        <el-button type="primary" :disabled="!newPlName.trim()" @click="handleCreateAndAdd">新建并添加</el-button>
      </template>
    </el-dialog>
  </div>
  <el-empty v-else description="歌曲不存在" />
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { VideoPlay, Star, StarFilled, FolderAdd, ArrowRight } from '@element-plus/icons-vue'
import { getSongById } from '../api/song'
import { toggleLike, checkLiked, getMyPlaylists, addToPlaylist, createPlaylist } from '../api/user'
import { getSimilarSongs } from '../api/recommend'
import { usePlayerStore } from '../store/usePlayerStore'
import { useUserStore } from '../store/useUserStore'
import { ElMessage } from 'element-plus'
import CommentSection from '../components/CommentSection.vue'
import { formatTime, formatCount } from '../utils/format'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const userStore = useUserStore()
const song = ref(null)
const liked = ref(false)
const showPlaylistDialog = ref(false)
const playlists = ref([])
const newPlName = ref('')
const similarSongs = ref([])

const ambientStyle = computed(() => ({
  backgroundImage: `linear-gradient(90deg, var(--bg-surface-solid) 0%, rgba(0,0,0,0) 100%), url("${song.value?.coverUrl || '/default-cover.png'}")`
}))

async function loadSong(sid) {
  try { song.value = await getSongById(sid) } catch (e) { return }
  try { const r = await checkLiked(sid); liked.value = r.liked } catch (e) { liked.value = false }
  try { playlists.value = await getMyPlaylists() } catch (e) { playlists.value = [] }
  try { similarSongs.value = await getSimilarSongs(sid, 6) } catch (e) { similarSongs.value = [] }
}

onMounted(() => loadSong(route.params.id))
watch(() => route.params.id, (nid) => { if (nid) loadSong(nid) })

function play() { if (song.value) playerStore.play(song.value, [song.value]) }
function goToSong(sid) { router.push('/song/' + sid); window.scrollTo(0, 0) }
async function handleToggleLike() {
  try { const r = await toggleLike(song.value.id); liked.value = r.liked; ElMessage.success(r.liked ? '已收藏' : '已取消收藏') } catch (e) { ElMessage.error('请先登录') }
}
async function handleAddToPlaylist(pid) { try { await addToPlaylist(pid, song.value.id); ElMessage.success('已添加到歌单'); showPlaylistDialog.value = false } catch (e) { ElMessage.error('操作失败') } }
async function handleCreateAndAdd() {
  const n = newPlName.value.trim(); if (!n) return
  try { const pl = await createPlaylist({ name: n }); await addToPlaylist(pl.id, song.value.id); userStore.refreshPlaylists(); ElMessage.success('已创建歌单并添加'); showPlaylistDialog.value = false; newPlName.value = '' } catch (e) { ElMessage.error('操作失败') }
}
</script>

<style scoped>
.detail-hero {
  position: relative;
  overflow: hidden;
  min-height: 420px;
  padding: 44px;
  display: grid;
  grid-template-columns: 340px minmax(0, 1fr);
  gap: 44px;
  align-items: center;
}
.ambient {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  opacity: 0.24;
  filter: blur(34px) saturate(1.2);
  transform: scale(1.08);
  pointer-events: none;
}
[data-theme="dark"] .ambient { opacity: 0.38; }
.cover-wrap, .detail-info { position: relative; z-index: 1; }
.detail-cover {
  width: 320px;
  height: 320px;
  border-radius: 24px;
  object-fit: cover;
  box-shadow: 0 32px 80px rgba(0,0,0,0.34);
}
.detail-info { display: flex; flex-direction: column; align-items: flex-start; }
.detail-info h1 {
  max-width: 760px;
  margin-top: 18px;
  color: var(--text-primary);
  font-size: 56px;
  line-height: 1.04;
  font-weight: 920;
  letter-spacing: 0;
}
.artist { margin-top: 12px; color: var(--accent); font-size: 22px; font-weight: 800; }
.meta-line {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 12px;
  color: var(--text-muted);
  font-size: 14px;
}
.actions { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 30px; }
.actions .el-button { font-weight: 800; }
.btn-play { padding: 12px 32px; }
.btn-liked { color: var(--accent); border-color: var(--accent); }
.stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 130px));
  gap: 12px;
  margin-top: 30px;
}
.stats div {
  padding: 14px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: var(--glass-bg);
}
.stats strong { display: block; color: var(--text-primary); font-size: 22px; line-height: 1; }
.stats span { display: block; margin-top: 7px; color: var(--text-muted); font-size: 12px; }
.section-head { margin: 38px 0 18px; }
.similar-grid { grid-template-columns: repeat(6, minmax(0, 1fr)); }
.comments-shell { margin-top: 38px; padding: 8px 24px 24px; }
.pl-option { padding: 14px 16px; cursor: pointer; border-radius: 10px; transition: all 0.15s; color: var(--text-muted); }
.pl-option:hover { background: var(--bg-active); color: var(--accent); }

@media (max-width: 1180px) {
  .detail-hero { grid-template-columns: 280px 1fr; gap: 34px; }
  .detail-cover { width: 270px; height: 270px; }
  .detail-info h1 { font-size: 46px; }
  .similar-grid { grid-template-columns: repeat(4, minmax(0, 1fr)); }
}
@media (max-width: 820px) {
  .detail-hero { grid-template-columns: 1fr; padding: 28px; }
}
</style>
