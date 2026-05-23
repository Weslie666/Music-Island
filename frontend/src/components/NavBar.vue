<template>
  <div class="navbar">
    <div class="nav-left">
      <router-link to="/" class="logo">
        <span class="logo-icon">♪</span>
        <span class="logo-text">Music Island</span>
      </router-link>
      <router-link to="/discover" class="nav-link">发现</router-link>
    </div>
    <div class="nav-center">
      <el-input v-model="keyword" placeholder="想听什么歌？" prefix-icon="Search"
        class="search-input" size="large" @keyup.enter="search" />
    </div>
    <div class="nav-right">
      <el-button :icon="themeStore.isDark ? 'Sunny' : 'Moon'" circle
        class="theme-btn" :title="themeStore.isDark ? '切换亮色' : '切换暗色'"
        @click="themeStore.toggle(); themeStore.applyTheme()" />
      <template v-if="userStore.isLoggedIn">
        <el-dropdown trigger="click">
          <span class="user-info">
            <el-avatar :size="34" :src="userStore.userInfo?.avatar" />
            <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
            <el-icon class="arrow"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/my/favorites')">
                <el-icon><Star /></el-icon> 我的收藏
              </el-dropdown-item>
              <el-dropdown-item @click="$router.push('/my/history')">
                <el-icon><Clock /></el-icon> 播放历史
              </el-dropdown-item>
              <el-dropdown-item v-if="userStore.isAdmin" @click="$router.push('/admin')">
                <el-icon><Setting /></el-icon> 管理后台
              </el-dropdown-item>
              <el-dropdown-item divided @click="logout">
                <el-icon><SwitchButton /></el-icon> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
      <template v-else>
        <el-button size="small" round class="btn-outline" @click="$router.push('/login')">登录</el-button>
        <el-button size="small" round type="primary" @click="$router.push('/register')">注册</el-button>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/useUserStore'
import { useThemeStore } from '../store/useThemeStore'
import { ArrowDown, Star, Clock, Setting, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const themeStore = useThemeStore()
const keyword = ref('')

function search() { if (keyword.value.trim()) router.push({ path: '/search', query: { q: keyword.value.trim() } }) }
function logout() { userStore.logout(); router.push('/') }
</script>

<style scoped>
.navbar {
  position: fixed; top: 0; left: 0; right: 0; height: 64px; z-index: 1000;
  background: var(--nav-bg); backdrop-filter: blur(20px);
  display: flex; align-items: center; padding: 0 28px;
  border-bottom: 1px solid var(--border);
  transition: background 0.4s ease;
}
.nav-left { display: flex; align-items: center; gap: 32px; }
.logo { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.logo-icon {
  font-size: 22px; color: var(--accent);
  display: flex; align-items: center; justify-content: center;
  width: 34px; height: 34px; border-radius: 10px;
  background: var(--accent-glow);
}
.logo-text { font-size: 19px; font-weight: 800; color: var(--text-primary); letter-spacing: -0.5px; }
.nav-link { font-size: 14px; font-weight: 500; color: var(--text-muted); transition: color 0.2s; }
.nav-link:hover { color: var(--text-primary); }
.nav-link.router-link-active { color: var(--text-primary); }
.nav-center { flex: 1; display: flex; justify-content: center; padding: 0 40px; }
.search-input { max-width: 480px; }
.nav-right { display: flex; align-items: center; gap: 12px; }
.theme-btn { color: var(--text-muted); border-color: var(--border); transition: all 0.2s; }
.theme-btn:hover { color: var(--accent); border-color: var(--accent); }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 4px 8px; border-radius: 8px; transition: background 0.2s; }
.user-info:hover { background: var(--bg-hover); }
.username { font-size: 13px; font-weight: 500; color: var(--text-secondary); }
.arrow { font-size: 12px; color: var(--text-dim); }
.btn-outline { color: var(--text-secondary); border-color: var(--border); }
.btn-outline:hover { border-color: var(--accent); color: var(--accent); }

[data-theme="light"] .navbar { background: rgba(255,255,255,0.85); }
</style>
