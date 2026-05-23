<template>
  <div class="song-detail" v-if="song">
    <div class="detail-header">
      <div class="cover-wrap">
        <img :src="song.coverUrl || '/default-cover.png'" class="detail-cover" />
        <div class="cover-shadow"></div>
      </div>
      <div class="detail-info">
        <span class="genre-tag" v-if="song.genre">{{ song.genre }}</span>
        <h1>{{ song.title }}</h1>
        <p class="artist">{{ song.artist }}</p>
        <p class="meta">
          <span v-if="song.album">专辑：{{ song.album }}</span>
          <span v-if="song.duration">时长：{{ formatTime(song.duration) }}</span>
        </p>
        <div class="actions">
          <el-button type="primary" size="large" round class="btn-play" @click="play">▶ 播放</el-button>
          <el-button size="large" round @click="handleToggleLike" :class="{ 'btn-liked': liked }">
            {{ liked ? '♥ 已收藏' : '♡ 收藏' }}
          </el-button>
          <el-button size="large" round @click="showPlaylistDialog = true">＋ 歌单</el-button>
        </div>
        <div class="stats">
          <div class="stat-item"><span class="stat-num">{{ formatCount(song.playCount || 0) }}</span> 次播放</div>
          <div class="stat-item"><span class="stat-num">{{ formatCount(song.likeCount || 0) }}</span> 次收藏</div>
        </div>
      </div>
    </div>

    <div class="similar-section" v-if="similarSongs.length">
      <h3 class="similar-title">相似歌曲</h3>
      <div class="song-grid">
        <div v-for="s in similarSongs" :key="s.id" class="song-card song-card-hover" @click="goToSong(s.id)">
          <div class="card-img-wrap">
            <img :src="s.coverUrl || '/default-cover.png'" class="card-cover" />
            <div class="card-play-btn">▶</div>
          </div>
          <div class="card-info">
            <div class="card-title">{{ s.title }}</div>
            <div class="card-artist">{{ s.artist }}</div>
          </div>
        </div>
      </div>
    </div>

    <CommentSection v-if="song" :songId="song.id" />

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
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
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

async function loadSong(sid) {
  try { song.value = await getSongById(sid) } catch (e) { return }
  try { const r = await checkLiked(sid); liked.value = r.liked } catch (e) {}
  try { playlists.value = await getMyPlaylists() } catch (e) {}
  try { similarSongs.value = await getSimilarSongs(sid, 6) } catch (e) {}
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
.song-detail { max-width: 960px; margin: 0 auto; }
.detail-header { display: flex; gap: 40px; margin-bottom: 40px; }
.cover-wrap { position: relative; flex-shrink: 0; }
.detail-cover {
  width: 300px; height: 300px; border-radius: 16px; object-fit: cover;
  box-shadow: 0 16px 48px rgba(0,0,0,0.3); position: relative; z-index: 1;
}
.cover-shadow {
  position: absolute; width: 260px; height: 260px; border-radius: 16px;
  background: inherit; filter: blur(60px); opacity: 0.4;
  top: 60px; left: 20px; z-index: 0;
}
.detail-info { flex: 1; display: flex; flex-direction: column; gap: 12px; padding-top: 8px; }
.genre-tag { display: inline-block; padding: 4px 14px; border-radius: 20px; background: var(--accent); color: #fff; font-size: 12px; font-weight: 600; width: fit-content; }
.detail-info h1 { font-size: 36px; color: var(--text-primary); font-weight: 800; letter-spacing: -1px; line-height: 1.2; }
.artist { font-size: 20px; color: var(--accent); font-weight: 600; }
.meta { font-size: 14px; color: var(--text-dim); display: flex; gap: 20px; }
.actions { display: flex; gap: 12px; margin-top: 20px; }
.btn-play { padding: 12px 32px; font-weight: 600; }
.btn-liked { color: var(--accent); border-color: var(--accent); }
.stats { display: flex; gap: 32px; margin-top: 16px; }
.stat-item { font-size: 13px; color: var(--text-dim); }
.stat-num { font-size: 18px; font-weight: 700; color: var(--text-primary); }

.similar-section { margin-top: 48px; }
.similar-title { font-size: 20px; font-weight: 700; margin-bottom: 18px; color: var(--text-primary); }
.song-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 16px; }
.song-card { background: var(--card-bg); border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s; border: 1px solid var(--border); }
.song-card:hover { transform: translateY(-4px); box-shadow: var(--card-hover-shadow); }
.card-img-wrap { position: relative; overflow: hidden; }
.card-cover { width: 100%; aspect-ratio: 1; object-fit: cover; display: block; transition: transform 0.4s; }
.song-card:hover .card-cover { transform: scale(1.06); }
.card-play-btn {
  position: absolute; bottom: 8px; right: 8px; z-index: 2;
  width: 38px; height: 38px; border-radius: 50%; background: #1db954; color: #fff;
  display: flex; align-items: center; justify-content: center; font-size: 13px;
  opacity: 0; transform: translateY(8px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.song-card:hover .card-play-btn { opacity: 1; transform: translateY(0); }
.card-info { padding: 10px 12px; }
.card-title { font-size: 12px; font-weight: 600; color: var(--text-secondary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-artist { font-size: 11px; color: var(--text-dim); margin-top: 2px; }

.pl-option { padding: 14px 16px; cursor: pointer; border-radius: 8px; transition: all 0.15s; color: var(--text-muted); }
.pl-option:hover { background: var(--bg-active); color: var(--accent); }
</style>
