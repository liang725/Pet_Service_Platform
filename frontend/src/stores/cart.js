import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCartItems, addToCart, updateCartItem, removeCartItem, clearCart } from '@/api/cart'
import { useUserStore } from './user'

export const useCartStore = defineStore('cart', () => {
  const userStore = useUserStore()
  const cartItems = ref([])
  const loading = ref(false)

  // 计算总数量
  const totalItems = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  // 计算总价格
  const totalPrice = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  // 加载购物车
  async function loadCart() {
    if (!userStore.isLoggedIn) {
      cartItems.value = []
      return
    }

    try {
      loading.value = true
      const response = await getCartItems()
      // 为每个商品添加默认选中状态
      cartItems.value = (response.items || []).map(item => ({
        ...item,
        selected: item.selected !== undefined ? item.selected : true
      }))
    } catch (error) {
      console.error('加载购物车失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 添加商品到购物车
  async function addItem(item) {
    if (!userStore.isLoggedIn) {
      // 未登录，提示登录
      throw new Error('请先登录')
    }

    try {
      loading.value = true
      await addToCart({
        productId: item.productId,
        spec: item.spec,
        quantity: item.quantity
      })
      await loadCart()
      return true
    } catch (error) {
      console.error('添加购物车失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 更新商品数量
  async function updateQuantity(itemId, quantity) {
    try {
      loading.value = true
      await updateCartItem(itemId, quantity)
      const item = cartItems.value.find(i => i.id === itemId)
      if (item) {
        item.quantity = quantity
      }
    } catch (error) {
      console.error('更新数量失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 删除商品
  async function removeItem(itemId) {
    try {
      loading.value = true
      await removeCartItem(itemId)
      const index = cartItems.value.findIndex(i => i.id === itemId)
      if (index > -1) {
        cartItems.value.splice(index, 1)
      }
    } catch (error) {
      console.error('删除商品失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 清空购物车
  async function clear() {
    try {
      loading.value = true
      await clearCart()
      cartItems.value = []
    } catch (error) {
      console.error('清空购物车失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 清空已选商品
  async function clearSelectedItems() {
    try {
      loading.value = true
      const selectedItems = cartItems.value.filter(item => item.selected)

      // 删除已选商品
      for (const item of selectedItems) {
        await removeCartItem(item.id)
      }

      // 重新加载购物车
      await loadCart()
    } catch (error) {
      console.error('清空已选商品失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    cartItems,
    loading,
    totalItems,
    totalPrice,
    loadCart,
    addItem,
    updateQuantity,
    removeItem,
    clear,
    clearSelectedItems
  }
})
