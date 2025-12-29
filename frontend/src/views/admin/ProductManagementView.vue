<template>
  <div class="product-management">
    <div class="page-header">
      <h1>商城管理</h1>
      <p>管理商品信息、库存和状态</p>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <div class="search-box">
        <Icon icon="mdi:magnify" class="search-icon" />
        <input v-model="searchKeyword" placeholder="搜索商品名称或品牌..." @keyup.enter="searchProducts" />
        <button class="search-btn" @click="searchProducts">搜索</button>
      </div>
      <button class="add-btn" @click="openAddDialog">
        <Icon icon="mdi:plus" /> 添加商品
      </button>
    </div>

    <!-- 商品列表 -->
    <div class="content-card">
      <div v-if="loading" class="loading-state">
        <Icon icon="mdi:loading" class="spin" />
        <span>加载中...</span>
      </div>

      <div v-else-if="products.length === 0" class="empty-state">
        <Icon icon="mdi:package-variant" class="empty-icon" />
        <h3>暂无商品</h3>
        <p>点击上方按钮添加第一个商品</p>
      </div>

      <table v-else class="product-table">
        <thead>
          <tr>
            <th>商品图片</th>
            <th>商品名称</th>
            <th>品牌</th>
            <th>价格</th>
            <th>库存</th>
            <th>销量</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id">
            <td>
              <div class="product-image">
                <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name" />
                <Icon v-else icon="mdi:image" class="no-image" />
              </div>
            </td>
            <td>
              <div class="product-name">{{ product.name }}</div>
              <div class="product-desc">{{ product.shortDescription }}</div>
            </td>
            <td>{{ product.brand || '-' }}</td>
            <td class="price">¥{{ product.price }}</td>
            <td :class="{ 'low-stock': product.stock < 10 }">{{ product.stock }}</td>
            <td>{{ product.sales || 0 }}</td>
            <td>
              <span class="status-tag" :class="product.status">
                {{ product.status === 'in_stock' ? '在售' : '下架' }}
              </span>
            </td>
            <td>
              <div class="actions">
                <button class="action-btn edit" @click="openEditDialog(product)" title="编辑">
                  <Icon icon="mdi:pencil" />
                </button>
                <button class="action-btn toggle" @click="toggleStatus(product)" :title="product.status === 'in_stock' ? '下架' : '上架'">
                  <Icon :icon="product.status === 'in_stock' ? 'mdi:eye-off' : 'mdi:eye'" />
                </button>
                <button class="action-btn delete" @click="confirmDelete(product)" title="删除">
                  <Icon icon="mdi:delete" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <div v-if="total > pageSize" class="pagination">
        <button :disabled="page === 1" @click="changePage(page - 1)">上一页</button>
        <span>第 {{ page }} / {{ totalPages }} 页，共 {{ total }} 条</span>
        <button :disabled="page >= totalPages" @click="changePage(page + 1)">下一页</button>
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <div v-if="showDialog" class="dialog-overlay" @click="closeDialog">
      <div class="dialog" @click.stop>
        <div class="dialog-header">
          <h3>{{ editingProduct ? '编辑商品' : '添加商品' }}</h3>
          <button class="close-btn" @click="closeDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>
        <form class="dialog-body" @submit.prevent="saveProduct">
          <!-- 图片上传区域 -->
          <div class="form-group image-upload-group">
            <label>商品图片</label>
            <div class="image-upload-area">
              <div v-if="form.imageUrl || imagePreview" class="image-preview">
                <img :src="imagePreview || form.imageUrl" alt="商品图片" />
                <button type="button" class="remove-image" @click="removeImage">
                  <Icon icon="mdi:close" />
                </button>
              </div>
              <div v-else class="upload-placeholder" @click="triggerFileInput">
                <Icon icon="mdi:cloud-upload" class="upload-icon" />
                <span>点击上传图片</span>
                <span class="upload-hint">支持 JPG、PNG 格式，最大5MB</span>
              </div>
              <input ref="fileInput" type="file" accept="image/*" @change="handleFileSelect" style="display: none" />
            </div>
            <div class="image-url-input">
              <span>或输入图片URL：</span>
              <input v-model="form.imageUrl" placeholder="https://example.com/image.jpg" @input="imagePreview = ''" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>商品名称 <span class="required">*</span></label>
              <input v-model="form.name" required placeholder="请输入商品名称" />
            </div>
            <div class="form-group">
              <label>品牌</label>
              <input v-model="form.brand" placeholder="请输入品牌" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>价格 <span class="required">*</span></label>
              <input v-model.number="form.price" type="number" step="0.01" required placeholder="0.00" />
            </div>
            <div class="form-group">
              <label>原价（划线价）</label>
              <input v-model.number="form.originalPrice" type="number" step="0.01" placeholder="0.00" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>库存 <span class="required">*</span></label>
              <input v-model.number="form.stock" type="number" required placeholder="0" />
            </div>
            <div class="form-group">
              <label>分类</label>
              <select v-model.number="form.categoryId">
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
              <label>商品标签</label>
              <input v-model="form.tag" placeholder="如：限时特惠、爆款" />
            </div>
            <div class="form-group">
              <label>排序权重</label>
              <input v-model.number="form.sortOrder" type="number" placeholder="数字越大越靠前" />
            </div>
          </div>
          <div class="form-group">
            <label>简短描述</label>
            <input v-model="form.shortDescription" placeholder="一句话描述商品特点" />
          </div>
          <div class="form-group">
            <label>详细描述</label>
            <textarea v-model="form.description" rows="3" placeholder="请输入详细描述"></textarea>
          </div>
          <div class="form-row checkboxes">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.isHot" /> 🔥 热门商品
            </label>
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.isNew" /> 🆕 新品
            </label>
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.isRecommended" /> ⭐ 推荐
            </label>
          </div>
          <div class="dialog-footer">
            <button type="button" class="cancel-btn" @click="closeDialog">取消</button>
            <button type="submit" class="submit-btn" :disabled="saving">
              {{ saving ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Icon } from '@iconify/vue'
import request from '@/utils/request'

const products = ref([])
const loading = ref(false)
const saving = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const showDialog = ref(false)
const editingProduct = ref(null)
const fileInput = ref(null)
const imagePreview = ref('')

const form = ref({
  name: '',
  brand: '',
  price: null,
  originalPrice: null,
  stock: 0,
  categoryId: null,
  imageUrl: '',
  shortDescription: '',
  description: '',
  tag: '',
  sortOrder: 0,
  isHot: false,
  isNew: false,
  isRecommended: false
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

onMounted(() => {
  loadProducts()
})

async function loadProducts() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    const res = await request({ url: '/api/admin/products', method: 'GET', params })
    if (res.code === 200) {
      products.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

function searchProducts() {
  page.value = 1
  loadProducts()
}

function changePage(newPage) {
  page.value = newPage
  loadProducts()
}

function openAddDialog() {
  editingProduct.value = null
  imagePreview.value = ''
  form.value = {
    name: '', brand: '', price: null, originalPrice: null, stock: 0,
    categoryId: null, imageUrl: '', shortDescription: '', description: '',
    tag: '', sortOrder: 0, isHot: false, isNew: false, isRecommended: false
  }
  showDialog.value = true
}

function openEditDialog(product) {
  editingProduct.value = product
  imagePreview.value = ''
  form.value = { ...product }
  showDialog.value = true
}

function closeDialog() {
  showDialog.value = false
  editingProduct.value = null
  imagePreview.value = ''
}

// 图片上传相关方法
function triggerFileInput() {
  fileInput.value?.click()
}

function handleFileSelect(event) {
  const file = event.target.files[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }

  if (file.size > 5 * 1024 * 1024) {
    alert('图片大小不能超过 5MB')
    return
  }

  const reader = new FileReader()
  reader.onload = (e) => {
    imagePreview.value = e.target.result
    form.value.imageUrl = e.target.result
  }
  reader.readAsDataURL(file)
}

function removeImage() {
  imagePreview.value = ''
  form.value.imageUrl = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

async function saveProduct() {
  saving.value = true
  try {
    let res
    if (editingProduct.value) {
      res = await request({ url: `/api/admin/products/${editingProduct.value.id}`, method: 'PUT', data: form.value })
    } else {
      res = await request({ url: '/api/admin/products', method: 'POST', data: form.value })
    }
    if (res.code === 200) {
      closeDialog()
      loadProducts()
      alert(editingProduct.value ? '更新成功' : '添加成功')
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

async function toggleStatus(product) {
  const newStatus = product.status === 'in_stock' ? 'out_of_stock' : 'in_stock'
  try {
    const res = await request({ url: `/api/admin/products/${product.id}/status`, method: 'PUT', data: { status: newStatus } })
    if (res.code === 200) {
      product.status = newStatus
    }
  } catch (error) {
    console.error('状态更新失败:', error)
  }
}

async function confirmDelete(product) {
  if (!confirm(`确定要删除商品"${product.name}"吗？此操作不可恢复。`)) return
  try {
    const res = await request({ url: `/api/admin/products/${product.id}`, method: 'DELETE' })
    if (res.code === 200) {
      loadProducts()
      alert('删除成功')
    }
  } catch (error) {
    console.error('删除失败:', error)
  }
}
</script>

<style scoped>
.product-management { padding: 20px; }
.page-header { margin-bottom: 20px; }
.page-header h1 { margin: 0 0 8px 0; color: #333; font-size: 26px; }
.page-header p { margin: 0; color: #666; }

.toolbar {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 20px; gap: 15px; flex-wrap: wrap;
}
.search-box {
  display: flex; align-items: center; background: white;
  border-radius: 8px; padding: 8px 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  flex: 1; max-width: 400px;
}
.search-box .search-icon { color: #999; margin-right: 8px; }
.search-box input {
  border: none; outline: none; flex: 1; font-size: 14px;
}
.search-btn {
  background: #e17055; color: white; border: none; padding: 6px 16px;
  border-radius: 6px; cursor: pointer; margin-left: 8px;
}
.add-btn {
  background: #e17055; color: white; border: none; padding: 10px 20px;
  border-radius: 8px; cursor: pointer; display: flex; align-items: center; gap: 6px;
  font-size: 14px; font-weight: 500;
}
.add-btn:hover { background: #d35400; }

.content-card {
  background: white; border-radius: 12px; padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}
.loading-state, .empty-state {
  display: flex; flex-direction: column; align-items: center;
  padding: 60px 20px; color: #999;
}
.spin { animation: spin 1s linear infinite; font-size: 32px; }
@keyframes spin { to { transform: rotate(360deg); } }
.empty-icon { font-size: 64px; opacity: 0.5; margin-bottom: 15px; }
.empty-state h3 { margin: 0 0 8px 0; color: #666; }
.empty-state p { margin: 0; }

.product-table { width: 100%; border-collapse: collapse; }
.product-table th, .product-table td {
  padding: 12px; text-align: left; border-bottom: 1px solid #eee;
}
.product-table th { background: #f8f9fa; font-weight: 600; color: #333; }
.product-image {
  width: 50px; height: 50px; border-radius: 6px; overflow: hidden;
  background: #f0f0f0; display: flex; align-items: center; justify-content: center;
}
.product-image img { width: 100%; height: 100%; object-fit: cover; }
.no-image { font-size: 24px; color: #ccc; }
.product-name { font-weight: 500; color: #333; }
.product-desc { font-size: 12px; color: #999; margin-top: 4px; }
.price { color: #e17055; font-weight: 600; }
.low-stock { color: #e74c3c; font-weight: 600; }
.status-tag {
  padding: 4px 10px; border-radius: 12px; font-size: 12px;
}
.status-tag.in_stock { background: #d4edda; color: #155724; }
.status-tag.out_of_stock { background: #f8d7da; color: #721c24; }

.actions { display: flex; gap: 8px; }
.action-btn {
  width: 32px; height: 32px; border: none; border-radius: 6px;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
}
.action-btn.edit { background: #e3f2fd; color: #1976d2; }
.action-btn.toggle { background: #fff3e0; color: #f57c00; }
.action-btn.delete { background: #ffebee; color: #c62828; }
.action-btn:hover { opacity: 0.8; }

.pagination {
  display: flex; justify-content: center; align-items: center;
  gap: 15px; margin-top: 20px; padding-top: 20px; border-top: 1px solid #eee;
}
.pagination button {
  padding: 8px 16px; border: 1px solid #ddd; background: white;
  border-radius: 6px; cursor: pointer;
}
.pagination button:disabled { opacity: 0.5; cursor: not-allowed; }
.pagination span { color: #666; font-size: 14px; }

.dialog-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5); display: flex; align-items: center;
  justify-content: center; z-index: 1000;
}
.dialog {
  background: white; border-radius: 12px; width: 90%; max-width: 600px;
  max-height: 90vh; overflow-y: auto;
}
.dialog-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px; border-bottom: 1px solid #eee;
}
.dialog-header h3 { margin: 0; }
.close-btn { background: none; border: none; cursor: pointer; font-size: 20px; color: #999; }
.dialog-body { padding: 20px; }
.form-row { display: flex; gap: 15px; margin-bottom: 15px; }
.form-group { flex: 1; }
.form-group label { display: block; margin-bottom: 6px; font-weight: 500; color: #333; }
.required { color: #e74c3c; }
.form-group input, .form-group textarea, .form-group select {
  width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;
  font-size: 14px; box-sizing: border-box;
}
.form-group textarea { resize: vertical; }
.checkboxes { gap: 20px; }
.checkbox-label { display: flex; align-items: center; gap: 6px; cursor: pointer; }
.dialog-footer {
  display: flex; justify-content: flex-end; gap: 10px;
  padding: 20px; border-top: 1px solid #eee;
}
.cancel-btn {
  padding: 10px 20px; border: 1px solid #ddd; background: white;
  border-radius: 6px; cursor: pointer;
}
.submit-btn {
  padding: 10px 20px; border: none; background: #e17055; color: white;
  border-radius: 6px; cursor: pointer;
}
.submit-btn:disabled { opacity: 0.6; cursor: not-allowed; }

/* 图片上传样式 */
.image-upload-group { margin-bottom: 20px; }
.image-upload-area {
  border: 2px dashed #ddd; border-radius: 8px; padding: 20px;
  text-align: center; margin-bottom: 10px; transition: border-color 0.3s;
}
.image-upload-area:hover { border-color: #e17055; }
.upload-placeholder {
  cursor: pointer; display: flex; flex-direction: column;
  align-items: center; gap: 8px; color: #999;
}
.upload-icon { font-size: 48px; color: #ccc; }
.upload-hint { font-size: 12px; color: #bbb; }
.image-preview {
  position: relative; display: inline-block;
}
.image-preview img {
  max-width: 200px; max-height: 200px; border-radius: 8px;
  object-fit: cover;
}
.remove-image {
  position: absolute; top: -8px; right: -8px;
  width: 24px; height: 24px; border-radius: 50%;
  background: #e74c3c; color: white; border: none;
  cursor: pointer; display: flex; align-items: center;
  justify-content: center; font-size: 14px;
}
.image-url-input {
  display: flex; align-items: center; gap: 10px; font-size: 13px; color: #666;
}
.image-url-input input { flex: 1; }
</style>
