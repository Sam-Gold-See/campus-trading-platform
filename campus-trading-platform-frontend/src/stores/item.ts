import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Item, SearchParams, ItemCreateParams } from '@/types/item'
import { getFeedList, searchItems, getMyItems, getItemDetail, publishItem, bumpItem, offlineItem, markItemSold } from '@/api/item'

export const useItemStore = defineStore('item', () => {
  // 首页Feed列表
  const feedList = ref<Item[]>([])
  const feedType = ref<number | undefined>(undefined) // 1求购 2转让 undefined全部
  const feedPage = ref(1)
  const feedLoading = ref(false)
  const feedHasMore = ref(true)

  // 我的发布列表
  const myItems = ref<Item[]>([])
  const myItemsStatus = ref<number | undefined>(undefined)
  const myItemsPage = ref(1)
  const myItemsLoading = ref(false)

  // 当前查看的物品详情
  const currentItem = ref<Item | null>(null)

  // 加载首页Feed
  async function loadFeed(reset = false) {
    if (feedLoading.value) return
    if (!reset && !feedHasMore.value) return

    feedLoading.value = true
    try {
      if (reset) {
        feedPage.value = 1
        feedList.value = []
        feedHasMore.value = true
      }

      const items = await getFeedList(feedType.value, feedPage.value, 10)
      if (reset) {
        feedList.value = items
      } else {
        feedList.value.push(...items)
      }

      feedHasMore.value = items.length >= 10
      feedPage.value++
    } finally {
      feedLoading.value = false
    }
  }

  // 切换交易类型
  async function switchFeedType(type: number | undefined) {
    feedType.value = type
    await loadFeed(true)
  }

  // 搜索物品
  async function search(params: SearchParams) {
    feedLoading.value = true
    try {
      const items = await searchItems(params)
      feedList.value = items
      feedHasMore.value = false // 搜索结果不支持分页
    } finally {
      feedLoading.value = false
    }
  }

  // 发布物品
  async function publish(params: ItemCreateParams) {
    const itemId = await publishItem(params)
    // 发布成功后刷新首页
    await loadFeed(true)
    return itemId
  }

  // 加载我的发布
  async function loadMyItems(status?: number, reset = true) {
    if (myItemsLoading.value) return

    myItemsLoading.value = true
    try {
      if (reset) {
        myItemsPage.value = 1
        myItems.value = []
      }

      myItemsStatus.value = status
      const items = await getMyItems(status, myItemsPage.value, 10)
      if (reset) {
        myItems.value = items
      } else {
        myItems.value.push(...items)
      }
      myItemsPage.value++
    } finally {
      myItemsLoading.value = false
    }
  }

  // 获取物品详情
  async function loadItemDetail(id: number) {
    currentItem.value = await getItemDetail(id)
  }

  // 擦亮物品
  async function bump(itemId: number) {
    await bumpItem(itemId)
    // 更新本地数据
    const idx = myItems.value.findIndex(i => i.id === itemId)
    if (idx >= 0) {
      const foundItem = myItems.value[idx]
      if (foundItem) {
        foundItem.createdAt = new Date().toISOString()
        // 移到列表顶部
        const item = myItems.value.splice(idx, 1)[0]
        if (item) {
          myItems.value.unshift(item)
        }
      }
    }
  }

  // 下架物品
  async function offline(itemId: number) {
    await offlineItem(itemId)
    // 从列表移除或更新状态
    const idx = myItems.value.findIndex(i => i.id === itemId)
    if (idx >= 0) {
      myItems.value.splice(idx, 1)
    }
  }

  // 标记已成交
  async function markSold(itemId: number, matchedUserId: number) {
    await markItemSold(itemId, matchedUserId)
    // 从列表移除或更新状态
    const idx = myItems.value.findIndex(i => i.id === itemId)
    if (idx >= 0) {
      myItems.value.splice(idx, 1)
    }
  }

  return {
    feedList, feedType, feedLoading, feedHasMore,
    myItems, myItemsStatus, myItemsLoading,
    currentItem,
    loadFeed, switchFeedType, search, publish,
    loadMyItems, loadItemDetail, bump, offline, markSold
  }
})