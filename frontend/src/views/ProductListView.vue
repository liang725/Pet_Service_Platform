<template>
  <div class="product-list-page">
    <!-- 分类导航 -->
    <nav class="category-nav">
      <div class="container">
        <ul class="categories">
          <li v-for="cat in categories" :key="cat.id">
            <a href="#" :class="{ active: selectedCategory === cat.id }"
              @click.prevent="selectCategory(cat.id)">
              <i :class="cat.icon"></i> {{ cat.name }}
            </a>
          </li>
        </ul>
      </div>
    </nav>

    <div class="container">
      <!-- 轮播图 -->
      <div class="banner">
        <img :src="bannerImage" alt="宠物之家欢迎您">
      </div>

      <!-- 特色服务横幅 -->
      <div class="services-banner">
        <div v-for="service in services" :key="service.title" class="service-item">
          <div class="service-icon">
            <i :class="service.icon"></i>
          </div>
          <div class="service-title">{{ service.title }}</div>
          <div class="service-desc">{{ service.desc }}</div>
        </div>
      </div>

      <!-- 热门推荐 -->
      <h2 class="section-title">🔥 热门推荐</h2>
      <div class="product-grid">
        <div v-for="product in hotProducts" :key="product.id"
          class="product-card" @click="goToProduct(product.id)">
          <div class="product-image">
            <img :src="product.image" :alt="product.name">
            <span v-if="product.tag" class="product-tag">{{ product.tag }}</span>
          </div>
          <div class="product-info">
            <div class="product-title">{{ product.name }}</div>
            <div class="product-price">
              ¥{{ product.price.toFixed(2) }}
              <span v-if="product.originalPrice" class="original-price">
                ¥{{ product.originalPrice.toFixed(2) }}
              </span>
            </div>
            <div class="product-rating">
              <i v-for="n in 5" :key="n" :class="getStarClass(n, product.rating)"></i>
              <span class="rating-count">({{ product.ratingCount }})</span>
            </div>
            <div class="product-actions">
              <button class="add-to-cart" @click.stop="addToCart(product)">
                <i class="fas fa-cart-plus"></i> 加入购物车
              </button>
              <button class="wishlist-btn" @click.stop="toggleWishlist(product.id)">
                <i :class="isInWishlist(product.id) ? 'fas fa-heart' : 'far fa-heart'"></i>
              </button>
            </div>
          </div>
        </div>
      </div>


      <!-- 宠物食品 -->
      <h2 class="section-title">🍖 宠物食品</h2>
      <div class="product-grid">
        <div v-for="product in foodProducts" :key="product.id"
          class="product-card" @click="goToProduct(product.id)">
          <div class="product-image">
            <img :src="product.image" :alt="product.name">
            <span v-if="product.tag" class="product-tag">{{ product.tag }}</span>
          </div>
          <div class="product-info">
            <div class="product-title">{{ product.name }}</div>
            <div class="product-price">
              ¥{{ product.price.toFixed(2) }}
              <span v-if="product.originalPrice" class="original-price">
                ¥{{ product.originalPrice.toFixed(2) }}
              </span>
            </div>
            <div class="product-rating">
              <i v-for="n in 5" :key="n" :class="getStarClass(n, product.rating)"></i>
              <span class="rating-count">({{ product.ratingCount }})</span>
            </div>
            <div class="product-actions">
              <button class="add-to-cart" @click.stop="addToCart(product)">
                <i class="fas fa-cart-plus"></i> 加入购物车
              </button>
              <button class="wishlist-btn" @click.stop="toggleWishlist(product.id)">
                <i :class="isInWishlist(product.id) ? 'fas fa-heart' : 'far fa-heart'"></i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 温馨提示 -->
      <div class="tips-section">
        <h2 class="tips-title"><i class="fas fa-lightbulb"></i> 养宠小贴士</h2>
        <div class="tips-content">
          <div v-for="tip in tips" :key="tip.title" class="tip-item">
            <h4>{{ tip.title }}</h4>
            <p>{{ tip.content }}</p>
          </div>
        </div>
      </div>

      <!-- 宠物用品 -->
      <h2 class="section-title">🏠 宠物用品</h2>
      <div class="product-grid">
        <div v-for="product in supplyProducts" :key="product.id"
          class="product-card" @click="goToProduct(product.id)">
          <div class="product-image">
            <img :src="product.image" :alt="product.name">
            <span v-if="product.tag" class="product-tag">{{ product.tag }}</span>
          </div>
          <div class="product-info">
            <div class="product-title">{{ product.name }}</div>
            <div class="product-price">
              ¥{{ product.price.toFixed(2) }}
              <span v-if="product.originalPrice" class="original-price">
                ¥{{ product.originalPrice.toFixed(2) }}
              </span>
            </div>
            <div class="product-rating">
              <i v-for="n in 5" :key="n" :class="getStarClass(n, product.rating)"></i>
              <span class="rating-count">({{ product.ratingCount }})</span>
            </div>
            <div class="product-actions">
              <button class="add-to-cart" @click.stop="addToCart(product)">
                <i class="fas fa-cart-plus"></i> 加入购物车
              </button>
              <button class="wishlist-btn" @click.stop="toggleWishlist(product.id)">
                <i :class="isInWishlist(product.id) ? 'fas fa-heart' : 'far fa-heart'"></i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 宠物玩具 -->
      <h2 class="section-title">🎾 宠物玩具</h2>
      <div class="product-grid">
        <div v-for="product in toyProducts" :key="product.id"
          class="product-card" @click="goToProduct(product.id)">
          <div class="product-image">
            <img :src="product.image" :alt="product.name">
            <span v-if="product.tag" class="product-tag">{{ product.tag }}</span>
          </div>
          <div class="product-info">
            <div class="product-title">{{ product.name }}</div>
            <div class="product-price">
              ¥{{ product.price.toFixed(2) }}
              <span v-if="product.originalPrice" class="original-price">
                ¥{{ product.originalPrice.toFixed(2) }}
              </span>
            </div>
            <div class="product-rating">
              <i v-for="n in 5" :key="n" :class="getStarClass(n, product.rating)"></i>
              <span class="rating-count">({{ product.ratingCount }})</span>
            </div>
            <div class="product-actions">
              <button class="add-to-cart" @click.stop="addToCart(product)">
                <i class="fas fa-cart-plus"></i> 加入购物车
              </button>
              <button class="wishlist-btn" @click.stop="toggleWishlist(product.id)">
                <i :class="isInWishlist(product.id) ? 'fas fa-heart' : 'far fa-heart'"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 回到顶部按钮 -->
    <button v-show="showBackToTop" class="back-to-top" @click="scrollToTop">
      <i class="fas fa-chevron-up"></i>
    </button>

    <!-- 通知提示 -->
    <transition name="notification">
      <div v-if="notification.show" class="notification">
        <i class="fas fa-check-circle"></i> {{ notification.message }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProductList } from '@/api/product'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()


// 响应式数据
const selectedCategory = ref('food')
const showBackToTop = ref(false)
const wishlist = ref([])
const notification = ref({ show: false, message: '' })

const bannerImage = ref('https://images.unsplash.com/photo-1518717758536-85ae29035b6d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1200&q=80')

const categories = [
  { id: 'food', name: '宠物食品', icon: 'fas fa-bone' },
  { id: 'supply', name: '宠物用品', icon: 'fas fa-utensils' },
  { id: 'toy', name: '宠物玩具', icon: 'fas fa-baseball-ball' },
  { id: 'sale', name: '限时特惠', icon: 'fas fa-tags' }
]

const services = [
  { icon: 'fas fa-shipping-fast', title: '快速配送', desc: '满99元免运费' },
  { icon: 'fas fa-shield-alt', title: '正品保证', desc: '100%官方授权' },
  { icon: 'fas fa-headset', title: '专业客服', desc: '养宠问题解答' },
  { icon: 'fas fa-undo-alt', title: '无忧退换', desc: '30天无忧退货' }
]

const tips = [
  {
    title: '如何选择宠物食品',
    content: '根据宠物年龄、品种和健康状况选择合适的主粮，注意查看成分表和营养配比。'
  },
  {
    title: '宠物日常护理',
    content: '定期为宠物梳理毛发、清洁耳朵、修剪指甲，保持宠物清洁健康。'
  },
  {
    title: '宠物健康检查',
    content: '建议每年为宠物进行一次全面体检，及时接种疫苗，预防疾病发生。'
  }
]

const hotProducts = ref([])
const foodProducts = ref([])
const supplyProducts = ref([])
const toyProducts = ref([])

// 方法
const getStarClass = (index, rating) => {
  if (index <= Math.floor(rating)) {
    return 'fas fa-star'
  } else if (index === Math.ceil(rating) && rating % 1 !== 0) {
    return 'fas fa-star-half-alt'
  }
  return 'far fa-star'
}

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  // 可以根据分类筛选商品
}

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

const addToCart = async (product) => {
  try {
    await cartStore.addItem({
      productId: product.id,
      spec: '标准装',
      quantity: 1
    })

    showNotification(`已将 ${product.name} 加入购物车`)
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

const toggleWishlist = (productId) => {
  const index = wishlist.value.indexOf(productId)
  if (index > -1) {
    wishlist.value.splice(index, 1)
    showNotification('已从收藏夹移除')
  } else {
    wishlist.value.push(productId)
    showNotification('已添加到收藏夹')
  }
}

const isInWishlist = (productId) => {
  return wishlist.value.includes(productId)
}

const showNotification = (message) => {
  notification.value.show = true
  notification.value.message = message

  setTimeout(() => {
    notification.value.show = false
  }, 3000)
}

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

const handleScroll = () => {
  showBackToTop.value = window.scrollY > 300
}

// 加载商品数据
const loadProducts = async () => {
  try {
    // 热门推荐
    const hotResponse = await getProductList({ category: 'hot', limit: 5 })
    if (hotResponse.code === 200) {
      hotProducts.value = hotResponse.data
    }

    // 宠物食品
    const foodResponse = await getProductList({ category: 'food', limit: 5 })
    if (foodResponse.code === 200) {
      foodProducts.value = foodResponse.data
    }

    // 宠物用品
    const supplyResponse = await getProductList({ category: 'supply', limit: 5 })
    if (supplyResponse.code === 200) {
      supplyProducts.value = supplyResponse.data
    }

    // 宠物玩具
    const toyResponse = await getProductList({ category: 'toy', limit: 5 })
    if (toyResponse.code === 200) {
      toyProducts.value = toyResponse.data
    }
  } catch (error) {
    console.error('加载商品失败:', error)
    showNotification('加载商品数据失败,请检查后端服务是否启动')
  }
}

onMounted(() => {
  loadProducts()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>


<style scoped>
.product-list-page {
  background-color: #fffaf0;
  min-height: 100vh;
  padding-bottom: 40px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

/* 分类导航 */
.category-nav {
  background-color: #fff8e1;
  box-shadow: 0 3px 10px rgba(255, 179, 71, 0.1);
  padding: 15px 0;
  margin-bottom: 25px;
  border-bottom: 3px solid #ffecb3;
}

.categories {
  display: flex;
  justify-content: space-between;
  list-style: none;
  padding: 0;
  margin: 0;
  flex-wrap: wrap;
  gap: 10px;
}

.categories a {
  color: #5a4a42;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  padding: 8px 20px;
  border-radius: 25px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
}

.categories a i {
  margin-right: 8px;
  font-size: 18px;
}

.categories a:hover,
.categories a.active {
  color: #e69500;
  background-color: rgba(255, 179, 71, 0.15);
}

/* 轮播图 */
.banner {
  margin-bottom: 30px;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
  border: 5px solid white;
}

.banner img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  display: block;
}

/* 特色服务横幅 */
.services-banner {
  background: linear-gradient(135deg, #fff8e1, #fff8e1);
  color: #5a4a42;
  padding: 25px;
  border-radius: 15px;
  margin: 40px 0;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 5px 20px rgba(255, 179, 71, 0.1);
  border: 2px dashed #ffecb3;
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 15px;
}

.service-icon {
  background-color: #ffcc80;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
  color: white;
  font-size: 28px;
  box-shadow: 0 5px 15px rgba(255, 179, 71, 0.3);
}

.service-title {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
  color: #5a4a42;
}

.service-desc {
  font-size: 14px;
  color: #8d6e63;
}

/* 商品区域 */
.section-title {
  font-size: 24px;
  font-weight: bold;
  margin: 35px 0 25px;
  padding-bottom: 12px;
  color: #5a4a42;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, #ffb347, #ffcc80);
  border-radius: 2px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 25px;
  margin-bottom: 50px;
}

.product-card {
  background-color: #fff8e1;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(255, 179, 71, 0.1);
  transition: all 0.4s;
  border: 1px solid #ffecb3;
  cursor: pointer;
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
}

.product-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
  border-color: #ffcc80;
}

.product-image {
  height: 220px;
  overflow: hidden;
  position: relative;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.7s;
}

.product-card:hover .product-image img {
  transform: scale(1.08);
}

.product-tag {
  position: absolute;
  top: 15px;
  left: 15px;
  background-color: #ffb347;
  color: white;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
}

.product-info {
  padding: 20px;
}

.product-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
  height: 44px;
  overflow: hidden;
  color: #5a4a42;
  line-height: 1.4;
}

.product-price {
  color: #e69500;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}

.original-price {
  color: #8d6e63;
  text-decoration: line-through;
  font-size: 15px;
  margin-left: 10px;
  font-weight: normal;
}

.product-rating {
  color: #ff9900;
  font-size: 14px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.rating-count {
  color: #8d6e63;
  font-size: 13px;
  margin-left: 8px;
}

.product-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.add-to-cart {
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 10px rgba(255, 179, 71, 0.3);
}

.add-to-cart:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(255, 179, 71, 0.4);
  background: linear-gradient(135deg, #e69500, #ffb347);
}

.add-to-cart i {
  margin-right: 8px;
}

.wishlist-btn {
  background: none;
  border: none;
  color: #8d6e63;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s;
  padding: 5px;
}

.wishlist-btn:hover {
  color: #ff6b6b;
  transform: scale(1.2);
}

/* 温馨提示 */
.tips-section {
  background-color: #fff8e1;
  border-radius: 15px;
  padding: 30px;
  margin: 40px 0;
  box-shadow: 0 5px 20px rgba(255, 179, 71, 0.1);
  border-left: 5px solid #ffb347;
}

.tips-title {
  font-size: 22px;
  color: #5a4a42;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.tips-title i {
  color: #ffb347;
  margin-right: 12px;
  font-size: 24px;
}

.tips-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.tip-item {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s;
}

.tip-item:hover {
  transform: translateY(-5px);
}

.tip-item h4 {
  color: #e69500;
  margin-bottom: 10px;
  font-size: 17px;
}

.tip-item p {
  color: #8d6e63;
  font-size: 14px;
  line-height: 1.5;
}

/* 回到顶部按钮 */
.back-to-top {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 50px;
  height: 50px;
  background-color: #ffb347;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 20px;
  z-index: 999;
  box-shadow: 0 5px 15px rgba(255, 179, 71, 0.4);
  transition: all 0.3s;
}

.back-to-top:hover {
  background-color: #e69500;
  transform: translateY(-5px);
}

/* 通知提示 */
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

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.product-card:nth-child(1) { animation-delay: 0.1s; }
.product-card:nth-child(2) { animation-delay: 0.2s; }
.product-card:nth-child(3) { animation-delay: 0.3s; }
.product-card:nth-child(4) { animation-delay: 0.4s; }
.product-card:nth-child(5) { animation-delay: 0.5s; }

/* 响应式设计 */
@media (max-width: 1200px) {
  .container {
    max-width: 100%;
  }

  .product-grid {
    grid-template-columns: repeat(4, 1fr);
  }

  .tips-content {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 992px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .services-banner {
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .tips-content {
    grid-template-columns: 1fr;
  }

  .banner img {
    height: 250px;
  }
}

@media (max-width: 576px) {
  .product-grid {
    grid-template-columns: 1fr;
  }

  .services-banner {
    flex-direction: column;
    gap: 20px;
  }

  .categories {
    justify-content: center;
  }

  .categories a {
    font-size: 14px;
    padding: 6px 15px;
  }
}
</style>
