<template>
  <div class="comment-section">
    <h3 class="section-title">评论 ({{ total }})</h3>

    <!-- Comment form -->
    <div class="comment-form" v-if="userStore.isLoggedIn">
      <el-input
        v-model="content"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
        maxlength="500"
        show-word-limit
      />
      <el-button type="primary" size="small" class="submit-btn" @click="handleSubmit" :loading="submitting">
        发表评论
      </el-button>
    </div>
    <div class="comment-login-hint" v-else>
      <router-link to="/login">登录</router-link> 后即可发表评论
    </div>

    <!-- Comment list -->
    <div v-if="comments.length" class="comment-list">
      <div v-for="item in comments" :key="item.id" class="comment-item">
        <div class="comment-header">
          <span class="comment-user">{{ item.username }}</span>
          <span class="comment-time">{{ item.createTime }}</span>
        </div>
        <p class="comment-content">{{ item.content }}</p>
        <el-button
          v-if="canDelete(item)"
          type="danger"
          size="small"
          text
          @click="handleDelete(item.id)"
        >删除</el-button>
      </div>
    </div>
    <el-empty v-else description="暂无评论" :image-size="60" />

    <!-- Pagination -->
    <div class="pagination" v-if="total > limit">
      <el-pagination
        v-model:current-page="page"
        :page-size="limit"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchComments"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getComments, addComment, deleteComment } from '../api/comment'
import { useUserStore } from '../store/useUserStore'
import { ElMessage } from 'element-plus'

const props = defineProps({
  songId: { type: [Number, String], required: true }
})

const userStore = useUserStore()
const comments = ref([])
const total = ref(0)
const page = ref(1)
const limit = ref(10)
const content = ref('')
const submitting = ref(false)

function canDelete(item) {
  if (!userStore.userInfo) return false
  return userStore.userInfo.id === item.userId || userStore.isAdmin
}

async function fetchComments() {
  try {
    const data = await getComments(props.songId, page.value, limit.value)
    comments.value = data.records
    total.value = data.total
  } catch (e) {
    // ignore
  }
}

async function handleSubmit() {
  if (!content.value.trim()) return
  submitting.value = true
  try {
    await addComment(props.songId, content.value.trim())
    ElMessage.success('评论发表成功')
    content.value = ''
    page.value = 1
    fetchComments()
  } catch (e) {
    ElMessage.error('发表失败，请先登录')
  }
  submitting.value = false
}

async function handleDelete(id) {
  try {
    await deleteComment(id)
    ElMessage.success('评论已删除')
    fetchComments()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchComments()
})

watch(() => props.songId, () => {
  comments.value = []
  total.value = 0
  page.value = 1
  content.value = ''
  fetchComments()
})
</script>

<style scoped>
.comment-section { margin-top: 36px; }
.section-title { font-size: 18px; margin-bottom: 16px; color: var(--text-primary); }

.comment-form { margin-bottom: 20px; }
.submit-btn { margin-top: 8px; }

.comment-login-hint { font-size: 14px; color: var(--text-dim); margin-bottom: 20px; }
.comment-login-hint a { color: var(--accent); }

.comment-item {
  padding: 14px 0;
  border-bottom: 1px solid #f0f0f0;
}
.comment-header { display: flex; gap: 12px; align-items: center; margin-bottom: 6px; }
.comment-user { font-size: 14px; font-weight: 500; color: var(--text-primary); }
.comment-time { font-size: 12px; color: var(--text-dim); }
.comment-content { font-size: 14px; color: #444; line-height: 1.6; margin: 6px 0; }

.pagination { display: flex; justify-content: center; margin-top: 20px; }
</style>
