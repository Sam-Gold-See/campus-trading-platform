export interface User {
  userId: number
  eduEmail: string
  nickname: string
  creditScore: number
  avatarUrl?: string
  createTime: string
}

export interface LoginParams {
  eduEmail: string
  password: string
}

export interface RegisterParams {
  eduEmail: string
  password: string
  nickname: string
}

export interface LoginResult {
  token: string
  user: User
}