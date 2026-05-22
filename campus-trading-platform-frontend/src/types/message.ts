export interface Message {
  messageId: number
  senderId: number
  receiverId: number
  itemId: number
  content: string
  isRead: boolean
  createTime: string
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