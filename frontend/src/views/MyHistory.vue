<template>
  <div class="my-history mi-page">
    <div class="page-head mi-card">
      <div>
        <div class="mi-kicker">Listening History</div>
        <h1>播放历史</h1>
      </div>
      <span class="mi-pill">{{ total }} 条记录</span>
    </div>
    <el-empty v-if="!records.length && !loading" description="还没有播放记录" />
    <div class="history-list" v-if="records.length">
      <div v-for="item in records" :key="item.id + '-' + item.play_time" class="history-item"
        @click="playSong(item)">
        <span class="h-index">{{ records.indexOf(item) + 1 + (page - 1) * limit }}</span>
        <img :src="item.coverUrl || '/default-cover.png'" class="h-cover" />
        <div class="h-info">
          <div class="h-title">{{ item.title }}</div>
          <div class="h-artist">{{ item.artist }}</div>
        </div>
        <span class="h-time">{{ formatPlayTime(item.playTime) }}</span>
      </div>
    </div>
    <el-pagination v-if="total > limit" layout="prev, pager, next" :total="total"
      :page-size="limit" :current-page="page" @current-change="load"
      class="pagination" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyHistory } from '../api/user'
import { usePlayerStore } from '../store/usePlayerStore'

const playerStore = usePlayerStore()
const records = ref([])
const page = ref(1)
const limit = 20
const total = ref(0)
const loading = ref(false)

async function load(p = 1) {
  loading.value = true
  page.value = p
  try {
    const data = await getMyHistory(p, limit)
    records.value = data.records || []
    total.value = data.total || 0
  } catch (e) { /* no data */ }
  loading.value = false
}

function playSong(song) {
  playerStore.play(song, records.value.map(r => ({ id: r.id, title: r.title, artist: r.artist,
    coverUrl: r.coverUrl, fileUrl: r.fileUrl, duration: r.duration })))
}

function formatPlayTime(timeStr) {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  const m = d.getMonth() + 1
  const day = d.getDate()
  return m + '-' + (day < 10 ? '0' : '') + day
}

onMounted(() => load())
</script>

<style scoped>
.my-history { max-width: 980px; }
.page-head { display: flex; justify-content: space-between; align-items: end; padding: 28px 32px; margin-bottom: 24px; }
.page-head h1 { margin-top: 8px; color: var(--text-primary); font-size: 36px; font-weight: 900; }
.history-list { background: var(--card-bg); border: 1px solid var(--border); border-radius: var(--radius-md); box-shadow: var(--card-shadow); overflow: hidden; }
.history-item {
  display: flex; align-items: center; gap: 14px; padding: 10px 16px;
  cursor: pointer; transition: background 0.15s;
}
.history-item:hover { background: var(--bg-hover); }
.h-index { width: 28px; text-align: center; font-size: 13px; color: var(--text-dim); }
.h-cover { width: 40px; height: 40px; border-radius: 4px; object-fit: cover; }
.h-info { flex: 1; min-width: 0; }
.h-title { font-size: 14px; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.h-artist { font-size: 12px; color: var(--text-dim); margin-top: 2px; }
.h-time { font-size: 12px; color: var(--text-dim); white-space: nowrap; }
.pagination { margin-top: 24px; justify-content: center; display: flex; }
</style>
