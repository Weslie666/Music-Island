# 识图能力

你的底层模型不具备原生识图能力。遇到图片时，**不要用 Read 工具**，改用 vision.js：

```
node ../vision-skill/vision.js "<图片路径>" "用中文描述这张图片"
```

## 触发场景

- 用户分享图片路径（本地或网络 URL）
- 消息中出现 "Saved attachments:" 并列出图片
- 用户要求分析、描述、识别图片内容

---

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Music Island (音乐岛) — a music recommendation system built with SpringBoot 2.7 + Vue3 + MySQL. This is a graduation project (毕设) for a student with limited development experience.

## Working Directory

The primary working directory for this project is `E:\Music_Island\frontend` (set in session). The backend is at `E:\Music_Island\backend`.

## Critical Project Files

- **DEV_STATE.md** — The source of truth for development progress. Read this first to recover context.
- **C:\Users\喜羊羊\.claude\projects\E--Music-Island\memory\database_state.md** — Current DB state: user IDs (1,4,5,6), song IDs (31-46), record counts. MUST query or check this before any SQL changes.
- **C:\Users\喜羊羊\.claude\projects\E--Music-Island\memory\MEMORY.md** — Memory index.
- **C:\Users\喜羊羊\.claude\plans\moonlit-pondering-map.md** — The 8-phase master plan.

## Development Workflow

**Per-phase pause rule:** After completing each phase, pause and present a summary. Do NOT auto-proceed.

**DB verification rule:** Always query the database before writing SQL that references specific IDs. Song IDs start from 31 (not 1).

## Commands

```bash
# Backend (SpringBoot, port 8080)
cd E:/Music_Island/backend && mvn spring-boot:run
cd E:/Music_Island/backend && mvn compile

# Frontend (Vite dev server, port 5173)
cd E:/Music_Island/frontend && npm run dev
cd E:/Music_Island/frontend && npm run build

# Database (add --default-character-set=utf8mb4 for Chinese)
mysql -u root -p123456 music_island --default-character-set=utf8mb4 -e "SELECT ..."
mysql -u root -p123456 < E:/Music_Island/backend/src/main/resources/sql/schema.sql
mysql -u root -p123456 < E:/Music_Island/backend/src/main/resources/sql/seed_behaviors.sql
mysql -u root -p123456 < E:/Music_Island/backend/src/main/resources/sql/reset_demo.sql

# Audio/image conversion
ffmpeg -i input.flac -b:a 192k output.mp3
ffmpeg -i input.webp output.jpg
ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 file.mp3
```

**Note:** 项目目前没有测试文件。`spring-boot-starter-test` 已包含在 pom.xml 中，但尚未编写任何测试用例。

## Architecture

### Backend (`com.musicisland`)

- **Framework:** SpringBoot 2.7.18, Java 8, MyBatis 2.3.1 (annotation-based, no XML mappers)
- **DB mapping:** `map-underscore-to-camel-case: true` — DB columns like `play_count` auto-map to Java fields like `playCount`, no need for manual column aliases
- **Auth:** JWT (HS256, 7-day expiry). `LoginInterceptor` reads `Authorization: Bearer <token>`, sets userId/username/role as request attributes.
  - Public (no login): `/api/auth/login`, `/api/auth/register`, `/api/songs/**`, `/api/recommend/popular`, `/api/recommend/similar/**`, `/api/upload/**`
  - Protected (login required): `/api/likes/**`, `/api/history/**`, `/api/playlists/**`, `/api/recommend/personal`, `/api/auth/me`
- **Response format:** `Result<T>` with `{code: 200, message: "success", data: ...}`. Use `Result.success(data)` and `Result.error(code, msg)`
- **Password:** BCrypt via `org.mindrot.jbcrypt`
- **Static files:** `/music-data/**` mapped to `file:../music-data/`
- **Package layout:** `controller/` → `service/impl/` → `mapper/` → `entity/`. DTOs in `dto/`, security in `security/`, config in `config/`

### Frontend (Vue3 + Vite)

- **UI Framework:** Element Plus 2.3.12 + @element-plus/icons-vue (component & icon libraries)
- **Constraints:** Node.js v14.17.3 requires Vite 3.x (NOT Vite 4+). Package versions are locked in package.json.
- **State:** Pinia stores — `useUserStore` (auth/token), `usePlayerStore` (audio playback, queue, play modes)
- **API layer:** `src/api/request.js` — axios instance with base URL `/api`. Response interceptor unwraps `{code, message, data}` → returns `data` directly on code=200. On 401, clears localStorage and redirects to `/login`.
- **Vite proxy:** Dev server proxies `/api` and `/music-data` to `http://localhost:8080`
- **Audio playback:** `PlayerBar.vue` contains a hidden `<audio>` element. It watches `playerStore.isPlaying` and `playerStore.currentSong` to control playback via the HTML5 Audio API. The store manages queue, index, and play mode logic.
- **Layout:** `App.vue` → NavBar (top, 60px) + SideBar (left, 200px) + `<router-view>` (center) + PlayerBar (bottom, fixed)

### Database

10 tables: `user`, `song`, `playlist`, `playlist_song`, `user_like`, `play_history`, `comment`, `user_behavior`, `rating`, `follow`. Default admin: `admin` / `admin123`. Schema at `backend/src/main/resources/sql/schema.sql`.

### Current Phase Status

Phase 1-6 complete. Next: Phase 8 (polish + documentation). See `DEV_STATE.md` for details.

**Phase 6 Summary:** Comments (public GET + login POST/DELETE), Admin dashboard (stats + ECharts + user management + song management + comment moderation). Follow feature was removed — DB table retained but unused. New song: 唯一 (id=48, G.E.M.邓紫棋).
