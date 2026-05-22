import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User, LoginParams, LoginResult } from '@/types/user'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/user'
import { wsClient } from '@/utils/websocket'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<User | null>(null)
  const nickname = ref<string>('')

  async function login(params: LoginParams) {
    const result: LoginResult = await loginApi(params)
    token.value = result.token
    userInfo.value = result.user
    nickname.value = result.user.nickname
    localStorage.setItem('token', result.token)
    wsClient.connect()
  }

  async function fetchUserInfo() {
    const user = await getUserInfoApi()
    userInfo.value = user
    nickname.value = user.nickname
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    nickname.value = ''
    localStorage.removeItem('token')
    wsClient.disconnect()
  }

  return { token, userInfo, nickname, login, fetchUserInfo, logout }
})