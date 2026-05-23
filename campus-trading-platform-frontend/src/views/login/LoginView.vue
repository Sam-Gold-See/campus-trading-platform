<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElCard } from 'element-plus'
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
    router.push('/')
  } catch {
    // error handled in request interceptor
  }
}
</script>

<template>
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh">
    <ElCard style="width: 400px">
      <template #header>
        <span>校园跳蚤市场 - 登录</span>
      </template>
      <ElForm ref="formRef" :model="form" :rules="rules" label-width="80px">
        <ElFormItem label="教育邮箱" prop="eduEmail">
          <ElInput v-model="form.eduEmail" placeholder="请输入 .edu.cn 邮箱" />
        </ElFormItem>
        <ElFormItem label="密码" prop="password">
          <ElInput v-model="form.password" type="password" show-password placeholder="请输入密码" @keyup.enter="handleLogin" />
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="handleLogin">登录</ElButton>
        </ElFormItem>
      </ElForm>
      <div style="text-align: center; margin-top: 12px">
        还没有账号？<ElButton type="primary" link @click="router.push('/register')">去注册</ElButton>
      </div>
    </ElCard>
  </div>
</template>