<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElCard, ElTag, ElImage, ElButton, ElDescriptions, ElDescriptionsItem, ElDialog, ElInput, ElMessage } from 'element-plus'
import { useItemStore } from '@/stores/item'
import { useConfigStore } from '@/stores/config'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const itemStore = useItemStore()
const configStore = useConfigStore()
const userStore = useUserStore()

const itemId = computed(() => Number(route.params.id))
const isOwner = computed(() => itemStore.currentItem?.userId === userStore.userInfo?.id)

// 标记成交对话框
const showSoldDialog = ref(false)
const matchedUserIdInput = ref<string>('')
const matchedNicknameInput = ref<string>('')

// 信用徽章颜色
function creditColor(score: number | undefined) {
  if (!score) return 'info'
  if (score >= 90) return 'success'
  if (score >= 75) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 跳转用户主页
function viewUser(userId: number) {
  router.push(`/user/${userId}`)
}

// 擦亮
async function handleBump() {
  try {
    await itemStore.bump(itemId.value)
    ElMessage.success('擦亮成功')
  } catch (e) {
    // error handled
  }
}

// 下架
async function handleOffline() {
  try {
    await itemStore.offline(itemId.value)
    ElMessage.success('已下架')
    router.push('/profile')
  } catch (e) {
    // error handled
  }
}

// 打开标记成交对话框
function openSoldDialog() {
  matchedUserIdInput.value = ''
  matchedNicknameInput.value = ''
  showSoldDialog.value = true
}

// 提交标记成交
async function handleMarkSold() {
  const userId = Number(matchedUserIdInput.value)
  if (!userId || userId <= 0) {
    ElMessage.warning('请输入有效的用户ID')
    return
  }
  try {
    await itemStore.markSold(itemId.value, userId)
    ElMessage.success('已标记成交')
    showSoldDialog.value = false
    router.push('/profile')
  } catch (e) {
    // error handled
  }
}

onMounted(async () => {
  await configStore.loadConfig()
  await itemStore.loadItemDetail(itemId.value)
})
</script>

<template>
  <div class="item-detail" v-if="itemStore.currentItem">
    <ElCard shadow="never" style="max-width: 800px; margin: 0 auto">
      <!-- 物品基本信息 -->
      <div class="item-header">
        <div class="item-tags">
          <ElTag :type="itemStore.currentItem.type === 1 ? 'warning' : 'success'" size="large">
            {{ itemStore.currentItem.type === 1 ? '求购' : '转让' }}
          </ElTag>
          <ElTag size="large">{{ configStore.getCategoryName(itemStore.currentItem.categoryId) }}</ElTag>
          <ElTag type="info" size="large">{{ itemStore.currentItem.campus }}</ElTag>
        </div>
        <h2 style="margin-top: 16px">{{ itemStore.currentItem.content }}</h2>
      </div>

      <!-- 图片 -->
      <div v-if="itemStore.currentItem.imageUrl" style="margin: 20px 0">
        <ElImage
          :src="itemStore.currentItem.imageUrl"
          fit="contain"
          style="max-width: 100%; max-height: 400px"
        />
      </div>

      <!-- 详细信息 -->
      <ElDescriptions :column="2" border style="margin-top: 20px">
        <ElDescriptionsItem label="期望价格">
          <span style="color: #f56c6c; font-weight: bold; font-size: 18px">
            {{ itemStore.currentItem.price ? `¥${itemStore.currentItem.price}` : '面议' }}
          </span>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="发布时间">{{ itemStore.currentItem.createdAt }}</ElDescriptionsItem>
        <ElDescriptionsItem label="有效期至">{{ itemStore.currentItem.expireAt }}</ElDescriptionsItem>
        <ElDescriptionsItem label="状态">
          <ElTag :type="itemStore.currentItem.itemStatus === 0 ? 'success' : 'info'">
            {{ itemStore.currentItem.itemStatus === 0 ? '展示中' : '已结束' }}
          </ElTag>
        </ElDescriptionsItem>
      </ElDescriptions>

      <!-- 发布者信息 -->
      <ElCard shadow="never" style="margin-top: 20px">
        <div style="display: flex; align-items: center; gap: 16px">
          <div style="width: 48px; height: 48px; border-radius: 50%; background: #f0f2f5; display: flex; align-items: center; justify-content: center">
            {{ itemStore.currentItem.userNickname?.charAt(0) }}
          </div>
          <div style="flex: 1">
            <h4 style="margin: 0">{{ itemStore.currentItem.userNickname }}</h4>
            <ElTag :type="creditColor(itemStore.currentItem.userCreditScore)" size="small">
              信用{{ itemStore.currentItem.userCreditScore }}
            </ElTag>
          </div>
          <ElButton link @click="viewUser(itemStore.currentItem.userId)">查看主页</ElButton>
        </div>
      </ElCard>

      <!-- 操作按钮 -->
      <div style="margin-top: 24px; display: flex; gap: 12px">
        <!-- 发布者操作 -->
        <template v-if="isOwner">
          <ElButton @click="handleBump">擦亮</ElButton>
          <ElButton type="warning" @click="handleOffline">下架</ElButton>
          <ElButton type="success" @click="openSoldDialog">标记已成交</ElButton>
        </template>
      </div>
    </ElCard>

    <!-- 标记成交对话框 -->
    <ElDialog v-model="showSoldDialog" title="标记已成交" width="400px">
      <p>请输入成交对象的用户ID（可在用户主页查看）：</p>
      <ElInput
        v-model="matchedUserIdInput"
        placeholder="输入用户ID"
        style="margin-bottom: 12px"
      />
      <ElInput
        v-model="matchedNicknameInput"
        placeholder="输入用户昵称（可选，用于备注）"
      />
      <template #footer>
        <ElButton @click="showSoldDialog = false">取消</ElButton>
        <ElButton type="primary" @click="handleMarkSold">确认</ElButton>
      </template>
    </ElDialog>
  </div>

  <div v-else style="text-align: center; padding: 40px">
    <p>物品不存在或已下架</p>
    <ElButton type="primary" @click="router.push('/')">返回首页</ElButton>
  </div>
</template>

<style scoped>
.item-detail {
  padding: 20px;
}

.item-header {
  margin-bottom: 20px;
}

.item-tags {
  display: flex;
  gap: 8px;
}
</style>