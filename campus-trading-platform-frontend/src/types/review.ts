export type RatingType = 1 | 0 | -1 // 好评/中评/差评

export interface Review {
  reviewId: number
  reviewerId: number
  revieweeId: number
  itemId: number
  ratingType: RatingType
  tags: string[]
  comment?: string
  createTime: string
}

export interface ReviewCreateParams {
  revieweeId: number
  itemId: number
  ratingType: RatingType
  tags: string[]
  comment?: string
}