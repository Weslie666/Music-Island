<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1>注册 Music Island</h1>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称（选填）" prefix-icon="UserFilled" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock"
            show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPassword">
          <el-input v-model="form.checkPassword" type="password" placeholder="请再次输入密码" prefix-icon="Lock"
            show-password />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="handleRegister" class="auth-btn">
          注册
        </el-button>
      </el-form>
      <p class="auth-link">已有账号？<router-link to="/login">立即登录</router-link></p>
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
  nickname: '',
  password: '',
  checkPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  checkPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.register(form.username, form.password, form.nickname || form.username)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    ElMessage.error(e.message || '注册失败')
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
