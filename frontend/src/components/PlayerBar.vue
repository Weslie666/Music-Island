<template>
  <div class="player-bar">
    <div class="player-left">
      <img :src="playerStore.currentSong?.coverUrl || '/default-cover.png'"
        class="player-cover" :class="{ 'playing-pulse': playerStore.isPlaying }"
        @click="goToSong" />
      <div class="song-info">
        <div class="song-title" @click="goToSong">{{ playerStore.currentSong?.title || '未选择歌曲' }}</div>
        <div class="song-artist">{{ playerStore.currentSong?.artist || 'Music Island' }}</div>
      </div>
      <button type="button" class="action-btn" :title="liked ? '取消收藏' : '收藏'" @click.stop="handleLike">
        <el-icon><StarFilled v-if="liked" /><Star v-else /></el-icon>
      </button>
      <button type="button" class="action-btn" title="添加到歌单" @click.stop="openPlaylistDialog">
        <el-icon><Plus /></el-icon>
      </button>
    </div>

    <div class="player-center">
      <div class="controls">
        <button type="button" class="ctrl-btn" :title="modeLabel" @click.stop="cycleMode">
          <el-icon><RefreshRight v-if="playerStore.playMode === 'list'" /><Switch v-else-if="playerStore.playMode === 'random'" /><Refresh v-else /></el-icon>
        </button>
        <button type="button" class="ctrl-btn" title="上一首" @click.stop="playerStore.prev()">
          <el-icon><DArrowLeft /></el-icon>
        </button>
        <button type="button" class="play-btn" :title="playerStore.isPlaying ? '暂停' : '播放'" @click.stop="playerStore.togglePlay()">
          <el-icon><VideoPause v-if="playerStore.isPlaying" /><VideoPlay v-else /></el-icon>
        </button>
        <button type="button" class="ctrl-btn" title="下一首" @click.stop="playerStore.next()">
          <el-icon><DArrowRight /></el-icon>
        </button>
        <button type="button" class="ctrl-btn" title="播放队列" @click.stop="openQueueDialog">
          <el-icon><Tickets /></el-icon>
        </button>
      </div>
      <div class="progress-bar">
        <span class="time">{{ formatTime(playerStore.currentTime) }}</span>
        <div class="progress-track" @click="seekByClick" ref="progressTrack">
          <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
          <div class="progress-thumb" :style="{ left: progressPercent + '%' }"></div>
        </div>
        <span class="time">{{ formatTime(playerStore.duration) }}</span>
      </div>
    </div>

    <div class="player-right">
      <button type="button" class="ctrl-btn" title="静音" @click.stop="toggleMute">
        <el-icon><Mute v-if="volume === 0" /><Bell v-else /></el-icon>
      </button>
      <div class="volume-track" @click="setVolumeByClick" ref="volumeTrack">
        <div class="volume-fill" :style="{ width: volume + '%' }"></div>
        <div class="volume-thumb" :style="{ left: volume + '%' }"></div>
      </div>
      <audio ref="audioRef" :src="playerStore.streamUrl" @timeupdate="onTimeUpdate"
        @loadedmetadata="onLoaded" @ended="onEnded" @error="onError" @canplay="onCanPlay" />
    </div>

    <el-dialog v-model="showQueue" title="播放队列" width="460px" append-to-body destroy-on-close>
      <el-empty v-if="!playerStore.queue.length" description="队列为空" />
      <div v-for="(song, i) in playerStore.queue" :key="i"
        class="queue-item" :class="{ active: i === playerStore.queueIndex }"
        @click="playQueueIndex(i)">
        <span class="q-index">{{ i + 1 }}</span>
        <img :src="song.coverUrl || '/default-cover.png'" class="q-cover" />
        <span class="q-title">{{ song.title }}</span>
        <span class="q-artist">{{ song.artist }}</span>
        <button type="button" class="q-remove" title="移除" @click.stop="removeFromQueue(i)">
          <el-icon><Close /></el-icon>
        </button>
      </div>
    </el-dialog>

    <el-dialog v-model="showPlDialog" title="添加到歌单" width="400px" append-to-body destroy-on-close>
      <el-empty v-if="!playlists.length" description="暂无歌单" />
      <div v-for="pl in playlists" :key="pl.id" class="pl-option" @click="handleAddToPlaylist(pl.id)">{{ pl.name }}</div>
      <template #footer>
        <el-input v-model="newPlName" placeholder="新建歌单名称" style="width: 180px" />
        <el-button type="primary" :disabled="!newPlName.trim()" @click="handleCreateAndAdd">新建并添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  Bell, Close, DArrowLeft, DArrowRight, Mute, Plus, Refresh, RefreshRight,
  Star, StarFilled, Switch, Tickets, VideoPause, VideoPlay
} from '@element-plus/icons-vue'
import { usePlayerStore } from '../store/usePlayerStore'
import { useUserStore } from '../store/useUserStore'
import { recordPlay, toggleLike, checkLiked, getMyPlaylists, addToPlaylist, createPlaylist } from '../api/user'
import { formatTime } from '../utils/format'
import { ElMessage } from 'element-plus'

const router = useRouter()
const playerStore = usePlayerStore()
const userStore = useUserStore()
const audioRef = ref(null)
const progressTrack = ref(null)
const volumeTrack = ref(null)
const volume = ref(70)
const showQueue = ref(false)
const savedVolume = ref(70)
const liked = ref(false)
const lastRecordedId = ref(null)
const showPlDialog = ref(false)
const playlists = ref([])
const newPlName = ref('')

const progressPercent = computed(() => {
  if (!playerStore.duration) return 0
  return (playerStore.currentTime / playerStore.duration) * 100
})

const modeLabel = computed(() => {
  if (playerStore.playMode === 'random') return '随机播放'
  if (playerStore.playMode === 'single') return '单曲循环'
  return '列表循环'
})

function openQueueDialog() {
  showQueue.value = true
}

function cycleMode() {
  const modes = ['list', 'random', 'single']
  const idx = modes.indexOf(playerStore.playMode)
  playerStore.playMode = modes[(idx + 1) % modes.length]
}

function seekByClick(e) {
  if (!progressTrack.value || !audioRef.value) return
  const rect = progressTrack.value.getBoundingClientRect()
  const ratio = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width))
  audioRef.value.currentTime = ratio * playerStore.duration
}

function setVolumeByClick(e) {
  if (!volumeTrack.value) return
  const rect = volumeTrack.value.getBoundingClientRect()
  const ratio = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width))
  const newVol = Math.round(ratio * 100)
  volume.value = newVol
  setVolume(newVol)
}

function toggleMute() {
  if (volume.value > 0) { savedVolume.value = volume.value; setVolume(0); volume.value = 0 }
  else { const v = savedVolume.value || 70; setVolume(v); volume.value = v }
}

function onTimeUpdate() {
  if (audioRef.value) playerStore.updateProgress(audioRef.value.currentTime, audioRef.value.duration)
}
function onLoaded() {
  if (audioRef.value) { playerStore.updateProgress(0, audioRef.value.duration); setVolume(volume.value) }
}
function onCanPlay() {
  if (audioRef.value && playerStore.isPlaying) {
    audioRef.value.play().catch(() => {})
    const sid = playerStore.currentSong?.id
    if (sid && sid !== lastRecordedId.value) { lastRecordedId.value = sid; recordPlay(sid).catch(() => {}) }
  }
}
function onEnded() { playerStore.next() }
function onError() { playerStore.isPlaying = false }
function setVolume(val) { if (audioRef.value) audioRef.value.volume = val / 100 }

function playQueueIndex(idx) {
  playerStore.queueIndex = idx
  playerStore.currentSong = playerStore.queue[idx]
  playerStore.isPlaying = true
  showQueue.value = false
}

function removeFromQueue(idx) {
  if (playerStore.queue.length <= 1) return
  playerStore.queue.splice(idx, 1)
  if (idx < playerStore.queueIndex) playerStore.queueIndex--
  else if (idx === playerStore.queueIndex) {
    const n = Math.min(idx, playerStore.queue.length - 1)
    playerStore.queueIndex = n; playerStore.currentSong = playerStore.queue[n]; playerStore.isPlaying = true
  }
}

async function handleLike() {
  const sid = playerStore.currentSong?.id; if (!sid) return
  try { const res = await toggleLike(sid); liked.value = res.liked; ElMessage.success(res.liked ? '已收藏' : '已取消收藏') } catch (e) { ElMessage.error('请先登录') }
}
async function openPlaylistDialog() {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); return }
  try { playlists.value = await getMyPlaylists() } catch (e) { playlists.value = [] }
  showPlDialog.value = true
}
async function handleAddToPlaylist(plId) {
  const sid = playerStore.currentSong?.id; if (!sid) return
  try { await addToPlaylist(plId, sid); ElMessage.success('已添加到歌单'); showPlDialog.value = false } catch (e) { ElMessage.error('操作失败') }
}
async function handleCreateAndAdd() {
  const name = newPlName.value.trim(); const sid = playerStore.currentSong?.id; if (!name || !sid) return
  try { const pl = await createPlaylist({ name }); await addToPlaylist(pl.id, sid); userStore.refreshPlaylists(); ElMessage.success('已创建'); showPlDialog.value = false; newPlName.value = '' } catch (e) { ElMessage.error('操作失败') }
}

watch(() => playerStore.currentSong, async (song) => {
  if (song?.id) { try { const res = await checkLiked(song.id); liked.value = res.liked } catch (e) { liked.value = false } }
}, { immediate: true })
function goToSong() { const id = playerStore.currentSong?.id; if (id) router.push('/song/' + id) }

watch(() => playerStore.isPlaying, (p) => {
  if (!audioRef.value) return
  if (p) {
    if (audioRef.value.readyState >= HTMLMediaElement.HAVE_FUTURE_DATA) {
      audioRef.value.play().catch(() => {})
      const sid = playerStore.currentSong?.id
      if (sid && sid !== lastRecordedId.value) { lastRecordedId.value = sid; recordPlay(sid).catch(() => {}) }
    }
  } else audioRef.value.pause()
}, { immediate: true })
</script>

<style scoped>
.player-bar {
  position: fixed;
  bottom: 14px;
  left: 244px;
  right: 24px;
  height: 88px;
  z-index: 1000;
  background: var(--player-bg);
  border: 1px solid var(--border);
  border-radius: 24px;
  box-shadow: 0 24px 70px rgba(0,0,0,0.18);
  backdrop-filter: blur(28px) saturate(1.2);
  display: grid;
  grid-template-columns: minmax(250px, 320px) minmax(360px, 1fr) minmax(150px, 210px);
  align-items: center;
  gap: 22px;
  padding: 0 20px;
  user-select: none;
}
.player-left { min-width: 0; display: flex; align-items: center; gap: 12px; }
.player-cover {
  width: 58px;
  height: 58px;
  border-radius: 14px;
  object-fit: cover;
  cursor: pointer;
  box-shadow: 0 12px 26px rgba(0,0,0,0.18);
}
.song-info { min-width: 0; flex: 1; }
.song-title {
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 800;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
}
.song-title:hover { color: var(--accent); }
.song-artist {
  margin-top: 4px;
  color: var(--text-muted);
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.action-btn, .ctrl-btn {
  width: 34px;
  height: 34px;
  border: 0;
  border-radius: 50%;
  background: transparent;
  color: var(--text-muted);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.18s ease;
}
.action-btn:hover, .ctrl-btn:hover { color: var(--text-primary); background: var(--bg-hover); transform: translateY(-1px); }
.player-center { display: flex; flex-direction: column; align-items: center; gap: 10px; min-width: 0; }
.controls { display: flex; align-items: center; gap: 12px; }
.play-btn {
  width: 52px;
  height: 52px;
  border: 0;
  border-radius: 50%;
  background: var(--accent-play);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 16px 34px var(--accent-glow);
  transition: transform 0.18s ease, filter 0.18s ease;
}
.play-btn:hover { transform: scale(1.06); filter: brightness(1.06); }
.progress-bar { display: flex; align-items: center; gap: 10px; width: 100%; max-width: 680px; }
.time { min-width: 38px; color: var(--text-dim); font-size: 11px; text-align: center; font-variant-numeric: tabular-nums; }
.progress-track, .volume-track {
  position: relative;
  height: 5px;
  border-radius: 999px;
  background: rgba(128,128,128,0.24);
  cursor: pointer;
}
.progress-track { flex: 1; }
.volume-track { width: 100px; }
.progress-fill, .volume-fill {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  border-radius: inherit;
  background: var(--accent-play);
}
.progress-thumb, .volume-thumb {
  position: absolute;
  top: 50%;
  width: 13px;
  height: 13px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.28);
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: opacity 0.18s ease;
}
.progress-track:hover .progress-thumb, .volume-track:hover .volume-thumb { opacity: 1; }
.player-right { display: flex; align-items: center; justify-content: flex-end; gap: 10px; }
.queue-item {
  display: grid;
  grid-template-columns: 28px 42px minmax(0, 1fr) 94px 28px;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 12px;
  color: var(--text-muted);
  cursor: pointer;
}
.queue-item:hover, .queue-item.active { background: var(--bg-active); color: var(--text-primary); }
.q-index { color: var(--text-dim); font-size: 12px; text-align: center; }
.q-cover { width: 42px; height: 42px; border-radius: 8px; object-fit: cover; }
.q-title, .q-artist { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.q-title { color: var(--text-secondary); font-size: 13px; font-weight: 750; }
.q-artist { color: var(--text-dim); font-size: 12px; }
.q-remove { border: 0; background: transparent; color: var(--text-dim); cursor: pointer; opacity: 0; }
.queue-item:hover .q-remove { opacity: 1; }
.pl-option { padding: 14px 16px; cursor: pointer; border-radius: 10px; color: var(--text-muted); }
.pl-option:hover { background: var(--bg-active); color: var(--accent); }

@media (max-width: 1100px) {
  .player-bar { left: 18px; right: 18px; grid-template-columns: minmax(210px, 280px) 1fr 140px; }
}
</style>
