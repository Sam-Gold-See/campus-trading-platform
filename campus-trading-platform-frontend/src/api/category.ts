import request from '@/utils/request'
import type { ConfigDict, Category } from '@/types/category'

export function getAllCategories() {
  return request.get<Category[]>('/config/categories')
}

export function getConfigDict() {
  return request.get<ConfigDict>('/config/dict')
}