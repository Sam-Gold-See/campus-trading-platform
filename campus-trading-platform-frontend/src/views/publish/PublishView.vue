<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElForm, ElFormItem, ElInput, ElSelect, ElRadioGroup, ElRadioButton, ElButton, ElCard, ElUpload, ElImage, ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useConfigStore } from '@/stores/config'
import { useItemStore } from '@/stores/item'
import { uploadImage } from '@/api/upload'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'

const router = useRouter()
const configStore = useConfigStore()
const itemStore = useItemStore()

const formRef = ref<FormInstance>()
const form = ref({
  type: 2, // 默认转让
  categoryId: undefined as number | undefined,
  campus: '',
  priceRange: '',
  price: undefined as number | undefined,
  content: '',
  imageUrl: '',
})

const rules: FormRules = {
  type: [{ required: true, message: '请选择交易类型', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  campus: [{ required: true, message: '请选择校区', trigger: 'change' }],
  content: [
    { required: true, message: '请输入描述', trigger: 'blur' },
    { min: 1, max: 200, message: '描述长度需在1-200字之间', trigger: 'blur' },
  ],
}

const uploading = ref(false)

// 图片上传处理
const handleUpload: UploadProps['beforeUpload'] = async (file) => {
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  uploading.value = true
  try {
    const url = await uploadImage(file)
    form.value.imageUrl = url
    ElMessage.success('上传成功')
  } catch (e) {
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
  return false // 阻止默认上传行为
}

// 移除图片
const removeImage = () => {
  form.value.imageUrl = ''
}

// 价格区间映射
const priceRangeMap: Record<string, number | undefined> = {
  'below50': undefined,
  '50to200': undefined,
  'above200': undefined,
  'negotiable': undefined,
}

// 提交发布
async function handleSubmit() {
  await formRef.value?.validate()

  // 处理价格
  let price: number | undefined
  if (form.value.priceRange === 'below50') price = 50
  else if (form.value.priceRange === '50to200') price = 150
  else if (form.value.priceRange === 'above200') price = 200
  else price = undefined // 面议

  try {
    const itemId = await itemStore.publish({
      type: form.value.type,
      categoryId: form.value.categoryId!,
      campus: form.value.campus,
      price,
      content: form.value.content,
      imageUrl: form.value.imageUrl,
    })
    ElMessage.success('发布成功')
    router.push(`/item/${itemId}`)
  } catch (e) {
    // error handled by request interceptor
  }
}

onMounted(async () => {
  await configStore.loadConfig()
})
</script>

<template>
  <div class="publish-view">
    <ElCard shadow="never" style="max-width: 600px; margin: 0 auto">
      <template #header>
        <span>发布需求</span>
      </template>

      <ElForm ref="formRef" :model="form" :rules="rules" label-width="100px">
        <!-- 交易类型 -->
        <ElFormItem label="交易类型" prop="type">
          <ElRadioGroup v-model="form.type">
            <ElRadioButton :value="2">转让</ElRadioButton>
            <ElRadioButton :value="1">求购</ElRadioButton>
          </ElRadioGroup>
        </ElFormItem>

        <!-- 分类 -->
        <ElFormItem label="物品分类" prop="categoryId">
          <ElSelect v-model="form.categoryId" placeholder="请选择分类">
            <ElSelect.Option
              v-for="cat in configStore.categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </ElSelect>
        </ElFormItem>

        <!-- 校区 -->
        <ElFormItem label="交接校区" prop="campus">
          <ElSelect v-model="form.campus" placeholder="请选择校区">
            <ElSelect.Option
              v-for="campus in configStore.campuses"
              :key="campus"
              :label="campus"
              :value="campus"
            />
          </ElSelect>
        </ElFormItem>

        <!-- 价格区间 -->
        <ElFormItem label="期望价格">
          <ElSelect v-model="form.priceRange" placeholder="请选择价格区间">
            <ElSelect.Option
              v-for="range in configStore.priceRanges"
              :key="range"
              :label="range"
              :value="range"
            />
          </ElSelect>
        </ElFormItem>

        <!-- 描述 -->
        <ElFormItem label="描述内容" prop="content">
          <ElInput
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="简要描述物品状况、交易要求等..."
            maxlength="200"
            show-word-limit
          />
        </ElFormItem>

        <!-- 图片 -->
        <ElFormItem label="实物图片">
          <div v-if="form.imageUrl" class="image-preview">
            <ElImage :src="form.imageUrl" fit="cover" style="width: 150px; height: 150px" />
            <ElButton size="small" type="danger" @click="removeImage">删除</ElButton>
          </div>
          <ElUpload
            v-else
            action=""
            :before-upload="handleUpload"
            :show-file-list="false"
            accept="image/*"
          >
            <ElButton :loading="uploading">上传图片（可选，最多1张）</ElButton>
          </ElUpload>
        </ElFormItem>

        <ElFormItem>
          <ElButton type="primary" @click="handleSubmit">发布</ElButton>
          <ElButton @click="router.back()">取消</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>
  </div>
</template>

<style scoped>
.publish-view {
  padding: 20px;
}

.image-preview {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>