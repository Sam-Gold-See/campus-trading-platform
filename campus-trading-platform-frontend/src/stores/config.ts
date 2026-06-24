import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Category, ConfigDict } from '@/types/category'
import { getAllCategories, getConfigDict } from '@/api/category'

export const useConfigStore = defineStore('config', () => {
  const categories = ref<Category[]>([])
  const campuses = ref<string[]>([])
  const priceRanges = ref<string[]>([])
  const loaded = ref(false)

  async function loadConfig() {
    if (loaded.value) return

    try {
      const dict = await getConfigDict()
      categories.value = dict.categories
      campuses.value = dict.campuses
      priceRanges.value = dict.priceRanges
      loaded.value = true
    } catch (e) {
      console.error('加载配置失败', e)
    }
  }

  function getCategoryName(id: number) {
    const cat = categories.value.find(c => c.id === id)
    return cat?.name || '未分类'
  }

  return { categories, campuses, priceRanges, loaded, loadConfig, getCategoryName }
})