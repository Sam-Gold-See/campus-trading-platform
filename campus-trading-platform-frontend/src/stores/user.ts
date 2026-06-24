import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User, AuthResult, RegisterParams } from '@/types/user'
import { login as loginApi, register as registerApi, getMyProfile as getMyProfileApi, logout as logoutApi } from '@/api/user'
import router from '@/router'

function authResultToUser(result: AuthResult): User {
  return {
    id: result.id,
    eduEmail: '',
    nickname: result.nickname,
    creditScore: 100,
    userStatus: 0,
    isAdmin: 0,
    createdAt: '',
  }
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<User | null>(null)

  // 初始化 - 检查是否有 token，如果有则获取用户信息
  async function init() {
    // 只有在token存在时才尝试获取profile
    // 但profile失败不应该清除token（可能是后端服务未启动等临时问题）
    if (token.value) {
      // 先设置基本用户信息（从登录时保存的）
      // 等待profile接口成功后再更新
      try {
        await fetchProfile()
      } catch (error) {
        // profile失败时不清除token，只是不更新用户信息
        console.log('获取用户信息失败，可能是后端服务未启动:', error)
      }
    }
  }

  async function login(params: { eduEmail: string; password?: string; verificationCode?: string }) {
    const result: AuthResult = await loginApi(params)
    token.value = result.token
    localStorage.setItem('token', result.token)
    userInfo.value = authResultToUser(result)
    // 尝试获取完整信息，失败不影响登录
    try {
      await fetchProfile()
    } catch {
      console.log('获取完整用户信息失败，使用基本信息')
    }
    // 返回成功，让LoginView处理跳转
    return result
  }

  async function register(params: RegisterParams) {
    const result: AuthResult = await registerApi(params)
    token.value = result.token
    localStorage.setItem('token', result.token)
    userInfo.value = authResultToUser(result)
    try {
      await fetchProfile()
    } catch {
      console.log('获取完整用户信息失败，使用基本信息')
    }
    return result
  }

  async function fetchProfile() {
    const user = await getMyProfileApi()
    userInfo.value = user
  }

  // 清除认证信息
  function clearAuth() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  async function logout() {
    try {
      await logoutApi()
    } catch {
      // 即使API失败也要清理本地状态
    }
    clearAuth()
    router.push('/login')
  }

  return { token, userInfo, init, login, register, fetchProfile, logout, clearAuth }
})