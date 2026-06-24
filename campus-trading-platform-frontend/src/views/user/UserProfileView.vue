<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElCard, ElProgress, ElTag, ElButton, ElTabs, ElTabPane, ElEmpty, ElSkeleton, ElMessage } from 'element-plus'
import { getUserProfile } from '@/api/user'
import { getUserReviews } from '@/api/review'
import { getMyItems } from '@/api/item'
import type { User } from '@/types/user'
import type { Review } from '@/types/review'
import type { Item } from '@/types/item'

const route = useRoute()
const router = useRouter()

const userId = computed(() => Number(route.params.id))
const user = ref<User | null>(null)
const reviews = ref<Review[]>([])
const userItems = ref<Item[]>([])
const loading = ref(true)

// 信用分徽章颜色
function creditColor(score: number) {
  if (score >= 90) return 'success'
  if (score >= 75) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 信用分徽章文本
function creditLabel(score: number) {
  if (score >= 90) return '优秀'
  if (score >= 75) return '良好'
  if (score >= 60) return '一般'
  return '低信用'
}

// 格式化时间
function formatTime(time: string) {
  return new Date(time).toLocaleDateString()
}

// 加载用户数据
async function loadUserData() {
  loading.value = true
  try {
    user.value = await getUserProfile(userId.value)
    // 如果是自己，跳转到个人中心
    const currentUserId = localStorage.getItem('userId')
    if (user.value?.id === Number(currentUserId)) {
      router.replace('/profile')
      return
    }
    reviews.value = await getUserReviews(userId.value, 1, 10)
    userItems.value = await getMyItems(undefined, 1, 5)
  } catch (e) {
    ElMessage.error('用户不存在或已被禁用')
    router.replace('/')
  } finally {
    loading.value = false
  }
}

onMounted(loadUserData)
</script>

<template>
  <div class="user-profile" v-if="loading">
    <ElSkeleton :rows="5" animated />
  </div>

  <div class="user-profile" v-else-if="user">
    <!-- 用户信息卡片 -->
    <ElCard shadow="hover" style="max-width: 800px; margin: 0 auto 20px">
      <div style="display: flex; align-items: center; gap: 20px">
        <div style="width: 80px; height: 80px; border-radius: 50%; background: #f0f2f5; display: flex; align-items: center; justify-content: center; font-size: 32px">
          {{ user.nickname.charAt(0) }}
        </div>
        <div style="flex: 1">
          <h2 style="margin: 0">{{ user.nickname }}</h2>
          <p style="margin: 4px 0 0; color: #909399">
            注册时间：{{ formatTime(user.createdAt) }}
          </p>
        </div>
        <div style="text-align: center">
          <ElProgress
            type="dashboard"
            :percentage="user.creditScore"
            :color="creditColor(user.creditScore)"
            :width="120"
          >
            <template #default>
              <span style="font-size: 24px; font-weight: bold">{{ user.creditScore }}</span>
            </template>
          </ElProgress>
          <ElTag :type="creditColor(user.creditScore)" size="default" style="margin-top: 8px">
            信用{{ creditLabel(user.creditScore) }}
          </ElTag>
        </div>
      </div>
    </ElCard>

    <!-- Tabs -->
    <ElTabs style="max-width: 800px; margin: 0 auto">
      <ElTabPane label="收到的评价">
        <ElCard shadow="never">
          <div v-if="reviews.length === 0">
            <ElEmpty description="暂无评价记录" />
          </div>
          <div v-else class="reviews-list">
            <ElCard v-for="review in reviews" :key="review.id" shadow="hover" style="margin-bottom: 10px">
              <div style="display: flex; gap: 16px">
                <div style="width: 40px; height: 40px; border-radius: 50%; background: #f0f2f5; display: flex; align-items: center; justify-content: center">
                  {{ review.reviewerNickname?.charAt(0) || '?' }}
                </div>
                <div style="flex: 1">
                  <div>
                    <span style="font-weight: bold">{{ review.reviewerNickname }}</span>
                    <ElTag :type="review.ratingType === 1 ? 'success' : review.ratingType === -1 ? 'danger' : 'info'" size="small">
                      {{ review.ratingType === 1 ? '好评' : review.ratingType === -1 ? '差评' : '中评' }}
                    </ElTag>
                    <span style="color: #909399; margin-left: 8px">{{ formatTime(review.createdAt) }}</span>
                  </div>
                  <div v-if="review.tags && review.tags.length > 0" style="margin-top: 8px">
                    <ElTag v-for="tag in review.tags" :key="tag" size="small" style="margin-right: 4px">{{ tag }}</ElTag>
                  </div>
                  <div v-if="review.content" style="margin-top: 8px; color: #606266">
                    {{ review.content }}
                  </div>
                </div>
              </div>
            </ElCard>
          </div>
        </ElCard>
      </ElTabPane>

      <ElTabPane label="发布的物品">
        <ElCard shadow="never">
          <div v-if="userItems.length === 0">
            <ElEmpty description="暂无发布的物品" />
          </div>
          <div v-else class="items-list">
            <ElCard v-for="item in userItems" :key="item.id" shadow="hover" style="margin-bottom: 10px; cursor: pointer" @click="router.push(`/item/${item.id}`)">
              <div style="display: flex; align-items: center">
                <ElTag :type="item.type === 1 ? 'warning' : 'success'" size="small">
                  {{ item.type === 1 ? '求购' : '转让' }}
                </ElTag>
                <span style="margin-left: 8px; flex: 1; overflow: hidden; text-overflow: ellipsis">
                  {{ item.content }}
                </span>
                <span style="color: #f56c6c; font-weight: bold">
                  {{ item.price ? `¥${item.price}` : '面议' }}
                </span>
              </div>
            </ElCard>
          </div>
        </ElCard>
      </ElTabPane>
    </ElTabs>
  </div>

  <div v-else style="text-align: center; padding: 40px">
    <ElEmpty description="用户不存在" />
    <ElButton type="primary" @click="router.push('/')">返回首页</ElButton>
  </div>
</template>

<style scoped>
.user-profile {
  padding: 20px;
}

.reviews-list,
.items-list {
  display: flex;
  flex-direction: column;
}
</style>