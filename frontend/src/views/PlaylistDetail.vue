<template>
  <div class="playlist-detail mi-page" v-if="playlist">
    <div class="pl-header mi-card">
      <div class="pl-cover-wrapper">
        <img v-if="coverImages.length <= 3"
          :src="coverImages[0] || '/default-cover.png'" class="pl-cover" />
        <div v-else class="pl-cover-grid">
          <img v-for="(url, i) in coverImages.slice(0, 4)" :key="i"
            :src="url" class="pl-cover-cell" />
        </div>
      </div>
      <div class="pl-info">
        <h1>{{ playlist.name }}</h1>
        <p class="pl-desc" v-if="playlist.description">{{ playlist.description }}</p>
        <p class="pl-meta">{{ songs.length }} 首歌曲</p>
        <div class="pl-actions" v-if="isOwner">
          <el-button size="small" @click="showEdit = true">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete">删除歌单</el-button>
        </div>
      </div>
    </div>

    <el-empty v-if="!songs.length" description="歌单为空" />
    <div class="song-list" v-if="songs.length">
      <div v-for="(song, i) in songs" :key="song.id" class="song-row"
        @click="playSong(song, i)">
        <span class="s-index">{{ i + 1 }}</span>
        <img :src="song.coverUrl || '/default-cover.png'" class="s-cover" />
        <div class="s-info">
          <div class="s-title">{{ song.title }}</div>
          <div class="s-artist">{{ song.artist }}</div>
        </div>
        <el-button v-if="isOwner" icon="Close" circle size="small" class="s-remove"
          @click.stop="handleRemove(song.id)" />
      </div>
    </div>

    <el-dialog v-model="showEdit" title="编辑歌单" width="400px">
      <el-form :model="form" label-width="60px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEdit = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPlaylistDetail, updatePlaylist, deletePlaylist, removeFromPlaylist } from '../api/user'
import { usePlayerStore } from '../store/usePlayerStore'
import { useUserStore } from '../store/useUserStore'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const userStore = useUserStore()

const playlist = ref(null)
const songs = ref([])
const showEdit = ref(false)
const form = ref({ name: '', description: '' })

const isOwner = computed(() =>
  userStore.userInfo && playlist.value && userStore.userInfo.id === playlist.value.userId
)

const coverImages = computed(() =>
  songs.value.map(s => s.coverUrl).filter(Boolean)
)

async function loadPlaylist(id) {
  playlist.value = null
  songs.value = []
  showEdit.value = false
  try {
    const data = await getPlaylistDetail(id)
    playlist.value = data.playlist
    songs.value = data.songs || []
    form.value = {
      name: data.playlist?.name || '',
      description: data.playlist?.description || ''
    }
  } catch (e) { /* not found */ }
}

watch(() => route.params.id, (id) => {
  if (id) loadPlaylist(id)
}, { immediate: true })

function playSong(song, idx) {
  playerStore.play(song, songs.value)
  playerStore.queueIndex = idx
}

async function handleRemove(songId) {
  try {
    await removeFromPlaylist(route.params.id, songId)
    songs.value = songs.value.filter(s => s.id !== songId)
  } catch (e) { /* ignore */ }
}

function handleDelete() {
  ElMessageBox.confirm('确定删除该歌单吗？', '确认', { type: 'warning' }).then(async () => {
    await deletePlaylist(route.params.id)
    userStore.refreshPlaylists()
    router.push('/')
  }).catch(() => {})
}

async function handleUpdate() {
  await updatePlaylist(route.params.id, form.value)
  playlist.value.name = form.value.name
  playlist.value.description = form.value.description
  showEdit.value = false
}
</script>

<style scoped>
.playlist-detail { max-width: 1100px; }
.pl-header { display: flex; gap: 30px; margin-bottom: 28px; padding: 32px; align-items: center; }
.pl-cover-wrapper { width: 220px; height: 220px; flex-shrink: 0; }
.pl-cover { width: 100%; height: 100%; border-radius: 12px; object-fit: cover; box-shadow: 0 8px 32px rgba(0,0,0,0.2); }
.pl-cover-grid {
  width: 100%; height: 100%; border-radius: 12px; overflow: hidden;
  display: grid; grid-template-columns: 1fr 1fr; grid-template-rows: 1fr 1fr; gap: 2px;
}
.pl-cover-cell { width: 100%; height: 100%; object-fit: cover; }
.pl-info { flex: 1; display: flex; flex-direction: column; gap: 10px; }
.pl-info h1 { font-size: 38px; color: var(--text-primary); font-weight: 900; }
.pl-desc { font-size: 14px; color: var(--text-muted); }
.pl-meta { font-size: 14px; color: var(--text-dim); }
.pl-actions { display: flex; gap: 8px; margin-top: 12px; }
.song-list { background: var(--card-bg); border-radius: var(--radius-md); border: 1px solid var(--border); box-shadow: var(--card-shadow); overflow: hidden; }
.song-row {
  display: flex; align-items: center; gap: 14px; padding: 12px 16px;
  cursor: pointer; transition: background 0.15s; border-radius: 8px;
}
.song-row:hover { background: var(--bg-hover); }
.s-index { width: 28px; text-align: center; font-size: 13px; color: var(--text-dim); }
.s-cover { width: 40px; height: 40px; border-radius: 4px; object-fit: cover; }
.s-info { flex: 1; min-width: 0; }
.s-title { font-size: 14px; font-weight: 500; color: var(--text-secondary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.s-artist { font-size: 12px; color: var(--text-dim); margin-top: 2px; }
.s-remove { opacity: 0; transition: opacity 0.15s; }
.song-row:hover .s-remove { opacity: 1; }
</style>
