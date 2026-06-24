export interface Message {
  id: number
  senderId: number
  receiverId: number
  itemId: number
  content: string
  isRead: boolean
  createdAt: string
}

export interface Conversation {
  peerId: number
  peerNickname: string
  peerAvatar?: string
  itemId: number
  itemTitle: string
  lastMessage: string
  lastTime: string
  unreadCount: number
}

export interface SendMessageParams {
  receiverId: number
  itemId: number
  content: string
}