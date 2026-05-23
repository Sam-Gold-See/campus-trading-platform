import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User, AuthResult, RegisterParams } from '@/types/user'
import { login as loginApi, register as registerApi, getUserProfile as getUserProfileApi, logout as logoutApi } from '@/api/user'
import { wsClient } from '@/utils/websocket'
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

  async function login(params: { eduEmail: string; password?: string; verificationCode?: string }) {
    const result: AuthResult = await loginApi(params)
    token.value = result.token
    localStorage.setItem('token', result.token)
    userInfo.value = authResultToUser(result)
    // 后端 profile 接口可用后，用 fetchProfile 覆盖完整信息
    try { await fetchProfile() } catch { /* profile 接口未实现时忽略 */ }
    wsClient.connect()
  }

  async function register(params: RegisterParams) {
    const result: AuthResult = await registerApi(params)
    token.value = result.token
    localStorage.setItem('token', result.token)
    userInfo.value = authResultToUser(result)
    try { await fetchProfile() } catch { /* profile 接口未实现时忽略 */ }
    wsClient.connect()
  }

  async function fetchProfile() {
    const user = await getUserProfileApi()
    userInfo.value = user
  }

  async function logout() {
    try {
      await logoutApi()
    } catch {
      // 即使API失败也要清理本地状态
    }
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    wsClient.disconnect()
    router.push('/login')
  }

  return { token, userInfo, login, register, fetchProfile, logout }
})