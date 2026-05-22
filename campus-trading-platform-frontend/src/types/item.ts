export type ItemType = 1 | 2 // 1=求购 2=转让

export type ItemStatus = 'ACTIVE' | 'SOLD' | 'EXPIRED' | 'REMOVED'

export interface Item {
  itemId: number
  userId: number
  type: ItemType
  categoryId: number
  content: string
  imageUrl?: string
  priceMin?: number
  priceMax?: number
  campusLocation: string
  status: ItemStatus
  createTime: string
  updateTime: string
}

export interface ItemCreateParams {
  type: ItemType
  categoryId: number
  content: string
  imageUrl?: string
  priceMin?: number
  priceMax?: number
  campusLocation: string
}