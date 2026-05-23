<template>
  <div class="sidebar">
    <div class="menu">
      <router-link to="/" class="menu-item" active-class="active">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </router-link>
      <router-link to="/discover" class="menu-item" active-class="active">
        <el-icon><Compass /></el-icon>
        <span>发现音乐</span>
      </router-link>
      <router-link to="/my/favorites" class="menu-item" active-class="active">
        <el-icon><Star /></el-icon>
        <span>我的收藏</span>
      </router-link>
      <router-link to="/my/history" class="menu-item" active-class="active">
        <el-icon><Clock /></el-icon>
        <span>播放历史</span>
      </router-link>
    </div>

    <div class="playlist-section" v-if="playlists.length">
      <div class="section-label">我的歌单</div>
      <router-link v-for="pl in playlists" :key="pl.id"
        :to="'/playlist/' + pl.id" class="pl-item"
        active-class="pl-active">
        <div class="pl-cover-mini">
          <template v-if="pl.covers && pl.covers.length">
            <img v-if="pl.covers.length <= 3"
              :src="pl.covers[0]" class="pl-mini-single" />
            <div v-else class="pl-mini-grid">
              <img v-for="(url, i) in pl.covers.slice(0, 4)" :key="i"
                :src="url" class="pl-mini-cell" />
            </div>
          </template>
          <el-icon v-else><Picture /></el-icon>
        </div>
        <span class="pl-name">{{ pl.name }}</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useUserStore } from '../store/useUserStore'
import { getMyPlaylists } from '../api/user'

const userStore = useUserStore()
const playlists = ref([])

async function loadPlaylists() {
  if (userStore.isLoggedIn) {
    try {
      playlists.value = await getMyPlaylists()
    } catch (e) { playlists.value = [] }
  } else {
    playlists.value = []
  }
}

// Initial load + reload on login state change
watch(() => userStore.isLoggedIn, () => loadPlaylists(), { immediate: true })

// Reload when playlistVersion bumps — covers create/delete from any component
watch(() => userStore.playlistVersion, () => loadPlaylists())
</script>

<style scoped>
.sidebar {
  width: 220px; min-height: calc(100vh - 64px);
  background: var(--bg-surface);
  border-right: 1px solid var(--border);
  padding: 8px 0; overflow-y: auto;
}
.menu { display: flex; flex-direction: column; padding: 8px; }
.menu-item {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 16px; border-radius: 8px;
  font-size: 14px; font-weight: 500; color: var(--text-muted);
  transition: all 0.2s; margin: 2px 0;
}
.menu-item:hover { background: var(--bg-hover); color: var(--text-primary); }
.menu-item.active { color: var(--accent); background: var(--bg-active); }

.playlist-section { margin-top: 12px; padding: 0 8px; }
.section-label {
  font-size: 11px; color: var(--text-dim); text-transform: uppercase;
  letter-spacing: 1px; margin-bottom: 8px; padding-left: 16px;
  font-weight: 600;
}
.pl-item {
  display: flex; align-items: center; gap: 10px;
  padding: 8px 16px; border-radius: 8px; margin: 2px 0;
  font-size: 13px; color: var(--text-dim); transition: all 0.2s;
}
.pl-item:hover { background: var(--bg-hover); color: var(--text-primary); }
.pl-item.pl-active { color: var(--accent); background: var(--bg-active); }
.pl-name { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; }

.pl-cover-mini { width: 36px; height: 36px; flex-shrink: 0;
  border-radius: 6px; overflow: hidden; background: var(--bg-hover);
  display: flex; align-items: center; justify-content: center; }
.pl-mini-single { width: 100%; height: 100%; object-fit: cover; }
.pl-mini-grid {
  width: 100%; height: 100%;
  display: grid; grid-template-columns: 1fr 1fr; grid-template-rows: 1fr 1fr; gap: 1px;
}
.pl-mini-cell { width: 100%; height: 100%; object-fit: cover; }
</style>
