<script setup lang="ts">
import { ref } from 'vue'
import { ElMenu, ElMenuItem, ElHeader, ElContainer, ElAside, ElMain } from 'element-plus'
import { RouterView } from 'vue-router'
import { useUserStore } from '@/stores/user'

const isCollapse = ref(false)
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
}
</script>

<template>
  <ElContainer style="height: 100vh">
    <ElAside :width="isCollapse ? '64px' : '200px'">
      <ElMenu
        :collapse="isCollapse"
        default-active="/"
        router
        style="height: 100%"
      >
        <ElMenuItem index="/">
          需求墙
        </ElMenuItem>
        <ElMenuItem index="/chat">
          站内信
        </ElMenuItem>
        <ElMenuItem index="/profile">
          个人中心
        </ElMenuItem>
      </ElMenu>
    </ElAside>

    <ElContainer>
      <ElHeader style="display: flex; justify-content: space-between; align-items: center">
        <span @click="isCollapse = !isCollapse" style="cursor: pointer">
          {{ isCollapse ? '展开' : '收起' }}
        </span>
        <span>
          {{ userStore.userInfo?.nickname ?? '用户' }} |
          <a @click="handleLogout" style="cursor: pointer">退出</a>
        </span>
      </ElHeader>

      <ElMain>
        <RouterView />
      </ElMain>
    </ElContainer>
  </ElContainer>
</template>