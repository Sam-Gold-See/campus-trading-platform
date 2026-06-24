import request from '@/utils/request'
import type { Item, ItemCreateParams, SearchParams } from '@/types/item'

export function publishItem(params: ItemCreateParams) {
  return request.post<number>('/item/publish', params)
}

export function getFeedList(type?: number, page = 1, size = 10) {
  return request.get<Item[]>('/item/feed', { params: { type, page, size } })
}

export function searchItems(params: SearchParams) {
  return request.get<Item[]>('/item/search', { params })
}

export function getMyItems(status?: number, page = 1, size = 10) {
  return request.get<Item[]>('/item/my-list', { params: { status, page, size } })
}

export function getItemDetail(id: number) {
  return request.get<Item>(`/item/${id}`)
}

export function bumpItem(id: number) {
  return request.put<never>(`/item/${id}/bump`)
}

export function offlineItem(id: number) {
  return request.put<never>(`/item/${id}/offline`)
}

export function markItemSold(id: number, matchedUserId: number) {
  return request.put<never>(`/item/${id}/sold`, { matchedUserId })
}