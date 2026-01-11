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

    // 验证和处理 productId
    let productId = item.productId
    if (typeof productId === 'string') {
      productId = parseInt(productId)
    }

    if (!productId || isNaN(productId) || productId <= 0) {
      throw new Error(`无效的商品ID: ${item.productId}`)
    }

    // 确保 spec 有默认值
    const spec = item.spec || '标准装'

    // 验证和处理 quantity
    let quantity = item.quantity
    if (typeof quantity === 'string') {
      quantity = parseInt(quantity)
    }
    if (!quantity || isNaN(quantity) || quantity <= 0) {
      quantity = 1
    }

    const requestData = {
      productId: productId,
      spec: spec,
      quantity: quantity
    }

    console.log('购物车store - 添加商品请求:', requestData)

    const response = await addToCart(requestData)

    // 检查响应
    if (response && response.code !== 200) {
      throw new Error(response.message || '添加到购物车失败')
    }

    // 重新加载购物车以获取最新数据
    await loadCart()

    console.log('购物车store - 商品添加成功')
    return true

  } catch (error) {
    console.error('购物车store - 添加商品失败:', error)

    // 提供更详细的错误信息
    let errorMessage = error.message || '添加到购物车失败'

    if (error.response) {
      // HTTP错误响应
      if (error.response.status === 404) {
        errorMessage = '商品不存在'
      } else if (error.response.status === 401) {
        errorMessage = '登录已过期，请重新登录'
      } else if (error.response.data && error.response.data.message) {
        errorMessage = error.response.data.message
      }
    }

    throw new Error(errorMessage)
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
