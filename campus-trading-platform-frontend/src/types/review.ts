export type RatingType = 1 | 0 | -1 // 好评/中评/差评

export interface Review {
  id: number
  itemId: number
  reviewerId: number
  reviewerNickname?: string
  revieweeId: number
  ratingType: number // 1=好评 0=中评 -1=差评
  tags: string[]
  content?: string
  createdAt: string
}

export interface SubmitReviewParams {
  itemId: number
  revieweeId: number
  ratingType: number // 1=好评 0=中评 -1=差评
  tags?: string[]
  content?: string
}