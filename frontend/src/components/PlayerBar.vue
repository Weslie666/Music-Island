<template>
  <div class="player-bar">
    <!-- Song info -->
    <div class="player-left">
      <img :src="playerStore.currentSong?.coverUrl || '/default-cover.png'"
        class="player-cover" :class="{ 'playing-pulse': playerStore.isPlaying }"
        @click="goToSong" />
      <div class="song-info">
        <div class="song-title" @click="goToSong">{{ playerStore.currentSong?.title || '未选择' }}</div>
        <div class="song-artist">{{ playerStore.currentSong?.artist || '' }}</div>
      </div>
      <button class="action-btn" :title="liked ? '取消收藏' : '收藏'" @click="handleLike">
        <span v-if="liked" class="icon-heart-filled">♥</span>
        <span v-else class="icon-heart">♡</span>
      </button>
      <button class="action-btn" title="添加到歌单" @click="openPlaylistDialog">
        <span class="icon-plus">＋</span>
      </button>
    </div>

    <!-- Controls -->
    <div class="player-center">
      <div class="controls">
        <button class="ctrl-btn" :title="modeLabel" @click="cycleMode">
          <span v-if="playerStore.playMode === 'list'" class="icon-mode">🔀</span>
          <span v-else-if="playerStore.playMode === 'random'" class="icon-mode">🔀</span>
          <span v-else class="icon-mode">🔂</span>
        </button>
        <button class="ctrl-btn" title="上一首" @click="playerStore.prev()">
          <span class="icon-prev">⏮</span>
        </button>
        <button class="play-btn" @click="playerStore.togglePlay()">
          <span v-if="playerStore.isPlaying" class="icon-pause">⏸</span>
          <span v-else class="icon-play">▶</span>
        </button>
        <button class="ctrl-btn" title="下一首" @click="playerStore.next()">
          <span class="icon-next">⏭</span>
        </button>
        <button class="ctrl-btn" title="播放队列" @click="showQueue = true">
          <span class="icon-queue">☰</span>
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

    <!-- Volume -->
    <div class="player-right">
      <button class="ctrl-btn" title="静音" @click="toggleMute">
        <span v-if="volume === 0" class="icon-vol">🔇</span>
        <span v-else-if="volume < 40" class="icon-vol">🔈</span>
        <span v-else class="icon-vol">🔊</span>
      </button>
      <div class="volume-track" @click="setVolumeByClick" ref="volumeTrack">
        <div class="volume-fill" :style="{ width: volume + '%' }"></div>
        <div class="volume-thumb" :style="{ left: volume + '%' }"></div>
      </div>
      <audio ref="audioRef" :src="playerStore.streamUrl" @timeupdate="onTimeUpdate"
        @loadedmetadata="onLoaded" @ended="onEnded" @error="onError" @canplay="onCanPlay" />
    </div>

    <!-- Queue dialog -->
    <el-dialog v-model="showQueue" title="播放队列" width="420px">
      <el-empty v-if="!playerStore.queue.length" description="队列为空" />
      <div v-for="(song, i) in playerStore.queue" :key="i"
        class="queue-item" :class="{ active: i === playerStore.queueIndex }">
        <span class="q-index">{{ i + 1 }}</span>
        <span class="q-title">{{ song.title }}</span>
        <span class="q-artist">{{ song.artist }}</span>
        <button class="q-remove" @click.stop="removeFromQueue(i)">✕</button>
      </div>
    </el-dialog>

    <!-- Add to playlist -->
    <el-dialog v-model="showPlDialog" title="添加到歌单" width="400px">
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
const isLiked = ref(false)
const lastRecordedId = ref(null)
const showPlDialog = ref(false)
const playlists = ref([])
const newPlName = ref('')

const progressPercent = computed(() => {
  if (!playerStore.duration) return 0
  return (playerStore.currentTime / playerStore.duration) * 100
})

const volumeIcon = computed(() => {
  if (volume.value === 0) return '🔇'
  if (volume.value < 40) return '🔈'
  return '🔊'
})

// Progress track click
function seekByClick(e) {
  if (!progressTrack.value || !audioRef.value) return
  const rect = progressTrack.value.getBoundingClientRect()
  const ratio = (e.clientX - rect.left) / rect.width
  audioRef.value.currentTime = ratio * playerStore.duration
}

// Volume track click
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
  if (audioRef.value) { playerStore.updateProgress(0, audioRef.value.duration); volume.value = Math.round(audioRef.value.volume * 100) }
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
  try { const res = await toggleLike(sid); isLiked.value = res.liked; ElMessage.success(res.liked ? '已收藏' : '已取消收藏') } catch (e) { ElMessage.error('请先登录') }
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
  if (song?.id) { try { const res = await checkLiked(song.id); isLiked.value = res.liked } catch (e) { isLiked.value = false } }
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
  position: fixed; bottom: 0; left: 0; right: 0; height: 90px; z-index: 1000;
  background: var(--player-bg); border-top: 1px solid var(--border);
  display: flex; align-items: center; padding: 0 24px;
  transition: background 0.4s ease;
  user-select: none;
}

/* === LEFT: Song Info === */
.player-left { width: 280px; display: flex; align-items: center; gap: 14px; }
.player-cover {
  width: 56px; height: 56px; border-radius: 6px; object-fit: cover;
  cursor: pointer; transition: transform 0.2s; flex-shrink: 0;
}
.player-cover:hover { transform: scale(1.05); }
.song-info { min-width: 0; flex: 1; }
.song-title {
  font-size: 14px; font-weight: 600; color: var(--text-secondary);
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap; cursor: pointer;
  line-height: 1.3;
}
.song-title:hover { text-decoration: underline; }
.song-artist {
  font-size: 12px; color: var(--text-dim); overflow: hidden;
  text-overflow: ellipsis; white-space: nowrap; margin-top: 2px;
}

/* Action buttons */
.action-btn {
  background: none; border: none; cursor: pointer; padding: 6px;
  color: var(--text-dim); font-size: 18px; line-height: 1;
  transition: color 0.15s; flex-shrink: 0;
}
.action-btn:hover { color: var(--accent); }
.icon-heart-filled { color: var(--accent); }
.icon-plus { font-size: 20px; font-weight: 300; }

/* === CENTER: Controls === */
.player-center {
  flex: 1; display: flex; flex-direction: column;
  align-items: center; gap: 12px; max-width: 640px; margin: 0 auto;
}
.controls { display: flex; align-items: center; gap: 24px; }

/* Control buttons */
.ctrl-btn {
  background: none; border: none; cursor: pointer; padding: 4px;
  color: var(--text-dim); font-size: 14px; line-height: 1;
  transition: color 0.15s, transform 0.15s;
}
.ctrl-btn:hover { color: var(--text-primary); transform: scale(1.1); }
.icon-mode { font-size: 14px; }
.icon-prev, .icon-next { font-size: 14px; }
.icon-queue { font-size: 16px; font-weight: 700; }

/* Play button - large prominent circle */
.play-btn {
  width: 56px; height: 56px; border-radius: 50%; border: none; cursor: pointer;
  background: #fff; color: #000;
  display: flex; align-items: center; justify-content: center;
  transition: transform 0.2s ease, background 0.2s;
  flex-shrink: 0;
}
.play-btn:hover { transform: scale(1.06); background: #f0f0f0; }
.icon-play { font-size: 20px; margin-left: 3px; }
.icon-pause { font-size: 18px; }

/* Progress bar */
.progress-bar {
  display: flex; align-items: center; gap: 10px; width: 100%;
}
.time {
  font-size: 11px; color: var(--text-dim); min-width: 38px;
  text-align: center; font-variant-numeric: tabular-nums;
}
.progress-track {
  flex: 1; height: 4px; background: rgba(128,128,128,0.25);
  border-radius: 2px; cursor: pointer; position: relative;
  transition: height 0.15s;
}
.progress-track:hover { height: 6px; }
.progress-fill {
  height: 100%; background: var(--accent); border-radius: 2px;
  position: absolute; left: 0; top: 0; transition: width 0.1s linear;
}
.progress-track:hover .progress-fill { background: #1db954; }
.progress-thumb {
  position: absolute; top: 50%; transform: translate(-50%, -50%);
  width: 12px; height: 12px; border-radius: 50%; background: #fff;
  opacity: 0; transition: opacity 0.2s; pointer-events: none;
}
.progress-track:hover .progress-thumb { opacity: 1; }

/* === RIGHT: Volume === */
.player-right {
  width: 200px; display: flex; align-items: center; gap: 10px;
  justify-content: flex-end;
}
.icon-vol { font-size: 14px; }

.volume-track {
  width: 100px; height: 4px; background: rgba(128,128,128,0.25);
  border-radius: 2px; cursor: pointer; position: relative;
  transition: height 0.15s;
}
.volume-track:hover { height: 6px; }
.volume-fill {
  height: 100%; background: var(--text-muted); border-radius: 2px;
  position: absolute; left: 0; top: 0;
}
.volume-track:hover .volume-fill { background: #1db954; }
.volume-thumb {
  position: absolute; top: 50%; transform: translate(-50%, -50%);
  width: 12px; height: 12px; border-radius: 50%; background: #fff;
  opacity: 0; transition: opacity 0.2s; pointer-events: none;
}
.volume-track:hover .volume-thumb { opacity: 1; }

/* === Queue dialog === */
.queue-item {
  display: flex; align-items: center; gap: 12px; padding: 10px 14px;
  border-radius: 8px; transition: background 0.15s; color: var(--text-muted);
}
.queue-item:hover { background: var(--bg-hover); }
.queue-item.active { color: var(--accent); background: var(--bg-active); }
.q-index { width: 24px; font-size: 12px; color: var(--text-dim); text-align: center; }
.q-title { flex: 1; font-size: 14px; color: var(--text-secondary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.q-artist { font-size: 12px; color: var(--text-dim); width: 90px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.q-remove { background: none; border: none; cursor: pointer; color: var(--text-dim); font-size: 14px; opacity: 0; transition: opacity 0.15s; }
.queue-item:hover .q-remove { opacity: 1; }
.q-remove:hover { color: var(--accent); }

/* === Playlist dialog === */
.pl-option {
  padding: 14px 16px; cursor: pointer; border-radius: 8px;
  transition: all 0.15s; color: var(--text-muted); font-size: 14px;
}
.pl-option:hover { background: var(--bg-active); color: var(--accent); }

/* === Dark theme === */
[data-theme="dark"] .player-bar {
  background: rgba(18, 18, 18, 0.95);
  backdrop-filter: blur(24px);
}
[data-theme="dark"] .play-btn { background: #fff; color: #000; }
</style>
