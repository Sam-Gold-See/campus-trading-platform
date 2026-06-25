<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  ElTabs, ElTabPane, ElCard, ElDescriptions, ElDescriptionsItem,
  ElTag, ElForm, ElFormItem, ElInput, ElButton, ElDialog,
  ElMessage, ElMessageBox, ElProgress, ElBadge,
  ElEmpty, ElSkeleton,
} from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useItemStore } from '@/stores/item'
import { useRouter } from 'vue-router'
import { editNickname, editPassword } from '@/api/user'
import { getUserReviews } from '@/api/review'
import type { Review } from '@/types/review'
import type { Item } from '@/types/item'

const userStore = useUserStore()
const itemStore = useItemStore()
const router = useRouter()

const activeTab = ref('info')
const user = computed(() => userStore.userInfo)

// 信用分徽章
const creditLevel = computed(() => {
  const score = user.value?.creditScore ?? 100
  if (score >= 90) return { label: '优秀', type: 'success' as const, color: '#67c23a' }
  if (score >= 75) return { label: '良好', type: 'primary' as const, color: '#409eff' }
  if (score >= 60) return { label: '一般', type: 'warning' as const, color: '#e6a23c' }
  return { label: '低信用', type: 'danger' as const, color: '#f56c6c' }
})

// 邮箱脱敏
const maskedEmail = computed(() => {
  const email = user.value?.eduEmail ?? ''
  if (!email) return '--'
  const [prefix, domain] = email.split('@')
  if (!prefix || !domain) return email
  const visible = prefix.length <= 2 ? prefix : prefix.slice(0, 2)
  return `${visible}***@${domain}`
})

// 昵称修改
const nicknameForm = ref({ nickname: '' })
const nicknameFormRef = ref()
const nicknameRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 4, max: 16, message: '昵称需4-16位', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: (err?: Error) => void) => {
        if (value && /[^a-zA-Z0-9一-龥_]/.test(value)) {
          callback(new Error('昵称不可包含特殊字符'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
}

const handleEditNickname = async () => {
  await nicknameFormRef.value?.validate()
  await ElMessageBox.confirm(
    `确认将昵称修改为"${nicknameForm.value.nickname}"？每30天仅可修改一次。`,
    '修改昵称',
    { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' },
  )
  try {
    await editNickname(nicknameForm.value)
    ElMessage.success('昵称修改成功')
    nicknameForm.value.nickname = ''
    showNicknameDialog.value = false
    try { await userStore.fetchProfile() } catch { /* 后端未实现时忽略 */ }
  } catch {
    // error handled by request interceptor
  }
}

// 密码修改
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: '',
})
const passwordFormRef = ref()
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码需8-20位', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: (err?: Error) => void) => {
        if (value && !/[a-zA-Z]/.test(value)) {
          callback(new Error('密码需包含字母'))
        } else if (value && !/[0-9]/.test(value)) {
          callback(new Error('密码需包含数字'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
  confirmNewPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: (err?: Error) => void) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
}

const handleEditPassword = async () => {
  await passwordFormRef.value?.validate()
  await ElMessageBox.confirm(
    '修改密码后将强制下线其他设备，需重新登录。是否继续？',
    '修改密码',
    { confirmButtonText: '确认修改', cancelButtonText: '取消', type: 'warning' },
  )
  try {
    await editPassword(passwordForm.value)
    ElMessage.success('密码修改成功，请重新登录')
    passwordForm.value = { oldPassword: '', newPassword: '', confirmNewPassword: '' }
    await userStore.logout()
  } catch {
    // error handled by request interceptor
  }
}

// 昵称修改对话框
const showNicknameDialog = ref(false)
const openNicknameDialog = () => {
  nicknameForm.value.nickname = ''
  showNicknameDialog.value = true
}

// 我的发布列表
const myItemsStatus = ref<number | undefined>(undefined)
const myItemsStatusTabs = [
  { label: '进行中', value: 0 },
  { label: '已成交', value: 1 },
  { label: '已失效', value: 2 },
]

async function loadMyItems(status?: number) {
  myItemsStatus.value = status
  await itemStore.loadMyItems(status)
}

// 物品操作
async function handleBumpItem(item: Item) {
  try {
    await itemStore.bump(item.id)
    ElMessage.success('擦亮成功')
  } catch {
    // error handled
  }
}

async function handleOfflineItem(item: Item) {
  await ElMessageBox.confirm('确定下架该信息吗？', '下架', { type: 'warning' })
  try {
    await itemStore.offline(item.id)
    ElMessage.success('已下架')
  } catch {
    // error handled
  }
}

function viewItemDetail(item: Item) {
  // 跳转到物品详情
  window.location.href = `/item/${item.id}`
}

function goToReview(item: Item) {
  // 判断当前用户是卖家还是买家，评价对方
  const currentUserId = user.value?.id
  const revieweeId = item.userId === currentUserId ? item.matchedUserId : item.userId
  router.push({ path: '/review/submit', query: { itemId: item.id, revieweeId } })
}

// 我的评价列表
const reviews = ref<Review[]>([])
const reviewsLoading = ref(false)

async function loadReviews() {
  if (!user.value?.id) return
  reviewsLoading.value = true
  try {
    reviews.value = await getUserReviews(user.value.id)
  } finally {
    reviewsLoading.value = false
  }
}

// 格式化时间
function formatTime(time: string) {
  return new Date(time).toLocaleDateString()
}

onMounted(async () => {
  await userStore.fetchProfile()
  await loadMyItems()
})
</script>

<template>
  <div v-if="user" style="max-width: 800px; margin: 0 auto">
    <!-- 信用分展示卡片 -->
    <ElCard shadow="hover" style="margin-bottom: 20px">
      <div style="display: flex; align-items: center; gap: 20px">
        <div style="width: 64px; height: 64px; border-radius: 50%; background: #f0f2f5; display: flex; align-items: center; justify-content: center; font-size: 24px">
          {{ user.nickname.charAt(0) }}
        </div>
        <div style="flex: 1">
          <h3 style="margin: 0">{{ user.nickname }}</h3>
          <p style="margin: 4px 0 0; color: #909399">{{ maskedEmail }}</p>
        </div>
        <div style="text-align: center">
          <ElProgress
            type="dashboard"
            :percentage="user.creditScore"
            :color="creditLevel.color"
            :width="100"
          >
            <template #default>
              <span style="font-size: 20px; font-weight: bold">{{ user.creditScore }}</span>
            </template>
          </ElProgress>
          <ElTag :type="creditLevel.type" size="small" style="margin-top: 4px">
            信用{{ creditLevel.label }}
          </ElTag>
        </div>
      </div>
    </ElCard>

    <!-- 标签页 -->
    <ElTabs v-model="activeTab">
      <ElTabPane label="基本信息" name="info">
        <ElCard>
          <ElDescriptions :column="1" border>
            <ElDescriptionsItem label="昵称">
              {{ user.nickname }}
              <ElButton size="small" type="primary" link @click="openNicknameDialog">修改</ElButton>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="教育邮箱">{{ maskedEmail }}</ElDescriptionsItem>
            <ElDescriptionsItem label="信用分">
              <ElTag :type="creditLevel.type">{{ user.creditScore }} - {{ creditLevel.label }}</ElTag>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="账号状态">
              <ElTag :type="user.userStatus === 0 ? 'success' : 'danger'">
                {{ user.userStatus === 0 ? '正常' : '禁用' }}
              </ElTag>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="注册时间">{{ user.createdAt || '--' }}</ElDescriptionsItem>
          </ElDescriptions>

          <div style="margin-top: 16px; display: flex; gap: 12px">
            <ElButton @click="activeTab = 'password'">修改密码</ElButton>
            <ElButton type="danger" @click="userStore.logout()">退出登录</ElButton>
          </div>
        </ElCard>
      </ElTabPane>

      <ElTabPane label="我的发布" name="myItems">
        <ElCard>
          <!-- 状态筛选 -->
          <div style="margin-bottom: 16px">
            <ElButton
              v-for="tab in myItemsStatusTabs"
              :key="tab.value"
              :type="myItemsStatus === tab.value ? 'primary' : 'default'"
              @click="loadMyItems(tab.value)"
            >
              {{ tab.label }}
            </ElButton>
          </div>

          <!-- 物品列表 -->
          <div v-if="itemStore.myItemsLoading">
            <ElSkeleton :rows="3" animated />
          </div>
          <div v-else-if="itemStore.myItems.length === 0">
            <ElEmpty description="暂无发布记录" />
          </div>
          <div v-else class="my-items-list">
            <ElCard v-for="item in itemStore.myItems" :key="item.id" shadow="hover" style="margin-bottom: 10px">
              <div style="display: flex; justify-content: space-between">
                <div style="flex: 1">
                  <ElTag :type="item.type === 1 ? 'warning' : 'success'" size="small">
                    {{ item.type === 1 ? '求购' : '转让' }}
                  </ElTag>
                  <span style="margin-left: 8px">{{ item.content }}</span>
                  <div style="color: #909399; margin-top: 4px">
                    {{ formatTime(item.createdAt) }} | {{ item.price ? `¥${item.price}` : '面议' }}
                  </div>
                </div>
                <div v-if="myItemsStatus === 0">
                  <ElButton size="small" @click="handleBumpItem(item)">擦亮</ElButton>
                  <ElButton size="small" type="warning" @click="handleOfflineItem(item)">下架</ElButton>
                  <ElButton size="small" type="success" @click="viewItemDetail(item)">标记成交</ElButton>
                </div>
                <div v-else-if="myItemsStatus === 1 && item.matchedUserId">
                  <ElButton size="small" type="primary" @click="goToReview(item)">
                    去评价
                  </ElButton>
                </div>
              </div>
            </ElCard>
          </div>
        </ElCard>
      </ElTabPane>

      <ElTabPane label="收到的评价" name="reviews">
        <ElCard>
          <div v-if="reviewsLoading">
            <ElSkeleton :rows="3" animated />
          </div>
          <div v-else-if="reviews.length === 0">
            <ElEmpty description="暂无评价记录" />
          </div>
          <div v-else class="reviews-list">
            <ElCard v-for="review in reviews" :key="review.id" shadow="hover" style="margin-bottom: 10px">
              <div style="display: flex; gap: 16px">
                <div style="width: 40px; height: 40px; border-radius: 50%; background: #f0f2f5; display: flex; align-items: center; justify-content: center">
                  {{ review.reviewerNickname?.charAt(0) }}
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
          <ElButton @click="loadReviews">加载评价</ElButton>
        </ElCard>
      </ElTabPane>

      <ElTabPane label="修改密码" name="password">
        <ElCard>
          <ElForm
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="120px"
            style="max-width: 500px"
          >
            <ElFormItem label="旧密码" prop="oldPassword">
              <ElInput
                v-model="passwordForm.oldPassword"
                type="password"
                show-password
                placeholder="请输入旧密码"
              />
            </ElFormItem>
            <ElFormItem label="新密码" prop="newPassword">
              <ElInput
                v-model="passwordForm.newPassword"
                type="password"
                show-password
                placeholder="8-20位，需包含字母和数字"
              />
            </ElFormItem>
            <ElFormItem label="确认新密码" prop="confirmNewPassword">
              <ElInput
                v-model="passwordForm.confirmNewPassword"
                type="password"
                show-password
                placeholder="再次输入新密码"
              />
            </ElFormItem>
            <ElFormItem>
              <ElButton type="primary" @click="handleEditPassword">确认修改</ElButton>
            </ElFormItem>
          </ElForm>
        </ElCard>
      </ElTabPane>
    </ElTabs>

    <!-- 昵称修改对话框 -->
    <ElDialog v-model="showNicknameDialog" title="修改昵称" width="400px" :close-on-click-modal="false">
      <ElForm
        ref="nicknameFormRef"
        :model="nicknameForm"
        :rules="nicknameRules"
        label-width="80px"
      >
        <ElFormItem label="新昵称" prop="nickname">
          <ElInput v-model="nicknameForm.nickname" placeholder="4-16位，不含特殊字符" maxlength="16" show-word-limit />
        </ElFormItem>
        <p style="color: #909399; font-size: 12px; margin: 0; padding-left: 80px">
          注意：每30天仅可修改一次昵称
        </p>
      </ElForm>
      <template #footer>
        <ElButton @click="showNicknameDialog = false">取消</ElButton>
        <ElButton type="primary" @click="handleEditNickname">确认修改</ElButton>
      </template>
    </ElDialog>
  </div>

  <div v-else style="text-align: center; padding: 40px">
    <p>未获取到用户信息</p>
    <ElButton type="primary" @click="userStore.logout()">返回登录</ElButton>
  </div>
</template>

<style scoped>
.my-items-list {
  display: flex;
  flex-direction: column;
}

.reviews-list {
  display: flex;
  flex-direction: column;
}
</style>