<template>
  <div class="product-list-page">
    <!-- 搜索栏 -->
    <div class="search-section">
      <div class="container">
        <div class="search-bar">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索商品名称、品牌..."
            @keyup.enter="searchProducts"
            class="search-input"
          />
          <button @click="searchProducts" class="search-btn">
            <i class="fas fa-search"></i> 搜索
          </button>
          <button v-if="searchKeyword" @click="clearSearch" class="clear-btn">
            <i class="fas fa-times"></i> 清除
          </button>
        </div>

        <div v-if="isSearching" class="search-info">
          搜索 "{{ searchKeyword }}" 的结果 ，共找到 {{ searchResults.length }} 件商品
        </div>
      </div>
    </div>

    <!-- 分类导航 -->
    <nav class="category-nav">
      <div class="container">
        <ul class="categories">
          <li v-for="cat in categories" :key="cat.id">
            <a
              href="#"
              :class="{ active: selectedCategory === cat.id }"
              @click.prevent="selectCategory(cat.id)"
            >
              <i :class="cat.icon"></i> {{ cat.name }}
            </a>
          </li>
        </ul>
      </div>
    </nav>

    <div class="container">
      <!-- 搜索结果 -->
      <div v-if="isSearching" class="search-results-section">
        <h2 class="section-title">搜索结果</h2>
        <div v-if="searchResults.length === 0" class="empty-search">
          <i class="fas fa-search empty-icon"></i>
          <p>没有找到相关商品</p>
          <button @click="clearSearch" class="back-btn">返回商品列表</button>
        </div>
        <div v-else class="product-grid">
          <div
            v-for="product in searchResults"
            :key="product.id"
            class="product-card"
            @click="goToProduct(product.id)"
          >
            <div class="product-image">
              <img :src="product.image" :alt="product.name" />
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

      <!-- 原有商品列表（非搜索状态） -->
      <div v-else>
        <!-- 商品轮播（只保留图片） -->
        <div class="banner-carousel">
          <button @click="prevBannerProduct" class="carousel-btn prev-btn">
            <i class="fas fa-chevron-left"></i>
          </button>
          <div class="carousel-container" ref="carouselContainer">
            <div
              class="carousel-track"
              :style="{ transform: `translateX(-${currentBannerIndex * 100}%)` }"
            >
              <div
                v-for="(product, idx) in bannerProducts"
                :key="product.id"
                class="carousel-item"
                @click="goToProduct(product.id)"
              >
                <img
                  :src="product.image"
                  :alt="product.name"
                  :style="imgStyles[idx]"
                  @load="onImgLoad(idx, $event)"
                />
              </div>
            </div>
          </div>
          <button @click="nextBannerProduct" class="carousel-btn next-btn">
            <i class="fas fa-chevron-right"></i>
          </button>
          <div class="carousel-indicators">
            <span
              v-for="(p, i) in bannerProducts"
              :key="i"
              :class="['indicator', { active: i === currentBannerIndex }]"
              @click="goToBannerProduct(i)"
            />
          </div>
        </div>

        <!-- 精选推荐滑动区 -->
        <div class="featured-section">
          <div class="section-header">
            <h2 class="section-title">⭐ 精选推荐</h2>
            <div class="scroll-controls">
              <button @click="scrollFeatured('left')" class="scroll-btn">
                <i class="fas fa-chevron-left"></i>
              </button>
              <button @click="scrollFeatured('right')" class="scroll-btn">
                <i class="fas fa-chevron-right"></i>
              </button>
            </div>
          </div>
          <div class="featured-scroll" ref="featuredScroll">
            <div class="featured-products">
              <div
                v-for="product in featuredProducts"
                :key="product.id"
                class="featured-card"
                @click="goToProduct(product.id)"
              >
                <div class="featured-image">
                  <img :src="product.image" :alt="product.name" />
                  <span v-if="product.tag" class="featured-tag">{{ product.tag }}</span>
                </div>
                <div class="featured-info">
                  <div class="featured-title">{{ product.name }}</div>
                  <div class="featured-price">
                    ¥{{ product.price.toFixed(2) }}
                    <span v-if="product.originalPrice" class="featured-original-price">
                      ¥{{ product.originalPrice.toFixed(2) }}
                    </span>
                  </div>
                  <button class="featured-cart-btn" @click.stop="addToCart(product)">
                    <i class="fas fa-cart-plus"></i> 加入购物车
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 热门推荐 -->
        <h2 class="section-title">热门推荐</h2>
        <div class="product-grid">
          <div
            v-for="product in hotProducts"
            :key="product.id"
            class="product-card"
            @click="goToProduct(product.id)"
          >
            <div class="product-image">
              <img :src="product.image" :alt="product.name" />
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
        <h2 class="section-title">宠物食品</h2>
        <div class="product-grid">
          <div
            v-for="product in foodProducts"
            :key="product.id"
            class="product-card"
            @click="goToProduct(product.id)"
          >
            <div class="product-image">
              <img :src="product.image" :alt="product.name" />
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
        <h2 class="section-title">宠物用品</h2>
        <div class="product-grid">
          <div
            v-for="product in supplyProducts"
            :key="product.id"
            class="product-card"
            @click="goToProduct(product.id)"
          >
            <div class="product-image">
              <img :src="product.image" :alt="product.name" />
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
        <h2 class="section-title">宠物玩具</h2>
        <div class="product-grid">
          <div
            v-for="product in toyProducts"
            :key="product.id"
            class="product-card"
            @click="goToProduct(product.id)"
          >
            <div class="product-image">
              <img :src="product.image" :alt="product.name" />
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
import { addFavorite, removeFavorite, getFavorites } from '@/api/favorites'

const router = useRouter()
const cartStore = useCartStore()

// 响应式数据
const selectedCategory = ref('food')
const showBackToTop = ref(false)
const wishlist = ref([])
const notification = ref({ show: false, message: '' })

// 搜索相关
const searchKeyword = ref('')
const isSearching = ref(false)
const searchResults = ref([])

const featuredScroll = ref(null)
const featuredProducts = ref([])

// 轮播商品相关
const bannerProducts = ref([])
const currentBannerIndex = ref(0)
const carouselContainer = ref(null)
const imgStyles = ref([])
let boxW = 0
let boxH = 0

// 图片加载完成后计算样式
const onImgLoad = (idx, e) => {
  if (!carouselContainer.value) return
  const img = e.target
  const { naturalWidth: nw, naturalHeight: nh } = img

  boxW = carouselContainer.value.clientWidth
  boxH = carouselContainer.value.clientHeight

  // 计算缩放比例，让图片填满容器（可能会裁剪）
  const scale = Math.max(boxW / nw, boxH / nh)
  const w = nw * scale
  const h = nh * scale

  imgStyles.value[idx] = {
    width: w + 'px',
    height: h + 'px'
  }
}

// 窗口大小变化时重新计算
const reCalcImgStyles = () => {
  if (!carouselContainer.value) return
  boxW = carouselContainer.value.clientWidth
  boxH = carouselContainer.value.clientHeight

  const imgs = carouselContainer.value.querySelectorAll('.carousel-item img')
  imgs.forEach((img, idx) => {
    if (img.complete && img.naturalWidth) {
      const { naturalWidth: nw, naturalHeight: nh } = img
      const scale = Math.max(boxW / nw, boxH / nh)
      imgStyles.value[idx] = {
        width: nw * scale + 'px',
        height: nh * scale + 'px'
      }
    }
  })
}

const categories = [
  { id: 'food', name: '宠物食品', icon: 'fas fa-bone' },
  { id: 'supply', name: '宠物用品', icon: 'fas fa-utensils' },
  { id: 'toy', name: '宠物玩具', icon: 'fas fa-baseball-ball' },
  { id: 'sale', name: '限时特惠', icon: 'fas fa-tags' }
]

// 滑动控制
const scrollFeatured = (direction) => {
  if (!featuredScroll.value) return
  const scrollAmount = 300
  const currentScroll = featuredScroll.value.scrollLeft

  if (direction === 'left') {
    featuredScroll.value.scrollTo({
      left: currentScroll - scrollAmount,
      behavior: 'smooth'
    })
  } else {
    featuredScroll.value.scrollTo({
      left: currentScroll + scrollAmount,
      behavior: 'smooth'
    })
  }
}

// 轮播商品控制
const prevBannerProduct = () => {
  if (currentBannerIndex.value > 0) {
    currentBannerIndex.value--
  } else {
    currentBannerIndex.value = bannerProducts.value.length - 1
  }
}

const nextBannerProduct = () => {
  if (currentBannerIndex.value < bannerProducts.value.length - 1) {
    currentBannerIndex.value++
  } else {
    currentBannerIndex.value = 0
  }
}

const goToBannerProduct = (index) => {
  currentBannerIndex.value = index
}

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
  clearSearch()

  const sectionMap = {
    food: '宠物食品',
    supply: '宠物用品',
    toy: '宠物玩具',
    sale: '热门推荐'
  }
  const targetTitle = sectionMap[categoryId]
  if (targetTitle) {
    setTimeout(() => {
      const sections = document.querySelectorAll('.section-title')
      for (const section of sections) {
        if (section.textContent.includes(targetTitle)) {
          section.scrollIntoView({ behavior: 'smooth', block: 'start' })
          section.style.color = '#ffb347'
          setTimeout(() => {
            section.style.color = '#5a4a42'
          }, 2000)
          break
        }
      }
    }, 100)
  }
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
      setTimeout(() => router.push('/auth'), 1000)
    } else {
      showNotification('添加失败，请重试')
    }
  }
}

const toggleWishlist = async (productId) => {
  try {
    const index = wishlist.value.indexOf(productId)
    if (index > -1) {
      const response = await removeFavorite(productId)
      if (response.code === 200) {
        wishlist.value.splice(index, 1)
        showNotification('已从收藏夹移除')
      } else {
        showNotification('取消收藏失败：' + (response.message || '未知错误'))
      }
    } else {
      const response = await addFavorite(productId)
      if (response.code === 200) {
        wishlist.value.push(productId)
        showNotification('已添加到收藏夹')
      } else if (response.code === 500 && response.message && response.message.includes('已在收藏列表中')) {
        if (!wishlist.value.includes(productId)) wishlist.value.push(productId)
        showNotification('商品已在收藏列表中')
      } else {
        showNotification('添加收藏失败：' + (response.message || '未知错误'))
      }
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    if (error.message && error.message.includes('401')) {
      showNotification('请先登录')
      setTimeout(() => router.push('/auth'), 1000)
    } else {
      showNotification('收藏操作失败，请重试')
    }
  }
}

const isInWishlist = (productId) => wishlist.value.includes(productId)

const showNotification = (message) => {
  notification.value.show = true
  notification.value.message = message
  setTimeout(() => (notification.value.show = false), 3000)
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const searchProducts = async () => {
  if (!searchKeyword.value.trim()) {
    showNotification('请输入搜索关键词')
    return
  }
  try {
    isSearching.value = true
    const allProducts = [
      ...hotProducts.value,
      ...foodProducts.value,
      ...supplyProducts.value,
      ...toyProducts.value
    ]
    const uniqueProducts = allProducts.filter(
      (product, index, self) => index === self.findIndex((p) => p.id === product.id)
    )
    const keyword = searchKeyword.value.toLowerCase()
    searchResults.value = uniqueProducts.filter(
      (product) =>
        product.name.toLowerCase().includes(keyword) ||
        (product.description && product.description.toLowerCase().includes(keyword)) ||
        (product.brand && product.brand.toLowerCase().includes(keyword))
    )
    showNotification(`找到 ${searchResults.value.length} 件相关商品`)
  } catch (error) {
    console.error('搜索失败:', error)
    showNotification('搜索失败，请重试')
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
  isSearching.value = false
  searchResults.value = []
}

const handleScroll = () => {
  showBackToTop.value = window.scrollY > 300
}

const loadFavorites = async () => {
  try {
    const response = await getFavorites()
    if (response.code === 200 && response.data) {
      wishlist.value = response.data.map((item) => item.id)
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    wishlist.value = []
  }
}

const loadProducts = async () => {
  try {
    const bannerResponse = await getProductList({ category: 'hot', limit: 3 })
    if (bannerResponse.code === 200) bannerProducts.value = bannerResponse.data

    const featuredResponse = await getProductList({ category: 'hot', limit: 10 })
    if (featuredResponse.code === 200) featuredProducts.value = featuredResponse.data

    const hotResponse = await getProductList({ category: 'hot', limit: 5 })
    if (hotResponse.code === 200) hotProducts.value = hotResponse.data

    const foodResponse = await getProductList({ category: 'food', limit: 5 })
    if (foodResponse.code === 200) foodProducts.value = foodResponse.data

    const supplyResponse = await getProductList({ category: 'supply', limit: 5 })
    if (supplyResponse.code === 200) supplyProducts.value = supplyResponse.data

    const toyResponse = await getProductList({ category: 'toy', limit: 5 })
    if (toyResponse.code === 200) toyProducts.value = toyResponse.data
  } catch (error) {
    console.error('加载商品失败:', error)
    showNotification('加载商品数据失败,请检查后端服务是否启动')
  }
}

onMounted(() => {
  loadProducts()
  loadFavorites()
  window.addEventListener('scroll', handleScroll)
  window.addEventListener('resize', reCalcImgStyles)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('resize', reCalcImgStyles)
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

/* 搜索栏 */
.search-section {
  background-color: #fff8e1;
  padding: 20px 0;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(255, 179, 71, 0.1);
}
.search-bar {
  display: flex;
  gap: 10px;
  max-width: 800px;
  margin: 0 auto;
}
.search-input {
  flex: 1;
  padding: 12px 20px;
  border: 2px solid #ffecb3;
  border-radius: 25px;
  font-size: 15px;
  outline: none;
  transition: all 0.3s;
}
.search-input:focus {
  border-color: #ffb347;
  box-shadow: 0 0 0 3px rgba(255, 179, 71, 0.1);
}
.search-btn {
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 10px rgba(255, 179, 71, 0.3);
}
.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(255, 179, 71, 0.4);
}
.clear-btn {
  background-color: #8d6e63;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}
.clear-btn:hover {
  background-color: #6d4c41;
  transform: translateY(-2px);
}
.search-info {
  text-align: center;
  margin-top: 15px;
  color: #5a4a42;
  font-size: 15px;
  font-weight: 500;
}
.search-results-section {
  margin-top: 30px;
}
.empty-search {
  text-align: center;
  padding: 80px 20px;
  color: #8d6e63;
}
.empty-icon {
  font-size: 80px;
  color: #ffecb3;
  margin-bottom: 20px;
}
.empty-search p {
  font-size: 18px;
  margin-bottom: 25px;
}
.back-btn {
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s;
  box-shadow: 0 4px 10px rgba(255, 179, 71, 0.3);
}
.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(255, 179, 71, 0.4);
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

/* 商品轮播（只展示图片） */
.banner-carousel {
  position: relative;
  margin-bottom: 30px;
  height: 400px;          /* 想要多高就写多高 */
  background-color: #fff8e1;
}
.carousel-container {
  width: 100%;
  height: 100%;
  overflow: hidden;       /* 圆角/溢出在这里切 */
  border-radius: 15px;    /* 如果想保留圆角 */
}
.carousel-track {
  display: flex;
  height: 100%;
  transition: transform 0.5s ease-in-out;
}
.carousel-item {
  min-width: 100%;
  height: 100%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;      /* 关键：铺满容器，多余部分裁剪 */
  display: block;
}
.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 179, 71, 0.9);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  z-index: 10;
  font-size: 20px;
}
.carousel-btn:hover {
  background: rgba(230, 149, 0, 1);
  transform: translateY(-50%) scale(1.1);
}
.prev-btn {
  left: 20px;
}
.next-btn {
  right: 20px;
}
.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 10;
}
.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid white;
}
.indicator:hover {
  background-color: rgba(255, 179, 71, 0.7);
}
.indicator.active {
  background-color: #ffb347;
  width: 30px;
  border-radius: 6px;
}

/* 精选推荐滑动区 */
.featured-section {
  margin: 40px 0;
  background-color: #fff8e1;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 5px 20px rgba(255, 179, 71, 0.1);
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.section-header .section-title {
  margin: 0;
}
.scroll-controls {
  display: flex;
  gap: 10px;
}
.scroll-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  box-shadow: 0 3px 10px rgba(255, 179, 71, 0.3);
}
.scroll-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 5px 15px rgba(255, 179, 71, 0.4);
}
.featured-scroll {
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: thin;
  scrollbar-color: #ffb347 #ffecb3;
}
.featured-scroll::-webkit-scrollbar {
  height: 8px;
}
.featured-scroll::-webkit-scrollbar-track {
  background: #ffecb3;
  border-radius: 10px;
}
.featured-scroll::-webkit-scrollbar-thumb {
  background: #ffb347;
  border-radius: 10px;
}
.featured-scroll::-webkit-scrollbar-thumb:hover {
  background: #e69500;
}
.featured-products {
  display: flex;
  gap: 20px;
  padding: 10px 0;
}
.featured-card {
  min-width: 280px;
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  cursor: pointer;
  border: 2px solid #ffecb3;
}
.featured-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  border-color: #ffb347;
}
.featured-image {
  height: 200px;
  overflow: hidden;
  position: relative;
}
.featured-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}
.featured-card:hover .featured-image img {
  transform: scale(1.1);
}
.featured-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background-color: #ff6b6b;
  color: white;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
}
.featured-info {
  padding: 15px;
}
.featured-title {
  font-size: 16px;
  font-weight: bold;
  color: #5a4a42;
  margin-bottom: 10px;
  height: 40px;
  overflow: hidden;
  line-height: 1.3;
}
.featured-price {
  color: #e69500;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}
.featured-original-price {
  color: #8d6e63;
  text-decoration: line-through;
  font-size: 14px;
  margin-left: 8px;
  font-weight: normal;
}
.featured-cart-btn {
  width: 100%;
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  border: none;
  padding: 10px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 3px 10px rgba(255, 179, 71, 0.3);
}
.featured-cart-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 179, 71, 0.4);
}

/* 商品网格 */
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
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
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
  from {
    opacity: 1;
  }
  to {
    opacity: 0;
  }
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

/* 响应式 */
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
  .banner-carousel {
    height: 350px;
  }
}
@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .tips-content {
    grid-template-columns: 1fr;
  }
  .banner-carousel {
    height: 300px;
  }
  .carousel-btn {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
  .prev-btn {
    left: 10px;
  }
  .next-btn {
    right: 10px;
  }
}
@media (max-width: 576px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
  .categories {
    justify-content: center;
  }
  .categories a {
    font-size: 14px;
    padding: 6px 15px;
  }
  .banner-carousel {
    height: 250px;
  }
}
</style>
