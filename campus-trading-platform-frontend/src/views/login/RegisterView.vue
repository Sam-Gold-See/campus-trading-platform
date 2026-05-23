<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElCard, ElMessage } from 'element-plus'
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
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh">
    <ElCard style="width: 440px">
      <template #header>
        <span>校园跳蚤市场 - 注册</span>
      </template>
      <ElForm ref="formRef" :model="form" :rules="rules" label-width="100px">
        <ElFormItem label="教育邮箱" prop="eduEmail">
          <ElInput v-model="form.eduEmail" placeholder="请输入 .edu.cn 邮箱" />
        </ElFormItem>
        <ElFormItem label="验证码" prop="verificationCode">
          <div style="display: flex; gap: 8px; width: 100%">
            <ElInput v-model="form.verificationCode" placeholder="6位验证码" maxlength="6" />
            <ElButton :disabled="!canSendCode()" @click="handleSendCode">
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </ElButton>
          </div>
        </ElFormItem>
        <ElFormItem label="昵称" prop="nickname">
          <ElInput v-model="form.nickname" placeholder="4-16位，不含特殊字符" maxlength="16" show-word-limit />
        </ElFormItem>
        <ElFormItem label="密码" prop="password">
          <ElInput v-model="form.password" type="password" show-password placeholder="8-20位，需含字母和数字" />
        </ElFormItem>
        <ElFormItem label="确认密码" prop="confirmPassword">
          <ElInput v-model="form.confirmPassword" type="password" show-password placeholder="再次输入密码" />
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="handleRegister">注册</ElButton>
        </ElFormItem>
      </ElForm>
      <div style="text-align: center; margin-top: 12px">
        已有账号？<ElButton type="primary" link @click="router.push('/login')">去登录</ElButton>
      </div>
    </ElCard>
  </div>
</template>