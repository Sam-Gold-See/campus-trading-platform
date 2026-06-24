<script setup lang="ts">
import { ref } from 'vue'
import { ElMenu, ElMenuItem, ElHeader, ElContainer, ElAside, ElMain } from 'element-plus'
import { RouterView, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const isCollapse = ref(false)
const userStore = useUserStore()
const route = useRoute()

const handleLogout = () => {
  userStore.logout()
}
</script>

<template>
  <ElContainer class="cyber-layout">
    <!-- 左侧导航栏 -->
    <ElAside :width="isCollapse ? '64px' : '200px'" class="cyber-aside">
      <!-- Logo区域 -->
      <div class="cyber-logo">
        <div class="logo-icon">
          <span class="logo-text">CTP</span>
        </div>
        <div v-if="!isCollapse" class="logo-title">
          <span class="cyber-glow-text">校园交易</span>
        </div>
      </div>

      <!-- 导航菜单 -->
      <ElMenu
        :collapse="isCollapse"
        :default-active="route.path"
        router
        class="cyber-menu"
      >
        <ElMenuItem index="/">
          <template #title>
            <span class="menu-text">需求墙</span>
          </template>
        </ElMenuItem>
        <ElMenuItem index="/publish">
          <template #title>
            <span class="menu-text">发布</span>
          </template>
        </ElMenuItem>
        <ElMenuItem index="/profile">
          <template #title>
            <span class="menu-text">个人中心</span>
          </template>
        </ElMenuItem>
      </ElMenu>

      <!-- 底部装饰 -->
      <div class="cyber-aside-footer">
        <div class="cyber-line"></div>
      </div>
    </ElAside>

    <!-- 主内容区域 -->
    <ElContainer class="cyber-main-container">
      <!-- 顶部导航 -->
      <ElHeader class="cyber-header">
        <div class="header-left">
          <div class="collapse-btn" @click="isCollapse = !isCollapse">
            <span class="collapse-icon">{{ isCollapse ? '▶' : '◀' }}</span>
          </div>
          <div class="page-title cyber-glow-text">{{ route.meta.title || '校园交易平台' }}</div>
        </div>

        <div class="header-right">
          <div class="user-info">
            <div class="user-avatar">
              {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
            </div>
            <span class="user-name">{{ userStore.userInfo?.nickname ?? '用户' }}</span>
            <span class="cyber-status-dot active"></span>
          </div>
          <div class="logout-btn" @click="handleLogout">
            <span class="logout-text">退出</span>
          </div>
        </div>
      </ElHeader>

      <!-- 主内容 -->
      <ElMain class="cyber-main">
        <RouterView />
      </ElMain>
    </ElContainer>
  </ElContainer>
</template>

<style scoped>
/* 整体布局 */
.cyber-layout {
  height: 100vh;
  background: var(--cyber-bg-darkest);
}

/* 左侧导航 */
.cyber-aside {
  background: linear-gradient(180deg, var(--cyber-bg-darker), var(--cyber-bg-darkest));
  border-right: 1px solid var(--cyber-border-base);
  position: relative;
  overflow: hidden;
}

/* Logo区域 */
.cyber-logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 12px;
  border-bottom: 1px solid var(--cyber-border-base);
  position: relative;
}

.cyber-logo::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--cyber-primary), transparent);
  opacity: 0.5;
}

.logo-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cyber-bg-card);
  border: 2px solid var(--cyber-primary);
  border-radius: var(--cyber-radius-base);
  box-shadow: 0 0 15px var(--cyber-primary-glow);
}

.logo-text {
  font-family: 'Orbitron', sans-serif;
  font-size: 16px;
  font-weight: 700;
  color: var(--cyber-primary);
  letter-spacing: 2px;
}

.logo-title {
  font-family: var(--cyber-font-family);
  font-size: 14px;
  font-weight: 600;
  color: var(--cyber-primary);
  letter-spacing: 1px;
}

/* 导航菜单 */
.cyber-menu {
  background: transparent !important;
  border-right: none !important;
  padding: 8px 0;
}

/* 顶部导航 */
.cyber-header {
  height: 64px;
  background: var(--cyber-bg-darker);
  border-bottom: 1px solid var(--cyber-border-base);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: relative;
}

.cyber-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--cyber-primary), var(--cyber-secondary), var(--cyber-primary));
  opacity: 0.3;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cyber-bg-card);
  border: 1px solid var(--cyber-border-highlight);
  border-radius: var(--cyber-radius-sm);
  cursor: pointer;
  transition: all var(--cyber-transition-base);
}

.collapse-btn:hover {
  border-color: var(--cyber-primary);
  box-shadow: 0 0 10px var(--cyber-primary-glow);
}

.collapse-icon {
  color: var(--cyber-primary);
  font-size: 12px;
}

.page-title {
  font-family: 'Orbitron', sans-serif;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cyber-bg-card);
  border: 2px solid var(--cyber-primary);
  border-radius: 50%;
  font-size: 14px;
  font-weight: 600;
  color: var(--cyber-primary);
  box-shadow: 0 0 10px var(--cyber-primary-glow);
}

.user-name {
  color: var(--cyber-text-primary);
  font-weight: var(--cyber-font-weight-medium);
}

.logout-btn {
  padding: 8px 16px;
  background: transparent;
  border: 1px solid var(--cyber-border-highlight);
  border-radius: var(--cyber-radius-base);
  cursor: pointer;
  transition: all var(--cyber-transition-base);
}

.logout-btn:hover {
  border-color: var(--cyber-danger);
  color: var(--cyber-danger);
  box-shadow: 0 0 10px var(--cyber-danger-glow);
}

.logout-text {
  color: var(--cyber-text-secondary);
  font-size: 13px;
  font-weight: 500;
  transition: color var(--cyber-transition-fast);
}

.logout-btn:hover .logout-text {
  color: var(--cyber-danger);
}

/* 主内容区域 */
.cyber-main {
  background: var(--cyber-bg-darkest);
  padding: 24px;
  overflow-y: auto;
}

/* 底部装饰 */
.cyber-aside-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
}

.cyber-line {
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--cyber-primary), transparent);
  opacity: 0.3;
}

/* 菜单项文字 */
.menu-text {
  font-family: var(--cyber-font-family);
  font-weight: 500;
  letter-spacing: 1px;
}
</style>