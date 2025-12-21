<template>
  <div class="product-detail-page">
    <nav class="breadcrumb">
      <div class="container">
        <ul class="breadcrumb-list">
          <li><a href="/">首页</a></li>
          <li>{{ product.name }}</li>
        </ul>
      </div>
    </nav>

    <div class="container">
      <div class="product-detail-container">
        <div class="product-images">
          <div class="main-image-container">
            <img :src="currentImage" :alt="product.name" class="main-image">
          </div>
        </div>

        <div class="product-info">
          <h1 class="product-title">{{ product.name }}</h1>
          <p class="product-subtitle">{{ product.subtitle }}</p>

          <div class="product-meta">
            <div class="rating">
              <div class="stars">
                <i v-for="n in 5" :key="n" :class="getStarClass(n, product.rating)"></i>
              </div>
              <span class="rating-score">{{ product.rating }}</span>
              <span class="rating-count">({{ product.reviewCount }}条评价)</span>
            </div>
            <div class="sales">月销 {{ product.monthlySales }}+ 件</div>
          </div>

          <div class="price-section">
            <div class="current-price">
              <span class="currency">¥</span>{{ selectedSpec.price }}
              <span class="original-price">¥{{ selectedSpec.originalPrice }}</span>
            </div>
          </div>

          <div class="spec-section" v-if="product.specifications && product.specifications.length">
            <div class="spec-title">规格选择：</div>
            <div class="spec-options">
              <div v-for="spec in product.specifications" :key="spec.id"
                class="spec-option" :class="{ active: selectedSpecId === spec.id }"
                @click="selectSpec(spec.id)">
                {{ spec.name }}
                <span v-if="spec.tag" :style="{ color: spec.tagColor, fontSize: '12px' }">
                  ({{ spec.tag }})
                </span>
              </div>
            </div>
          </div>

          <div class="quantity-section">
            <div class="spec-title">购买数量：</div>
            <div class="quantity-selector">
              <button class="quantity-btn minus" @click="decreaseQuantity">-</button>
              <input type="number" class="quantity-input" v-model.number="quantity" min="1" :max="product.stock">
              <button class="quantity-btn plus" @click="increaseQuantity">+</button>
            </div>
            <div class="stock-info">
              <i class="fas fa-box"></i>
              <span :class="{ 'in-stock': product.stock > 0 }">
                {{ product.stock > 0 ? `库存充足 (还剩 ${product.stock} 件)` : '暂时缺货' }}
              </span>
              <span v-if="product.maxPurchase"> | 限购 {{ product.maxPurchase }} 件</span>
            </div>
          </div>

          <div class="action-buttons">
            <button class="btn btn-primary" @click="addToCart" :disabled="product.stock === 0">
              <i class="fas fa-cart-plus"></i> 加入购物车
            </button>
            <button class="btn btn-secondary" @click="buyNow" :disabled="product.stock === 0">
              <i class="fas fa-bolt"></i> 立即购买
            </button>
          </div>

          <div class="service-guarantee">
            <div v-for="service in services" :key="service.icon" class="service-item">
              <div class="service-icon">
                <i :class="service.icon"></i>
              </div>
              <div class="service-text">{{ service.text }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="product-tabs">
        <div class="tab-headers">
          <div v-for="tab in tabs" :key="tab.id"
            class="tab-header" :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id">
            {{ tab.label }}
          </div>
        </div>

        <div class="tab-content">
          <div v-show="activeTab === 'description'" class="tab-pane">
            <div class="product-description" v-html="product.description"></div>
          </div>

          <div v-show="activeTab === 'specifications'" class="tab-pane">
            <table class="spec-table">
              <tr v-for="(value, key) in product.params" :key="key">
                <td>{{ key }}</td>
                <td>{{ value }}</td>
              </tr>
            </table>
          </div>

          <div v-show="activeTab === 'reviews'" class="tab-pane">
            <div class="review-summary">
              <div class="overall-rating">
                <div class="rating-score-large">{{ product.rating }}</div>
                <div class="rating-stars-large">
                  <i v-for="n in 5" :key="n" :class="getStarClass(n, product.rating)"></i>
                </div>
                <div>{{ product.reviewCount }}条评价</div>
              </div>

              <div class="rating-distribution">
                <div v-for="dist in product.ratingDistribution" :key="dist.star" class="rating-bar">
                  <div class="rating-label">{{ dist.star }}星 ({{ dist.percentage }}%)</div>
                  <div class="rating-progress">
                    <div class="rating-progress-fill" :style="{ width: dist.percentage + '%' }"></div>
                  </div>
                  <div class="rating-percentage">{{ dist.percentage }}%</div>
                </div>
              </div>
            </div>

            <div class="review-list">
              <div v-for="review in product.reviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <div class="reviewer">{{ review.userName }}</div>
                  <div class="review-date">{{ review.date }}</div>
                </div>
                <div class="review-rating">
                  <i v-for="n in review.rating" :key="n" class="fas fa-star"></i>
                </div>
                <div class="review-content">{{ review.content }}</div>
              </div>
            </div>
          </div>

          <div v-show="activeTab === 'qa'" class="tab-pane">
            <h3>常见问题</h3>
            <ul class="feature-list">
              <li v-for="qa in product.faqs" :key="qa.question">
                <i class="fas fa-question-circle" style="color: var(--primary-color);"></i>
                <div>
                  <strong>问：{{ qa.question }}</strong><br>
                  答：{{ qa.answer }}
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div class="recommended-products" v-if="recommendedProducts.length">
        <h2 class="section-title">推荐商品</h2>
        <div class="product-grid">
          <div v-for="item in recommendedProducts" :key="item.id"
            class="product-card-small" @click="goToProduct(item.id)">
            <img :src="item.image" :alt="item.name">
            <div class="info">
              <div class="title">{{ item.name }}</div>
              <div class="price">¥{{ item.price }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal" v-if="showImageModal" @click="showImageModal = false">
      <span class="modal-close">&times;</span>
      <div class="modal-content" @click.stop>
        <img :src="currentImage" alt="" class="modal-img">
      </div>
    </div>

    <transition name="notification">
      <div v-if="notification.show" class="notification">
        <i class="fas fa-check-circle"></i> {{ notification.message }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail, getRecommendedProducts } from '@/api/product'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const product = ref({
  id: null,
  name: '',
  subtitle: '',
  rating: 0,
  reviewCount: 0,
  monthlySales: 0,
  images: [],
  specifications: [],
  stock: 0,
  maxPurchase: 10,
  promotions: [],
  description: '',
  params: {},
  ratingDistribution: [],
  reviews: [],
  faqs: []
})

const recommendedProducts = ref([])
const currentImageIndex = ref(0)
const selectedSpecId = ref(null)
const quantity = ref(1)
const activeTab = ref('description')
const showImageModal = ref(false)
const notification = ref({ show: false, message: '' })

// 获取购物车组件引用（通过事件总线或全局状态）
const cartRef = ref(null)

const services = [
  { icon: 'fas fa-shield-alt', text: '正品保证' },
  { icon: 'fas fa-shipping-fast', text: '快速发货' },
  { icon: 'fas fa-undo-alt', text: '七天退换' },
  { icon: 'fas fa-headset', text: '专业客服' }
]

const tabs = [
  { id: 'description', label: '商品详情' },
  { id: 'specifications', label: '规格参数' },
  { id: 'reviews', label: computed(() => `商品评价 (${product.value.reviewCount})`) },
  { id: 'qa', label: '常见问题' }
]


const currentImage = computed(() => {
  return product.value.images[currentImageIndex.value] || ''
})

const selectedSpec = computed(() => {
  return product.value.specifications?.find(s => s.id === selectedSpecId.value) ||
         product.value.specifications?.[0] ||
         { price: 0, originalPrice: 0 }
})



const getStarClass = (index, rating) => {
  if (index <= Math.floor(rating)) {
    return 'fas fa-star'
  } else if (index === Math.ceil(rating) && rating % 1 !== 0) {
    return 'fas fa-star-half-alt'
  }
  return 'far fa-star'
}

const selectSpec = (specId) => {
  selectedSpecId.value = specId
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const increaseQuantity = () => {
  const max = product.value.maxPurchase || 99
  if (quantity.value < max && quantity.value < product.value.stock) {
    quantity.value++
  }
}

const addToCart = async () => {
  if (product.value.stock === 0) return

  try {
    await cartStore.addItem({
      productId: product.value.id,
      spec: selectedSpec.value.name || '标准装',
      quantity: quantity.value
    })

    showNotification(`成功添加 ${quantity.value} 件商品到购物车`)
  } catch (err) {
    if (err.message === '请先登录') {
      showNotification('请先登录')
      setTimeout(() => {
        router.push('/auth')
      }, 1000)
    } else {
      showNotification('添加失败，请重试')
    }
  }
}

const buyNow = () => {
  if (product.value.stock === 0) return

  router.push({
    path: '/checkout',
    query: {
      productId: product.value.id,
      specId: selectedSpecId.value,
      quantity: quantity.value
    }
  })
}

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

const showNotification = (message) => {
  notification.value.show = true
  notification.value.message = message

  setTimeout(() => {
    notification.value.show = false
  }, 3000)
}

const loadProductDetail = async () => {
  try {
    const productId = route.params.id
    const response = await getProductDetail(productId)

    if (response.code === 200) {
      product.value = response.data

      if (product.value.specifications && product.value.specifications.length > 0) {
        selectedSpecId.value = product.value.specifications[0].id
      }
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
    showNotification('加载商品信息失败')
  }
}

const loadRecommendedProducts = async () => {
  try {
    const response = await getRecommendedProducts(route.params.id)

    if (response.code === 200) {
      recommendedProducts.value = response.data
    }
  } catch (error) {
    console.error('加载推荐商品失败:', error)
  }
}

watch(() => route.params.id, (newId) => {
  if (newId) {
    // 滚动到页面顶部
    window.scrollTo({ top: 0, behavior: 'smooth' })

    loadProductDetail()
    loadRecommendedProducts()
    currentImageIndex.value = 0
    quantity.value = 1
    activeTab.value = 'description'
  }
})

onMounted(() => {
  // 页面加载时滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })

  loadProductDetail()
  loadRecommendedProducts()
})
</script>


<style scoped>
.product-detail-page {
  background-color: #f8f9fa;
  min-height: 100vh;
  padding-bottom: 40px;
}

.breadcrumb {
  background-color: white;
  padding: 15px 0;
  margin-bottom: 20px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

.breadcrumb-list {
  display: flex;
  list-style: none;
  font-size: 14px;
  color: #8d6e63;
  padding: 0;
  margin: 0;
}

.breadcrumb-list li {
  margin-right: 10px;
}

.breadcrumb-list li:not(:last-child)::after {
  content: ">";
  margin-left: 10px;
}

.breadcrumb-list a {
  color: #8d6e63;
  text-decoration: none;
}

.breadcrumb-list a:hover {
  color: #ffb347;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

.product-detail-container {
  display: flex;
  gap: 30px;
  margin-bottom: 40px;
}

.product-images {
  flex: 1;
  background-color: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.08);
}

.main-image-container {
  width: 100%;
  height: 500px;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  background-color: #f9f9f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.product-info {
  flex: 1;
  background-color: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.08);
}

.product-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #5a4a42;
}

.product-subtitle {
  color: #8d6e63;
  font-size: 16px;
  margin-bottom: 20px;
}

.product-meta {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.rating {
  display: flex;
  align-items: center;
  margin-right: 30px;
}

.stars {
  color: #ffb400;
  font-size: 18px;
  margin-right: 8px;
}

.rating-score {
  font-weight: bold;
  margin-right: 5px;
}

.rating-count {
  color: #8d6e63;
  font-size: 14px;
}

.sales {
  color: #8d6e63;
  font-size: 14px;
}

.price-section {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 25px;
}

.current-price {
  font-size: 36px;
  font-weight: bold;
  color: #e69500;
  display: flex;
  align-items: baseline;
}

.currency {
  font-size: 22px;
  margin-right: 3px;
}

.original-price {
  color: #8d6e63;
  text-decoration: line-through;
  font-size: 20px;
  margin-left: 15px;
  font-weight: normal;
}





.spec-section {
  margin-bottom: 25px;
}

.spec-title {
  font-weight: bold;
  margin-bottom: 12px;
  color: #5a4a42;
}

.spec-options {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.spec-option {
  padding: 10px 20px;
  border: 2px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
}

.spec-option:hover {
  border-color: #ffcc80;
}

.spec-option.active {
  border-color: #ffb347;
  background-color: rgba(255, 179, 71, 0.1);
  color: #e69500;
}

.quantity-section {
  margin-bottom: 25px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  width: fit-content;
  border: 2px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}

.quantity-btn {
  background-color: #f5f5f5;
  border: none;
  width: 45px;
  height: 45px;
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.quantity-btn:hover {
  background-color: #e9e9e9;
}

.quantity-input {
  width: 70px;
  height: 45px;
  text-align: center;
  border: none;
  font-size: 18px;
  font-weight: bold;
}

.stock-info {
  margin-top: 10px;
  color: #8d6e63;
  font-size: 14px;
}

.stock-info i {
  margin-right: 5px;
}

.in-stock {
  color: #4caf50;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
}

.btn {
  padding: 15px 25px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  border: none;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
}

.btn i {
  margin-right: 10px;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(255, 179, 71, 0.4);
}

.btn-secondary {
  background-color: white;
  color: #e69500;
  border: 2px solid #ffb347;
}

.btn-secondary:hover:not(:disabled) {
  background-color: rgba(255, 179, 71, 0.1);
}

.service-guarantee {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.service-icon {
  color: #ffb347;
  font-size: 24px;
  margin-bottom: 8px;
}

.service-text {
  font-size: 13px;
  color: #8d6e63;
}


.product-tabs {
  background-color: white;
  border-radius: 15px;
  margin-bottom: 40px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.08);
  overflow: hidden;
}

.tab-headers {
  display: flex;
  border-bottom: 2px solid #eee;
}

.tab-header {
  padding: 20px 30px;
  font-weight: bold;
  cursor: pointer;
  color: #8d6e63;
  transition: all 0.3s;
  position: relative;
}

.tab-header.active {
  color: #e69500;
}

.tab-header.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #ffb347;
}

.tab-content {
  padding: 30px;
}

.tab-pane {
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.product-description {
  font-size: 16px;
  line-height: 1.8;
  color: #555;
}

.product-description h3 {
  margin-top: 20px;
  margin-bottom: 15px;
  color: #5a4a42;
}

.feature-list {
  list-style: none;
  margin: 20px 0;
  padding: 0;
}

.feature-list li {
  margin-bottom: 12px;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.feature-list i {
  color: #4caf50;
  margin-top: 3px;
}

.spec-table {
  width: 100%;
  border-collapse: collapse;
}

.spec-table tr {
  border-bottom: 1px solid #eee;
}

.spec-table td {
  padding: 15px 10px;
}

.spec-table td:first-child {
  font-weight: bold;
  width: 30%;
  color: #5a4a42;
}

.review-summary {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
}

.overall-rating {
  text-align: center;
  margin-right: 40px;
  min-width: 120px;
}

.rating-score-large {
  font-size: 48px;
  font-weight: bold;
  color: #e69500;
}

.rating-stars-large {
  color: #ffb400;
  font-size: 24px;
  margin-bottom: 10px;
}

.rating-distribution {
  flex: 1;
}

.rating-bar {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.rating-label {
  width: 100px;
  font-size: 14px;
  color: #8d6e63;
}

.rating-progress {
  flex: 1;
  height: 10px;
  background-color: #eee;
  border-radius: 5px;
  overflow: hidden;
  margin: 0 15px;
}

.rating-progress-fill {
  height: 100%;
  background-color: #ffb400;
  transition: width 0.3s;
}

.rating-percentage {
  width: 40px;
  text-align: right;
  font-size: 14px;
  color: #8d6e63;
}

.review-list {
  margin-top: 30px;
}

.review-item {
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.review-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.reviewer {
  font-weight: bold;
  color: #5a4a42;
}

.review-date {
  color: #8d6e63;
  font-size: 14px;
}

.review-rating {
  color: #ffb400;
  margin-bottom: 10px;
}

.review-content {
  color: #555;
  line-height: 1.6;
}

.recommended-products {
  margin-bottom: 40px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 25px;
  color: #5a4a42;
  position: relative;
  padding-bottom: 10px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 80px;
  height: 3px;
  background-color: #ffb347;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 25px;
}

.product-card-small {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  transition: all 0.3s;
  cursor: pointer;
}

.product-card-small:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}

.product-card-small img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.product-card-small .info {
  padding: 15px;
}

.product-card-small .title {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 8px;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-card-small .price {
  color: #e69500;
  font-weight: bold;
  font-size: 18px;
}

.modal {
  display: flex;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.9);
  z-index: 1100;
  justify-content: center;
  align-items: center;
}

.modal-content {
  max-width: 90%;
  max-height: 90%;
  position: relative;
}

.modal-close {
  position: absolute;
  top: 20px;
  right: 30px;
  color: white;
  font-size: 40px;
  cursor: pointer;
  z-index: 1110;
}

.modal-img {
  width: 100%;
  height: auto;
  border-radius: 10px;
}

.notification {
  position: fixed;
  top: 100px;
  right: 30px;
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  padding: 15px 25px;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.2);
  z-index: 9999;
  font-weight: bold;
  display: flex;
  align-items: center;
  max-width: 400px;
}

.notification i {
  margin-right: 10px;
  font-size: 18px;
}

.notification-enter-active {
  animation: slideIn 0.3s ease;
}

.notification-leave-active {
  animation: fadeOut 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes fadeOut {
  from { opacity: 1; }
  to { opacity: 0; }
}

@media (max-width: 1200px) {
  .container {
    max-width: 100%;
  }

  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .product-detail-container {
    flex-direction: column;
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .service-guarantee {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
  }

  .review-summary {
    flex-direction: column;
    text-align: center;
  }

  .overall-rating {
    margin-right: 0;
    margin-bottom: 20px;
  }
}

@media (max-width: 576px) {
  .tab-headers {
    overflow-x: auto;
  }

  .tab-header {
    white-space: nowrap;
  }
}
</style>
