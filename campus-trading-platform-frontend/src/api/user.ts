import request from '@/utils/request'
import type { AuthResult, User, EditNicknameParams, EditPasswordParams, RegisterParams } from '@/types/user'

export function login(params: { eduEmail: string; password?: string; verificationCode?: string }) {
  return request.post<AuthResult>('/user/login', params)
}

export function sendRegisterCode(eduEmail: string) {
  return request.post<never>('/user/sendRegisterCode', { eduEmail })
}

export function register(params: RegisterParams) {
  return request.post<AuthResult>('/user/register', params)
}

export function getMyProfile() {
  return request.get<User>('/user/profile')
}

export function getUserProfile(userId: number) {
  return request.get<User>(`/user/profile/${userId}`)
}

export function editNickname(params: EditNicknameParams) {
  return request.put<never>('/user/editNickname', params)
}

export function editPassword(params: EditPasswordParams) {
  return request.put<never>('/user/editPassword', params)
}

export function logout() {
  return request.post<never>('/user/logout')
}