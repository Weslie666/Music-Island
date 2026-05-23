# 🎵 Music Island 音乐岛

> 基于 SpringBoot + Vue3 的智能音乐推荐系统

[![Java](https://img.shields.io/badge/Java-8-orange)](https://www.oracle.com/java/)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-2.7-brightgreen)](https://spring.io/)
[![Vue](https://img.shields.io/badge/Vue-3.3-42b883)](https://vuejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

## ✨ 项目简介

音乐岛是一个集音乐浏览、在线播放和个性化推荐于一体的Web音乐平台。采用前后端分离架构，实现了三级渐进式推荐策略，支持亮/暗主题一键切换。

## 🎯 核心功能

### 用户端
- **🎧 音乐播放器** — 支持播放/暂停、进度拖拽、音量调节、三种播放模式（列表循环/随机/单曲循环）、播放队列管理
- **🔍 音乐发现** — 首页推荐、风格分类浏览、关键词搜索、热门排行
- **❤️ 用户交互** — 收藏歌曲、播放历史、个人歌单（四宫格封面）
- **🤖 智能推荐** — 热门推荐 + 协同过滤 + 内容推荐，三级渐进式策略
- **💬 评论互动** — 歌曲评论、评论管理

### 管理端
- **📊 数据统计** — ECharts可视化图表（风格分布环形图 + 热门歌曲柱状图）
- **👤 用户管理** — 用户列表、冻结/解冻操作
- **🎵 歌曲管理** — 歌曲CRUD
- **📝 评论审核** — 全站评论管理

## 🏗️ 技术架构

```
┌──────────────────────────────────────────┐
│              前端 (Vue3)                  │
│  Element Plus + Pinia + Axios + Vite     │
│              Port 5173                    │
└──────────────┬───────────────────────────┘
               │ RESTful API (JSON)
┌──────────────┴───────────────────────────┐
│            后端 (SpringBoot 2.7)          │
│  Controller → Service → Mapper → Entity   │
│       JWT Auth + BCrypt + MyBatis         │
│              Port 8080                    │
└──────────────┬───────────────────────────┘
               │
┌──────────────┴───────────────────────────┐
│           MySQL 8.0 + 文件存储            │
└──────────────────────────────────────────┘
```

## 🧠 推荐算法

| 策略 | 说明 | 适用场景 |
|------|------|---------|
| **热门推荐** | 播放量+收藏数加权排序 | 冷启动、未登录用户 |
| **协同过滤** | User-based CF，余弦相似度 + Top-10近邻 | 登录且有行为数据 |
| **内容推荐** | 风格+歌手特征匹配 | 歌曲详情页相似推荐 |

## 🚀 快速启动

### 环境要求
- JDK 8+
- Node.js 14+
- MySQL 8.0+
- Maven 3.6+

### 1. 初始化数据库
```bash
mysql -u root -p < backend/src/main/resources/sql/schema.sql
mysql -u root -p < backend/src/main/resources/sql/seed_songs.sql
mysql -u root -p < backend/src/main/resources/sql/seed_behaviors.sql
```

### 2. 启动后端
```bash
cd backend
mvn spring-boot:run
```

### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```

### 4. 访问
浏览器打开 `http://localhost:5173`

**测试账号：** admin / admin123

## 📂 项目结构

```
Music_Island/
├── backend/                    # SpringBoot后端
│   ├── src/main/java/
│   │   └── com/musicisland/
│   │       ├── controller/     # 控制器层
│   │       ├── service/        # 业务逻辑层
│   │       ├── mapper/         # 数据访问层(MyBatis)
│   │       ├── entity/         # 实体类
│   │       ├── security/       # JWT认证
│   │       └── config/         # 配置类
│   └── src/main/resources/
│       ├── application.yml     # 应用配置
│       └── sql/                # 数据库脚本
├── frontend/                   # Vue3前端
│   └── src/
│       ├── views/              # 页面组件
│       ├── components/         # 通用组件
│       ├── store/              # Pinia状态管理
│       ├── api/                # API接口封装
│       └── router/             # 路由配置
└── music-data/                 # 静态资源(不上传Git)
    ├── audio/                  # MP3音频文件
    ├── covers/                 # 封面图片
    └── avatars/                # 用户头像
```

## 🎨 亮/暗主题

导航栏右侧 ☀️/🌙 按钮一键切换，偏好自动保存。

## 📄 License

MIT License - 毕业设计项目

---

> 软件工程专业毕业设计 | 2026
