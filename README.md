<h1 align="center">
  <br>
  <img src="https://raw.githubusercontent.com/twitter/twemoji/master/assets/svg/1f3b5.svg" width="80">
  <br>
  Music Island 音乐岛
  <br>
</h1>

<h4 align="center">基于 SpringBoot + Vue3 的智能音乐推荐系统</h4>

<p align="center">
  <img src="https://img.shields.io/badge/Java-8-ED8B00?logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/SpringBoot-2.7.18-6DB33F?logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Vue-3.3-4FC08D?logo=vuedotjs&logoColor=white">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/MyBatis-2.3.1-0F172A?logo=mybatis&logoColor=white">
  <img src="https://img.shields.io/badge/Vite-3.2-646CFF?logo=vite&logoColor=white">
  <img src="https://img.shields.io/badge/Element_Plus-2.3-409EFF?logo=element&logoColor=white">
  <img src="https://img.shields.io/badge/ECharts-6.0-AA344D?logo=apacheecharts&logoColor=white">
</p>

<p align="center">
  <a href="#核心功能">核心功能</a> ·
  <a href="#推荐算法">推荐算法</a> ·
  <a href="#系统架构">系统架构</a> ·
  <a href="#快速启动">快速启动</a>
</p>

---

## 🎯 项目概述

**音乐岛**是一个功能完整的在线音乐平台，集音乐浏览、在线播放、个性化推荐和后台管理于一体。采用前后端分离的B/S架构，实现了三级渐进式推荐策略，支持亮/暗主题一键切换。

本项目为软件工程专业本科毕业设计，从需求分析到系统实现独立完成全流程开发。

## ✨ 核心功能

### 🎧 音乐播放器
- 播放/暂停、上一首/下一首切歌
- 进度条点击/拖拽定位，支持HTTP Range断点续传
- 音量调节与静音切换
- 三种播放模式：列表循环 · 随机播放 · 单曲循环
- 播放队列管理（查看、移除、切换）
- 播放历史自动记录与上报

### 🔍 音乐发现
- 首页多维度展示：热门推荐 + 最新上线 + 为你推荐
- 风格分类浏览：华语流行 / 英语流行 / 日语流行 / 韩语流行 / 轻音乐
- 关键词搜索（歌曲名 & 歌手名模糊匹配）
- 歌曲详情页：完整元数据 + 相似歌曲推荐 + 评论区

### ❤️ 用户交互
- 收藏切换（Toggle模式），收藏列表分页展示
- 个人歌单 CRUD，歌曲添加与移除
- 歌单封面自动生成：1-3首显示单封面，≥4首显示2×2拼接
- 播放足迹时间线

### 🤖 智能推荐
- **热门推荐**：播放量+收藏数加权，冷启动兜底
- **协同过滤**：User-based CF，余弦相似度计算，隐式评分建模
- **内容推荐**：风格+歌手特征匹配，歌曲详情页相似推荐

### 💬 评论系统
- 歌曲详情页嵌入评论区
- 登录后发表评论、删除自己的评论
- 管理员全站评论审核

### 📊 管理后台
- 统计概览：ECharts环形图（风格分布）+ 柱状图（热门排行）
- 用户管理：列表查询、冻结/解冻
- 歌曲管理：添加/编辑/删除
- 评论审核：查看/删除全站评论

### 🎨 UI设计
- 亮色/暗色主题一键切换，偏好自动存储
- Spotify风格卡片交互（悬停浮现播放按钮、封面缩放）
- 页间路由过渡动画
- 自定义进度条与音量条

## 🧠 推荐算法详解

### 1. 热门推荐 (基础层)
```
热度分 = play_count + like_count × 2
```
对所有用户可见，同时作为协同过滤推荐失败时的降级方案。

### 2. 协同过滤 (核心层)
采用**基于用户的协同过滤**（User-based CF）：

1. **隐式评分**：`score = play_count × 0.3 + like_flag × 0.7`
2. **相似度计算**：余弦相似度，两用户评分向量的点积除以模长乘积
3. **近邻选取**：Top-10 最相似用户
4. **推荐聚合**：`推荐分 = Σ(相似度 × 近邻评分)`，排除已收藏
5. **冷启动**：行为记录 < 3 条自动回退热门推荐

### 3. 内容推荐 (补充层)
```
匹配分 = 同风格(+0.6) + 同歌手(+0.4) + 部分匹配(+0.3)
Tiebreaker = log(play_count) × 0.1
```

## 🏗️ 系统架构

```
┌───────────────────────────────────────────┐
│           Browser (Chrome/Edge)            │
├───────────────────────────────────────────┤
│   Vue3 + Element Plus + Pinia + Axios     │
│   Port: 5173 (Vite Dev Server)            │
│   Proxy: /api → localhost:8080             │
├──────────────────┬────────────────────────┤
│   RESTful API    │   JSON + JWT Auth       │
├──────────────────┴────────────────────────┤
│         SpringBoot 2.7.18 (JDK 8)         │
│   Controller → Service → Mapper → Entity  │
│   BCrypt + JWT(HS256) + MyBatis           │
│   Port: 8080                               │
├──────────────────┬────────────────────────┤
│    MySQL 8.0     │   music-data/ (文件存储) │
│   10 Tables      │   MP3 + JPG + SVG       │
└──────────────────┴────────────────────────┘
```

### 数据库设计 (10张表)
| 表名 | 说明 |
|------|------|
| user | 用户信息（BCrypt密码加密） |
| song | 歌曲元数据 |
| playlist / playlist_song | 歌单与歌曲关联 |
| user_like | 收藏关系 |
| play_history | 播放记录 |
| comment | 评论数据 |
| user_behavior | 协同过滤数据源（隐式评分） |
| rating / follow | 预留扩展表 |

### 安全设计
- **JWT认证**：HS256签名，7天过期，无状态会话
- **权限边界**：浏览公开，交互需登录，管理需ADMIN角色
- **拦截器**：LoginInterceptor统一鉴权，公开路径白名单
- **密码加密**：BCrypt + 跨库兼容处理（$2b$ → $2a$）

## 🚀 快速启动

### 环境要求
- JDK 8+ · Node.js 14+ · MySQL 8.0+ · Maven 3.6+
- FFmpeg（音频转码用）

### 初始化
```bash
# 1. 数据库
mysql -u root -p < backend/src/main/resources/sql/schema.sql
mysql -u root -p < backend/src/main/resources/sql/seed_songs.sql
mysql -u root -p < backend/src/main/resources/sql/seed_behaviors.sql

# 2. 后端
cd backend && mvn spring-boot:run

# 3. 前端
cd frontend && npm install && npm run dev

# 4. 访问
# 浏览器打开 http://localhost:5173
# 测试账号: admin / admin123
```

### 音频文件配置
将MP3音频文件放入 `music-data/audio/`，JPG封面图片放入 `music-data/covers/`，然后运行种子脚本导入数据库。

## 📂 项目结构

```
Music_Island/
├── backend/                         # SpringBoot 后端
│   ├── src/main/java/com/musicisland/
│   │   ├── controller/              # 8个Controller, 25+ REST端點
│   │   ├── service/impl/            # 7个Service, 业务逻辑层
│   │   ├── mapper/                  # 8个Mapper, MyBatis注解SQL
│   │   ├── entity/                  # 5个实体类
│   │   ├── dto/                     # 请求/响应DTO
│   │   ├── security/                # JWT工具 + 登录拦截器
│   │   ├── config/                  # CORS + 拦截器 + 静态资源配置
│   │   └── common/                  # 统一响应 + 全局异常处理
│   └── src/main/resources/
│       ├── application.yml
│       └── sql/                     # 建表 + 种子数据脚本
├── frontend/                        # Vue3 前端
│   └── src/
│       ├── views/                   # 10个路由页面
│       │   └── admin/               # 管理后台
│       ├── components/              # 4个核心组件
│       ├── store/                   # 3个Pinia Store
│       ├── api/                     # 7个API模块
│       ├── router/                  # Vue Router配置
│       └── utils/                   # 工具函数
└── music-data/                      # 静态资源（Git排除）
    ├── audio/    (.mp3)
    ├── covers/   (.jpg)
    └── avatars/  (.svg)
```

## 📊 项目数据

- **用户**：12人（含管理员）
- **歌曲**：37首（5种音乐风格）
- **后端**：7个Service，8个Controller，25+个API端点
- **前端**：10个页面，4个核心组件，3个Pinia Store
- **数据库**：10张表，InnoDB + utf8mb4
- **代码量**：~6,000行（Java + JavaScript + Vue）

## 💡 技术亮点

- 🔐 **JWT无状态认证**：BCrypt密码加密 + 拦截器统一鉴权 + 角色权限控制
- 🎵 **HTTP Range流媒体**：断点续传，支持拖拽进度条无缝播放
- 🧮 **协同过滤算法**：余弦相似度 + 隐式评分 + 冷启动回退
- 🎨 **主题切换**：CSS Variables + data-theme + localStorage持久化
- ⚡ **性能优化**：GROUP BY聚合替代N+1查询，ECharts实例管理防内存泄漏
- 📱 **响应式设计**：自定义滚动条、骨架屏过渡、路由动画

## 🛠️ 技术栈

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3.3 (Composition API) |
| UI组件库 | Element Plus 2.3 |
| 状态管理 | Pinia 2.1 |
| 构建工具 | Vite 3.2 |
| HTTP客户端 | Axios |
| 图表 | ECharts 6.0 |
| 后端框架 | SpringBoot 2.7.18 |
| 持久层 | MyBatis 2.3 (注解模式) |
| 数据库 | MySQL 8.0 (InnoDB) |
| 认证 | JWT (JJWT + jBCrypt) |
| 音频处理 | FFmpeg |

## 📝 License

2026 软件工程专业毕业设计
