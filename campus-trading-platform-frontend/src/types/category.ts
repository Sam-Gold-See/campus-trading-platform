export interface Category {
  id: number
  name: string
  sortOrder?: number
}

export interface ConfigDict {
  categories: Category[]
  campuses: string[]
  priceRanges: string[]
}