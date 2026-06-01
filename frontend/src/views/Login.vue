<template>
  <div class="auth-page">
    <div class="auth-showcase">
      <div class="mi-kicker">Welcome Back</div>
      <h1>回到音乐岛</h1>
      <p>继续收听你的播放历史，让推荐算法更懂你的下一首心动旋律。</p>
      <div class="auth-covers">
        <img src="/showcase/login-1.webp" />
        <img src="/showcase/login-2.jpg" />
        <img src="/showcase/login-3.webp" />
        <img src="/showcase/login-4.jpg" />
      </div>
    </div>
    <div class="auth-card mi-card">
      <h2>登录 Music Island</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock"
            @keyup.enter="handleLogin" show-password />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="handleLogin" class="auth-btn">
          登录
        </el-button>
      </el-form>
      <p class="auth-link">还没有账号？<router-link to="/register">立即注册</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/useUserStore'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: calc(100vh - 120px);
  display: grid;
  grid-template-columns: minmax(420px, 0.95fr) 440px;
  gap: 42px;
  align-items: center;
  max-width: 1180px;
  margin: 0 auto;
}
.auth-showcase h1 {
  margin-top: 12px;
  color: var(--text-primary);
  font-size: 58px;
  line-height: 1.05;
  font-weight: 920;
}
.auth-showcase p {
  max-width: 520px;
  margin-top: 16px;
  color: var(--text-muted);
  font-size: 17px;
  line-height: 1.8;
}
.auth-covers {
  display: grid;
  grid-template-columns: repeat(4, 112px);
  gap: 14px;
  margin-top: 34px;
}
.auth-covers img {
  width: 112px;
  height: 112px;
  object-fit: cover;
  border-radius: 18px;
  box-shadow: var(--card-shadow);
}
.auth-covers img:nth-child(even) { transform: translateY(22px); }
.auth-card {
  width: 100%;
  padding: 38px;
}
.auth-card h2 { text-align: center; margin-bottom: 32px; color: var(--text-primary); font-size: 26px; font-weight: 900; }
.auth-btn { width: 100%; margin-top: 8px; }
.auth-link { text-align: center; margin-top: 16px; color: var(--text-dim); font-size: 14px; }
.auth-link a { color: var(--accent); }
@media (max-width: 980px) {
  .auth-page { grid-template-columns: 1fr; }
  .auth-showcase { display: none; }
}
</style>
