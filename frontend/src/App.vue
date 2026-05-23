<template>
  <div class="app-container">
    <div class="bg-glow bg-glow-1"></div>
    <div class="bg-glow bg-glow-2"></div>
    <NavBar />
    <div class="main-wrapper">
      <SideBar />
      <div class="content">
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
import { onMounted } from 'vue'
import { usePlayerStore } from './store/usePlayerStore'
import { useThemeStore } from './store/useThemeStore'
import NavBar from './components/NavBar.vue'
import SideBar from './components/SideBar.vue'
import PlayerBar from './components/PlayerBar.vue'

const playerStore = usePlayerStore()
const themeStore = useThemeStore()
onMounted(() => themeStore.applyTheme())
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap');

:root,
[data-theme="light"] {
  --bg-body: #f8f9fa;
  --bg-surface: #ffffff;
  --bg-hover: #f0f1f3;
  --bg-active: #fef0f2;
  --border: #e9eaed;
  --text-primary: #121212;
  --text-secondary: #2a2a2a;
  --text-muted: #6b6b6b;
  --text-dim: #969696;
  --accent: #e94560;
  --accent-glow: rgba(233,69,96,0.15);
  --card-bg: #ffffff;
  --card-shadow: 0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);
  --card-hover-shadow: 0 10px 40px rgba(0,0,0,0.1), 0 4px 12px rgba(0,0,0,0.06);
  --nav-bg: rgba(255,255,255,0.85);
  --player-bg: rgba(255,255,255,0.92);
  --sidebar-bg: rgba(255,255,255,0.7);
  --input-bg: #f0f1f3;
  --glass-bg: rgba(255,255,255,0.7);
  --gradient-1: #e94560;
  --gradient-2: #ff6b6b;
}

[data-theme="dark"] {
  --bg-body: #000000;
  --bg-surface: rgba(255,255,255,0.04);
  --bg-hover: rgba(255,255,255,0.08);
  --bg-active: rgba(233,69,96,0.15);
  --border: rgba(255,255,255,0.08);
  --text-primary: #ffffff;
  --text-secondary: #e4e4e4;
  --text-muted: #b0b0b0;
  --text-dim: #727272;
  --accent: #e94560;
  --accent-glow: rgba(233,69,96,0.2);
  --card-bg: rgba(255,255,255,0.04);
  --card-shadow: 0 1px 2px rgba(0,0,0,0.2);
  --card-hover-shadow: 0 12px 40px rgba(0,0,0,0.5), 0 4px 12px rgba(0,0,0,0.3);
  --nav-bg: rgba(0,0,0,0.8);
  --player-bg: rgba(18,18,18,0.95);
  --sidebar-bg: rgba(0,0,0,0.5);
  --input-bg: rgba(255,255,255,0.08);
  --glass-bg: rgba(255,255,255,0.04);
  --gradient-1: #e94560;
  --gradient-2: #ff6b6b;
}

* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  background: var(--bg-body);
  color: var(--text-secondary);
  min-height: 100vh;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  transition: background 0.4s ease, color 0.4s ease;
}

.app-container { min-height: 100vh; display: flex; flex-direction: column; position: relative; overflow: hidden; }

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

.main-wrapper { display: flex; flex: 1; margin-top: 64px; position: relative; z-index: 1; }
.content { flex: 1; padding: 24px 32px; padding-bottom: 104px; overflow-y: auto; min-height: calc(100vh - 64px); }
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


/* 脉冲动画 - 正在播放 */
@keyframes pulse-playing {
  0%, 100% { box-shadow: 0 0 0 0 rgba(29, 185, 84, 0.4); }
  50% { box-shadow: 0 0 0 12px rgba(29, 185, 84, 0); }
}
.playing-pulse { animation: pulse-playing 2s infinite; }
</style>
