<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElCard, ElForm, ElFormItem, ElRadioGroup, ElRadioButton, ElButton, ElTag, ElInput, ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { submitReview } from '@/api/review'
import { getItemDetail } from '@/api/item'
import type { Item } from '@/types/item'

const route = useRoute()
const router = useRouter()

const itemId = computed(() => Number(route.query.itemId))
const revieweeId = computed(() => Number(route.query.revieweeId))

const item = ref<Item | null>(null)
const formRef = ref<FormInstance>()
const form = ref({
  ratingType: 1,
  tags: [] as string[],
  content: '',
})

const rules: FormRules = {
  ratingType: [{ required: true, message: '请选择评价类型', trigger: 'change' }],
  content: [{ max: 50, message: '评价内容不能超过50字', trigger: 'blur' }],
}

// 评价标签选项
const goodTags = ['爽快', '准时', '描述相符', '态度好', '信誉好']
const badTags = ['迟到', '描述不符', '态度差', '砍价', '失信']

// 切换标签选择
function toggleTag(tag: string) {
  const idx = form.value.tags.indexOf(tag)
  if (idx >= 0) {
    form.value.tags.splice(idx, 1)
  } else {
    if (form.value.tags.length < 5) {
      form.value.tags.push(tag)
    } else {
      ElMessage.warning('最多选择5个标签')
    }
  }
}

// 获取标签类型
function getTagType(tag: string): 'success' | 'danger' | 'info' {
  if (form.value.ratingType === 1) {
    return form.value.tags.includes(tag) ? 'success' : 'info'
  } else {
    return form.value.tags.includes(tag) ? 'danger' : 'info'
  }
}

// 提交评价
async function handleSubmit() {
  await formRef.value?.validate()

  try {
    await submitReview({
      itemId: itemId.value,
      revieweeId: revieweeId.value,
      ratingType: form.value.ratingType,
      tags: form.value.tags,
      content: form.value.content,
    })
    ElMessage.success('评价提交成功')
    router.push('/profile')
  } catch {
    // error handled
  }
}

// 加载物品信息
async function loadItem() {
  try {
    item.value = await getItemDetail(itemId.value)
  } catch {
    ElMessage.error('物品不存在')
    router.push('/')
  }
}

onMounted(loadItem)
</script>

<template>
  <div class="submit-review" style="max-width: 600px; margin: 0 auto; padding: 20px">
    <ElCard shadow="never">
      <template #header>
        <span>提交评价</span>
      </template>

      <!-- 物品信息 -->
      <div v-if="item" style="margin-bottom: 20px">
        <ElTag :type="item.type === 1 ? 'warning' : 'success'" size="small">
          {{ item.type === 1 ? '求购' : '转让' }}
        </ElTag>
        <span style="margin-left: 8px">{{ item.content }}</span>
        <span style="color: #f56c6c; font-weight: bold; margin-left: 8px">
          {{ item.price ? `¥${item.price}` : '面议' }}
        </span>
      </div>

      <ElForm ref="formRef" :model="form" :rules="rules" label-width="100px">
        <!-- 评价类型 -->
        <ElFormItem label="评价类型" prop="ratingType">
          <ElRadioGroup v-model="form.ratingType">
            <ElRadioButton :value="1">好评</ElRadioButton>
            <ElRadioButton :value="0">中评</ElRadioButton>
            <ElRadioButton :value="-1">差评</ElRadioButton>
          </ElRadioGroup>
        </ElFormItem>

        <!-- 评价标签 -->
        <ElFormItem label="评价标签">
          <div style="display: flex; flex-direction: column; gap: 8px">
            <div v-if="form.ratingType === 1">
              <ElTag
                v-for="tag in goodTags"
                :key="tag"
                :type="getTagType(tag)"
                style="margin-right: 8px; cursor: pointer"
                @click="toggleTag(tag)"
              >
                {{ tag }}
              </ElTag>
            </div>
            <div v-else-if="form.ratingType === -1">
              <ElTag
                v-for="tag in badTags"
                :key="tag"
                :type="getTagType(tag)"
                style="margin-right: 8px; cursor: pointer"
                @click="toggleTag(tag)"
              >
                {{ tag }}
              </ElTag>
            </div>
          </div>
        </ElFormItem>

        <!-- 文字评价 -->
        <ElFormItem label="补充说明">
          <ElInput
            v-model="form.content"
            type="textarea"
            :rows="3"
            placeholder="可选，简短描述交易感受..."
            maxlength="50"
            show-word-limit
          />
        </ElFormItem>

        <ElFormItem>
          <ElButton type="primary" @click="handleSubmit">提交评价</ElButton>
          <ElButton @click="router.back()">取消</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>
  </div>
</template>

<style scoped>
.submit-review {
  padding: 20px;
}
</style>