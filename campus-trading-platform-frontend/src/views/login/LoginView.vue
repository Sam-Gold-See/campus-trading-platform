<script setup lang="ts">
import { ref } from 'vue'
import { ElForm, ElFormItem, ElInput, ElButton, ElCard } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const form = ref({
  eduEmail: '',
  password: '',
})

const handleLogin = async () => {
  try {
    await userStore.login(form.value)
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
      <ElForm :model="form" label-width="80px">
        <ElFormItem label="教育邮箱">
          <ElInput v-model="form.eduEmail" placeholder="请输入 .edu.cn 邮箱" />
        </ElFormItem>
        <ElFormItem label="密码">
          <ElInput v-model="form.password" type="password" placeholder="请输入密码" />
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="handleLogin">登录</ElButton>
          <ElButton>注册</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>
  </div>
</template>