# Music Island 开发状态

## 项目信息
- **题目**: 基于SpringBoot的音乐推荐系统
- **架构**: SpringBoot 2.7 + Vue3 + MySQL, 前后端分离
- **数据库**: music_island (10张表)
- **默认管理员**: admin / admin123 (BCrypt: $2a$12$pt1/OOe4ciqOXsJJ/qQT0e...)

## 当前阶段: Phase 6 - 互动功能 + 管理后台 (已完成) ✅

### 最新变更 (2026-05-06)
- **Phase 6 完成**: 评论功能 + 管理后台(歌曲管理/用户管理/统计图表)
- 新增歌曲 唯一 - G.E.M.邓紫棋 (id=48), 华语流行
- 修复: play_count/like_count 默认值设为 0, RecommendServiceImpl null 保护
- 修复: SongDetail 路由切换时评论/相似歌曲正确刷新
- 修复: PlayerBar 封面和歌名点击可跳转歌曲详情
- **注意**: 用户关注功能已删除（数据库 follow 表保留但未使用）
- 代码审查优化: N+1 查询修复、ECharts 内存泄漏修复、formatTime 提取公共工具

### Phase 1 ✅ 已完成
- 项目搭建 + JWT认证 + 注册登录
- 前后端编译通过，联调正常

### Phase 2 后端 ✅
```
backend/src/main/java/com/musicisland/
├── mapper/SongMapper.java         # @Select动态查询: 热门/最新/搜索/分类
├── service/SongService.java
├── service/impl/SongServiceImpl.java  # 分页逻辑 + 多条件查询
├── controller/SongController.java     # GET/POST/PUT/DELETE + stream(Range)
└── resources/sql/seed_songs.sql       # 15首真实歌曲(华语/英语/日语, 时长已验证)
```

### Phase 2 前端 ✅
```
frontend/src/views/
├── Home.vue        # 已修复: 热门推荐 + 最新上线, 正确使用records分页
├── Discover.vue    # 风格标签切换浏览 + 分页
├── Search.vue      # 搜索结果页(读取?q=参数)
└── SongDetail.vue  # 歌曲详情页(封面+信息+播放/收藏按钮)
```

### Phase 2 API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/songs?page=&limit=&sort=&keyword=&genre= | 歌曲列表(分页) |
| GET | /api/songs/genres | 风格标签列表 |
| GET | /api/songs/{id} | 歌曲详情 |
| GET | /api/songs/{id}/stream | 音频流(支持Range) |
| POST | /api/songs | 添加歌曲 |
| PUT | /api/songs/{id} | 更新歌曲 |
| DELETE | /api/songs/{id} | 删除歌曲 |

## Phase 2 验证步骤
1. 重启后端: `cd backend && mvn spring-boot:run`
2. 导入种子数据: 执行 `backend/src/main/resources/sql/seed_songs.sql`
3. 前端已在运行则热更新，否则 `cd frontend && npm run dev`
4. 验证:
   - 首页热门推荐/最新上线显示歌曲卡片 ✅/❌
   - 发现页风格标签切换，点击标签加载对应歌曲 ✅/❌
   - NavBar搜索 → 搜索结果页 ✅/❌
   - 歌曲详情页(从URL直接访问 /song/1) ✅/❌
   - 点击歌曲卡片 → 底部播放条出现（没有MP3文件则播放不了，但UI应正常） ✅/❌

### Phase 3 ✅ 已完成
- PlayerBar: 播放/暂停/切歌/进度条/音量
- 播放队列管理 (队列对话框)
- 播放模式 (列表循环/随机/单曲循环)
- Bug修复: 自动播放竞态条件 (immediate watcher + @canplay)

### Phase 4 后端 ✅
```
backend/src/main/java/com/musicisland/
├── mapper/
│   ├── UserLikeMapper.java         # 收藏查询/插入/删除/计数
│   ├── PlayHistoryMapper.java      # 播放历史查询/插入
│   ├── PlaylistMapper.java         # 歌单CRUD
│   └── PlaylistSongMapper.java     # 歌单歌曲关联操作
├── service/
│   ├── LikeService.java / impl/LikeServiceImpl.java
│   ├── HistoryService.java / impl/HistoryServiceImpl.java
│   └── PlaylistService.java / impl/PlaylistServiceImpl.java
└── controller/
    ├── LikeController.java         # GET/POST /api/likes
    ├── HistoryController.java      # GET/POST /api/history
    └── PlaylistController.java     # CRUD /api/playlists + 歌曲增删
```

### Phase 4 前端 ✅
```
frontend/src/
├── api/user.js                     # likes/history/playlists API函数
├── views/
│   ├── MyFavorites.vue             # 收藏列表 (网格+分页+取消收藏)
│   ├── MyHistory.vue               # 播放历史 (列表+分页+时间显示)
│   └── PlaylistDetail.vue          # 歌单详情 (歌曲列表+编辑+删除)
└── components/
    ├── PlayerBar.vue               # 新增: 收藏按钮、添加到歌单、播放记录上报
    ├── SongDetail.vue              # 新增: API收藏/取消、添加到歌单对话框
    └── SideBar.vue                 # 新增: 显示用户歌单列表
```

### Phase 4 API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/likes?page=&limit= | 我的收藏列表 |
| POST | /api/likes/{songId} | 切换收藏状态 |
| GET | /api/likes/{songId} | 检查是否已收藏 |
| GET | /api/history?page=&limit= | 播放历史列表 |
| POST | /api/history/{songId} | 记录播放 |
| GET | /api/playlists | 我的歌单列表 |
| GET | /api/playlists/{id} | 歌单详情+歌曲 |
| POST | /api/playlists | 创建歌单 |
| PUT | /api/playlists/{id} | 更新歌单 |
| DELETE | /api/playlists/{id} | 删除歌单 |
| POST | /api/playlists/{id}/songs | 添加歌曲到歌单 |
| DELETE | /api/playlists/{id}/songs/{songId} | 移除歌曲 |

### Phase 5 ✅ 后端
```
backend/src/main/java/com/musicisland/
├── mapper/UserBehaviorMapper.java     # user_behavior CRUD + upsert
├── service/RecommendService.java      # 推荐接口
├── service/impl/RecommendServiceImpl.java  # CF(余弦相似度) + 内容推荐
├── controller/RecommendController.java    # /api/recommend/* 端点
└── resources/sql/seed_behaviors.sql   # 3个测试用户 + ~23条行为数据
```

### Phase 5 ✅ 前端
```
frontend/src/
├── api/recommend.js                   # 推荐API函数
├── views/Home.vue                     # 新增"为你推荐"(CF个性化)
└── views/SongDetail.vue               # 新增"相似歌曲"(content-based)
```

**修改文件:** HistoryServiceImpl/LikeServiceImpl — 同步更新 user_behavior 表

### Phase 5 算法
- **热门推荐**: play_count + like_count 加权 (冷启动兜底)
- **协同过滤**: User-based CF, 余弦相似度, Top-10 相似用户, play_count*0.3+like*0.7 评分
- **内容推荐**: genre*0.6 + artist*0.4 + popularity tiebreaker
- **冷启动**: 行为数 < 3 时自动回退到热门推荐

### Phase 5 API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/recommend/popular | 热门推荐 |
| GET | /api/recommend/personal | 个性化CF推荐(需登录) |
| GET | /api/recommend/similar/{songId} | 相似歌曲(内容推荐) |

### Phase 6 ✅ 后端
```
backend/src/main/java/com/musicisland/
├── entity/Comment.java              # 评论实体(含username/songTitle展示字段)
├── mapper/CommentMapper.java        # 评论CRUD + JOIN查询
├── service/CommentService.java      # 评论接口
├── service/impl/CommentServiceImpl.java  # 评论业务逻辑
├── controller/CommentController.java    # GET公开, POST/DELETE需登录(手动JWT)
└── controller/AdminController.java  # 统计概览 + 用户管理 + 评论审核
```

**修改文件:**
- UserMapper: +findAll, countAll, updateStatus
- PlayHistoryMapper: +countAll
- UserLikeMapper: +countAll
- SongMapper: +countGroupByGenre
- WebMvcConfig: 排除 /api/comments
- RecommendServiceImpl: null 保护

### Phase 6 ✅ 前端
```
frontend/src/
├── api/comment.js                   # 评论API
├── api/admin.js                     # 管理后台API
├── utils/format.js                  # formatTime/formatCount 公共工具
├── components/CommentSection.vue    # 评论组件(监听songId变化)
└── views/admin/Dashboard.vue        # 管理后台(4标签页: 概览+ECharts/用户/歌曲/评论)
```

**修改文件:**
- SongDetail.vue: 嵌入CommentSection + watch路由参数
- PlayerBar.vue: 封面/歌名点击跳转
- router/index.js: 懒加载加载方式
- SideBar.vue: 移除关注导航
- api/song.js: +addSong/updateSong/deleteSong

### Phase 6 API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/comments?songId=&page=&limit= | 歌曲评论列表(公开) |
| POST | /api/comments | 发表评论(需登录) |
| DELETE | /api/comments/{id} | 删除评论(本人/管理员) |
| GET | /api/admin/stats | 统计概览(需管理员) |
| GET | /api/admin/users | 用户列表(需管理员) |
| PUT | /api/admin/users/{id}/status | 冻结/解冻(需管理员) |
| GET | /api/admin/comments | 全部评论(需管理员) |
| DELETE | /api/admin/comments/{id} | 管理员删评论 |

**注意:** 关注功能(follow)已在开发过程中移除，数据库 follow 表保留。

## 下一阶段: Phase 8 - 打磨 + 文档

## 关键技术决策
- MyBatis使用注解(非XML) - 动态SQL用 `<script>` 标签
- 密码加密: BCrypt (`$2a$` 前缀, jBCrypt 0.4)
- JWT过期: 7天
- 音频流: 后端支持HTTP Range, 路由排除拦截 (/api/songs/*/stream)
- 前后端开发端口不同, 通过Vite proxy解决跨域
- 分页格式: `{records, total, page, limit}` 统一返回

## 启动命令
```bash
# 后端
cd E:/Music_Island/backend && mvn spring-boot:run

# 前端
cd E:/Music_Island/frontend && npm run dev
```

## 数据库
```bash
# 建表
mysql -u root -p123456 < E:/Music_Island/backend/src/main/resources/sql/schema.sql

# 种子数据
mysql -u root -p123456 < E:/Music_Island/backend/src/main/resources/sql/seed_songs.sql
```
