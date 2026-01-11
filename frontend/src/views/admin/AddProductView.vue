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

          <!-- 图片上传 -->
          <div class="form-group full-width">
            <label>商品图片 <span class="required">*</span></label>
            <div class="upload-container">
              <!-- 左侧：上传区域 -->
              <div class="upload-area">
                <div class="upload-methods">
                  <!-- 本地上传 -->
                  <label
                    class="upload-box local-upload"
                    :class="{ 'drag-over': isDragging }"
                    @dragover.prevent="handleDragOver"
                    @dragleave.prevent="handleDragLeave"
                    @drop.prevent="handleDrop"
                  >
                    <input type="file" accept="image/*" @change="handleFileUpload" hidden />
                    <div class="upload-icon">
                      <Icon icon="mdi:cloud-upload" />
                    </div>
                    <div class="upload-text">
                      <h4>本地上传</h4>
                      <p>点击选择或拖拽图片文件</p>
                    </div>
                    <div class="upload-hint">支持 JPG、PNG、GIF，最大 5MB</div>
                  </label>

                  <!-- URL输入 -->
                  <div class="upload-box url-upload">
                    <div class="upload-icon">
                      <Icon icon="mdi:link-variant" />
                    </div>
                    <div class="upload-text">
                      <h4>图片链接</h4>
                      <input
                        v-model="form.imageUrl"
                        placeholder="输入图片URL地址"
                        class="url-input"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <!-- 右侧：预览区域 -->
              <div class="preview-area">
                <div class="preview-label">图片预览</div>
                <div v-if="form.imageUrl" class="preview-box has-image">
                  <img :src="form.imageUrl" alt="商品预览" @error="handleImageError" />
                  <button type="button" class="remove-image" @click="removeImage" title="删除图片">
                    <Icon icon="mdi:close-circle" />
                  </button>
                </div>
                <div v-else class="preview-box empty">
                  <Icon icon="mdi:image-outline" />
                  <p>暂无图片</p>
                </div>
              </div>
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

          <!-- 商品标签和特性 -->
          <div class="form-row">
            <div class="form-group">
              <label>商品标签</label>
              <input v-model="form.tag" placeholder="例如：热销、新品、推荐" />
            </div>
          </div>

          <div class="form-group full-width">
            <label>商品特性</label>
            <div class="checkbox-group">
              <label class="checkbox-item">
                <input type="checkbox" v-model="form.isRecommended" />
                <span class="checkbox-label">
                  <Icon icon="mdi:star" class="icon-recommended" />
                  推荐商品
                </span>
              </label>
              <label class="checkbox-item">
                <input type="checkbox" v-model="form.isHot" />
                <span class="checkbox-label">
                  <Icon icon="mdi:fire" class="icon-hot" />
                  热销商品
                </span>
              </label>
              <label class="checkbox-item">
                <input type="checkbox" v-model="form.isNew" />
                <span class="checkbox-label">
                  <Icon icon="mdi:new-box" class="icon-new" />
                  新品上市
                </span>
              </label>
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
const uploadingImage = ref(false)
const isDragging = ref(false)

const form = ref({
  name: '',
  categoryId: null,
  price: null,
  originalPrice: null,
  stock: 0,
  sales: 0,
  imageUrl: '',
  shortDescription: '',
  description: '',
  tag: '',
  isRecommended: false,
  isHot: false,
  isNew: false
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
        sales: product.sales || 0,
        imageUrl: product.imageUrl || '',
        shortDescription: product.shortDescription || '',
        description: product.description || '',
        tag: product.tag || '',
        isRecommended: product.isRecommended || false,
        isHot: product.isHot || false,
        isNew: product.isNew || false
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

// 删除图片
function removeImage() {
  form.value.imageUrl = ''
}

// 拖拽相关事件处理
function handleDragOver() {
  isDragging.value = true
}

function handleDragLeave() {
  isDragging.value = false
}

function handleDrop(event) {
  isDragging.value = false
  const file = event.dataTransfer.files[0]
  if (file) {
    processImageFile(file)
  }
}

// 处理文件上传
async function handleFileUpload(event) {
  const file = event.target.files[0]
  if (!file) return
  processImageFile(file)
}

// 统一处理图片文件
async function processImageFile(file) {
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }

  // 验证文件大小（5MB）
  if (file.size > 5 * 1024 * 1024) {
    alert('图片大小不能超过 5MB')
    return
  }

  uploadingImage.value = true

  try {
    // 方式1: 转换为 Base64（适合小图片）
    const reader = new FileReader()
    reader.onload = (e) => {
      form.value.imageUrl = e.target.result
      uploadingImage.value = false
      alert('图片上传成功')
    }
    reader.onerror = () => {
      uploadingImage.value = false
      alert('图片读取失败')
    }
    reader.readAsDataURL(file)

    // 方式2: 如果有图片上传服务器的接口，可以这样实现：
    /*
    const formData = new FormData()
    formData.append('file', file)

    const res = await request({
      url: '/api/upload/image',
      method: 'POST',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (res.code === 200) {
      form.value.imageUrl = res.data.url
      alert('图片上传成功')
    } else {
      alert('图片上传失败：' + res.message)
    }
    uploadingImage.value = false
    */
  } catch (error) {
    console.error('上传失败:', error)
    alert('图片上传失败')
    uploadingImage.value = false
  }
}
</script>

<style scoped>
.add-product {
  padding: 16px;
  min-height: 100vh;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
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
  font-size: 20px;
}

.form-container {
  max-width: 1400px;
  margin: 0 auto;
}

.product-form {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.form-section {
  margin-bottom: 16px;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.form-group {
  flex: 1;
  margin-bottom: 12px;
}

.form-group.full-width {
  flex: 1 1 100%;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #333;
  font-size: 13px;
}

.required {
  color: #e74c3c;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 8px 12px;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  font-size: 13px;
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
  min-height: 60px;
  font-family: inherit;
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
  font-weight: 600;
  z-index: 1;
}

.price-input input {
  padding-left: 32px;
}

/* 新的上传容器样式 */
.upload-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 8px;
}

.upload-area {
  display: flex;
  flex-direction: column;
}

.upload-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.upload-box {
  background: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;
}

.upload-box:hover {
  border-color: #007bff;
  background: #f0f7ff;
}

.upload-box.drag-over {
  border-color: #28a745;
  background: #d4edda;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
}

.local-upload {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  min-height: 120px;
  justify-content: center;
}

.local-upload:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.2);
}

.url-upload {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-icon {
  font-size: 2rem;
  color: #007bff;
  margin-bottom: 8px;
}

.upload-icon .iconify {
  width: 2rem;
  height: 2rem;
}

.upload-text h4 {
  margin: 0 0 4px 0;
  color: #333;
  font-size: 0.95rem;
  font-weight: 600;
}

.upload-text p {
  margin: 0;
  color: #6c757d;
  font-size: 0.8rem;
}

.upload-hint {
  margin-top: 8px;
  font-size: 0.75rem;
  color: #6c757d;
}

.url-input {
  width: 100%;
  padding: 8px 12px;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  font-size: 13px;
  transition: all 0.3s ease;
  margin-top: 4px;
}

.url-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

/* 预览区域 */
.preview-area {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  height: 100%;
}

.preview-label {
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  font-size: 0.9rem;
  height: 0;
  opacity: 0;
  pointer-events: none;
}

.preview-box {
  width: 100%;
  flex: 1;
  min-height: 280px;
  max-height: 300px;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  position: relative;
  overflow: hidden;
  margin-top: 0;
}

.preview-box.empty {
  flex-direction: column;
  gap: 8px;
  color: #adb5bd;
}

.preview-box.empty .iconify {
  font-size: 3rem;
  width: 3rem;
  height: 3rem;
}

.preview-box.empty p {
  margin: 0;
  font-size: 0.85rem;
}

.preview-box.has-image {
  background: #ffffff;
  border-color: #007bff;
  padding: 8px;
}

.preview-box img {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  object-fit: contain;
  border-radius: 6px;
}

.remove-image {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(220, 53, 69, 0.9);
  color: white;
  border: none;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1.2rem;
}

.remove-image:hover {
  background: #dc3545;
  transform: scale(1.1);
}

.remove-image .iconify {
  width: 1.2rem;
  height: 1.2rem;
}

/* 复选框组样式 - 简洁版 */
.checkbox-group {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.checkbox-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 0;
  background: transparent;
  border: none;
  transition: all 0.2s ease;
}

.checkbox-item input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  margin: 0;
  accent-color: #007bff;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 400;
  color: #495057;
  cursor: pointer;
  user-select: none;
}

.icon-recommended {
  color: #ffc107;
  font-size: 1rem;
}

.icon-hot {
  color: #ff5722;
  font-size: 1rem;
}

.icon-new {
  color: #4caf50;
  font-size: 1rem;
}

.checkbox-item:hover .checkbox-label {
  color: #212529;
}

.checkbox-item input[type="checkbox"]:checked ~ .checkbox-label {
  color: #212529;
  font-weight: 500;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e9ecef;
}

.cancel-btn {
  padding: 8px 24px;
  border: 1px solid #dee2e6;
  background: #f8f9fa;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  color: #495057;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.submit-btn {
  padding: 8px 24px;
  border: none;
  background: #007bff;
  color: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
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
@media (max-width: 1024px) {
  .upload-container {
    grid-template-columns: 1fr;
  }
}

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
