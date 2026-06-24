export type ItemType = 1 | 2 // 1=求购 2=转让

export type ItemStatus = 0 | 1 | 2 | 3 // 0=展示中 1=已成交 2=已失效 3=已删除

export interface Item {
  id: number
  userId: number
  userNickname?: string
  userCreditScore?: number
  type: number // 1=求购 2=转让
  categoryId: number
  categoryName?: string
  campus: string
  price?: number
  content: string
  imageUrl?: string
  itemStatus: number // 0=展示中 1=已成交 2=已失效 3=已删除
  matchedUserId?: number
  createdAt: string
  expireAt: string
}

export interface ItemCreateParams {
  type: number // 1=求购 2=转让
  categoryId: number
  campus: string
  price?: number
  content: string
  imageUrl?: string
}

export interface SearchParams {
  keyword?: string
  type?: number
  categoryId?: number
  campus?: string
  priceRange?: string
  page: number
  size: number
}