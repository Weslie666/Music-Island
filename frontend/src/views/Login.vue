<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1>登录 Music Island</h1>
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
  min-height: calc(100vh - 60px);
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}
.auth-card {
  width: 400px; background: #fff; border-radius: 12px;
  padding: 40px; box-shadow: 0 8px 32px rgba(0,0,0,0.2);
}
.auth-card h1 { text-align: center; margin-bottom: 32px; color: var(--text-primary); }
.auth-btn { width: 100%; margin-top: 8px; }
.auth-link { text-align: center; margin-top: 16px; color: var(--text-dim); font-size: 14px; }
.auth-link a { color: var(--accent); }
</style>
