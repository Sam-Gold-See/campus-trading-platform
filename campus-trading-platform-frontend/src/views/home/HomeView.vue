<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { ElTabs, ElTabPane, ElCard, ElTag, ElButton, ElInput, ElSelect, ElImage, ElEmpty, ElSkeleton, ElRow, ElCol } from 'element-plus'
import { useRouter } from 'vue-router'
import { useItemStore } from '@/stores/item'
import { useConfigStore } from '@/stores/config'
import { useUserStore } from '@/stores/user'
import type { Item } from '@/types/item'

const router = useRouter()
const itemStore = useItemStore()
const configStore = useConfigStore()
const userStore = useUserStore()

// 交易类型tab
const activeTab = ref('all')
const tabs = [
  { label: '全部', value: 'all' },
  { label: '我要转让', value: 'sell' },
  { label: '我要求购', value: 'buy' },
]

// 搜索筛选
const searchKeyword = ref('')
const filterCategory = ref<number | undefined>()
const filterCampus = ref<string | undefined>()
const filterPriceRange = ref<string | undefined>()

const typeMap: Record<string, number | undefined> = {
  all: undefined,
  sell: 2,
  buy: 1,
}

// 切换tab
async function handleTabChange(tab: string) {
  activeTab.value = tab
  await itemStore.switchFeedType(typeMap[tab])
}

// 搜索
async function handleSearch() {
  if (!searchKeyword.value && !filterCategory.value && !filterCampus.value && !filterPriceRange.value) {
    await itemStore.loadFeed(true)
  } else {
    await itemStore.search({
      keyword: searchKeyword.value,
      type: typeMap[activeTab.value],
      categoryId: filterCategory.value,
      campus: filterCampus.value,
      priceRange: filterPriceRange.value,
      page: 1,
      size: 20
    })
  }
}

// 清空筛选
async function clearFilters() {
  searchKeyword.value = ''
  filterCategory.value = undefined
  filterCampus.value = undefined
  filterPriceRange.value = undefined
  await itemStore.loadFeed(true)
}

// 点击物品卡片
function viewItem(item: Item) {
  router.push(`/item/${item.id}`)
}

// 跳转发布页
function goPublish() {
  router.push('/publish')
}

// 格式化时间
function formatTime(time: string) {
  const d = new Date(time)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return d.toLocaleDateString()
}

// 信用徽章颜色
function creditColor(score: number | undefined) {
  if (!score) return 'info'
  if (score >= 90) return 'success'
  if (score >= 75) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 截断内容
function truncate(str: string, len: number) {
  return str.length > len ? str.slice(0, len) + '...' : str
}

onMounted(async () => {
  await configStore.loadConfig()
  await itemStore.loadFeed(true)
})
</script>

<template>
  <div class="cyber-home">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="cyber-title">需求墙</h1>
      <div class="cyber-line"></div>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="cyber-search-panel">
      <div class="panel-header">
        <span class="panel-icon">◇</span>
        <span class="panel-title">搜索筛选</span>
      </div>
      <div class="panel-body">
        <div class="search-row">
          <!-- 搜索关键词 -->
          <div class="search-input-wrap">
            <ElInput
              v-model="searchKeyword"
              placeholder="搜索关键词..."
              clearable
              class="cyber-search-input"
              @keyup.enter="handleSearch"
              @clear="handleSearch"
            >
              <template #suffix>
                <span class="search-icon" @click="handleSearch">⌕</span>
              </template>
            </ElInput>
          </div>

          <!-- 筛选下拉 -->
          <div class="filter-group">
            <div class="filter-item">
              <span class="filter-label">分类</span>
              <ElSelect
                v-model="filterCategory"
                placeholder="全部"
                clearable
                class="cyber-select"
                @change="handleSearch"
              >
                <ElSelect.Option
                  v-for="cat in configStore.categories"
                  :key="cat.id"
                  :label="cat.name"
                  :value="cat.id"
                />
              </ElSelect>
            </div>

            <div class="filter-item">
              <span class="filter-label">校区</span>
              <ElSelect
                v-model="filterCampus"
                placeholder="全部"
                clearable
                class="cyber-select"
                @change="handleSearch"
              >
                <ElSelect.Option
                  v-for="campus in configStore.campuses"
                  :key="campus"
                  :label="campus"
                  :value="campus"
                />
              </ElSelect>
            </div>

            <div class="filter-item">
              <span class="filter-label">价格</span>
              <ElSelect
                v-model="filterPriceRange"
                placeholder="全部"
                clearable
                class="cyber-select"
                @change="handleSearch"
              >
                <ElSelect.Option
                  v-for="range in configStore.priceRanges"
                  :key="range"
                  :label="range"
                  :value="range"
                />
              </ElSelect>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-group">
            <button class="cyber-button secondary" @click="clearFilters">
              <span class="btn-text">重置</span>
            </button>
            <button class="cyber-button" @click="goPublish">
              <span class="btn-text">+ 发布</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Tab切换 -->
    <div class="cyber-tabs-container">
      <div class="tab-header">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          class="cyber-tab-btn"
          :class="{ active: activeTab === tab.value }"
          @click="handleTabChange(tab.value)"
        >
          <span class="tab-text">{{ tab.label }}</span>
        </button>
      </div>

      <!-- 物品列表 -->
      <div class="item-list-container">
        <!-- 加载骨架屏 -->
        <div v-if="itemStore.feedLoading" class="skeleton-grid">
          <div v-for="i in 3" :key="i" class="skeleton-card">
            <div class="skeleton-image"></div>
            <div class="skeleton-content">
              <div class="skeleton-line skeleton-tags"></div>
              <div class="skeleton-line skeleton-text"></div>
              <div class="skeleton-line skeleton-text"></div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else-if="itemStore.feedList.length === 0" class="empty-state">
          <div class="empty-icon">◇</div>
          <p class="empty-text">暂无匹配内容</p>
          <p class="empty-subtext">快去发布求购/转让吧~</p>
          <button class="cyber-button" @click="goPublish">
            <span class="btn-text">立即发布</span>
          </button>
        </div>

        <!-- 物品卡片列表 -->
        <div v-else class="item-grid">
          <div
            v-for="item in itemStore.feedList"
            :key="item.id"
            class="cyber-item-card"
            @click="viewItem(item)"
          >
            <!-- 卡片装饰线 -->
            <div class="card-decoration"></div>

            <!-- 图片区域 -->
            <div v-if="item.imageUrl" class="card-image">
              <ElImage
                :src="item.imageUrl"
                fit="cover"
                class="item-image"
              />
              <div class="image-overlay"></div>
            </div>

            <!-- 内容区域 -->
            <div class="card-content">
              <!-- 标签 -->
              <div class="item-tags">
                <ElTag :type="item.type === 1 ? 'warning' : 'success'" size="small">
                  {{ item.type === 1 ? '求购' : '转让' }}
                </ElTag>
                <ElTag size="small">{{ configStore.getCategoryName(item.categoryId) }}</ElTag>
                <ElTag type="info" size="small">{{ item.campus }}</ElTag>
              </div>

              <!-- 内容描述 -->
              <div class="item-content-text">
                {{ truncate(item.content, 80) }}
              </div>

              <!-- 底部信息 -->
              <div class="card-footer">
                <div class="price-section">
                  <span class="price-label">¥</span>
                  <span class="price-value">{{ item.price || '面议' }}</span>
                </div>
                <div class="time-section">
                  <span class="time-text">{{ formatTime(item.createdAt) }}</span>
                </div>
              </div>

              <!-- 用户信息 -->
              <div class="user-section">
                <div class="user-avatar">{{ item.userNickname?.charAt(0) || '?' }}</div>
                <span class="user-name">{{ item.userNickname }}</span>
                <ElTag :type="creditColor(item.userCreditScore)" size="small" class="credit-tag">
                  {{ item.userCreditScore }}
                </ElTag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 页面整体 */
.cyber-home {
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}

.cyber-title {
  font-size: 28px;
  margin-bottom: 16px;
}

.cyber-line {
  height: 2px;
  background: linear-gradient(90deg, var(--cyber-primary), var(--cyber-secondary), var(--cyber-primary));
  opacity: 0.5;
}

/* 搜索筛选面板 */
.cyber-search-panel {
  background: var(--cyber-bg-card);
  border: 1px solid var(--cyber-border-highlight);
  border-radius: var(--cyber-radius-xl);
  padding: 20px;
  margin-bottom: 24px;
  position: relative;
  box-shadow: var(--cyber-shadow-card);
}

.cyber-search-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--cyber-primary), transparent);
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.panel-icon {
  color: var(--cyber-primary);
  font-size: 12px;
}

.panel-title {
  font-family: var(--cyber-font-family);
  font-size: 14px;
  font-weight: var(--cyber-font-weight-semibold);
  color: var(--cyber-primary);
  letter-spacing: 2px;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.search-input-wrap {
  flex: 1;
  min-width: 200px;
}

.search-icon {
  color: var(--cyber-primary);
  font-size: 18px;
  cursor: pointer;
}

.filter-group {
  display: flex;
  gap: 12px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-family: var(--cyber-font-family);
  font-size: 13px;
  color: var(--cyber-text-secondary);
}

.cyber-select {
  width: 120px;
}

.action-group {
  display: flex;
  gap: 12px;
}

.cyber-button.secondary {
  background: var(--cyber-bg-card);
  border: 1px solid var(--cyber-border-highlight);
  color: var(--cyber-primary);
  box-shadow: none;
}

.cyber-button.secondary:hover {
  background: var(--cyber-bg-hover);
  box-shadow: 0 0 10px var(--cyber-primary-glow);
}

.btn-text {
  font-family: 'Orbitron', sans-serif;
  font-weight: 600;
  letter-spacing: 1px;
}

/* Tab容器 */
.cyber-tabs-container {
  margin-bottom: 24px;
}

.tab-header {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--cyber-border-base);
}

.cyber-tab-btn {
  padding: 12px 24px;
  background: var(--cyber-bg-card);
  border: 1px solid var(--cyber-border-base);
  border-radius: var(--cyber-radius-base);
  color: var(--cyber-text-secondary);
  font-family: 'Orbitron', sans-serif;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all var(--cyber-transition-base);
}

.cyber-tab-btn:hover {
  border-color: var(--cyber-border-highlight);
  color: var(--cyber-primary);
}

.cyber-tab-btn.active {
  background: linear-gradient(135deg, var(--cyber-primary-dark), var(--cyber-primary));
  border-color: var(--cyber-primary);
  color: var(--cyber-text-inverse);
  box-shadow: 0 0 15px var(--cyber-primary-glow);
}

.tab-text {
  text-transform: uppercase;
}

/* 物品列表容器 */
.item-list-container {
  min-height: 400px;
}

/* 骨架屏 */
.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.skeleton-card {
  background: var(--cyber-bg-card);
  border: 1px solid var(--cyber-border-base);
  border-radius: var(--cyber-radius-lg);
  padding: 16px;
}

.skeleton-image {
  width: 100%;
  height: 120px;
  background: var(--cyber-bg-darker);
  border-radius: var(--cyber-radius-base);
  margin-bottom: 12px;
}

.skeleton-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skeleton-line {
  height: 12px;
  background: var(--cyber-bg-darker);
  border-radius: 2px;
}

.skeleton-tags {
  width: 60%;
}

.skeleton-text {
  width: 90%;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: var(--cyber-bg-card);
  border: 1px solid var(--cyber-border-base);
  border-radius: var(--cyber-radius-xl);
}

.empty-icon {
  font-size: 48px;
  color: var(--cyber-primary);
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text {
  font-family: var(--cyber-font-family);
  font-size: 18px;
  color: var(--cyber-text-primary);
  margin: 0 0 8px;
}

.empty-subtext {
  font-size: 14px;
  color: var(--cyber-text-secondary);
  margin: 0 0 24px;
}

/* 物品卡片网格 */
.item-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

/* 物品卡片 */
.cyber-item-card {
  background: var(--cyber-bg-card);
  border: 1px solid var(--cyber-border-base);
  border-radius: var(--cyber-radius-lg);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all var(--cyber-transition-base);
}

.cyber-item-card:hover {
  border-color: var(--cyber-primary);
  box-shadow: var(--cyber-shadow-hover), 0 0 20px var(--cyber-primary-glow);
  transform: translateY(-4px);
}

/* 卡片装饰线 */
.card-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 2px;
  height: 100%;
  background: var(--cyber-primary);
  opacity: 0;
  transition: opacity var(--cyber-transition-base);
}

.cyber-item-card:hover .card-decoration {
  opacity: 1;
}

/* 图片区域 */
.card-image {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.item-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, transparent 50%, var(--cyber-bg-card) 100%);
  opacity: 0.5;
}

/* 内容区域 */
.card-content {
  padding: 16px;
}

.item-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.item-content-text {
  font-family: var(--cyber-font-family);
  font-size: 14px;
  color: var(--cyber-text-primary);
  line-height: 1.6;
  margin-bottom: 12px;
}

/* 底部信息 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.price-section {
  display: flex;
  align-items: baseline;
}

.price-label {
  font-family: 'Orbitron', sans-serif;
  font-size: 14px;
  color: var(--cyber-success);
  font-weight: 600;
}

.price-value {
  font-family: 'Orbitron', sans-serif;
  font-size: 18px;
  color: var(--cyber-success);
  font-weight: 700;
  text-shadow: 0 0 8px var(--cyber-success-glow);
}

.time-section {
  font-size: 12px;
  color: var(--cyber-text-secondary);
}

.time-text {
  font-family: var(--cyber-font-family);
}

/* 用户信息 */
.user-section {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid var(--cyber-border-base);
}

.user-avatar {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cyber-bg-darker);
  border: 1px solid var(--cyber-primary);
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  color: var(--cyber-primary);
}

.user-name {
  font-family: var(--cyber-font-family);
  font-size: 13px;
  color: var(--cyber-text-secondary);
}

.credit-tag {
  margin-left: auto;
}

/* 输入框样式增强 */
.cyber-search-input :deep(.el-input__wrapper) {
  background: var(--cyber-bg-input) !important;
  border: 1px solid var(--cyber-border-highlight) !important;
  border-radius: var(--cyber-radius-lg) !important;
}

.cyber-search-input :deep(.el-input__wrapper:hover) {
  border-color: var(--cyber-primary) !important;
}

.cyber-search-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--cyber-primary) !important;
  box-shadow: 0 0 15px var(--cyber-primary-glow) !important;
}

/* 响应式 */
@media (max-width: 768px) {
  .search-row {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-group {
    flex-wrap: wrap;
  }

  .action-group {
    justify-content: center;
  }

  .item-grid {
    grid-template-columns: 1fr;
  }
}
</style>