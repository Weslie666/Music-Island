<template>
  <div class="admin-page">
    <h2>管理后台</h2>

    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <!-- Tab 1: 数据概览 -->
      <el-tab-pane label="数据概览" name="overview">
        <div class="stat-cards">
          <div class="stat-card"><div class="stat-num">{{ stats.totalUsers }}</div><div class="stat-label">用户总数</div></div>
          <div class="stat-card"><div class="stat-num">{{ stats.totalSongs }}</div><div class="stat-label">歌曲总数</div></div>
          <div class="stat-card"><div class="stat-num">{{ stats.totalPlays }}</div><div class="stat-label">播放次数</div></div>
          <div class="stat-card"><div class="stat-num">{{ stats.totalLikes }}</div><div class="stat-label">收藏次数</div></div>
          <div class="stat-card"><div class="stat-num">{{ stats.totalComments }}</div><div class="stat-label">评论总数</div></div>
        </div>
        <div class="charts-row">
          <div class="chart-box"><h4>风格分布</h4><div ref="genreChartRef" class="chart"></div></div>
          <div class="chart-box"><h4>热门歌曲 Top 10</h4><div ref="topChartRef" class="chart"></div></div>
        </div>
      </el-tab-pane>

      <!-- Tab 2: 用户管理 -->
      <el-tab-pane label="用户管理" name="users">
        <el-table :data="users" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="nickname" label="昵称" width="140" />
          <el-table-column prop="role" label="角色" width="80" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '正常' : '冻结' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="注册时间" min-width="160" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button v-if="row.role !== 'ADMIN'" size="small"
                :type="row.status === 1 ? 'danger' : 'success'"
                @click="handleToggleUser(row)">{{ row.status === 1 ? '冻结' : '解冻' }}</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination"><el-pagination v-model:current-page="userPage" :page-size="userLimit" :total="userTotal" layout="prev, pager, next" @current-change="fetchUsers" /></div>
      </el-tab-pane>

      <!-- Tab 3: 歌曲管理 -->
      <el-tab-pane label="歌曲管理" name="songs">
        <div class="toolbar"><el-button type="primary" @click="openSongDialog()">添加歌曲</el-button></div>
        <el-table :data="songs" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="歌名" width="160" />
          <el-table-column prop="artist" label="歌手" width="140" />
          <el-table-column prop="genre" label="风格" width="100" />
          <el-table-column prop="playCount" label="播放量" width="90" />
          <el-table-column label="操作" width="140">
            <template #default="{ row }">
              <el-button size="small" @click="openSongDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteSong(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination"><el-pagination v-model:current-page="songPage" :page-size="songLimit" :total="songTotal" layout="prev, pager, next" @current-change="fetchSongs" /></div>
      </el-tab-pane>

      <!-- Tab 4: 评论管理 -->
      <el-tab-pane label="评论管理" name="comments">
        <el-table :data="adminComments" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="username" label="用户" width="120" />
          <el-table-column prop="songTitle" label="歌曲" width="160" />
          <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createTime" label="时间" width="160" />
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="handleDeleteComment(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination"><el-pagination v-model:current-page="commentPage" :page-size="commentLimit" :total="commentTotal" layout="prev, pager, next" @current-change="fetchAdminComments" /></div>
      </el-tab-pane>
    </el-tabs>

    <!-- Song dialog (add/edit) -->
    <el-dialog v-model="songDialogVisible" :title="editingSong ? '编辑歌曲' : '添加歌曲'" width="500px">
      <el-form :model="songForm" label-width="80px">
        <el-form-item label="歌名"><el-input v-model="songForm.title" /></el-form-item>
        <el-form-item label="歌手"><el-input v-model="songForm.artist" /></el-form-item>
        <el-form-item label="专辑"><el-input v-model="songForm.album" /></el-form-item>
        <el-form-item label="风格"><el-input v-model="songForm.genre" /></el-form-item>
        <el-form-item label="时长(秒)"><el-input-number v-model="songForm.duration" :min="0" /></el-form-item>
        <el-form-item label="封面URL"><el-input v-model="songForm.coverUrl" /></el-form-item>
        <el-form-item label="文件URL"><el-input v-model="songForm.fileUrl" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="songDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveSong">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { getStats, getUsers, updateUserStatus, getAdminComments, deleteAdminComment } from '../../api/admin'
import { getSongs, addSong, updateSong, deleteSong } from '../../api/song'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'

// Tab state
const activeTab = ref('overview')

// Stats
const stats = reactive({ totalUsers: 0, totalSongs: 0, totalPlays: 0, totalLikes: 0, totalComments: 0 })
const genreChartRef = ref(null)
const topChartRef = ref(null)
let genreChart = null
let topChart = null

async function fetchStats() {
  try {
    const data = await getStats()
    Object.assign(stats, data.overview)
    await nextTick()
    renderCharts(data.genreDistribution, data.topSongs)
  } catch (e) { /* ignore */ }
}

function renderCharts(genreData, topSongs) {
  if (genreChartRef.value) {
    if (!genreChart) genreChart = echarts.init(genreChartRef.value)
    genreChart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', radius: ['40%', '70%'],
        data: genreData,
        label: { formatter: '{b}: {c}' }
      }]
    })
  }
  if (topChartRef.value) {
    if (!topChart) topChart = echarts.init(topChartRef.value)
    topChart.setOption({
      tooltip: { trigger: 'axis', formatter: function(p) { return p[0].name + '<br/>播放次数: ' + p[0].value } },
      xAxis: { type: 'category', data: topSongs.map(s => s.title), axisLabel: { rotate: 30 } },
      yAxis: { type: 'value', name: '播放次数' },
      series: [{ type: 'bar', data: topSongs.map(s => s.playCount), itemStyle: { color: '#e94560' } }],
      grid: { left: 50, right: 20, bottom: 60, top: 20 }
    })
  }
}

// User management
const users = ref([])
const userPage = ref(1)
const userLimit = ref(10)
const userTotal = ref(0)

async function fetchUsers() {
  try {
    const data = await getUsers(userPage.value, userLimit.value)
    users.value = data.records
    userTotal.value = data.total
  } catch (e) { /* ignore */ }
}

async function handleToggleUser(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '冻结' : '解冻'
  try {
    await ElMessageBox.confirm('确认' + action + '用户 "' + row.username + '" ？', '提示', { type: 'warning' })
    await updateUserStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(action + '成功')
  } catch (e) { /* cancelled or error */ }
}

// Song management
const songs = ref([])
const songPage = ref(1)
const songLimit = ref(10)
const songTotal = ref(0)
const songDialogVisible = ref(false)
const editingSong = ref(null)
const songForm = reactive({ title: '', artist: '', album: '', genre: '', duration: 0, coverUrl: '', fileUrl: '' })

async function fetchSongs() {
  try {
    const data = await getSongs({ page: songPage.value, limit: songLimit.value })
    songs.value = data.records
    songTotal.value = data.total
  } catch (e) { /* ignore */ }
}

function openSongDialog(row) {
  if (row) {
    editingSong.value = row
    Object.assign(songForm, row)
  } else {
    editingSong.value = null
    Object.assign(songForm, { title: '', artist: '', album: '', genre: '', duration: 0, coverUrl: '', fileUrl: '' })
  }
  songDialogVisible.value = true
}

async function handleSaveSong() {
  try {
    if (editingSong.value) {
      await updateSong(editingSong.value.id, songForm)
      ElMessage.success('更新成功')
    } else {
      await addSong(songForm)
      ElMessage.success('添加成功')
    }
    songDialogVisible.value = false
    fetchSongs()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function handleDeleteSong(id) {
  try {
    await ElMessageBox.confirm('确认删除该歌曲？', '警告', { type: 'warning' })
    await deleteSong(id)
    ElMessage.success('删除成功')
    fetchSongs()
  } catch (e) { /* cancelled or error */ }
}

// Comment management
const adminComments = ref([])
const commentPage = ref(1)
const commentLimit = ref(10)
const commentTotal = ref(0)

async function fetchAdminComments() {
  try {
    const data = await getAdminComments(commentPage.value, commentLimit.value)
    adminComments.value = data.records
    commentTotal.value = data.total
  } catch (e) { /* ignore */ }
}

async function handleDeleteComment(id) {
  try {
    await ElMessageBox.confirm('确认删除该评论？', '警告', { type: 'warning' })
    await deleteAdminComment(id)
    ElMessage.success('删除成功')
    fetchAdminComments()
  } catch (e) { /* cancelled or error */ }
}

function handleTabChange(tab) {
  if (tab.paneName === 'overview') fetchStats()
  else if (tab.paneName === 'users') fetchUsers()
  else if (tab.paneName === 'songs') fetchSongs()
  else if (tab.paneName === 'comments') fetchAdminComments()
}

function onResize() {
  genreChart?.resize()
  topChart?.resize()
}
window.addEventListener('resize', onResize)

onMounted(() => {
  fetchStats()
})

onUnmounted(() => {
  window.removeEventListener('resize', onResize)
  genreChart?.dispose()
  topChart?.dispose()
})
</script>

<style scoped>
.admin-page { max-width: 1100px; margin: 0 auto; }
.admin-page h2 { font-size: 22px; color: var(--text-primary); margin-bottom: 20px; }

.stat-cards { display: flex; gap: 16px; margin-bottom: 24px; }
.stat-card {
  flex: 1; text-align: center; padding: 20px 12px;
  background: #fff; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.stat-num { font-size: 28px; font-weight: 700; color: var(--accent); }
.stat-label { font-size: 13px; color: var(--text-dim); margin-top: 4px; }

.charts-row { display: flex; gap: 24px; }
.chart-box { flex: 1; background: #fff; border-radius: 10px; padding: 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.chart-box h4 { font-size: 15px; color: var(--text-primary); margin: 0 0 12px 0; }
.chart { width: 100%; height: 300px; }

.toolbar { margin-bottom: 12px; }

.pagination { display: flex; justify-content: center; margin-top: 16px; }
</style>
