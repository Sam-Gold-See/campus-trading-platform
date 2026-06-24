import request from '@/utils/request'
import type { Review, SubmitReviewParams } from '@/types/review'

export function submitReview(params: SubmitReviewParams) {
  return request.post<never>('/evaluate/submit', params)
}

export function getUserReviews(revieweeId: number, page = 1, size = 10) {
  return request.get<Review[]>(`/evaluate/user/${revieweeId}`, { params: { page, size } })
}

export function checkReviewed(itemId: number, reviewerId: number) {
  return request.get<boolean>('/evaluate/check', { params: { itemId, reviewerId } })
}