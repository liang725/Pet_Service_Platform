<template>
  <div class="shopping-cart-float">
    <!-- 购物车图标按钮 -->
    <div class="cart-icon" @click="toggleCart">
      <i class="fas fa-shopping-cart"></i>
      <span v-if="totalItems > 0" class="cart-badge">{{ totalItems }}</span>
    </div>

    <!-- 购物车面板 -->
    <transition name="slide-fade">
      <div v-if="isOpen" class="cart-panel">
        <div class="cart-header">
          <h3>购物车</h3>
          <button class="close-btn" @click="isOpen = false">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="cart-body">
          <!-- 空购物车 -->
          <div v-if="cartItems.length === 0" class="empty-cart">
            <i class="fas fa-shopping-cart"></i>
            <p>购物车是空的</p>
            <button class="btn-go-shop" @click="goToProducts">去逛逛</button>
          </div>

          <!-- 购物车商品列表 -->
          <div v-else class="cart-items">
            <div v-for="item in cartItems" :key="item.id" class="cart-item">
              <div class="item-image" @click="goToProduct(item.productId)">
                <img :src="item.image" :alt="item.name">
              </div>
              <div class="item-info">
                <div class="item-name" @click="goToProduct(item.productId)">{{ item.name }}</div>
                <div class="item-spec" v-if="item.spec">{{ item.spec }}</div>
                <div class="item-price">¥{{ item.price }}</div>
              </div>
              <div class="item-actions">
                <div class="quantity-control">
                  <button @click="decreaseQuantity(item.id)">-</button>
                  <span>{{ item.quantity }}</span>
                  <button @click="increaseQuantity(item.id)">+</button>
                </div>
                <button class="remove-btn" @click="removeItem(item.id)">
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-if="cartItems.length > 0" class="cart-footer">
          <div class="total-info">
            <span>总计:</span>
            <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <button class="btn-checkout" @click="goToCheckout">
            去结算 ({{ totalItems }})
          </button>
        </div>
      </div>
    </transition>

    <!-- 遮罩层 - 已移除 -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()
const isOpen = ref(false)

// 使用 store 中的数据
const cartItems = computed(() => cartStore.cartItems)
const totalItems = computed(() => cartStore.totalItems)
const totalPrice = computed(() => cartStore.totalPrice)

// 切换购物车显示
const toggleCart = async () => {
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    router.push('/auth')
    return
  }

  if (!isOpen.value) {
    // 打开时加载购物车
    await cartStore.loadCart()
  }
  isOpen.value = !isOpen.value
}

// 增加数量
const increaseQuantity = async (itemId) => {
  const item = cartItems.value.find(i => i.id === itemId)
  if (item) {
    try {
      await cartStore.updateQuantity(itemId, item.quantity + 1)
    } catch (error) {
      alert('更新失败，请重试')
    }
  }
}

// 减少数量
const decreaseQuantity = async (itemId) => {
  const item = cartItems.value.find(i => i.id === itemId)
  if (item && item.quantity > 1) {
    try {
      await cartStore.updateQuantity(itemId, item.quantity - 1)
    } catch (error) {
      alert('更新失败，请重试')
    }
  }
}

// 移除商品
const removeItem = async (itemId) => {
  if (confirm('确定要删除这个商品吗？')) {
    try {
      await cartStore.removeItem(itemId)
    } catch (error) {
      alert('删除失败，请重试')
    }
  }
}

// 跳转到商品详情
const goToProduct = (productId) => {
  isOpen.value = false
  router.push(`/product/${productId}`)
}

// 跳转到商品列表
const goToProducts = () => {
  isOpen.value = false
  router.push('/products')
}

// 跳转到结算页
const goToCheckout = () => {
  isOpen.value = false
  alert('结算功能开发中...')
}

// 监听登录状态变化
watch(() => userStore.isLoggedIn, (newVal) => {
  if (newVal) {
    cartStore.loadCart()
  } else {
    cartStore.cartItems = []
  }
})

// 初始加载
onMounted(() => {
  if (userStore.isLoggedIn) {
    cartStore.loadCart()
  }
})
</script>

<style scoped>
.shopping-cart-float {
  position: fixed;
  right: 30px;
  bottom: 100px;
  z-index: 1000;
}

/* 购物车图标 */
.cart-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #FFB347, #E69500);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(255, 179, 71, 0.4);
  transition: all 0.3s;
  position: relative;
}

.cart-icon:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(255, 179, 71, 0.5);
}

.cart-icon i {
  font-size: 24px;
  color: white;
}

.cart-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #ff4444;
  color: white;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  border: 2px solid white;
}

/* 购物车面板 */
.cart-panel {
  position: fixed;
  right: 30px;
  bottom: 170px;
  width: 380px;
  max-height: 600px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.cart-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #FFF8E1, #FFECB3);
}

.cart-header h3 {
  margin: 0;
  font-size: 18px;
  color: #5a4a42;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  padding: 5px;
  transition: color 0.3s;
}

.close-btn:hover {
  color: #333;
}

/* 购物车主体 */
.cart-body {
  flex: 1;
  overflow-y: auto;
  max-height: 400px;
}

/* 空购物车 */
.empty-cart {
  padding: 60px 20px;
  text-align: center;
  color: #999;
}

.empty-cart i {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-cart p {
  font-size: 16px;
  margin-bottom: 20px;
}

.btn-go-shop {
  background: linear-gradient(135deg, #FFB347, #E69500);
  color: white;
  border: none;
  padding: 10px 30px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-go-shop:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 179, 71, 0.4);
}

/* 购物车商品列表 */
.cart-items {
  padding: 10px;
}

.cart-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.3s;
}

.cart-item:hover {
  background: #fafafa;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-name {
  font-size: 14px;
  color: #333;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-name:hover {
  color: #FFB347;
}

.item-spec {
  font-size: 12px;
  color: #999;
}

.item-price {
  font-size: 16px;
  color: #E69500;
  font-weight: bold;
}

.item-actions {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-end;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
}

.quantity-control button {
  width: 24px;
  height: 24px;
  border: none;
  background: #f5f5f5;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s;
}

.quantity-control button:hover {
  background: #FFB347;
  color: white;
}

.quantity-control span {
  min-width: 30px;
  text-align: center;
  font-size: 14px;
}

.remove-btn {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 4px;
  font-size: 14px;
  transition: color 0.3s;
}

.remove-btn:hover {
  color: #ff4444;
}

/* 购物车底部 */
.cart-footer {
  padding: 20px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.total-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 16px;
}

.total-price {
  font-size: 24px;
  color: #E69500;
  font-weight: bold;
}

.btn-checkout {
  width: 100%;
  background: linear-gradient(135deg, #FFB347, #E69500);
  color: white;
  border: none;
  padding: 14px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s;
}

.btn-checkout:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 179, 71, 0.4);
}

/* 遮罩层样式已移除 */

/* 动画 */
.slide-fade-enter-active {
  transition: all 0.3s ease;
}

.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from {
  transform: translateY(20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateY(20px);
  opacity: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 购物车面板的z-index调整 */
.cart-panel {
  position: fixed;
  right: 30px;
  bottom: 170px;
  width: 380px;
  max-height: 600px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  z-index: 1001;
}

/* 滚动条样式 */
.cart-body::-webkit-scrollbar {
  width: 6px;
}

.cart-body::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.cart-body::-webkit-scrollbar-thumb {
  background: #FFB347;
  border-radius: 3px;
}

.cart-body::-webkit-scrollbar-thumb:hover {
  background: #E69500;
}

/* 响应式 */
@media (max-width: 768px) {
  .shopping-cart-float {
    right: 15px;
    bottom: 80px;
  }

  .cart-icon {
    width: 50px;
    height: 50px;
  }

  .cart-icon i {
    font-size: 20px;
  }

  .cart-panel {
    right: 15px;
    bottom: 140px;
    width: calc(100vw - 30px);
    max-width: 380px;
  }
}
</style>
