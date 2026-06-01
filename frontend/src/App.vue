<template>
  <div class="app-container">
    <div class="bg-glow bg-glow-1"></div>
    <div class="bg-glow bg-glow-2"></div>
    <NavBar />
    <div class="main-wrapper">
      <SideBar v-if="!isAuthPage" />
      <div class="content" :class="{ 'content-auth': isAuthPage }">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
    <PlayerBar v-if="playerStore.hasSong" />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { usePlayerStore } from './store/usePlayerStore'
import { useThemeStore } from './store/useThemeStore'
import NavBar from './components/NavBar.vue'
import SideBar from './components/SideBar.vue'
import PlayerBar from './components/PlayerBar.vue'

const playerStore = usePlayerStore()
const themeStore = useThemeStore()
const route = useRoute()
const isAuthPage = computed(() => route.path === '/login' || route.path === '/register')
onMounted(() => themeStore.applyTheme())
</script>

<style>
:root,
[data-theme="light"] {
  --bg-body: #f6f4f1;
  --bg-page: radial-gradient(circle at 18% 0%, rgba(233,69,96,0.12), transparent 34%), linear-gradient(180deg, #fffaf7 0%, #f5f3ef 45%, #f7f7f5 100%);
  --bg-surface: rgba(255,255,255,0.82);
  --bg-surface-solid: #ffffff;
  --bg-hover: rgba(27, 28, 32, 0.06);
  --bg-active: rgba(233,69,96,0.12);
  --border: rgba(32, 34, 38, 0.1);
  --border-strong: rgba(32, 34, 38, 0.16);
  --text-primary: #17171b;
  --text-secondary: #2c2d33;
  --text-muted: #686b73;
  --text-dim: #9397a0;
  --accent: #e94560;
  --accent-strong: #c81f40;
  --accent-play: #e94560;
  --accent-glow: rgba(233,69,96,0.18);
  --card-bg: rgba(255,255,255,0.76);
  --card-shadow: 0 12px 32px rgba(33, 28, 24, 0.08);
  --card-hover-shadow: 0 22px 54px rgba(33, 28, 24, 0.15);
  --nav-bg: rgba(255,255,255,0.72);
  --player-bg: rgba(255,255,255,0.86);
  --sidebar-bg: rgba(255,255,255,0.52);
  --input-bg: rgba(255,255,255,0.78);
  --glass-bg: rgba(255,255,255,0.58);
  --hero-ink: #141418;
  --hero-muted: #656973;
  --gradient-1: #e94560;
  --gradient-2: #ff8a65;
  --radius-xs: 8px;
  --radius-sm: 12px;
  --radius-md: 18px;
  --radius-lg: 28px;
}

[data-theme="dark"] {
  --bg-body: #050505;
  --bg-page: radial-gradient(circle at 20% 0%, rgba(29,185,84,0.16), transparent 32%), linear-gradient(180deg, #151515 0%, #070707 42%, #050505 100%);
  --bg-surface: rgba(255,255,255,0.055);
  --bg-surface-solid: #111111;
  --bg-hover: rgba(255,255,255,0.085);
  --bg-active: rgba(29,185,84,0.14);
  --border: rgba(255,255,255,0.09);
  --border-strong: rgba(255,255,255,0.16);
  --text-primary: #ffffff;
  --text-secondary: #e4e4e4;
  --text-muted: #b7b7b7;
  --text-dim: #7f7f7f;
  --accent: #e94560;
  --accent-strong: #ff6078;
  --accent-play: #21c55d;
  --accent-glow: rgba(29,185,84,0.18);
  --card-bg: rgba(255,255,255,0.055);
  --card-shadow: 0 16px 44px rgba(0,0,0,0.35);
  --card-hover-shadow: 0 24px 70px rgba(0,0,0,0.58);
  --nav-bg: rgba(8,8,8,0.72);
  --player-bg: rgba(16,16,16,0.88);
  --sidebar-bg: rgba(8,8,8,0.46);
  --input-bg: rgba(255,255,255,0.08);
  --glass-bg: rgba(255,255,255,0.055);
  --hero-ink: #ffffff;
  --hero-muted: #b7b7b7;
  --gradient-1: #e94560;
  --gradient-2: #21c55d;
}

* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", "Microsoft YaHei", sans-serif;
  background: var(--bg-body);
  color: var(--text-secondary);
  min-height: 100vh;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  transition: background 0.4s ease, color 0.4s ease;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  background: var(--bg-page);
}

/* 背景光晕 */
.bg-glow {
  position: fixed; border-radius: 50%; filter: blur(180px);
  pointer-events: none; z-index: 0; opacity: 0.4;
  transition: opacity 0.4s ease;
}
.bg-glow-1 {
  width: 600px; height: 600px;
  background: var(--accent-glow);
  top: -200px; right: -150px;
}
.bg-glow-2 {
  width: 500px; height: 500px;
  background: rgba(100,100,255,0.08);
  bottom: -150px; left: -100px;
}
[data-theme="light"] .bg-glow { opacity: 0.25; }

.main-wrapper {
  display: flex; margin-top: 64px; position: relative; z-index: 1;
  height: calc(100vh - 64px); overflow: hidden;
}
.content {
  flex: 1;
  padding: 28px 36px;
  padding-bottom: 118px;
  overflow-y: auto;
}
.content-auth { padding-left: 36px; padding-right: 36px; }
a { text-decoration: none; color: inherit; }

/* 页面过渡动画 */
.page-fade-enter-active, .page-fade-leave-active { transition: opacity 0.25s ease, transform 0.25s ease; }
.page-fade-enter-from { opacity: 0; transform: translateY(12px); }
.page-fade-leave-to { opacity: 0; transform: translateY(-12px); }

/* 滚动条 */
::-webkit-scrollbar { width: 8px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: rgba(128,128,128,0.25); border-radius: 4px; }
::-webkit-scrollbar-thumb:hover { background: rgba(128,128,128,0.4); }

/* Element Plus 暗色适配 */
[data-theme="dark"] .el-input__inner { background: rgba(255,255,255,0.06); border-color: rgba(255,255,255,0.1); color: #e0e0e0; }
[data-theme="dark"] .el-input__inner::placeholder { color: #555; }
[data-theme="dark"] .el-dialog { background: #1a1a2e; border-radius: 16px; }
[data-theme="dark"] .el-dialog__title { color: #fff; }
[data-theme="dark"] .el-dialog__body { color: #ccc; }
[data-theme="dark"] .el-pagination button, [data-theme="dark"] .el-pager li { background: rgba(255,255,255,0.04); color: #ccc; border-radius: 6px; }
[data-theme="dark"] .el-pager li.active { background: #e94560; }
[data-theme="dark"] .el-empty__description { color: #888; }
[data-theme="dark"] .el-table { background: transparent; color: #ddd; }
[data-theme="dark"] .el-table th { background: rgba(255,255,255,0.04); color: #aaa; }
[data-theme="dark"] .el-table tr { background: transparent; }
[data-theme="dark"] .el-table td { border-bottom-color: rgba(255,255,255,0.04); }
[data-theme="dark"] .el-tabs__item { color: #aaa; }
[data-theme="dark"] .el-tabs__item.is-active { color: #e94560; }

.el-button--primary {
  --el-button-bg-color: var(--accent);
  --el-button-border-color: var(--accent);
  --el-button-hover-bg-color: var(--accent-strong);
  --el-button-hover-border-color: var(--accent-strong);
}
.el-input__wrapper {
  background: var(--input-bg);
  border-radius: 999px;
  box-shadow: 0 0 0 1px var(--border) inset;
}
.el-input__wrapper.is-focus { box-shadow: 0 0 0 1px var(--accent) inset, 0 12px 26px var(--accent-glow); }
.el-dialog {
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
  box-shadow: var(--card-hover-shadow);
}

.mi-page { max-width: 1400px; margin: 0 auto; }
.mi-kicker {
  color: var(--accent);
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}
.mi-title {
  color: var(--text-primary);
  font-size: 32px;
  line-height: 1.1;
  font-weight: 850;
  letter-spacing: 0;
}
.mi-muted { color: var(--text-muted); }
.mi-card {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  box-shadow: var(--card-shadow);
  backdrop-filter: blur(22px);
}
.mi-song-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 18px;
}
.mi-song-card {
  position: relative;
  overflow: hidden;
  cursor: pointer;
  border-radius: var(--radius-sm);
  background: var(--card-bg);
  border: 1px solid var(--border);
  box-shadow: var(--card-shadow);
  transition: transform 0.28s ease, box-shadow 0.28s ease, border-color 0.28s ease;
}
.mi-song-card:hover {
  transform: translateY(-6px);
  border-color: var(--border-strong);
  box-shadow: var(--card-hover-shadow);
}
.mi-cover-wrap { position: relative; overflow: hidden; background: var(--bg-hover); }
.mi-cover {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  display: block;
  transition: transform 0.45s ease, filter 0.45s ease;
}
.mi-song-card:hover .mi-cover { transform: scale(1.055); filter: saturate(1.06); }
.mi-play-fab {
  position: absolute;
  right: 12px;
  bottom: 12px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 0;
  background: var(--accent-play);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 14px 30px rgba(0,0,0,0.24);
  opacity: 0;
  transform: translateY(10px) scale(0.94);
  transition: opacity 0.24s ease, transform 0.24s ease;
}
.mi-song-card:hover .mi-play-fab { opacity: 1; transform: translateY(0) scale(1); }
.mi-card-info { padding: 13px 14px 15px; }
.mi-card-title {
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.35;
  font-weight: 720;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.mi-card-artist {
  color: var(--text-muted);
  font-size: 12px;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.mi-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 34px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid var(--border);
  background: var(--glass-bg);
  color: var(--text-muted);
  font-size: 13px;
  font-weight: 650;
}


/* 脉冲动画 - 正在播放 */
@keyframes pulse-playing {
  0%, 100% { box-shadow: 0 0 0 0 rgba(29, 185, 84, 0.4); }
  50% { box-shadow: 0 0 0 12px rgba(29, 185, 84, 0); }
}
.playing-pulse { animation: pulse-playing 2s infinite; }

@media (max-width: 1280px) {
  .content { padding: 24px 28px 112px; }
  .mi-song-grid { gap: 16px; }
}

@media (max-width: 980px) {
  .content { padding: 20px 18px 118px; }
  .mi-song-grid { grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 14px; }
}

@media (max-width: 680px) {
  .mi-song-grid { grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); gap: 12px; }
}
</style>
