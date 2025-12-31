<template>
  <div class="add-product">
    <div class="page-header">
      <button class="back-btn" @click="goBack">
        <Icon icon="mdi:arrow-left" />
        返回商品列表
      </button>
      <h1>{{ isEdit ? '编辑商品' : '添加商品' }}</h1>
    </div>

    <div class="form-container">
      <form @submit.prevent="saveProduct" class="product-form">

        <!-- 商品基本信息 -->
        <div class="form-section">
          <div class="form-row">
            <div class="form-group">
              <label>商品名称 <span class="required">*</span></label>
              <input v-model="form.name" required placeholder="请输入商品名称" />
            </div>
            <div class="form-group">
              <label>商品分类 <span class="required">*</span></label>
              <select v-model.number="form.categoryId" required>
                <option :value="null">请选择分类</option>
                <option :value="1">宠物食品</option>
                <option :value="2">宠物用品</option>
                <option :value="3">宠物玩具</option>
                <option :value="4">宠物服饰</option>
                <option :value="5">宠物医疗</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>当前价格 <span class="required">*</span></label>
              <div class="price-input">
                <span class="currency">¥</span>
                <input v-model.number="form.price" type="number" step="0.01" required placeholder="0.00" />
              </div>
            </div>
            <div class="form-group">
              <label>原价</label>
              <div class="price-input">
                <span class="currency">¥</span>
                <input v-model.number="form.originalPrice" type="number" step="0.01" placeholder="0.00" />
              </div>
            </div>
            <div class="form-group">
              <label>库存数量 <span class="required">*</span></label>
              <input v-model.number="form.stock" type="number" required placeholder="0" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>商品标签</label>
              <input v-model="form.tag" placeholder="如：限时特惠、爆款推荐" />
            </div>
            <div class="form-group">
              <label>图片URL</label>
              <input v-model="form.imageUrl" placeholder="请输入商品图片链接" />
            </div>
          </div>

          <div class="form-group">
            <label>简短描述</label>
            <input v-model="form.shortDescription" placeholder="一句话描述商品特点" />
          </div>

          <div class="form-group">
            <label>详细描述</label>
            <textarea v-model="form.description" rows="4" placeholder="请输入商品的详细描述信息"></textarea>
          </div>

          <!-- 图片预览 -->
          <div v-if="form.imageUrl" class="image-preview">
            <label>图片预览</label>
            <div class="preview-container">
              <img :src="form.imageUrl" alt="商品预览" @error="handleImageError" />
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="goBack">取消</button>
          <button type="submit" class="submit-btn" :disabled="saving">
            <Icon v-if="saving" icon="mdi:loading" class="spin" />
            {{ saving ? '保存中...' : (isEdit ? '更新商品' : '创建商品') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const saving = ref(false)
const isEdit = ref(false)
const productId = ref(null)

const form = ref({
  name: '',
  categoryId: null,
  price: null,
  originalPrice: null,
  stock: 0,
  imageUrl: '',
  shortDescription: '',
  description: '',
  tag: ''
})

onMounted(() => {
  // 检查是否是编辑模式
  if (route.params.id) {
    isEdit.value = true
    productId.value = route.params.id
    loadProduct()
  }
})

async function loadProduct() {
  try {
    const res = await request({
      url: `/api/admin/products/${productId.value}`,
      method: 'GET'
    })
    if (res.code === 200) {
      const product = res.data
      form.value = {
        name: product.name || '',
        categoryId: product.categoryId || null,
        price: product.price || null,
        originalPrice: product.originalPrice || null,
        stock: product.stock || 0,
        imageUrl: product.imageUrl || '',
        shortDescription: product.shortDescription || '',
        description: product.description || '',
        tag: product.tag || ''
      }
    }
  } catch (error) {
    console.error('加载商品失败:', error)
    alert('加载商品失败')
  }
}

async function saveProduct() {
  saving.value = true
  try {
    let res
    if (isEdit.value) {
      res = await request({
        url: `/api/admin/products/${productId.value}`,
        method: 'PUT',
        data: form.value
      })
    } else {
      res = await request({
        url: '/api/admin/products',
        method: 'POST',
        data: form.value
      })
    }

    if (res.code === 200) {
      alert(isEdit.value ? '更新成功' : '添加成功')
      goBack()
    } else {
      alert(res.message || '操作失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.push('/admin/products')
}

function handleImageError(event) {
  event.target.style.display = 'none'
}
</script>

<style scoped>
.add-product {
  padding: 20px;
  min-height: 100vh;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #495057;
  font-weight: 500;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
  color: #212529;
}

.page-header h1 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.form-container {
  max-width: 900px;
}

.product-form {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.form-section {
  margin-bottom: 32px;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  flex: 1;
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.required {
  color: #e74c3c;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
  transition: all 0.3s ease;
  background: #fff;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #e17055;
  box-shadow: 0 0 0 3px rgba(225, 112, 85, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
}

.price-input {
  position: relative;
  display: flex;
  align-items: center;
}

.currency {
  position: absolute;
  left: 16px;
  color: #666;
  font-weight: 600;
  z-index: 1;
}

.price-input input {
  padding-left: 40px;
}

.image-preview {
  margin-top: 20px;
}

.preview-container {
  margin-top: 12px;
  text-align: center;
  padding: 20px;
  border: 2px dashed #e9ecef;
  border-radius: 8px;
  background: #f8f9fa;
}

.preview-container img {
  max-width: 300px;
  max-height: 200px;
  border-radius: 8px;
  object-fit: cover;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e9ecef;
}

.cancel-btn {
  padding: 12px 32px;
  border: 1px solid #dee2e6;
  background: #f8f9fa;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #495057;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.submit-btn {
  padding: 12px 32px;
  border: none;
  background: #007bff;
  color: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.submit-btn:hover {
  background: #0056b3;
}

.submit-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .form-container {
    max-width: 100%;
  }

  .product-form {
    padding: 20px;
  }
}
</style>
