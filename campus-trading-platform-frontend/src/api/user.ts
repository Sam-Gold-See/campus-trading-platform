import request from '@/utils/request'
import type { LoginParams, LoginResult, User } from '@/types/user'

export function login(params: LoginParams) {
  return request.post<LoginResult>('/user/login', params)
}

export function register(params: LoginParams) {
  return request.post<never>('/user/register', params)
}

export function getUserInfo() {
  return request.get<User>('/user/info')
}