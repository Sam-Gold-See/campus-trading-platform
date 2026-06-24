<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { sendRegisterCode } from '@/api/user'
import type { FormInstance, FormRules } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const formRef = ref<FormInstance>()
const form = reactive({
  eduEmail: '',
  verificationCode: '',
  nickname: '',
  password: '',
  confirmPassword: '',
})

const isEduEmail = (email: string) => /@[\w.-]+\.edu\.cn$/.test(email)

const rules: FormRules = {
  eduEmail: [
    { required: true, message: '请输入教育邮箱', trigger: 'blur' },
    { validator: (_r, v, cb) => (isEduEmail(v) ? cb() : cb(new Error('仅限高校教育邮箱（@*.edu.cn）'))), trigger: 'blur' },
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位', trigger: 'blur' },
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 4, max: 16, message: '昵称需4-16位', trigger: 'blur' },
    { validator: (_r, v, cb) => (/[^a-zA-Z0-9一-龥_]/.test(v) ? cb(new Error('昵称不可包含特殊字符')) : cb()), trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码需8-20位', trigger: 'blur' },
    { validator: (_r, v, cb) => {
      if (!/[a-zA-Z]/.test(v)) cb(new Error('密码需包含字母'))
      else if (!/[0-9]/.test(v)) cb(new Error('密码需包含数字'))
      else cb()
    }, trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (_r, v, cb) => (v !== form.password ? cb(new Error('两次密码不一致')) : cb()), trigger: 'blur' },
  ],
}

const countdown = ref(0)
let countdownTimer: ReturnType<typeof setInterval> | null = null

const canSendCode = () => isEduEmail(form.eduEmail) && countdown.value === 0

const startCountdown = () => {
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0 && countdownTimer) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
  }, 1000)
}

const handleSendCode = async () => {
  try {
    await sendRegisterCode(form.eduEmail)
    ElMessage.success('验证码已发送')
    startCountdown()
  } catch {
    // error handled by request interceptor
  }
}

const handleRegister = async () => {
  await formRef.value?.validate()
  try {
    await userStore.register(form)
    ElMessage.success('注册成功')
    router.push('/')
  } catch {
    // error handled by request interceptor
  }
}
</script>

<template>
  <div class="cyber-register-page">
    <!-- 背景装饰 -->
    <div class="cyber-bg-grid"></div>
    <div class="cyber-bg-lines"></div>

    <!-- 注册卡片 -->
    <div class="cyber-register-container">
      <div class="cyber-register-card">
        <!-- Logo区域 -->
        <div class="register-header">
          <div class="logo-icon">
            <span class="logo-text">CTP</span>
          </div>
          <h1 class="register-title cyber-glow-text-secondary">用户注册</h1>
          <p class="register-subtitle">CREATE NEW ACCOUNT</p>
        </div>

        <!-- 表单区域 -->
        <ElForm ref="formRef" :model="form" :rules="rules" class="register-form">
          <!-- 教育邮箱 -->
          <ElFormItem prop="eduEmail">
            <div class="form-label">
              <span class="label-icon">◆</span>
              <span>教育邮箱</span>
            </div>
            <ElInput
              v-model="form.eduEmail"
              placeholder="请输入 .edu.cn 箱"
              class="cyber-input"
            />
          </ElFormItem>

          <!-- 验证码 -->
          <ElFormItem prop="verificationCode">
            <div class="form-label">
              <span class="label-icon">◆</span>
              <span>验证码</span>
            </div>
            <div class="code-input-group">
              <ElInput
                v-model="form.verificationCode"
                placeholder="6位验证码"
                maxlength="6"
                class="cyber-input code-input"
              />
              <button
                class="cyber-button code-btn"
                :disabled="!canSendCode()"
                @click="handleSendCode"
              >
                <span class="btn-text">{{ countdown > 0 ? `${countdown}s` : '获取验证码' }}</span>
              </button>
            </div>
          </ElFormItem>

          <!-- 昵称 -->
          <ElFormItem prop="nickname">
            <div class="form-label">
              <span class="label-icon">◆</span>
              <span>昵称</span>
            </div>
            <ElInput
              v-model="form.nickname"
              placeholder="4-16位，不含特殊字符"
              maxlength="16"
              show-word-limit
              class="cyber-input"
            />
          </ElFormItem>

          <!-- 密码 -->
          <ElFormItem prop="password">
            <div class="form-label">
              <span class="label-icon">◆</span>
              <span>密码</span>
            </div>
            <ElInput
              v-model="form.password"
              type="password"
              show-password
              placeholder="8-20位，需含字母和数字"
              class="cyber-input"
            />
          </ElFormItem>

          <!-- 确认密码 -->
          <ElFormItem prop="confirmPassword">
            <div class="form-label">
              <span class="label-icon">◆</span>
              <span>确认密码</span>
            </div>
            <ElInput
              v-model="form.confirmPassword"
              type="password"
              show-password
              placeholder="再次输入密码"
              class="cyber-input"
            />
          </ElFormItem>

          <!-- 注册按钮 -->
          <ElFormItem>
            <button class="cyber-button register-btn" @click="handleRegister">
              <span class="btn-text">注册</span>
            </button>
          </ElFormItem>
        </ElForm>

        <!-- 底部链接 -->
        <div class="register-footer">
          <span class="footer-text">已有账号？</span>
          <span class="login-link" @click="router.push('/login')">去登录 →</span>
        </div>
      </div>

      <!-- 装饰元素 -->
      <div class="cyber-decoration">
        <div class="deco-corner deco-top-left"></div>
        <div class="deco-corner deco-top-right"></div>
        <div class="deco-corner deco-bottom-left"></div>
        <div class="deco-corner deco-bottom-right"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 注册页面整体 */
.cyber-register-page {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cyber-bg-darkest);
  overflow: hidden;
}

/* 网格背景 */
.cyber-bg-grid {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(var(--cyber-border-base) 1px, transparent 1px),
    linear-gradient(90deg, var(--cyber-border-base) 1px, transparent 1px);
  background-size: 50px 50px;
  opacity: 0.3;
}

/* 动态线条背景 */
.cyber-bg-lines {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.cyber-bg-lines::before,
.cyber-bg-lines::after {
  content: '';
  position: absolute;
  width: 2px;
  height: 100%;
  background: linear-gradient(180deg, transparent, var(--cyber-secondary), transparent);
  animation: line-move 8s linear infinite;
}

.cyber-bg-lines::before {
  left: 30%;
}

.cyber-bg-lines::after {
  right: 20%;
  animation-delay: -4s;
}

@keyframes line-move {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(100%); }
}

/* 注册容器 */
.cyber-register-container {
  position: relative;
  z-index: 1;
}

/* 注册卡片 */
.cyber-register-card {
  width: 440px;
  background: var(--cyber-bg-card);
  border: 1px solid var(--cyber-border-accent);
  border-radius: var(--cyber-radius-xl);
  padding: 40px;
  position: relative;
  box-shadow: var(--cyber-shadow-hover), 0 0 30px var(--cyber-secondary-glow);
}

/* Logo区域 */
.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cyber-bg-darker);
  border: 2px solid var(--cyber-secondary);
  border-radius: var(--cyber-radius-lg);
  box-shadow: 0 0 20px var(--cyber-secondary-glow);
}

.logo-text {
  font-family: 'Orbitron', sans-serif;
  font-size: 20px;
  font-weight: 700;
  color: var(--cyber-secondary);
  letter-spacing: 3px;
}

.register-title {
  font-family: 'Orbitron', sans-serif;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 2px;
  margin: 0;
}

.register-subtitle {
  font-family: var(--cyber-font-mono);
  font-size: 12px;
  color: var(--cyber-text-secondary);
  letter-spacing: 4px;
  margin-top: 8px;
}

/* 表单 */
.register-form {
  margin-top: 24px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: var(--cyber-font-family);
  font-size: 14px;
  font-weight: var(--cyber-font-weight-semibold);
  color: var(--cyber-secondary);
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.label-icon {
  font-size: 10px;
}

/* 验证码输入组 */
.code-input-group {
  display: flex;
  gap: 12px;
}

.code-input {
  flex: 1;
}

.code-btn {
  width: 120px;
  height: 48px;
  font-size: 13px;
}

.code-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

/* 注册按钮 */
.register-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  letter-spacing: 2px;
  background: linear-gradient(135deg, var(--cyber-secondary-dark), var(--cyber-secondary));
  border: 1px solid var(--cyber-secondary);
  box-shadow: 0 0 15px var(--cyber-secondary-glow);
}

.register-btn:hover {
  background: linear-gradient(135deg, var(--cyber-secondary), var(--cyber-secondary-light));
  box-shadow: var(--cyber-glow-secondary);
}

.btn-text {
  font-family: 'Orbitron', sans-serif;
  font-weight: 600;
}

/* 底部链接 */
.register-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--cyber-border-base);
}

.footer-text {
  color: var(--cyber-text-secondary);
  font-size: 13px;
}

.login-link {
  color: var(--cyber-secondary);
  font-weight: var(--cyber-font-weight-semibold);
  cursor: pointer;
  transition: all var(--cyber-transition-base);
}

.login-link:hover {
  text-shadow: 0 0 10px var(--cyber-secondary-glow);
}

/* 装饰角落 */
.cyber-decoration {
  position: absolute;
  top: -1px;
  left: -1px;
  right: -1px;
  bottom: -1px;
  pointer-events: none;
}

.deco-corner {
  position: absolute;
  width: 20px;
  height: 20px;
  border: 2px solid var(--cyber-secondary);
}

.deco-top-left {
  top: 0;
  left: 0;
  border-right: none;
  border-bottom: none;
  box-shadow: -2px -2px 10px var(--cyber-secondary-glow);
}

.deco-top-right {
  top: 0;
  right: 0;
  border-left: none;
  border-bottom: none;
  box-shadow: 2px -2px 10px var(--cyber-secondary-glow);
}

.deco-bottom-left {
  bottom: 0;
  left: 0;
  border-right: none;
  border-top: none;
  box-shadow: -2px 2px 10px var(--cyber-secondary-glow);
}

.deco-bottom-right {
  bottom: 0;
  right: 0;
  border-left: none;
  border-top: none;
  box-shadow: 2px 2px 10px var(--cyber-secondary-glow);
}

/* 输入框样式增强 */
.cyber-register-card :deep(.el-input__wrapper) {
  height: 48px;
  background: var(--cyber-bg-input) !important;
  border: 1px solid var(--cyber-border-base) !important;
  border-radius: var(--cyber-radius-base) !important;
}

.cyber-register-card :deep(.el-input__wrapper:hover) {
  border-color: var(--cyber-secondary) !important;
}

.cyber-register-card :deep(.el-input__wrapper.is-focus) {
  border-color: var(--cyber-secondary) !important;
  box-shadow: 0 0 15px var(--cyber-secondary-glow) !important;
}

.cyber-register-card :deep(.el-form-item__error) {
  color: var(--cyber-danger);
  font-family: var(--cyber-font-family);
}
</style>