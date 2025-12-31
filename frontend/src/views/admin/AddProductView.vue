<template>
  <div class="add-product">
    <div class="page-header">
      <div class="header-left">
        <button class="back-btn" @click="goBack">
          <Icon icon="mdi:arrow-left" />
          返回商品列表
        </button>
        <div class="title-section">
          <h1>{{ isEdit ? '编辑商品' : '添加商品' }}</h1>
          <p>{{ isEdit ? '修改商品信息' : '创建新的商品信息' }}</p>
        </div>
      </div>
    </div>

    <div class="form-container">
      <form @submit.prevent="saveProduct" class="product-form">
        <!-- 基本信息 -->
        <div class="form-card">
          <div class="card-header">
            <Icon icon="mdi:information" class="card-icon" />
            <h3>基本信息</h3>
          </div>
          <div class="card-body">
            <div class="form-group">
              <label>商品名称 <span class="required">*</span></label>
              <input v-model="form.name" required placeholder="请输入商品名称" />
            </div>

            <div class="form-row">
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
              <div class="form-group">
                <label>商品标签</label>
                <input v-model="form.tag" placeholder="如：限时特惠、爆款" />
              </div>
            </div>
          </div>
        </div>

        <!-- 价格库存 -->
        <div class="form-card">
          <div class="card-header">
            <Icon icon="mdi:currency-usd" class="card-icon" />
            <h3>价格库存</h3>
          </div>
          <div class="card-body">
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
            </div>

            <div class="form-group">
              <label>库存数量 <span class="required">*</span></label>
              <input v-model.number="form.stock" type="number" required placeholder="0" />
            </div>
          </div>
        </div>

        <!-- 商品图片 -->
        <div class="form-card">
          <div class="card-header">
            <Icon icon="mdi:image" class="card-icon" />
            <h3>商品图片</h3>
          </div>
          <div class="card-body">
            <div class="form-group">
              <label>图片URL</label>
              <input v-model="form.imageUrl" placeholder="请输入图片URL" />
              <div v-if="form.imageUrl" class="image-preview">
                <img :src="form.imageUrl" alt="商品预览" />
              </div>
            </div>
          </div>
        </div>

        <!-- 商品描述 -->
        <div class="form-card">
          <div class="card-header">
            <Icon icon="mdi:text" class="card-icon" />
            <h3>商品描述</h3>
          </div>
          <div class="card-body">
            <div class="form-group">
              <label>简短描述</label>
              <input v-model="form.shortDescription" placeholder="一句话描述商品特点" />
            </div>
            <div class="form-group">
              <label>详细描述</label>
              <textarea v-model="form.description" rows="6" placeholder="请输入详细描述"></textarea>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="goBack">取消</button>
          <button type="submit" class="submit-btn" :disabled="saving">
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
</script>

<style scoped>
.add-product {
  padding: 20px;
  background: #f8f9fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #f8f9fa;
  color: #333;
}

.title-section h1 {
  margin: 0 0 4px 0;
  color: #333;
  font-size: 24px;
}

.title-section p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.form-container {
  max-width: 800px;
}

.product-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.card-icon {
  font-size: 20px;
  color: #e17055;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.card-body {
  padding: 24px;
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
  font-weight: 500;
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
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
  transition: border-color 0.3s;
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
  min-height: 120px;
}

.price-input {
  position: relative;
  display: flex;
  align-items: center;
}

.currency {
  position: absolute;
  left: 12px;
  color: #666;
  font-weight: 500;
  z-index: 1;
}

.price-input input {
  padding-left: 32px;
}

.image-preview {
  margin-top: 16px;
  text-align: center;
}

.image-preview img {
  max-width: 300px;
  max-height: 200px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #ddd;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.cancel-btn {
  padding: 12px 32px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.cancel-btn:hover {
  background: #f8f9fa;
}

.submit-btn {
  padding: 12px 32px;
  border: none;
  background: #e17055;
  color: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.submit-btn:hover {
  background: #d35400;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
