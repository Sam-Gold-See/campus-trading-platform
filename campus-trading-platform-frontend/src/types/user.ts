export interface User {
  id: number
  eduEmail: string
  nickname: string
  avatarUrl?: string
  creditScore: number
  userStatus: number // 0=正常, 1=禁用
  isAdmin: number // 0=普通用户, 1=管理员
  createdAt: string
}

export interface LoginParams {
  eduEmail: string
  password: string
}

export interface RegisterParams {
  eduEmail: string
  verificationCode: string
  nickname: string
  password: string
  confirmPassword: string
}

export interface AuthResult {
  id: number
  nickname: string
  token: string
  accessExpire: number
}

export interface EditNicknameParams {
  nickname: string
}

export interface EditPasswordParams {
  oldPassword: string
  newPassword: string
  confirmNewPassword: string
}