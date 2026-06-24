<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElCard, ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const formRef = ref<FormInstance>()
const form = reactive({
  eduEmail: '',
  password: '',
})

const rules: FormRules = {
  eduEmail: [
    { required: true, message: '请输入教育邮箱', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
  ],
}

const handleLogin = async () => {
  await formRef.value?.validate()
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    // 使用 replace 防止回退到登录页
    router.replace('/')
  } catch (error) {
    // error handled in request interceptor
    console.error('登录失败:', error)
  }
}
</script>

<template>
  <div class="cyber-login-page">
    <!-- 背景装饰 -->
    <div class="cyber-bg-grid"></div>
    <div class="cyber-bg-lines"></div>

    <!-- 登录卡片 -->
    <div class="cyber-login-container">
      <div class="cyber-login-card">
        <!-- Logo区域 -->
        <div class="login-header">
          <div class="logo-icon">
            <span class="logo-text">CTP</span>
          </div>
          <h1 class="login-title cyber-glow-text">校园交易平台</h1>
          <p class="login-subtitle">CAMPUS TRADING PLATFORM</p>
        </div>

        <!-- 表单区域 -->
        <ElForm ref="formRef" :model="form" :rules="rules" class="login-form">
          <ElFormItem prop="eduEmail">
            <div class="form-label">教育邮箱</div>
            <ElInput
              v-model="form.eduEmail"
              placeholder="请输入 .edu.cn 邮箱"
              class="cyber-input"
            />
          </ElFormItem>

          <ElFormItem prop="password">
            <div class="form-label">密码</div>
            <ElInput
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
              class="cyber-input"
              @keyup.enter="handleLogin"
            />
          </ElFormItem>

          <ElFormItem>
            <button type="button" class="cyber-button login-btn" @click="handleLogin">
              <span class="btn-text">登录</span>
            </button>
          </ElFormItem>
        </ElForm>

        <!-- 底部链接 -->
        <div class="login-footer">
          <span class="footer-text">还没有账号？</span>
          <span class="register-link" @click="router.push('/register')">去注册 →</span>
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
/* 登录页面整体 */
.cyber-login-page {
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
  background: linear-gradient(180deg, transparent, var(--cyber-primary), transparent);
  animation: line-move 8s linear infinite;
}

.cyber-bg-lines::before {
  left: 20%;
}

.cyber-bg-lines::after {
  right: 30%;
  animation-delay: -4s;
}

@keyframes line-move {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(100%); }
}

/* 登录容器 */
.cyber-login-container {
  position: relative;
  z-index: 1;
}

/* 登录卡片 */
.cyber-login-card {
  width: 400px;
  background: var(--cyber-bg-card);
  border: 1px solid var(--cyber-border-highlight);
  border-radius: var(--cyber-radius-xl);
  padding: 40px;
  position: relative;
  box-shadow: var(--cyber-shadow-hover), 0 0 30px var(--cyber-primary-glow);
}

/* Logo区域 */
.login-header {
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
  border: 2px solid var(--cyber-primary);
  border-radius: var(--cyber-radius-lg);
  box-shadow: 0 0 20px var(--cyber-primary-glow);
}

.logo-text {
  font-family: 'Orbitron', sans-serif;
  font-size: 20px;
  font-weight: 700;
  color: var(--cyber-primary);
  letter-spacing: 3px;
}

.login-title {
  font-family: 'Orbitron', sans-serif;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 2px;
  margin: 0;
}

.login-subtitle {
  font-family: var(--cyber-font-mono);
  font-size: 12px;
  color: var(--cyber-text-secondary);
  letter-spacing: 4px;
  margin-top: 8px;
}

/* 表单 */
.login-form {
  margin-top: 24px;
}

.form-label {
  font-family: var(--cyber-font-family);
  font-size: 14px;
  font-weight: var(--cyber-font-weight-semibold);
  color: var(--cyber-primary);
  letter-spacing: 1px;
  margin-bottom: 8px;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  letter-spacing: 2px;
}

.btn-text {
  font-family: 'Orbitron', sans-serif;
  font-weight: 600;
}

/* 底部链接 */
.login-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--cyber-border-base);
}

.footer-text {
  color: var(--cyber-text-secondary);
  font-size: 13px;
}

.register-link {
  color: var(--cyber-primary);
  font-weight: var(--cyber-font-weight-semibold);
  cursor: pointer;
  transition: all var(--cyber-transition-base);
}

.register-link:hover {
  text-shadow: 0 0 10px var(--cyber-primary-glow);
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
  border: 2px solid var(--cyber-primary);
}

.deco-top-left {
  top: 0;
  left: 0;
  border-right: none;
  border-bottom: none;
  box-shadow: -2px -2px 10px var(--cyber-primary-glow);
}

.deco-top-right {
  top: 0;
  right: 0;
  border-left: none;
  border-bottom: none;
  box-shadow: 2px -2px 10px var(--cyber-primary-glow);
}

.deco-bottom-left {
  bottom: 0;
  left: 0;
  border-right: none;
  border-top: none;
  box-shadow: -2px 2px 10px var(--cyber-primary-glow);
}

.deco-bottom-right {
  bottom: 0;
  right: 0;
  border-left: none;
  border-top: none;
  box-shadow: 2px 2px 10px var(--cyber-primary-glow);
}

/* 输入框样式增强 */
.cyber-login-card :deep(.el-input__wrapper) {
  height: 48px;
  background: var(--cyber-bg-input) !important;
  border: 1px solid var(--cyber-border-base) !important;
  border-radius: var(--cyber-radius-base) !important;
}

.cyber-login-card :deep(.el-input__wrapper:hover) {
  border-color: var(--cyber-primary) !important;
}

.cyber-login-card :deep(.el-input__wrapper.is-focus) {
  border-color: var(--cyber-primary) !important;
  box-shadow: 0 0 15px var(--cyber-primary-glow) !important;
}

.cyber-login-card :deep(.el-form-item__error) {
  color: var(--cyber-danger);
  font-family: var(--cyber-font-family);
}
</style>