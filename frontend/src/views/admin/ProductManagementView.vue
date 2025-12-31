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
        <input v-model="searchKeyword" placeholder="搜索商品名称..." @keyup.enter="searchProducts" />
        <button class="search-btn" @click="searchProducts">搜索</button>
      </div>
      <button class="add-btn" @click="goToAddProduct">
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
            <th>分类</th>
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
                <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name" @error="handleImageError" />
                <div v-else class="no-image">
                  <Icon icon="mdi:image" />
                  <span>暂无图片</span>
                </div>
              </div>
            </td>
            <td>
              <div class="product-name">{{ product.name }}</div>
              <div class="product-desc" v-if="product.shortDescription">{{ product.shortDescription }}</div>
            </td>
            <td>
              <span class="category-tag">{{ getCategoryName(product.categoryId) }}</span>
            </td>
            <td class="price-cell">
              <div class="current-price">¥{{ formatPrice(product.price) }}</div>
              <div v-if="product.originalPrice && product.originalPrice > product.price" class="original-price">
                ¥{{ formatPrice(product.originalPrice) }}
              </div>
            </td>
            <td :class="{ 'low-stock': product.stock < 10 }">
              <div class="stock-info">
                <span class="stock-number">{{ product.stock }}</span>
                <Icon v-if="product.stock < 10" icon="mdi:alert" class="warning-icon" title="库存不足" />
              </div>
            </td>
            <td>{{ product.sales || 0 }}</td>
            <td>
              <span class="status-tag" :class="product.status">
                {{ product.status === 'in_stock' ? '在售' : '下架' }}
              </span>
            </td>
            <td>
              <div class="actions">
                <button class="action-btn edit" @click="goToEditProduct(product)" title="编辑">
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'
import request from '@/utils/request'

const router = useRouter()

const products = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(6)
const total = ref(0)
const searchKeyword = ref('')

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 分类名称映射
const getCategoryName = (categoryId) => {
  const categories = {
    1: '宠物食品',
    2: '宠物用品',
    3: '宠物玩具',
    4: '宠物服饰',
    5: '宠物医疗'
  }
  return categories[categoryId] || '-'
}

// 价格格式化
const formatPrice = (price) => {
  return price ? parseFloat(price).toFixed(2) : '0.00'
}

// 图片加载错误处理
const handleImageError = (event) => {
  event.target.style.display = 'none'
  event.target.nextElementSibling.style.display = 'flex'
}

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

function goToAddProduct() {
  router.push('/admin/products/add')
}

function goToEditProduct(product) {
  router.push(`/admin/products/edit/${product.id}`)
}

async function toggleStatus(product) {
  const newStatus = product.status === 'in_stock' ? 'out_of_stock' : 'in_stock'
  try {
    const res = await request({
      url: `/api/admin/products/${product.id}/status`,
      method: 'PUT',
      data: { status: newStatus }
    })
    if (res.code === 200) {
      product.status = newStatus
      alert('状态更新成功')
    } else {
      alert(res.message || '状态更新失败')
    }
  } catch (error) {
    console.error('状态更新失败:', error)
    alert('状态更新失败')
  }
}

async function confirmDelete(product) {
  if (!confirm(`确定要删除商品"${product.name}"吗？此操作不可恢复。`)) return
  try {
    const res = await request({
      url: `/api/admin/products/${product.id}`,
      method: 'DELETE'
    })
    if (res.code === 200) {
      loadProducts()
      alert('删除成功')
    } else {
      alert(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败')
  }
}
</script>

<style scoped>
.product-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 26px;
}

.page-header p {
  margin: 0;
  color: #666;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 15px;
  flex-wrap: wrap;
}

.search-box {
  display: flex;
  align-items: center;
  background: white;
  border-radius: 8px;
  padding: 8px 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  flex: 1;
  max-width: 400px;
}

.search-box .search-icon {
  color: #999;
  margin-right: 8px;
}

.search-box input {
  border: none;
  outline: none;
  flex: 1;
  font-size: 14px;
}

.search-btn {
  background: #e17055;
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 6px;
  cursor: pointer;
  margin-left: 8px;
}

.add-btn {
  background: #e17055;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
}

.add-btn:hover {
  background: #d35400;
}

.content-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}

.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  color: #999;
}

.spin {
  animation: spin 1s linear infinite;
  font-size: 32px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-icon {
  font-size: 64px;
  opacity: 0.5;
  margin-bottom: 15px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  color: #666;
}

.empty-state p {
  margin: 0;
}

.product-table {
  width: 100%;
  border-collapse: collapse;
}

.product-table th, .product-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.product-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #eee;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #ccc;
  font-size: 12px;
}

.no-image .iconify {
  font-size: 24px;
  margin-bottom: 4px;
}

.product-name {
  font-weight: 500;
  color: #333;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-tag {
  background: #e3f2fd;
  color: #1976d2;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.price-cell {
  min-width: 100px;
}

.current-price {
  font-size: 16px;
  color: #e17055;
  font-weight: 600;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
  margin-top: 2px;
}

.stock-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stock-number {
  font-weight: 500;
}

.warning-icon {
  color: #ff9800;
  font-size: 16px;
}

.low-stock {
  color: #e74c3c;
}

.low-stock .stock-number {
  font-weight: 600;
}

.status-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.status-tag.in_stock {
  background: #d4edda;
  color: #155724;
}

.status-tag.out_of_stock {
  background: #f8d7da;
  color: #721c24;
}

.actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  font-size: 16px;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.action-btn.edit {
  background: linear-gradient(135deg, #e3f2fd, #bbdefb);
  color: #1976d2;
}

.action-btn.edit:hover {
  background: linear-gradient(135deg, #bbdefb, #90caf9);
}

.action-btn.toggle {
  background: linear-gradient(135deg, #fff3e0, #ffe0b2);
  color: #f57c00;
}

.action-btn.toggle:hover {
  background: linear-gradient(135deg, #ffe0b2, #ffcc80);
}

.action-btn.delete {
  background: linear-gradient(135deg, #ffebee, #ffcdd2);
  color: #c62828;
}

.action-btn.delete:hover {
  background: linear-gradient(135deg, #ffcdd2, #ef9a9a);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination span {
  color: #666;
  font-size: 14px;
}
</style>
