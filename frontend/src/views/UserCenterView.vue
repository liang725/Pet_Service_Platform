<template>
  <div class="user-center-page">
    <div class="container">
      <!-- 左侧边栏 -->
      <div class="sidebar">
        <!-- 用户信息卡片 -->
        <div class="user-info-card">
          <div class="edit-profile-btn" @click="openProfileEditDialog" title="编辑个人信息">
            <Icon icon="mdi:pencil" />
          </div>
          <div class="user-avatar">
            <Icon icon="mdi:account" />
          </div>
          <div class="user-details">
            <h3>{{ userInfo.name || '爱宠人士张先生' }}</h3>
            <div class="user-level">{{ userInfo.level || '黄金会员' }}</div>
          </div>
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-number">{{ orderCount }}</div>
              <div class="stat-label">订单数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ cartCount }}</div>
              <div class="stat-label">购物车</div>
            </div>
          </div>
        </div>

        <!-- 导航菜单 -->
        <div class="nav-menu">
          <div
            class="nav-item"
            :class="{ active: activeTab === 'orders' }"
            @click="switchTab('orders')"
          >
            <Icon icon="mdi:clipboard-list" />
            <span>我的订单</span>
            <span v-if="orderCount > 0" class="nav-badge">{{ orderCount }}</span>
          </div>
          <div
            class="nav-item"
            :class="{ active: activeTab === 'cart' }"
            @click="switchTab('cart')"
          >
            <Icon icon="mdi:cart" />
            <span>购物车</span>
            <span v-if="cartCount > 0" class="nav-badge">{{ cartCount }}</span>
          </div>
          <div
            class="nav-item"
            :class="{ active: activeTab === 'favorites' }"
            @click="switchTab('favorites')"
          >
            <Icon icon="mdi:heart" />
            <span>我的收藏</span>
          </div>
          <div
            class="nav-item"
            :class="{ active: activeTab === 'address' }"
            @click="switchTab('address')"
          >
            <Icon icon="mdi:map-marker" />
            <span>收货地址</span>
          </div>
          <div
            class="nav-item"
            :class="{ active: activeTab === 'appointments' }"
            @click="switchTab('appointments')"
          >
            <Icon icon="mdi:calendar-check" />
            <span>我的预约</span>
          </div>
          <div
            class="nav-item"
            :class="{ active: activeTab === 'settings' }"
            @click="switchTab('settings')"
          >
            <Icon icon="mdi:cog" />
            <span>账户设置</span>
          </div>
        </div>
      </div>

      <!-- 右侧主内容区 -->
      <div class="main-content">
        <!-- 我的订单 -->
        <div class="content-section" v-show="activeTab === 'orders'">
          <div class="content-header">
            <h2>我的订单</h2>
            <p>查看和管理您的所有订单</p>
          </div>

          <!-- 订单状态筛选栏 -->
          <div class="order-filter-tabs">
            <div
              class="filter-tab"
              :class="{ active: orderFilter === 'all' }"
              @click="setOrderFilter('all')"
            >
              全部订单
            </div>
            <div
              class="filter-tab"
              :class="{ active: orderFilter === 'pending' }"
              @click="setOrderFilter('pending')"
            >
              待付款
              <span v-if="getPendingCount() > 0" class="filter-badge">{{ getPendingCount() }}</span>
            </div>
            <div
              class="filter-tab"
              :class="{ active: orderFilter === 'processing' }"
              @click="setOrderFilter('processing')"
            >
              待收货/使用
            </div>
            <div
              class="filter-tab"
              :class="{ active: orderFilter === 'delivered' }"
              @click="setOrderFilter('delivered')"
            >
              待评价
              <span v-if="getDeliveredCount() > 0" class="filter-badge">{{ getDeliveredCount() }}</span>
            </div>
            <div
              class="filter-tab"
              :class="{ active: orderFilter === 'cancelled' }"
              @click="setOrderFilter('cancelled')"
            >
              订单回收站
            </div>
            <!-- 清空回收站按钮 -->
            <div v-if="orderFilter === 'cancelled' && getCancelledCount() > 0" class="clear-trash-btn" @click="clearTrash">
              <Icon icon="mdi:delete-sweep" />
              清空回收站
            </div>
          </div>

            <!-- 订单列表 -->
            <div v-if="loading" class="loading-state">
              <div class="loading-spinner">
                <Icon icon="mdi:loading" class="spin" />
                <span>加载中...</span>
              </div>
            </div>
            <div v-else-if="orders.length === 0" class="empty-state">
              <div class="empty-icon">
                <Icon icon="mdi:clipboard-list" />
              </div>
              <div class="empty-title">暂无订单</div>
              <div class="empty-description">您还没有任何订单记录</div>
              <button class="empty-action" @click="continueShopping">
                <Icon icon="mdi:shopping" /> 去购物
              </button>
            </div>
            <div v-else class="orders-list">
              <div v-for="order in filteredOrders" :key="order.id" class="order-card">
                <div class="order-header">
                  <div class="order-info">
                    <div class="order-info-item">
                      <div class="order-info-label">订单号</div>
                      <div class="order-info-value">{{ order.orderNo || order.id }}</div>
                    </div>
                    <div class="order-info-item">
                      <div class="order-info-label">下单时间</div>
                      <div class="order-info-value">{{ formatDateTime(order.createdAt || order.date) }}</div>
                    </div>
                    <div class="order-info-item">
                      <div class="order-info-label">订单金额</div>
                      <div class="order-info-value">¥{{ (order.finalAmount || order.total || 0).toFixed(2) }}</div>
                    </div>
                  </div>
                  <div class="order-status-section">
                    <div class="order-status" :class="getStatusClass(order.status)">
                      {{ getStatusText(order.status) }}
                    </div>
                    <!-- 未付款订单显示剩余支付时间在右上角 -->
                    <div v-if="order.status === 'pending'" class="payment-countdown-badge" :class="{ 'countdown-warning': getPaymentTimeLeft(order) <= 180 }">
                      <Icon icon="mdi:clock-outline" />
                      <span>{{ formatPaymentTime(getPaymentTimeLeft(order)) }}</span>
                    </div>
                  </div>
                </div>

                <!-- 订单商品预览 -->
                <div v-if="order.items && order.items.length > 0" class="order-preview">
                  <div class="preview-product" @click="goToProduct(order.items[0].productId)">
                    <div class="product-image">
                      <img v-if="order.items[0].image" :src="order.items[0].image" :alt="order.items[0].name">
                      <Icon v-else icon="mdi:image" />
                    </div>
                    <div class="product-info">
                      <h4>{{ order.items[0].name }}</h4>
                      <p>{{ order.items[0].spec }}</p>
                    </div>
                  </div>
                  <div class="order-summary">
                    <p>共 {{ order.items.length }} 件商品</p>
                    <h3>实付：¥{{ order.total.toFixed(2) }}</h3>
                  </div>
                </div>

                <!-- 订单操作按钮 -->
                <div class="order-actions">
                  <button class="action-btn action-btn-secondary" @click="toggleOrderDetail(order)">
                    <Icon icon="mdi:eye" />
                    {{ order.showDetail ? '收起详情' : '查看详情' }}
                  </button>
                  <button v-if="order.status === 'delivered' || order.status === 'paid' || order.status === 'shipped'" class="action-btn action-btn-primary" @click="reorder(order)">
                    <Icon icon="mdi:refresh" />
                    再次购买
                  </button>
                  <button v-if="order.status === 'pending' && !isOrderExpired(order)" class="action-btn action-btn-primary" @click="payOrder(order)">
                    <Icon icon="mdi:credit-card" />
                    立即支付
                  </button>
                  <button v-if="order.status === 'pending' && !isOrderExpired(order)" class="action-btn action-btn-danger" @click="cancelOrder(order)">
                    <Icon icon="mdi:close" />
                    取消订单
                  </button>
                  <div v-if="order.status === 'pending' && isOrderExpired(order)" class="expired-order-notice">
                    <Icon icon="mdi:clock-alert" />
                    <span>订单已超时，无法支付</span>
                  </div>
                </div>

                <!-- 订单详情展开 -->
                <div v-if="order.showDetail" class="order-detail-expanded">
                  <div class="detail-section">
                    <h4>商品清单</h4>
                    <div class="detail-products">
                      <div v-for="item in order.items" :key="item.id" class="detail-product-item" @click="goToProduct(item.productId)">
                        <div class="product-image">
                          <img v-if="item.image" :src="item.image" :alt="item.name">
                          <Icon v-else icon="mdi:image" />
                        </div>
                        <div class="product-details">
                          <h5>{{ item.name }}</h5>
                          <p>{{ item.spec }}</p>
                          <div class="price-quantity">
                            <span class="price">¥{{ item.price.toFixed(2) }}</span>
                            <span class="quantity">数量：{{ item.quantity }}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div v-if="order.summary" class="detail-section">
                    <h4>费用明细</h4>
                    <div class="order-summary-detail">
                      <div class="summary-row">
                        <span>商品总价</span>
                        <span>¥{{ order.summary.subtotal.toFixed(2) }}</span>
                      </div>
                      <div class="summary-row">
                        <span>运费</span>
                        <span>¥{{ order.summary.shipping.toFixed(2) }}</span>
                      </div>
                      <div v-if="order.summary.coupon > 0" class="summary-row">
                        <span>优惠券折扣</span>
                        <span>-¥{{ order.summary.coupon.toFixed(2) }}</span>
                      </div>
                      <div v-if="order.summary.discount > 0" class="summary-row">
                        <span>会员折扣</span>
                        <span>-¥{{ order.summary.discount.toFixed(2) }}</span>
                      </div>
                      <div class="summary-row total">
                        <span>实付金额</span>
                        <span>¥{{ order.summary.total.toFixed(2) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
        </div>

        <!-- 购物车 -->
        <div class="content-section" v-show="activeTab === 'cart'">
          <div class="content-header">
            <h2>我的购物车</h2>
            <p>已选择 {{ selectedCount }} 件商品</p>
            <!-- 商品搜索框 -->
            <div class="cart-search">
              <input
                type="text"
                v-model="cartSearchKeyword"
                placeholder="搜索购物车商品..."
                class="search-input"
                @input="filterCartItems"
              >
              <Icon icon="mdi:magnify" class="search-icon" />
            </div>
          </div>

            <!-- 购物车内容 -->
            <div v-if="loading" class="loading-state">
              <div class="loading-spinner">
                <Icon icon="mdi:loading" class="spin" />
                <span>加载中...</span>
              </div>
            </div>
            <div v-else-if="cartItems.length === 0" class="empty-state">
              <div class="empty-icon">
                <Icon icon="mdi:cart" />
              </div>
              <div class="empty-title">购物车是空的</div>
              <div class="empty-description">您还没有添加任何商品到购物车</div>
              <button class="empty-action" @click="continueShopping">
                <Icon icon="mdi:shopping" /> 去首页逛逛
              </button>
            </div>
            <div v-else class="cart-content">
              <!-- 购物车商品列表 -->
              <div class="cart-items-card">
                <!-- 全选控制栏 -->
                <div class="cart-select-all">
                  <input
                    type="checkbox"
                    :checked="selectAll"
                    @change="toggleSelectAll"
                    class="cart-checkbox"
                  >
                  <span class="select-all-text">全选</span>
                  <span class="selected-info">已选 {{ selectedCount }} 件商品</span>
                </div>

                <div v-for="item in paginatedCartItems" :key="item.id" class="cart-item">
                  <input
                    type="checkbox"
                    :checked="item.selected"
                    @change="toggleItemSelect(item)"
                    class="cart-checkbox"
                  >
                  <div class="cart-item-image">
                    <img v-if="item.image" :src="item.image" :alt="item.name">
                    <Icon v-else icon="mdi:image" />
                  </div>
                  <div class="cart-item-details">
                    <h4>{{ item.name }}</h4>
                    <p>{{ item.spec }}</p>
                  </div>
                  <div class="cart-item-controls">
                    <div class="quantity-control">
                      <button class="quantity-btn" @click="decreaseQuantity(item)">-</button>
                      <input type="number" v-model.number="item.quantity" @change="updateQuantity(item)" min="1" max="10" class="quantity-input">
                      <button class="quantity-btn" @click="increaseQuantity(item)">+</button>
                    </div>
                    <div class="cart-item-price">¥{{ item.price.toFixed(2) }}</div>
                    <button class="cart-item-remove" @click="removeFromCart(item)">
                      <Icon icon="mdi:delete" />
                    </button>
                  </div>
                </div>

                <!-- 分页控制移到商品列表下方 -->
                <div v-if="filteredCartItems.length > cartPageSize" class="cart-pagination-bottom">
                  <div class="pagination-info">
                    <span>共 {{ filteredCartItems.length }} 件商品，第 {{ cartCurrentPage }} / {{ cartTotalPages }} 页</span>
                  </div>
                  <div class="pagination-controls">
                    <button
                      @click="prevCartPage"
                      :disabled="cartCurrentPage === 1"
                      class="page-btn"
                    >
                      <Icon icon="mdi:chevron-left" />
                      上一页
                    </button>
                    <span class="page-numbers">
                      <button
                        v-for="page in getPageNumbers"
                        :key="page"
                        @click="goToCartPage(page)"
                        :class="['page-number', { active: page === cartCurrentPage }]"
                      >
                        {{ page }}
                      </button>
                    </span>
                    <button
                      @click="nextCartPage"
                      :disabled="cartCurrentPage === cartTotalPages"
                      class="page-btn"
                    >
                      下一页
                      <Icon icon="mdi:chevron-right" />
                    </button>
                  </div>
                </div>
              </div>

              <!-- 购物车汇总 -->
              <div class="cart-summary-card">
                <div class="summary-header">
                  <h3>订单总计</h3>
                  <div class="summary-total-price">¥{{ cartTotal.toFixed(2) }}</div>
                </div>

                <div class="summary-details">
                  <div class="summary-row">
                    <span>商品总价</span>
                    <span>¥{{ cartSubtotal.toFixed(2) }}</span>
                  </div>
                  <div class="summary-row">
                    <span>运费</span>
                    <span>¥0.00</span>
                  </div>
                  <div v-if="promoDiscount > 0" class="summary-row">
                    <span>优惠折扣</span>
                    <span>-¥{{ promoDiscount.toFixed(2) }}</span>
                  </div>
                </div>

                <!-- 优惠券 -->
                <div class="promo-section">
                  <h4><Icon icon="mdi:tag" /> 使用优惠券</h4>
                  <div class="promo-input">
                    <input type="text" v-model="promoCode" placeholder="输入优惠券代码">
                    <button @click="applyPromo">应用</button>
                  </div>
                  <div v-if="promoMessage" class="promo-message" :class="promoMessageType">
                    {{ promoMessage }}
                  </div>
                </div>

                <!-- 结算按钮 -->
                <div class="cart-actions">
                  <button class="cart-action-btn continue-shopping" @click="continueShopping">
                    <Icon icon="mdi:shopping" /> 继续购物
                  </button>
                  <button class="cart-action-btn checkout-btn" @click="checkout">
                    <Icon icon="mdi:credit-card" /> 去结算 (¥{{ cartTotal.toFixed(2) }})
                  </button>
                </div>
              </div>
            </div>
        </div>

        <!-- 我的收藏 -->
        <div class="content-section" v-show="activeTab === 'favorites'">
          <div class="content-header">
            <h2>我的收藏</h2>
            <p>您收藏的商品和服务，共 {{ favorites.length }} 件</p>
            <!-- 收藏搜索框 -->
            <div class="favorites-search">
              <input
                type="text"
                v-model="favoritesSearchKeyword"
                placeholder="搜索收藏的商品..."
                class="search-input"
                @input="filterFavorites"
              >
              <Icon icon="mdi:magnify" class="search-icon" />
            </div>
          </div>

          <div v-if="loading" class="loading-state">
            <div class="loading-spinner">
              <Icon icon="mdi:loading" class="spin" />
              <span>加载中...</span>
            </div>
          </div>

          <div v-else-if="filteredFavorites.length === 0" class="empty-state">
            <div class="empty-icon">
              <Icon icon="mdi:heart" />
            </div>
            <div class="empty-title">暂无收藏</div>
            <div class="empty-description">您还没有收藏任何商品</div>
            <button class="empty-action" @click="continueShopping">
              <Icon icon="mdi:shopping" /> 去逛逛
            </button>
          </div>

          <div v-else class="favorites-content">
            <!-- 分页控制 -->
            <div v-if="filteredFavorites.length > favoritesPageSize" class="favorites-pagination">
              <button
                @click="prevFavoritesPage"
                :disabled="favoritesCurrentPage === 1"
                class="page-btn"
              >
                <Icon icon="mdi:chevron-left" />
              </button>
              <span class="page-info">{{ favoritesCurrentPage }} / {{ favoritesTotalPages }}</span>
              <button
                @click="nextFavoritesPage"
                :disabled="favoritesCurrentPage === favoritesTotalPages"
                class="page-btn"
              >
                <Icon icon="mdi:chevron-right" />
              </button>
            </div>

            <!-- 收藏列表 - 详细信息展示 -->
            <div class="favorites-list">
              <div v-for="item in paginatedFavorites" :key="item.id" class="favorite-item">
                <!-- 商品图片 -->
                <div class="favorite-image">
                  <img v-if="item.image" :src="item.image" :alt="item.name">
                  <Icon v-else icon="mdi:image" />
                </div>

                <!-- 商品详细信息 -->
                <div class="favorite-details">
                  <div class="favorite-main-info">
                    <h4 class="favorite-name">{{ item.name }}</h4>
                    <p class="favorite-description" v-if="item.description">{{ item.description }}</p>
                  </div>

                  <div class="favorite-meta-info">
                    <div class="meta-row">
                      <div class="meta-item">
                        <span class="meta-label">商品类型：</span>
                        <span class="favorite-type">{{ item.type || '商品' }}</span>
                      </div>
                      <div class="meta-item">
                        <span class="meta-label">收藏时间：</span>
                        <span class="favorite-time">{{ formatDate(item.createdAt) }}</span>
                      </div>
                    </div>

                    <div class="meta-row">
                      <div class="meta-item">
                        <span class="meta-label">商品编号：</span>
                        <span class="product-id">#{{ item.id }}</span>
                      </div>
                      <div class="meta-item" v-if="item.brand">
                        <span class="meta-label">品牌：</span>
                        <span class="product-brand">{{ item.brand }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 价格和操作区域 -->
                <div class="favorite-actions-area">
                  <div class="favorite-price-section">
                    <div class="current-price">¥{{ (item.price || 0).toFixed(2) }}</div>
                    <div class="original-price" v-if="item.originalPrice && item.originalPrice > item.price">
                      ¥{{ item.originalPrice.toFixed(2) }}
                    </div>
                  </div>

                  <div class="favorite-actions">
                    <button class="btn-view-detail" @click="goToProduct(item.id)">
                      <Icon icon="mdi:eye" /> 查看详情
                    </button>
                    <button class="btn-add-cart" @click="addToCartFromFavorites(item)">
                      <Icon icon="mdi:cart-plus" /> 加入购物车
                    </button>
                    <button class="btn-remove-favorite" @click="removeFromFavorites(item.id)">
                      <Icon icon="mdi:heart-remove" /> 取消收藏
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 收货地址 -->
        <div class="content-section" v-show="activeTab === 'address'">
          <div class="content-header">
            <h2>收货地址</h2>
            <p>管理您的收货地址信息</p>
            <button class="action-btn action-btn-primary" @click="openAddDialog">
              <Icon icon="mdi:plus" /> 添加地址
            </button>
          </div>

          <!-- 地址列表 -->
          <div v-if="addresses.length === 0" class="empty-state">
            <div class="empty-icon">
              <Icon icon="mdi:map-marker" />
            </div>
            <div class="empty-title">暂无地址</div>
            <div class="empty-description">您还没有添加收货地址</div>
            <button class="empty-action" @click="openAddDialog">
              <Icon icon="mdi:plus" /> 添加地址
            </button>
          </div>

          <!-- 地址卡片列表 -->
          <div v-else class="address-list">
            <div v-for="address in addresses" :key="address.id" class="address-card">
              <div class="address-header">
                <div class="address-info">
                  <span class="recipient-name">{{ address.recipientName }}</span>
                  <span class="recipient-phone">{{ address.phone }}</span>
                  <span v-if="address.isDefault" class="default-tag">默认</span>
                </div>
                <div class="address-actions">
                  <button class="action-link" @click="openEditDialog(address)">
                    <Icon icon="mdi:pencil" /> 编辑
                  </button>
                  <button class="action-link danger" @click="confirmDeleteAddress(address)">
                    <Icon icon="mdi:delete" /> 删除
                  </button>
                </div>
              </div>
              <div class="address-detail">
                <Icon icon="mdi:map-marker" />
                <span>{{ address.fullAddress }}</span>
              </div>
              <div class="address-footer">
                <button
                  v-if="!address.isDefault"
                  class="set-default-btn"
                  @click="setDefaultAddress(address.id)"
                >
                  设为默认
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 我的预约 -->
        <div class="content-section" v-show="activeTab === 'appointments'">
          <div class="content-header">
            <h2>我的预约</h2>
            <p>查看您的服务预约记录</p>
            <button class="action-btn action-btn-primary" @click="openAppointmentDialog">
              <Icon icon="mdi:plus" /> 新建预约
            </button>
          </div>

          <!-- 预约状态筛选栏 -->
          <div class="appointment-filter-tabs">
            <div
              class="filter-tab"
              :class="{ active: appointmentFilter === 'all' }"
              @click="setAppointmentFilter('all')"
            >
              全部预约
            </div>
            <div
              class="filter-tab"
              :class="{ active: appointmentFilter === 'pending' }"
              @click="setAppointmentFilter('pending')"
            >
              待服务
              <span v-if="getPendingAppointmentCount() > 0" class="filter-badge">{{ getPendingAppointmentCount() }}</span>
            </div>
            <div
              class="filter-tab"
              :class="{ active: appointmentFilter === 'confirmed' }"
              @click="setAppointmentFilter('confirmed')"
            >
              已确认
            </div>
            <div
              class="filter-tab"
              :class="{ active: appointmentFilter === 'completed' }"
              @click="setAppointmentFilter('completed')"
            >
              已完成
            </div>
            <div
              class="filter-tab"
              :class="{ active: appointmentFilter === 'cancelled' }"
              @click="setAppointmentFilter('cancelled')"
            >
              已取消
            </div>
          </div>

          <div v-if="loading" class="loading-state">
            <div class="loading-spinner">
              <Icon icon="mdi:loading" class="spin" />
              <span>加载中...</span>
            </div>
          </div>

          <div v-else-if="filteredAppointments.length === 0" class="empty-state">
            <div class="empty-icon">
              <Icon icon="mdi:calendar-check" />
            </div>
            <div class="empty-title">暂无预约</div>
            <div class="empty-description">您还没有任何服务预约</div>
            <button class="empty-action" @click="openAppointmentDialog">
              <Icon icon="mdi:calendar-plus" /> 立即预约
            </button>
          </div>

          <div v-else class="appointments-list">
            <div v-for="appointment in filteredAppointments" :key="appointment.id" class="appointment-row">
              <!-- 左侧信息区 -->
              <div class="appointment-left">
                <div class="appointment-main-info">
                  <div class="service-name">{{ appointment.serviceName }}</div>
                  <div class="order-no">订单号：{{ appointment.orderNo }}</div>
                </div>
                <div class="appointment-meta">
                  <div class="meta-item">
                    <Icon icon="mdi:paw" />
                    <span>{{ appointment.petName }}</span>
                  </div>
                  <div class="meta-item">
                    <Icon icon="mdi:calendar" />
                    <span>{{ formatDate(appointment.appointmentDate) }}</span>
                  </div>
                  <div class="meta-item">
                    <Icon icon="mdi:clock" />
                    <span>{{ appointment.timeSlot }}</span>
                  </div>
                </div>
                <div v-if="appointment.specialNotes" class="special-notes">
                  <Icon icon="mdi:note-text" />
                  <span>{{ appointment.specialNotes }}</span>
                </div>
              </div>

              <!-- 中间状态区 -->
              <div class="appointment-center">
                <div class="appointment-status" :class="getStatusClass(appointment.status)">
                  {{ getStatusText(appointment.status) }}
                </div>
                <div class="appointment-price">¥{{ appointment.orderTotalAmount }}</div>
              </div>

              <!-- 右侧操作区 -->
              <div class="appointment-right">
                <div class="appointment-actions">
                  <button class="action-btn action-btn-secondary" @click="viewAppointmentDetail(appointment)">
                    <Icon icon="mdi:eye" /> 查看详情
                  </button>
                  <button
                    v-if="appointment.status === 'pending'"
                    class="action-btn action-btn-danger"
                    @click="cancelAppointment(appointment)"
                  >
                    <Icon icon="mdi:close" /> 取消预约
                  </button>
                  <button
                    v-if="appointment.status === 'completed'"
                    class="action-btn action-btn-primary"
                    @click="rebookAppointment(appointment)"
                  >
                    <Icon icon="mdi:refresh" /> 再次预约
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 账户设置 -->
        <div class="content-section" v-show="activeTab === 'settings'">
          <div class="content-header">
            <h2>账户设置</h2>
            <p>管理您的账户信息和偏好设置</p>
          </div>
          <div class="settings-content">
            <!-- 账户安全 -->
            <div class="setting-group">
              <h3><Icon icon="mdi:shield-account" /> 账户安全</h3>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">登录密码</span>
                  <span class="setting-desc">定期更换密码，保护账户安全</span>
                </div>
                <button class="setting-action-btn" @click="openPasswordDialog">
                  <Icon icon="mdi:key" /> 修改密码
                </button>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">手机号码</span>
                  <span class="setting-desc">{{ userInfo.phone || '未绑定' }}</span>
                </div>
                <button class="setting-action-btn" @click="openPhoneDialog">
                  <Icon icon="mdi:phone" /> {{ userInfo.phone ? '更换' : '绑定' }}
                </button>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">邮箱地址</span>
                  <span class="setting-desc">{{ userInfo.email || '未绑定' }}</span>
                </div>
                <button class="setting-action-btn" @click="openEmailDialog">
                  <Icon icon="mdi:email" /> {{ userInfo.email ? '更换' : '绑定' }}
                </button>
              </div>
            </div>

            <!-- 个人信息 -->
            <div class="setting-group">
              <h3><Icon icon="mdi:account-circle" /> 个人信息</h3>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">用户名</span>
                  <span class="setting-desc">{{ userInfo.name || '未设置' }}</span>
                </div>
                <button class="setting-action-btn" @click="openProfileEditDialog">
                  <Icon icon="mdi:pencil" /> 编辑
                </button>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">会员等级</span>
                  <span class="setting-desc">{{ userInfo.level || '普通会员' }}</span>
                </div>
                <button class="setting-action-btn" @click="viewMembershipBenefits">
                  <Icon icon="mdi:crown" /> 升级会员
                </button>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">积分余额</span>
                  <span class="setting-desc">{{ userInfo.points || 0 }} 积分</span>
                </div>
                <button class="setting-action-btn" @click="viewPointsHistory">
                  <Icon icon="mdi:star" /> 积分明细
                </button>
              </div>
            </div>

            <!-- 通知设置 -->
            <div class="setting-group">
              <h3><Icon icon="mdi:bell" /> 通知设置</h3>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">订单通知</span>
                  <span class="setting-desc">接收订单状态变更通知</span>
                </div>
                <label class="switch">
                  <input type="checkbox" v-model="notificationSettings.orderNotification">
                  <span class="slider"></span>
                </label>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">促销通知</span>
                  <span class="setting-desc">接收优惠活动和促销信息</span>
                </div>
                <label class="switch">
                  <input type="checkbox" v-model="notificationSettings.promotionNotification">
                  <span class="slider"></span>
                </label>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">预约提醒</span>
                  <span class="setting-desc">服务预约前24小时提醒</span>
                </div>
                <label class="switch">
                  <input type="checkbox" v-model="notificationSettings.appointmentReminder">
                  <span class="slider"></span>
                </label>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">邮件通知</span>
                  <span class="setting-desc">通过邮件接收重要通知</span>
                </div>
                <label class="switch">
                  <input type="checkbox" v-model="notificationSettings.emailNotification">
                  <span class="slider"></span>
                </label>
              </div>
            </div>

            <!-- 隐私设置 -->
            <div class="setting-group">
              <h3><Icon icon="mdi:shield-lock" /> 隐私设置</h3>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">个人资料可见性</span>
                  <span class="setting-desc">控制其他用户查看您的个人信息</span>
                </div>
                <select v-model="privacySettings.profileVisibility" class="setting-select">
                  <option value="public">公开</option>
                  <option value="friends">仅好友</option>
                  <option value="private">私密</option>
                </select>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">购买记录</span>
                  <span class="setting-desc">是否允许显示购买历史</span>
                </div>
                <label class="switch">
                  <input type="checkbox" v-model="privacySettings.showPurchaseHistory">
                  <span class="slider"></span>
                </label>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">数据分析</span>
                  <span class="setting-desc">允许使用数据改善服务体验</span>
                </div>
                <label class="switch">
                  <input type="checkbox" v-model="privacySettings.allowDataAnalysis">
                  <span class="slider"></span>
                </label>
              </div>
            </div>

            <!-- 商业化设置 -->
            <div class="setting-group">
              <h3><Icon icon="mdi:store" /> 商业化服务</h3>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">VIP会员服务</span>
                  <span class="setting-desc">享受专属优惠和优先服务</span>
                </div>
                <button class="setting-action-btn premium" @click="upgradeToVip">
                  <Icon icon="mdi:crown" /> 升级VIP
                </button>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">宠物保险</span>
                  <span class="setting-desc">为您的爱宠提供全面保障</span>
                </div>
                <button class="setting-action-btn" @click="viewInsuranceOptions">
                  <Icon icon="mdi:shield-heart" /> 了解详情
                </button>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">定制服务</span>
                  <span class="setting-desc">个性化宠物护理方案</span>
                </div>
                <button class="setting-action-btn" @click="viewCustomServices">
                  <Icon icon="mdi:palette" /> 定制方案
                </button>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">推荐奖励</span>
                  <span class="setting-desc">邀请好友注册获得奖励</span>
                </div>
                <button class="setting-action-btn" @click="viewReferralProgram">
                  <Icon icon="mdi:gift" /> 邀请好友
                </button>
              </div>
            </div>

            <!-- 其他设置 -->
            <div class="setting-group">
              <h3><Icon icon="mdi:cog" /> 其他设置</h3>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">语言设置</span>
                  <span class="setting-desc">选择界面显示语言</span>
                </div>
                <select v-model="otherSettings.language" class="setting-select">
                  <option value="zh-CN">简体中文</option>
                  <option value="zh-TW">繁體中文</option>
                  <option value="en-US">English</option>
                </select>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">主题模式</span>
                  <span class="setting-desc">选择浅色或深色主题</span>
                </div>
                <select v-model="otherSettings.theme" class="setting-select">
                  <option value="light">浅色模式</option>
                  <option value="dark">深色模式</option>
                  <option value="auto">跟随系统</option>
                </select>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">自动登录</span>
                  <span class="setting-desc">下次访问时自动登录</span>
                </div>
                <label class="switch">
                  <input type="checkbox" v-model="otherSettings.autoLogin">
                  <span class="slider"></span>
                </label>
              </div>
            </div>

            <!-- 危险操作 -->
            <div class="setting-group danger-zone">
              <h3><Icon icon="mdi:alert" /> 危险操作</h3>
              <div class="setting-item">
                <div class="setting-info">
                  <span class="setting-title">注销账户</span>
                  <span class="setting-desc">永久删除账户和所有数据，此操作不可恢复</span>
                </div>
                <button class="setting-action-btn danger" @click="confirmDeleteAccount">
                  <Icon icon="mdi:delete-forever" /> 注销账户
                </button>
              </div>
            </div>

            <!-- 保存按钮 -->
            <div class="settings-actions">
              <button class="save-settings-btn" @click="saveSettings">
                <Icon icon="mdi:content-save" /> 保存设置
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址对话框 -->
    <div v-if="showAddressDialog" class="dialog-overlay" @click="closeAddressDialog">
      <div class="dialog-container" @click.stop>
        <div class="dialog-header">
          <h3>{{ editingAddress ? '编辑地址' : '添加地址' }}</h3>
          <button class="dialog-close-btn" @click="closeAddressDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>

        <form class="dialog-form" @submit.prevent="saveAddress">
          <div class="form-row">
            <div class="form-group">
              <label>收货人姓名 <span class="required">*</span></label>
              <input
                type="text"
                v-model="addressForm.recipientName"
                placeholder="请输入收货人姓名"
                required
              >
            </div>
            <div class="form-group">
              <label>手机号码 <span class="required">*</span></label>
              <input
                type="tel"
                v-model="addressForm.phone"
                placeholder="请输入手机号码"
                required
              >
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>省份 <span class="required">*</span></label>
              <select v-model="addressForm.province" @change="onProvinceChange" required>
                <option value="">请选择省份</option>
                <option value="北京市">北京市</option>
                <option value="上海市">上海市</option>
                <option value="广东省">广东省</option>
                <option value="浙江省">浙江省</option>
                <option value="江苏省">江苏省</option>
              </select>
            </div>
            <div class="form-group">
              <label>城市 <span class="required">*</span></label>
              <select v-model="addressForm.city" @change="onCityChange" required>
                <option value="">请选择城市</option>
                <option v-for="city in availableCities" :key="city" :value="city">{{ city }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>区县 <span class="required">*</span></label>
              <select v-model="addressForm.district" required>
                <option value="">请选择区县</option>
                <option v-for="district in availableDistricts" :key="district" :value="district">{{ district }}</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label>详细地址 <span class="required">*</span></label>
            <textarea
              v-model="addressForm.detailAddress"
              placeholder="请输入详细地址，如街道、门牌号等"
              rows="3"
              required
            ></textarea>
          </div>

          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="addressForm.isDefault">
              <span class="checkmark"></span>
              设为默认地址
            </label>
          </div>

          <div class="dialog-actions">
            <button type="button" class="btn-cancel" @click="closeAddressDialog">取消</button>
            <button type="submit" class="btn-save">{{ editingAddress ? '保存修改' : '保存地址' }}</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteDialog" class="dialog-overlay" @click="closeDeleteDialog">
      <div class="dialog-container small" @click.stop>
        <div class="dialog-header">
          <h3>确认删除</h3>
          <button class="dialog-close-btn" @click="closeDeleteDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>
        <div class="dialog-content">
          <p>确定要删除这个收货地址吗？</p>
          <div class="delete-address-info">
            <strong>{{ deletingAddress?.recipientName }}</strong> {{ deletingAddress?.phone }}<br>
            {{ deletingAddress?.fullAddress }}
          </div>
        </div>
        <div class="dialog-actions">
          <button type="button" class="btn-cancel" @click="closeDeleteDialog">取消</button>
          <button type="button" class="btn-danger" @click="confirmDelete">确认删除</button>
        </div>
      </div>
    </div>

    <!-- 预约对话框 -->
    <div v-if="showAppointmentDialog" class="dialog-overlay" @click="closeAppointmentDialog">
      <div class="dialog-container" @click.stop>
        <div class="dialog-header">
          <h3>新建预约</h3>
          <button class="dialog-close-btn" @click="closeAppointmentDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>

        <form class="dialog-form" @submit.prevent="submitAppointment">
          <div class="form-group">
            <label>选择宠物 <span class="required">*</span></label>
            <select v-model="appointmentForm.petId" required>
              <option value="">请选择宠物</option>
              <option v-for="pet in pets" :key="pet.id" :value="pet.id">
                {{ pet.name }} ({{ pet.breed || '未知品种' }})
              </option>
            </select>
            <div v-if="pets.length === 0" class="no-pets-tip">
              <Icon icon="mdi:information" />
              <span>暂无宠物信息，请先到个人资料页面添加宠物</span>
              <button type="button" class="btn-link" @click="goToProfile">
                去添加宠物
              </button>
            </div>
          </div>

          <div class="form-group">
            <label>选择服务 <span class="required">*</span></label>
            <select v-model="appointmentForm.serviceId" required>
              <option value="">请选择服务</option>
              <option v-for="service in services" :key="service.id" :value="service.id">
                {{ service.itemName }} - ¥{{ service.finalPrice }}
              </option>
            </select>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>预约日期 <span class="required">*</span></label>
              <input
                type="date"
                v-model="appointmentForm.appointmentDate"
                :min="new Date().toISOString().split('T')[0]"
                required
              >
            </div>
            <div class="form-group">
              <label>预约时间 <span class="required">*</span></label>
              <select v-model="appointmentForm.timeSlot" required>
                <option value="">请选择时间</option>
                <option v-for="slot in timeSlots" :key="slot" :value="slot">
                  {{ slot }}
                </option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label>特殊要求</label>
            <textarea
              v-model="appointmentForm.specialNotes"
              placeholder="请告知特殊要求，如对某些用品过敏等"
              rows="3"
            ></textarea>
          </div>

          <div class="dialog-actions">
            <button type="button" class="btn-cancel" @click="closeAppointmentDialog">取消</button>
            <button type="submit" class="btn-save">确认预约</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 个人信息编辑对话框 -->
    <div v-if="showProfileEditDialog" class="dialog-overlay" @click="closeProfileEditDialog">
      <div class="dialog-container" @click.stop>
        <div class="dialog-header">
          <h3>编辑个人信息</h3>
          <button class="dialog-close-btn" @click="closeProfileEditDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>

        <form class="dialog-form" @submit.prevent="saveProfile">
          <div class="form-group">
            <label>用户名 <span class="required">*</span></label>
            <input
              type="text"
              v-model="profileEditForm.name"
              placeholder="请输入用户名"
              required
            >
          </div>

          <div class="form-group">
            <label>昵称</label>
            <input
              type="text"
              v-model="profileEditForm.nickname"
              placeholder="请输入昵称"
            >
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>性别</label>
              <select v-model="profileEditForm.gender">
                <option value="">请选择</option>
                <option value="male">男</option>
                <option value="female">女</option>
                <option value="other">其他</option>
              </select>
            </div>
            <div class="form-group">
              <label>生日</label>
              <input
                type="date"
                v-model="profileEditForm.birthday"
              >
            </div>
          </div>

          <div class="form-group">
            <label>个人简介</label>
            <textarea
              v-model="profileEditForm.bio"
              placeholder="介绍一下自己吧..."
              rows="3"
            ></textarea>
          </div>

          <div class="dialog-actions">
            <button type="button" class="btn-cancel" @click="closeProfileEditDialog">取消</button>
            <button type="submit" class="btn-save">保存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <div v-if="showPasswordDialog" class="dialog-overlay" @click="closePasswordDialog">
      <div class="dialog-container" @click.stop>
        <div class="dialog-header">
          <h3>修改密码</h3>
          <button class="dialog-close-btn" @click="closePasswordDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>

        <form class="dialog-form" @submit.prevent="changePassword">
          <div class="form-group">
            <label>当前密码 <span class="required">*</span></label>
            <input
              type="password"
              v-model="passwordForm.currentPassword"
              placeholder="请输入当前密码"
              required
            >
          </div>

          <div class="form-group">
            <label>新密码 <span class="required">*</span></label>
            <input
              type="password"
              v-model="passwordForm.newPassword"
              placeholder="请输入新密码（至少6位）"
              required
            >
          </div>

          <div class="form-group">
            <label>确认新密码 <span class="required">*</span></label>
            <input
              type="password"
              v-model="passwordForm.confirmPassword"
              placeholder="请再次输入新密码"
              required
            >
          </div>

          <div class="dialog-actions">
            <button type="button" class="btn-cancel" @click="closePasswordDialog">取消</button>
            <button type="submit" class="btn-save">确认修改</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 绑定/更换手机号对话框 -->
    <div v-if="showPhoneDialog" class="dialog-overlay" @click="closePhoneDialog">
      <div class="dialog-container" @click.stop>
        <div class="dialog-header">
          <h3>{{ userInfo.phone ? '更换手机号' : '绑定手机号' }}</h3>
          <button class="dialog-close-btn" @click="closePhoneDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>

        <form class="dialog-form" @submit.prevent="changePhone">
          <div class="form-group">
            <label>手机号码 <span class="required">*</span></label>
            <input
              type="tel"
              v-model="phoneForm.phone"
              placeholder="请输入手机号码"
              required
            >
          </div>

          <div class="form-group">
            <label>验证码 <span class="required">*</span></label>
            <div class="verification-input">
              <input
                type="text"
                v-model="phoneForm.verificationCode"
                placeholder="请输入验证码"
                required
              >
              <button type="button" class="send-code-btn" @click="sendPhoneCode">
                发送验证码
              </button>
            </div>
          </div>

          <div class="dialog-actions">
            <button type="button" class="btn-cancel" @click="closePhoneDialog">取消</button>
            <button type="submit" class="btn-save">确认绑定</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 绑定/更换邮箱对话框 -->
    <div v-if="showEmailDialog" class="dialog-overlay" @click="closeEmailDialog">
      <div class="dialog-container" @click.stop>
        <div class="dialog-header">
          <h3>{{ userInfo.email ? '更换邮箱' : '绑定邮箱' }}</h3>
          <button class="dialog-close-btn" @click="closeEmailDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>

        <form class="dialog-form" @submit.prevent="changeEmail">
          <div class="form-group">
            <label>邮箱地址 <span class="required">*</span></label>
            <input
              type="email"
              v-model="emailForm.email"
              placeholder="请输入邮箱地址"
              required
            >
          </div>

          <div class="form-group">
            <label>验证码 <span class="required">*</span></label>
            <div class="verification-input">
              <input
                type="text"
                v-model="emailForm.verificationCode"
                placeholder="请输入验证码"
                required
              >
              <button type="button" class="send-code-btn" @click="sendEmailCode">
                发送验证码
              </button>
            </div>
          </div>

          <div class="dialog-actions">
            <button type="button" class="btn-cancel" @click="closeEmailDialog">取消</button>
            <button type="submit" class="btn-save">确认绑定</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 支付对话框 -->
    <div v-if="showPaymentDialog" class="dialog-overlay payment-overlay" @click="closePaymentDialog">
      <div class="dialog-container payment-dialog compact-payment" @click.stop>
        <div class="dialog-header">
          <h3>支付订单</h3>
          <button class="dialog-close-btn" @click="closePaymentDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>

        <div class="payment-content compact-content">
          <!-- 订单信息和支付金额 -->
          <div class="payment-summary">
            <div class="order-info">
              <span class="order-label">订单号：</span>
              <span class="order-number">{{ currentPaymentOrder?.orderNo || currentPaymentOrder?.id }}</span>
            </div>
            <div class="payment-amount">
              <span class="amount-label">支付金额</span>
              <span class="amount-value">¥{{ (currentPaymentOrder?.finalAmount || currentPaymentOrder?.total || 0).toFixed(2) }}</span>
            </div>
          </div>

          <!-- 支付方式和二维码在同一行 -->
          <div class="payment-main" v-if="paymentStatus === 'waiting'">
            <!-- 左侧：支付方式选择 -->
            <div class="payment-methods-section">
              <h4>支付方式</h4>
              <div class="payment-options compact-options">
                <label class="payment-option compact-option" :class="{ active: selectedPaymentMethod === 'wechat' }">
                  <input type="radio" v-model="selectedPaymentMethod" value="wechat">
                  <div class="payment-icon wechat">
                    <Icon icon="mdi:wechat" />
                  </div>
                  <span>微信支付</span>
                </label>

                <label class="payment-option compact-option" :class="{ active: selectedPaymentMethod === 'alipay' }">
                  <input type="radio" v-model="selectedPaymentMethod" value="alipay">
                  <div class="payment-icon alipay">
                    <Icon icon="simple-icons:alipay" />
                  </div>
                  <span>支付宝</span>
                </label>

                <label class="payment-option compact-option" :class="{ active: selectedPaymentMethod === 'unionpay' }">
                  <input type="radio" v-model="selectedPaymentMethod" value="unionpay">
                  <div class="payment-icon unionpay">
                    <Icon icon="mdi:credit-card" />
                  </div>
                  <span>银联支付</span>
                </label>
              </div>
            </div>

            <!-- 右侧：二维码和说明 -->
            <div class="qr-code-section compact-qr">
              <div class="qr-code">
                <Icon icon="mdi:qrcode" />
              </div>
              <p class="qr-instruction">
                使用{{ getPaymentMethodName(selectedPaymentMethod) }}扫码支付
              </p>
              <div class="payment-timer compact-timer" :class="{ 'timer-warning': paymentTimeLeft <= 180 }">
                <Icon icon="mdi:clock-outline" />
                <span>{{ formatPaymentTime(paymentTimeLeft) }}</span>
                <span v-if="paymentTimeLeft <= 180" class="warning-text">即将超时</span>
              </div>
            </div>
          </div>

          <!-- 支付成功状态 -->
          <div v-if="paymentStatus === 'success'" class="payment-success">
            <div class="success-icon">
              <Icon icon="mdi:check-circle" />
            </div>
            <h4>支付成功！</h4>
            <p>订单已生成，我们将尽快为您处理</p>
          </div>

          <!-- 支付提示 -->
          <div class="payment-tips compact-tips" v-if="paymentStatus === 'waiting'">
            <div class="tips-content">
              <Icon icon="mdi:information-outline" />
              <span>请在剩余时间内完成支付，支付完成后点击"确认已支付"</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="payment-actions compact-actions">
            <button
              v-if="paymentStatus === 'waiting'"
              class="btn-confirm-payment"
              @click="confirmPayment"
              :disabled="!selectedPaymentMethod"
            >
              <Icon icon="mdi:check" />
              确认已支付
            </button>

            <button
              v-if="paymentStatus === 'success'"
              class="btn-view-order"
              @click="closePaymentDialog"
            >
              <Icon icon="mdi:eye" />
              查看订单
            </button>

            <button
              v-if="paymentStatus === 'waiting'"
              class="btn-cancel-payment"
              @click="cancelPayment"
            >
              <Icon icon="mdi:close" />
              取消支付
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getUserOrders, cancelOrder } from '@/api/order'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { getAddresses, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'

export default {
  name: 'UserCenterView',
  setup() {
    const cartStore = useCartStore()
    const userStore = useUserStore()
    return {
      cartStore,
      userStore
    }
  },
  data() {
    return {
      activeTab: 'orders',
      orderFilter: 'all', // 订单筛选状态
      appointmentFilter: 'all', // 预约筛选状态
      userInfo: {
        name: '',
        level: '',
        phone: '',
        email: '',
        points: 0
      },
      orders: [],
      loading: false,
      promoCode: '',
      promoDiscount: 0,
      promoMessage: '',
      promoMessageType: 'info',

      // 支付相关
      showPaymentDialog: false,
      paymentStatus: 'waiting', // 'waiting', 'success'
      selectedPaymentMethod: 'wechat', // 'wechat', 'alipay', 'unionpay'
      paymentTimeLeft: 600, // 10分钟倒计时（秒）
      paymentTimer: null,
      currentPaymentOrder: null,
      globalTimer: null, // 全局倒计时更新器

      // 购物车搜索和分页
      cartSearchKeyword: '',
      cartCurrentPage: 1,
      cartPageSize: 5,
      // 收藏相关数据
      favorites: [],
      favoritesSearchKeyword: '',
      favoritesCurrentPage: 1,
      favoritesPageSize: 8,
      // 预约相关数据
      appointments: [],
      showAppointmentDialog: false,
      selectedAppointment: null,
      appointmentForm: {
        petId: '',
        serviceId: '',
        appointmentDate: '',
        timeSlot: '',
        specialNotes: ''
      },
      pets: [],
      services: [],
      timeSlots: ['09:00', '10:30', '14:00', '15:30', '17:00'],
      availableTimeSlots: [],
      // 地址管理相关数据
      addresses: [],
      showAddressDialog: false,
      showDeleteDialog: false,
      editingAddress: null,
      deletingAddress: null,
      addressForm: {
        recipientName: '',
        phone: '',
        province: '',
        city: '',
        district: '',
        detailAddress: '',
        isDefault: false
      },
      // 地区数据
      cityData: {
        '北京市': ['北京市'],
        '上海市': ['上海市'],
        '天津市': ['天津市'],
        '重庆市': ['重庆市'],
        '广东省': ['广州市', '深圳市', '珠海市', '佛山市', '东莞市', '中山市', '惠州市'],
        '浙江省': ['杭州市', '宁波市', '温州市', '嘉兴市', '湖州市', '绍兴市', '金华市'],
        '江苏省': ['南京市', '苏州市', '无锡市', '常州市', '南通市', '徐州市', '扬州市'],
        '山东省': ['济南市', '青岛市', '烟台市', '潍坊市', '临沂市', '淄博市', '济宁市'],
        '河南省': ['郑州市', '洛阳市', '开封市', '南阳市', '安阳市', '新乡市', '平顶山市'],
        '湖北省': ['武汉市', '宜昌市', '襄阳市', '荆州市', '黄石市', '十堰市', '孝感市'],
        '湖南省': ['长沙市', '株洲市', '湘潭市', '衡阳市', '邵阳市', '岳阳市', '常德市'],
        '四川省': ['成都市', '绵阳市', '德阳市', '南充市', '宜宾市', '自贡市', '乐山市'],
        '福建省': ['福州市', '厦门市', '泉州市', '漳州市', '莆田市', '三明市', '龙岩市'],
        '安徽省': ['合肥市', '芜湖市', '蚌埠市', '淮南市', '马鞍山市', '淮北市', '铜陵市'],
        '江西省': ['南昌市', '九江市', '景德镇市', '萍乡市', '新余市', '鹰潭市', '赣州市'],
        '河北省': ['石家庄市', '唐山市', '秦皇岛市', '邯郸市', '邢台市', '保定市', '张家口市'],
        '山西省': ['太原市', '大同市', '阳泉市', '长治市', '晋城市', '朔州市', '晋中市'],
        '辽宁省': ['沈阳市', '大连市', '鞍山市', '抚顺市', '本溪市', '丹东市', '锦州市'],
        '吉林省': ['长春市', '吉林市', '四平市', '辽源市', '通化市', '白山市', '松原市'],
        '黑龙江省': ['哈尔滨市', '齐齐哈尔市', '鸡西市', '鹤岗市', '双鸭山市', '大庆市', '伊春市'],
        '陕西省': ['西安市', '铜川市', '宝鸡市', '咸阳市', '渭南市', '延安市', '汉中市'],
        '甘肃省': ['兰州市', '嘉峪关市', '金昌市', '白银市', '天水市', '武威市', '张掖市'],
        '青海省': ['西宁市', '海东市'],
        '宁夏回族自治区': ['银川市', '石嘴山市', '吴忠市', '固原市', '中卫市'],
        '新疆维吾尔自治区': ['乌鲁木齐市', '克拉玛依市', '吐鲁番市', '哈密市'],
        '西藏自治区': ['拉萨市', '日喀则市', '昌都市', '林芝市'],
        '内蒙古自治区': ['呼和浩特市', '包头市', '乌海市', '赤峰市', '通辽市', '鄂尔多斯市'],
        '广西壮族自治区': ['南宁市', '柳州市', '桂林市', '梧州市', '北海市', '防城港市', '钦州市'],
        '云南省': ['昆明市', '曲靖市', '玉溪市', '保山市', '昭通市', '丽江市', '普洱市'],
        '贵州省': ['贵阳市', '六盘水市', '遵义市', '安顺市', '毕节市', '铜仁市'],
        '海南省': ['海口市', '三亚市', '三沙市', '儋州市']
      },
      districtData: {
        '北京市': ['东城区', '西城区', '朝阳区', '海淀区', '丰台区', '石景山区', '门头沟区', '房山区', '通州区', '顺义区', '昌平区', '大兴区', '怀柔区', '平谷区', '密云区', '延庆区'],
        '上海市': ['黄浦区', '徐汇区', '长宁区', '静安区', '普陀区', '虹口区', '杨浦区', '闵行区', '宝山区', '嘉定区', '浦东新区', '金山区', '松江区', '青浦区', '奉贤区', '崇明区'],
        '天津市': ['和平区', '河东区', '河西区', '南开区', '河北区', '红桥区', '东丽区', '西青区', '津南区', '北辰区', '武清区', '宝坻区', '滨海新区', '宁河区', '静海区', '蓟州区'],
        '重庆市': ['万州区', '涪陵区', '渝中区', '大渡口区', '江北区', '沙坪坝区', '九龙坡区', '南岸区', '北碚区', '綦江区', '大足区', '渝北区', '巴南区', '黔江区', '长寿区', '江津区'],
        '广州市': ['天河区', '越秀区', '荔湾区', '海珠区', '白云区', '黄埔区', '番禺区', '花都区', '南沙区', '从化区', '增城区'],
        '深圳市': ['福田区', '罗湖区', '南山区', '宝安区', '龙岗区', '盐田区', '龙华区', '坪山区', '光明区', '大鹏新区'],
        '杭州市': ['西湖区', '拱墅区', '江干区', '钱塘区', '上城区', '下城区', '滨江区', '余杭区', '萧山区', '富阳区', '临安区', '桐庐县', '淳安县', '建德市'],
        '宁波市': ['海曙区', '江北区', '鄞州区', '镇海区', '北仑区', '奉化区', '象山县', '宁海县', '余姚市', '慈溪市'],
        '南京市': ['玄武区', '秦淮区', '建邺区', '鼓楼区', '浦口区', '栖霞区', '雨花台区', '江宁区', '六合区', '溧水区', '高淳区'],
        '苏州市': ['虎丘区', '吴中区', '相城区', '姑苏区', '吴江区', '常熟市', '张家港市', '昆山市', '太仓市'],
        '济南市': ['历下区', '市中区', '槐荫区', '天桥区', '历城区', '长清区', '章丘区', '济阳区', '莱芜区', '钢城区', '平阴县', '商河县'],
        '青岛市': ['市南区', '市北区', '黄岛区', '崂山区', '李沧区', '城阳区', '即墨区', '胶州市', '平度市', '莱西市'],
        '郑州市': ['中原区', '二七区', '管城区', '金水区', '上街区', '惠济区', '中牟县', '巩义市', '荥阳市', '新密市', '新郑市', '登封市'],
        '武汉市': ['江岸区', '江汉区', '硚口区', '汉阳区', '武昌区', '青山区', '洪山区', '东西湖区', '汉南区', '蔡甸区', '江夏区', '黄陂区', '新洲区'],
        '长沙市': ['芙蓉区', '天心区', '岳麓区', '开福区', '雨花区', '望城区', '长沙县', '浏阳市', '宁乡市'],
        '成都市': ['锦江区', '青羊区', '金牛区', '武侯区', '成华区', '龙泉驿区', '青白江区', '新都区', '温江区', '双流区', '郫都区', '新津区', '金堂县', '大邑县', '蒲江县', '都江堰市', '彭州市', '邛崃市', '崇州市', '简阳市'],
        '福州市': ['鼓楼区', '台江区', '仓山区', '马尾区', '晋安区', '长乐区', '闽侯县', '连江县', '罗源县', '闽清县', '永泰县', '平潭县', '福清市'],
        '厦门市': ['思明区', '海沧区', '湖里区', '集美区', '同安区', '翔安区'],
        '合肥市': ['瑶海区', '庐阳区', '蜀山区', '包河区', '长丰县', '肥东县', '肥西县', '庐江县', '巢湖市'],
        '南昌市': ['东湖区', '西湖区', '青云谱区', '湾里区', '青山湖区', '新建区', '南昌县', '安义县', '进贤县'],
        '石家庄市': ['长安区', '桥西区', '新华区', '井陉矿区', '裕华区', '藁城区', '鹿泉区', '栾城区', '井陉县', '正定县', '行唐县', '灵寿县', '高邑县', '深泽县', '赞皇县', '无极县', '平山县', '元氏县', '赵县', '辛集市', '晋州市', '新乐市'],
        '太原市': ['小店区', '迎泽区', '杏花岭区', '尖草坪区', '万柏林区', '晋源区', '清徐县', '阳曲县', '娄烦县', '古交市'],
        '沈阳市': ['和平区', '沈河区', '大东区', '皇姑区', '铁西区', '苏家屯区', '浑南区', '沈北新区', '于洪区', '辽中区', '康平县', '法库县', '新民市'],
        '大连市': ['中山区', '西岗区', '沙河口区', '甘井子区', '旅顺口区', '金州区', '普兰店区', '长海县', '瓦房店市', '庄河市'],
        '长春市': ['南关区', '宽城区', '朝阳区', '二道区', '绿园区', '双阳区', '九台区', '农安县', '榆树市', '德惠市'],
        '哈尔滨市': ['道里区', '南岗区', '道外区', '平房区', '松北区', '香坊区', '呼兰区', '阿城区', '双城区', '依兰县', '方正县', '宾县', '巴彦县', '木兰县', '通河县', '延寿县', '尚志市', '五常市'],
        '西安市': ['新城区', '碑林区', '莲湖区', '灞桥区', '未央区', '雁塔区', '阎良区', '临潼区', '长安区', '高陵区', '鄠邑区', '蓝田县', '周至县'],
        '兰州市': ['城关区', '七里河区', '西固区', '安宁区', '红古区', '永登县', '皋兰县', '榆中县'],
        '西宁市': ['城东区', '城中区', '城西区', '城北区', '大通县', '湟中区', '湟源县'],
        '银川市': ['兴庆区', '西夏区', '金凤区', '永宁县', '贺兰县', '灵武市'],
        '乌鲁木齐市': ['天山区', '沙依巴克区', '新市区', '水磨沟区', '头屯河区', '达坂城区', '米东区', '乌鲁木齐县'],
        '拉萨市': ['城关区', '堆龙德庆区', '达孜区', '林周县', '当雄县', '尼木县', '曲水县', '墨竹工卡县'],
        '呼和浩特市': ['新城区', '回民区', '玉泉区', '赛罕区', '土默特左旗', '托克托县', '和林格尔县', '清水河县', '武川县'],
        '南宁市': ['兴宁区', '青秀区', '江南区', '西乡塘区', '良庆区', '邕宁区', '武鸣区', '隆安县', '马山县', '上林县', '宾阳县', '横县'],
        '昆明市': ['五华区', '盘龙区', '官渡区', '西山区', '东川区', '呈贡区', '晋宁区', '富民县', '宜良县', '石林县', '嵩明县', '禄劝县', '寻甸县', '安宁市'],
        '贵阳市': ['南明区', '云岩区', '花溪区', '乌当区', '白云区', '观山湖区', '开阳县', '息烽县', '修文县', '清镇市'],
        '海口市': ['秀英区', '龙华区', '琼山区', '美兰区'],
        '三亚市': ['海棠区', '吉阳区', '天涯区', '崖州区']
      },
      // 设置相关数据
      showProfileEditDialog: false,
      showPasswordDialog: false,
      showPhoneDialog: false,
      showEmailDialog: false,
      showDeleteAccountDialog: false,
      profileEditForm: {
        name: '',
        nickname: '',
        gender: '',
        birthday: '',
        bio: ''
      },
      passwordForm: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      phoneForm: {
        phone: '',
        verificationCode: ''
      },
      emailForm: {
        email: '',
        verificationCode: ''
      },
      // 通知设置
      notificationSettings: {
        orderNotification: true,
        promotionNotification: true,
        appointmentReminder: true,
        emailNotification: false
      },
      // 隐私设置
      privacySettings: {
        profileVisibility: 'public',
        showPurchaseHistory: true,
        allowDataAnalysis: true
      },
      // 其他设置
      otherSettings: {
        language: 'zh-CN',
        theme: 'light',
        autoLogin: true
      }
    }
  },
  computed: {
    // 根据筛选条件过滤订单
    filteredOrders() {
      if (this.orderFilter === 'all') {
        return this.orders
      }
      return this.orders.filter(order => order.status === this.orderFilter)
    },
    // 根据筛选条件过滤预约
    filteredAppointments() {
      if (this.appointmentFilter === 'all') {
        return this.appointments
      }
      return this.appointments.filter(appointment => appointment.status === this.appointmentFilter)
    },
    orderCount() {
      return this.orders.filter(order => order.status === 'pending' || order.status === 'processing').length
    },
    cartCount() {
      return this.cartStore.totalItems
    },
    selectedCount() {
      return this.filteredCartItems.filter(item => item.selected).length
    },
    cartSubtotal() {
      return this.filteredCartItems
        .filter(item => item.selected)
        .reduce((sum, item) => sum + (item.price * item.quantity), 0)
    },
    cartTotal() {
      return this.cartSubtotal - this.promoDiscount
    },
    // 从store获取购物车商品，并添加selected属性
    cartItems() {
      return this.cartStore.cartItems.map(item => ({
        ...item,
        selected: item.selected !== undefined ? item.selected : false // 默认不选中
      }))
    },
    // 购物车搜索过滤
    filteredCartItems() {
      if (!this.cartSearchKeyword.trim()) {
        return this.cartItems
      }
      return this.cartItems.filter(item =>
        item.name.toLowerCase().includes(this.cartSearchKeyword.toLowerCase()) ||
        (item.spec && item.spec.toLowerCase().includes(this.cartSearchKeyword.toLowerCase()))
      )
    },
    // 购物车分页
    cartTotalPages() {
      return Math.ceil(this.filteredCartItems.length / this.cartPageSize)
    },

    // 获取页码数组
    getPageNumbers() {
      const total = this.cartTotalPages
      const current = this.cartCurrentPage
      const pages = []

      if (total <= 7) {
        // 总页数少于等于7页，显示所有页码
        for (let i = 1; i <= total; i++) {
          pages.push(i)
        }
      } else {
        // 总页数大于7页，显示部分页码
        if (current <= 4) {
          // 当前页在前4页
          for (let i = 1; i <= 5; i++) {
            pages.push(i)
          }
          pages.push('...')
          pages.push(total)
        } else if (current >= total - 3) {
          // 当前页在后4页
          pages.push(1)
          pages.push('...')
          for (let i = total - 4; i <= total; i++) {
            pages.push(i)
          }
        } else {
          // 当前页在中间
          pages.push(1)
          pages.push('...')
          for (let i = current - 1; i <= current + 1; i++) {
            pages.push(i)
          }
          pages.push('...')
          pages.push(total)
        }
      }

      return pages
    },
    paginatedCartItems() {
      const start = (this.cartCurrentPage - 1) * this.cartPageSize
      const end = start + this.cartPageSize
      return this.filteredCartItems.slice(start, end)
    },
    // 全选状态
    selectAll() {
      return this.filteredCartItems.length > 0 && this.filteredCartItems.every(item => item.selected)
    },
    // 可选城市列表
    availableCities() {
      return this.addressForm.province ? this.cityData[this.addressForm.province] || [] : []
    },
    // 可选区县列表
    availableDistricts() {
      return this.addressForm.city ? this.districtData[this.addressForm.city] || [] : []
    },
    // 收藏搜索过滤
    filteredFavorites() {
      if (!this.favoritesSearchKeyword.trim()) {
        return this.favorites
      }
      return this.favorites.filter(item =>
        item.name.toLowerCase().includes(this.favoritesSearchKeyword.toLowerCase()) ||
        (item.description && item.description.toLowerCase().includes(this.favoritesSearchKeyword.toLowerCase()))
      )
    },
    // 收藏分页
    favoritesTotalPages() {
      return Math.ceil(this.filteredFavorites.length / this.favoritesPageSize)
    },
    paginatedFavorites() {
      const start = (this.favoritesCurrentPage - 1) * this.favoritesPageSize
      const end = start + this.favoritesPageSize
      return this.filteredFavorites.slice(start, end)
    }
  },
  methods: {
    // 设置订单筛选条件
    setOrderFilter(filter) {
      this.orderFilter = filter
    },

    // 获取待付款订单数量
    getPendingCount() {
      return this.orders.filter(order => order.status === 'pending').length
    },

    // 获取待评价订单数量
    getDeliveredCount() {
      return this.orders.filter(order => order.status === 'delivered').length
    },

    // 获取已取消订单数量
    getCancelledCount() {
      return this.orders.filter(order => order.status === 'cancelled').length
    },

    // 清空回收站
    async clearTrash() {
      const cancelledOrders = this.orders.filter(order => order.status === 'cancelled')
      if (cancelledOrders.length === 0) {
        this.showNotification('回收站已经是空的')
        return
      }

      const confirmed = confirm(`确定要清空回收站吗？这将永久删除 ${cancelledOrders.length} 个已取消的订单，此操作不可恢复。`)
      if (!confirmed) {
        return
      }

      try {
        // 这里可以调用后端API来永久删除已取消的订单
        // 暂时只是从前端移除
        this.orders = this.orders.filter(order => order.status !== 'cancelled')
        this.showNotification('回收站已清空')
      } catch (error) {
        console.error('清空回收站失败:', error)
        this.showNotification('清空回收站失败，请重试')
      }
    },

    // 设置预约筛选条件
    setAppointmentFilter(filter) {
      this.appointmentFilter = filter
    },

    // 获取待服务预约数量
    getPendingAppointmentCount() {
      return this.appointments.filter(appointment => appointment.status === 'pending').length
    },

    async loadOrders() {
      try {
        this.loading = true
        const response = await getUserOrders()
        if (response.success) {
          this.orders = response.data.map(order => ({
            ...order,
            id: order.orderNo,
            date: order.createdAt,
            total: order.finalAmount
          }))
        } else {
          this.showNotification('获取订单列表失败：' + response.message)
        }
      } catch (error) {
        console.error('获取订单列表失败:', error)
        this.showNotification('获取订单列表失败')
      } finally {
        this.loading = false
      }
    },

    async loadCartItems() {
      try {
        this.loading = true
        // 使用store加载购物车数据
        await this.cartStore.loadCart()
        console.log('购物车数据从store加载:', this.cartStore.cartItems)
      } catch (error) {
        console.error('获取购物车失败:', error)
        this.showNotification('获取购物车失败')
      } finally {
        this.loading = false
      }
    },

    switchTab(tab) {
      this.activeTab = tab
      if (tab === 'orders') {
        this.loadOrders()
      } else if (tab === 'cart') {
        this.loadCartItems()
      } else if (tab === 'favorites') {
        this.loadFavorites()
      } else if (tab === 'address') {
        this.loadAddresses()
      } else if (tab === 'appointments') {
        this.loadAppointments()
      }
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      if (isNaN(date.getTime())) return dateStr // 如果日期无效，返回原字符串

      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    },

    formatDateTime(dateTimeStr) {
      if (!dateTimeStr) return ''
      const date = new Date(dateTimeStr)
      if (isNaN(date.getTime())) return dateTimeStr // 如果日期无效，返回原字符串

      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    getStatusClass(status) {
      const statusMap = {
        'pending': 'status-pending',
        'paid': 'status-paid',
        'processing': 'status-processing',
        'shipped': 'status-shipped',
        'delivered': 'status-delivered',
        'cancelled': 'status-cancelled'
      }
      return statusMap[status] || ''
    },

    getStatusText(status) {
      const statusMap = {
        'pending': '待支付',
        'paid': '已支付',
        'processing': '处理中',
        'shipped': '已发货',
        'delivered': '已送达',
        'cancelled': '已取消'
      }
      return statusMap[status] || status
    },

    // 订单操作
    reorder(order) {
      // 将订单商品重新加入购物车
      if (order.items && order.items.length > 0) {
        order.items.forEach(async (item) => {
          try {
            await this.cartStore.addItem({
              productId: item.productId,
              spec: item.spec || '标准装',
              quantity: item.quantity
            })
          } catch (error) {
            console.error('添加商品到购物车失败:', error)
          }
        })
        this.showNotification('商品已重新加入购物车')
      } else {
        this.showNotification('订单商品信息不完整')
      }
    },

    payOrder(order) {
      this.currentPaymentOrder = order
      this.showPaymentDialog = true
      this.paymentStatus = 'waiting'
      this.selectedPaymentMethod = 'wechat'

      // 计算剩余支付时间
      const timeLeft = this.getPaymentTimeLeft(order)
      this.paymentTimeLeft = Math.max(0, timeLeft)

      if (this.paymentTimeLeft > 0) {
        this.startPaymentTimer()
      } else {
        // 订单已超时，自动取消
        this.autoExpireOrder(order)
      }
    },

    // 判断订单是否已超时（超过10分钟）
    isOrderExpired(order) {
      if (!order || order.status !== 'pending') return false

      const createTime = new Date(order.createdAt || order.date)
      const now = new Date()
      const elapsed = Math.floor((now - createTime) / 1000) // 已过去的秒数
      const timeLimit = 10 * 60 // 10分钟时间限制

      return elapsed > timeLimit
    },

    // 计算订单剩余支付时间（秒）
    getPaymentTimeLeft(order) {
      if (!order || order.status !== 'pending') return 0

      const createTime = new Date(order.createdAt || order.date)
      const now = new Date()
      const elapsed = Math.floor((now - createTime) / 1000) // 已过去的秒数
      const totalTime = 10 * 60 // 10分钟总时间

      return Math.max(0, totalTime - elapsed)
    },

    // 格式化支付倒计时显示
    formatPaymentTime(seconds) {
      if (seconds <= 0) return '已超时'

      const minutes = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },

    // 启动支付倒计时
    startPaymentTimer() {
      this.stopPaymentTimer() // 先清除之前的定时器

      this.paymentTimer = setInterval(() => {
        this.paymentTimeLeft--

        if (this.paymentTimeLeft <= 0) {
          this.stopPaymentTimer()
          if (this.showPaymentDialog) {
            this.autoExpirePayment()
          }
        }
      }, 1000)
    },

    // 停止支付倒计时
    stopPaymentTimer() {
      if (this.paymentTimer) {
        clearInterval(this.paymentTimer)
        this.paymentTimer = null
      }
    },

    // 启动全局倒计时更新器
    startGlobalTimer() {
      this.stopGlobalTimer() // 先清除之前的定时器

      this.globalTimer = setInterval(() => {
        // 强制更新组件，让所有倒计时重新计算
        this.$forceUpdate()
      }, 1000)
    },

    // 停止全局倒计时更新器
    stopGlobalTimer() {
      if (this.globalTimer) {
        clearInterval(this.globalTimer)
        this.globalTimer = null
      }
    },

    // 自动超时处理
    autoExpirePayment() {
      this.showNotification('支付超时，订单已自动取消')
      this.closePaymentDialog()
      this.loadOrders() // 重新加载订单列表
    },

    // 订单超时处理
    autoExpireOrder(order) {
      this.showNotification(`订单 ${order.orderNo || order.id} 已超时`)
      this.loadOrders() // 重新加载订单列表
    },

    // 获取支付方式名称
    getPaymentMethodName(method) {
      const names = {
        'wechat': '微信',
        'alipay': '支付宝',
        'unionpay': '银联'
      }
      return names[method] || '微信'
    },

    // 确认支付
    async confirmPayment() {
      if (!this.currentPaymentOrder) return

      try {
        const { payOrder } = await import('@/api/order')
        const response = await payOrder(
          this.currentPaymentOrder.orderNo || this.currentPaymentOrder.id,
          this.selectedPaymentMethod
        )

        if (response.success) {
          this.paymentStatus = 'success'
          this.stopPaymentTimer()
          this.showNotification('支付成功！')

          setTimeout(() => {
            this.closePaymentDialog()
            this.loadOrders() // 重新加载订单列表
          }, 2000)
        } else {
          this.showNotification('支付失败：' + response.message)
        }
      } catch (error) {
        console.error('支付失败:', error)
        this.showNotification('支付失败，请重试')
      }
    },

    // 取消支付
    async cancelPayment() {
      this.stopPaymentTimer()

      if (this.paymentTimeLeft > 0) {
        const shouldCancelOrder = confirm('是否要取消订单？\n选择"确定"将取消订单\n选择"取消"将保留订单，稍后可继续支付')

        if (shouldCancelOrder && this.currentPaymentOrder) {
          try {
            const { cancelOrder } = await import('@/api/order')
            const response = await cancelOrder(this.currentPaymentOrder.orderNo || this.currentPaymentOrder.id)
            if (response.success) {
              this.showNotification('订单已取消')
              this.loadOrders() // 重新加载订单列表
            } else {
              this.showNotification('订单取消失败：' + response.message)
            }
          } catch (error) {
            console.error('取消订单失败:', error)
            this.showNotification('取消订单失败，请重试')
          }
        } else {
          this.showNotification('支付已取消，订单保留中')
        }
      }

      this.closePaymentDialog()
    },

    // 关闭支付对话框
    closePaymentDialog() {
      this.showPaymentDialog = false
      this.stopPaymentTimer()
      this.currentPaymentOrder = null
      this.paymentStatus = 'waiting'
    },

    async cancelOrder(order) {
      try {
        const response = await cancelOrder(order.orderId || order.id)
        if (response.success) {
          this.showNotification('订单取消成功')
          order.status = 'cancelled'
        } else {
          this.showNotification('订单取消失败：' + response.message)
        }
      } catch (error) {
        console.error('取消订单失败:', error)
        this.showNotification('取消订单失败')
      }
    },

    // 购物车操作
    async decreaseQuantity(item) {
      if (item.quantity > 1) {
        try {
          await this.cartStore.updateQuantity(item.id, item.quantity - 1)
        } catch (error) {
          console.error('更新商品数量失败:', error)
          this.showNotification('更新商品数量失败')
        }
      }
    },

    async increaseQuantity(item) {
      if (item.quantity < 10) {
        try {
          await this.cartStore.updateQuantity(item.id, item.quantity + 1)
        } catch (error) {
          console.error('更新商品数量失败:', error)
          this.showNotification('更新商品数量失败')
        }
      }
    },

    async updateQuantity(item) {
      if (item.quantity < 1) {
        item.quantity = 1
      } else if (item.quantity > 10) {
        item.quantity = 10
      }
      try {
        await this.cartStore.updateQuantity(item.id, item.quantity)
      } catch (error) {
        console.error('更新商品数量失败:', error)
        this.showNotification('更新商品数量失败')
      }
    },

    async removeFromCart(item) {
      try {
        await this.cartStore.removeItem(item.id)
        this.showNotification('商品已从购物车移除')
      } catch (error) {
        console.error('删除商品失败:', error)
        this.showNotification('删除商品失败')
      }
    },

    updateCartTotal() {
      // 计算总价逻辑已在computed中处理
    },

    // 切换单个商品的选中状态
    toggleItemSelect(item) {
      // 在 store 中找到对应的商品并切换状态
      const storeItem = this.cartStore.cartItems.find(i => i.id === item.id)
      if (storeItem) {
        storeItem.selected = !storeItem.selected
        this.$forceUpdate()
      }
    },

    // 全选/取消全选
    toggleSelectAll() {
      const newSelectState = !this.selectAll
      // 直接修改 store 中的数据以确保响应式更新
      this.cartStore.cartItems.forEach(item => {
        item.selected = newSelectState
      })
      this.$forceUpdate() // 强制更新视图
      this.updateCartTotal()
    },

    applyPromo() {
      if (this.promoCode === 'PET30' || this.promoCode === '宠物之家') {
        this.promoDiscount = 30
        this.promoMessage = '优惠券已应用：立减30元'
        this.promoMessageType = 'success'
        this.showNotification('优惠券应用成功，立减30元！')
      } else if (this.promoCode) {
        this.promoMessage = '优惠券无效或已过期'
        this.promoMessageType = 'error'
      } else {
        this.promoMessage = '请输入优惠券代码'
        this.promoMessageType = 'info'
      }
    },

    continueShopping() {
      this.$router.push('/')
    },

    checkout() {
      // 检查是否有商品
      if (this.cartItems.length === 0) {
        this.showNotification('购物车是空的，请先添加商品')
        return
      }

      // 如果商品有selected属性，检查是否有选中的商品
      const hasSelectedProperty = this.cartItems.some(item => 'selected' in item)
      if (hasSelectedProperty) {
        const selectedItems = this.cartItems.filter(item => item.selected)
        if (selectedItems.length === 0) {
          this.showNotification('请至少选择一件商品进行结算')
          return
        }
      }

      // 跳转到结算页面
      this.$router.push({
        path: '/checkout',
        query: {
          type: 'cart'
        }
      })
    },

    // 购物车搜索过滤
    filterCartItems() {
      this.cartCurrentPage = 1 // 搜索时重置到第一页
    },

    // 购物车分页方法
    prevCartPage() {
      if (this.cartCurrentPage > 1) {
        this.cartCurrentPage--
      }
    },

    nextCartPage() {
      if (this.cartCurrentPage < this.cartTotalPages) {
        this.cartCurrentPage++
      }
    },

    // 跳转到指定页面
    goToCartPage(page) {
      if (typeof page === 'number' && page >= 1 && page <= this.cartTotalPages) {
        this.cartCurrentPage = page
      }
    },

    // 跳转到商品详情页
    goToProduct(productId) {
      this.$router.push(`/product/${productId}`)
    },

    // 收藏搜索过滤
    filterFavorites() {
      this.favoritesCurrentPage = 1 // 搜索时重置到第一页
    },

    // 收藏分页方法
    prevFavoritesPage() {
      if (this.favoritesCurrentPage > 1) {
        this.favoritesCurrentPage--
      }
    },

    nextFavoritesPage() {
      if (this.favoritesCurrentPage < this.favoritesTotalPages) {
        this.favoritesCurrentPage++
      }
    },
    async loadFavorites() {
      try {
        this.loading = true
        const response = await this.userStore.getUserFavorites()
        console.log('获取收藏列表:', response)

        if (Array.isArray(response)) {
          this.favorites = response.map(item => ({
            id: item.id,
            name: item.name || item.productName || '未知商品',
            description: item.description || item.briefDesc || '',
            price: item.price || item.finalPrice || 0,
            image: item.image || item.thumbnailUrl || '',
            type: item.type || '商品',
            createdAt: item.createdAt || new Date().toISOString()
          }))
        } else {
          this.favorites = []
        }
      } catch (error) {
        console.error('获取收藏列表失败:', error)
        this.favorites = []
      } finally {
        this.loading = false
      }
    },

    async addToCartFromFavorites(item) {
      try {
        await this.cartStore.addItem({
          productId: item.id,
          spec: '标准装',
          quantity: 1
        })
        this.showNotification(`已将 ${item.name} 加入购物车`)
      } catch (error) {
        console.error('添加到购物车失败:', error)
        this.showNotification('添加到购物车失败')
      }
    },

    async removeFromFavorites(itemId) {
      try {
        await this.userStore.removeFavorite(itemId)
        this.favorites = this.favorites.filter(item => item.id !== itemId)
        this.showNotification('已取消收藏')
      } catch (error) {
        console.error('取消收藏失败:', error)
        this.showNotification('取消收藏失败')
      }
    },

    // 预约相关方法
    async loadAppointments() {
      try {
        this.loading = true
        const response = await this.userStore.getAllUserAppointments()
        console.log('获取预约列表:', response)

        if (Array.isArray(response)) {
          this.appointments = response.map(appointment => ({
            id: appointment.id,
            orderNo: appointment.orderNo || `ORD-${appointment.id}`,
            serviceName: appointment.serviceName || appointment.service?.itemName || '未知服务',
            petName: appointment.petName || appointment.pet?.name || '未知宠物',
            appointmentDate: appointment.appointmentDate,
            timeSlot: appointment.timeSlot,
            orderTotalAmount: appointment.orderTotalAmount || appointment.totalAmount || 0,
            status: appointment.status || 'pending',
            specialNotes: appointment.specialNotes || '',
            petId: appointment.petId,
            serviceId: appointment.serviceId,
            createdAt: appointment.createdAt,
            updatedAt: appointment.updatedAt
          }))
        } else {
          this.appointments = []
        }
      } catch (error) {
        console.error('获取预约列表失败:', error)
        this.appointments = []
      } finally {
        this.loading = false
      }
    },

    openAppointmentDialog() {
      this.showAppointmentDialog = true
      this.loadPetsForAppointment()
      this.loadServicesForAppointment()
    },

    closeAppointmentDialog() {
      this.showAppointmentDialog = false
      this.resetAppointmentForm()
    },

    resetAppointmentForm() {
      this.appointmentForm = {
        petId: '',
        serviceId: '',
        appointmentDate: '',
        timeSlot: '',
        specialNotes: ''
      }
    },

    async loadPetsForAppointment() {
      try {
        const response = await this.userStore.getUserPets()
        console.log('获取宠物列表:', response)

        if (Array.isArray(response)) {
          this.pets = response.map(pet => ({
            id: pet.id,
            name: pet.name || '未命名',
            breed: pet.breed || '未知品种',
            type: pet.type || pet.pet_type || '未知'
          }))
        } else {
          this.pets = []
        }
      } catch (error) {
        console.error('获取宠物列表失败:', error)
        this.pets = []
      }
    },

    async loadServicesForAppointment() {
      try {
        const response = await this.userStore.getServiceItems()
        console.log('获取服务列表:', response)

        if (Array.isArray(response)) {
          this.services = response.map(service => ({
            id: service.id,
            itemName: service.itemName || service.name || '未知服务',
            finalPrice: service.finalPrice || service.price || 0
          }))
        } else {
          this.services = []
        }
      } catch (error) {
        console.error('获取服务列表失败:', error)
        this.services = []
      }
    },

    async submitAppointment() {
      try {
        // 验证表单
        if (!this.appointmentForm.petId || !this.appointmentForm.serviceId ||
            !this.appointmentForm.appointmentDate || !this.appointmentForm.timeSlot) {
          this.showNotification('请填写完整的预约信息')
          return
        }

        const appointmentData = {
          petId: parseInt(this.appointmentForm.petId),
          serviceId: parseInt(this.appointmentForm.serviceId),
          appointmentDate: this.appointmentForm.appointmentDate,
          timeSlot: this.appointmentForm.timeSlot,
          specialNotes: this.appointmentForm.specialNotes || ''
        }

        console.log('提交预约数据:', appointmentData)

        const result = await this.userStore.createAppointment(appointmentData)
        console.log('预约结果:', result)

        this.showNotification('预约创建成功！')
        this.closeAppointmentDialog()
        this.loadAppointments() // 重新加载预约列表

      } catch (error) {
        console.error('创建预约失败:', error)
        this.showNotification(error.message || '预约失败，请稍后重试')
      }
    },

    viewAppointmentDetail(appointment) {
      // 跳转到预约详情页面
      this.$router.push(`/service/appointment/${appointment.id}`)
    },

    goToProfile() {
      this.$router.push('/profile')
    },

    async cancelAppointment(appointment) {
      if (!confirm(`确定要取消预约 ${appointment.orderNo} 吗？`)) {
        return
      }

      try {
        await this.userStore.cancelAppointment(appointment.id, '用户主动取消')
        appointment.status = 'cancelled'
        this.showNotification('预约已取消')
      } catch (error) {
        console.error('取消预约失败:', error)
        this.showNotification('取消预约失败')
      }
    },

    rebookAppointment(appointment) {
      // 复制预约信息到表单
      this.appointmentForm = {
        petId: appointment.petId?.toString() || '',
        serviceId: appointment.serviceId?.toString() || '',
        appointmentDate: '',
        timeSlot: '',
        specialNotes: appointment.specialNotes || ''
      }
      this.openAppointmentDialog()
    },

    // 切换订单详情显示
    toggleOrderDetail(order) {
      order.showDetail = !order.showDetail
    },

    async loadUserInfo() {
      try {
        // 检查userStore是否存在
        if (!this.userStore) {
          console.warn('userStore未初始化')
          this.userInfo = {
            name: '用户',
            level: '普通会员'
          }
          return
        }

        // 检查是否已登录
        const isLoggedIn = this.userStore.checkAuthStatus()
        if (!isLoggedIn) {
          console.warn('用户未登录')
          this.userInfo = {
            name: '游客',
            level: '普通会员'
          }
          return
        }

        // 从用户store获取用户信息
        let userInfo = this.userStore.userInfo

        // 如果store中没有用户信息，尝试从后端获取
        if (!userInfo) {
          try {
            userInfo = await this.userStore.fetchUserInfo()
          } catch (error) {
            console.error('从后端获取用户信息失败:', error)
          }
        }

        if (userInfo) {
          this.userInfo = {
            name: userInfo.username || userInfo.name || userInfo.nickname || '用户',
            level: userInfo.level || userInfo.memberLevel || '普通会员'
          }
        } else {
          // 如果仍然没有用户信息，使用默认值
          this.userInfo = {
            name: '用户',
            level: '普通会员'
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 使用默认值
        this.userInfo = {
          name: '用户',
          level: '普通会员'
        }
      }
    },

    // 设置相关方法
    openProfileEditDialog() {
      this.profileEditForm = {
        name: this.userInfo.name || '',
        nickname: this.userInfo.nickname || '',
        gender: this.userInfo.gender || '',
        birthday: this.userInfo.birthday || '',
        bio: this.userInfo.bio || ''
      }
      this.showProfileEditDialog = true
    },

    closeProfileEditDialog() {
      this.showProfileEditDialog = false
      this.profileEditForm = {
        name: '',
        nickname: '',
        gender: '',
        birthday: '',
        bio: ''
      }
    },

    openPasswordDialog() {
      this.showPasswordDialog = true
    },

    closePasswordDialog() {
      this.showPasswordDialog = false
      this.passwordForm = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    },

    openPhoneDialog() {
      this.showPhoneDialog = true
    },

    closePhoneDialog() {
      this.showPhoneDialog = false
      this.phoneForm = {
        phone: '',
        verificationCode: ''
      }
    },

    openEmailDialog() {
      this.showEmailDialog = true
    },

    closeEmailDialog() {
      this.showEmailDialog = false
      this.emailForm = {
        email: '',
        verificationCode: ''
      }
    },

    // 保存个人信息
    async saveProfile() {
      try {
        // 这里应该调用API保存个人信息
        this.userInfo.name = this.profileEditForm.name
        this.userInfo.nickname = this.profileEditForm.nickname
        this.userInfo.gender = this.profileEditForm.gender
        this.userInfo.birthday = this.profileEditForm.birthday
        this.userInfo.bio = this.profileEditForm.bio

        this.showNotification('个人信息保存成功')
        this.closeProfileEditDialog()
      } catch (error) {
        console.error('保存个人信息失败:', error)
        this.showNotification('保存个人信息失败')
      }
    },

    // 修改密码
    async changePassword() {
      if (!this.passwordForm.currentPassword || !this.passwordForm.newPassword || !this.passwordForm.confirmPassword) {
        this.showNotification('请填写完整的密码信息')
        return
      }

      if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
        this.showNotification('两次输入的新密码不一致')
        return
      }

      if (this.passwordForm.newPassword.length < 6) {
        this.showNotification('新密码长度不能少于6位')
        return
      }

      try {
        const res = await request({
          url: '/api/user/password',
          method: 'PUT',
          data: {
            currentPassword: this.passwordForm.currentPassword,
            newPassword: this.passwordForm.newPassword
          }
        })

        if (res.code === 200) {
          this.showNotification('密码修改成功，请重新登录')
          this.closePasswordDialog()
          // 清空密码表单
          this.passwordForm = {
            currentPassword: '',
            newPassword: '',
            confirmPassword: ''
          }
          // 可选：自动退出登录
          setTimeout(() => {
            this.$router.push('/auth')
          }, 2000)
        } else {
          this.showNotification(res.message || '密码修改失败')
        }
      } catch (error) {
        console.error('修改密码失败:', error)
        this.showNotification(error.response?.data?.message || '密码修改失败')
      }
    },

    // 绑定/更换手机号
    async changePhone() {
      if (!this.phoneForm.phone || !this.phoneForm.verificationCode) {
        this.showNotification('请填写完整的手机号和验证码')
        return
      }

      const phoneRegex = /^1[3-9]\d{9}$/
      if (!phoneRegex.test(this.phoneForm.phone)) {
        this.showNotification('请输入正确的手机号码')
        return
      }

      try {
        // 这里应该调用API绑定/更换手机号
        this.userInfo.phone = this.phoneForm.phone
        this.showNotification('手机号绑定成功')
        this.closePhoneDialog()
      } catch (error) {
        console.error('绑定手机号失败:', error)
        this.showNotification('绑定手机号失败')
      }
    },

    // 发送手机验证码
    async sendPhoneCode() {
      if (!this.phoneForm.phone) {
        this.showNotification('请先输入手机号')
        return
      }

      const phoneRegex = /^1[3-9]\d{9}$/
      if (!phoneRegex.test(this.phoneForm.phone)) {
        this.showNotification('请输入正确的手机号码')
        return
      }

      try {
        // 这里应该调用API发送验证码
        this.showNotification('验证码已发送')
      } catch (error) {
        console.error('发送验证码失败:', error)
        this.showNotification('发送验证码失败')
      }
    },

    // 绑定/更换邮箱
    async changeEmail() {
      if (!this.emailForm.email || !this.emailForm.verificationCode) {
        this.showNotification('请填写完整的邮箱和验证码')
        return
      }

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(this.emailForm.email)) {
        this.showNotification('请输入正确的邮箱地址')
        return
      }

      try {
        // 这里应该调用API绑定/更换邮箱
        this.userInfo.email = this.emailForm.email
        this.showNotification('邮箱绑定成功')
        this.closeEmailDialog()
      } catch (error) {
        console.error('绑定邮箱失败:', error)
        this.showNotification('绑定邮箱失败')
      }
    },

    // 发送邮箱验证码
    async sendEmailCode() {
      if (!this.emailForm.email) {
        this.showNotification('请先输入邮箱地址')
        return
      }

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(this.emailForm.email)) {
        this.showNotification('请输入正确的邮箱地址')
        return
      }

      try {
        // 这里应该调用API发送验证码
        this.showNotification('验证码已发送')
      } catch (error) {
        console.error('发送验证码失败:', error)
        this.showNotification('发送验证码失败')
      }
    },

    // 保存设置
    async saveSettings() {
      try {
        // 这里应该调用API保存设置
        this.showNotification('设置保存成功')
      } catch (error) {
        console.error('保存设置失败:', error)
        this.showNotification('保存设置失败')
      }
    },

    // 商业化功能
    upgradeToVip() {
      this.showNotification('VIP升级功能开发中')
    },

    viewMembershipBenefits() {
      this.showNotification('会员权益页面开发中')
    },

    viewPointsHistory() {
      this.showNotification('积分明细页面开发中')
    },

    viewInsuranceOptions() {
      this.showNotification('宠物保险页面开发中')
    },

    viewCustomServices() {
      this.showNotification('定制服务页面开发中')
    },

    viewReferralProgram() {
      this.showNotification('推荐奖励页面开发中')
    },

    // 危险操作
    confirmDeleteAccount() {
      if (confirm('确定要注销账户吗？此操作不可恢复！')) {
        this.showDeleteAccountDialog = true
      }
    },

    async deleteAccount() {
      try {
        // 这里应该调用API注销账户
        this.showNotification('账户注销成功')
        // 跳转到登录页面
        this.$router.push('/login')
      } catch (error) {
        console.error('注销账户失败:', error)
        this.showNotification('注销账户失败')
      }
    },

    showNotification(message) {
      // 简单的通知实现
      alert(message)
    },

    // 地址管理方法
    async loadAddresses() {
      try {
        const response = await getAddresses()
        if (response.success) {
          // 映射后端字段到前端字段
          this.addresses = response.data.map(address => ({
            ...address,
            recipientName: address.receiverName,  // receiverName -> recipientName
            phone: address.receiverPhone,         // receiverPhone -> phone
            detailAddress: address.address,       // address -> detailAddress
            fullAddress: address.fullAddress || `${address.province}${address.city}${address.district}${address.address}`
          }))
        } else {
          this.showNotification('获取地址列表失败：' + response.message)
        }
      } catch (error) {
        console.error('获取地址列表失败:', error)
        this.showNotification('获取地址列表失败')
      }
    },

    // 打开添加地址对话框
    openAddDialog() {
      this.editingAddress = null
      this.resetAddressForm()
      this.showAddressDialog = true
    },

    // 打开编辑地址对话框
    openEditDialog(address) {
      this.editingAddress = address
      this.addressForm = {
        recipientName: address.recipientName,
        phone: address.phone,
        province: address.province,
        city: address.city,
        district: address.district,
        detailAddress: address.detailAddress,
        isDefault: address.isDefault
      }
      this.showAddressDialog = true
    },

    // 关闭地址对话框
    closeAddressDialog() {
      this.showAddressDialog = false
      this.editingAddress = null
      this.resetAddressForm()
    },

    // 确认删除地址
    confirmDeleteAddress(address) {
      this.deletingAddress = address
      this.showDeleteDialog = true
    },

    // 关闭删除对话框
    closeDeleteDialog() {
      this.showDeleteDialog = false
      this.deletingAddress = null
    },

    // 执行删除
    async confirmDelete() {
      if (this.deletingAddress) {
        try {
          const response = await deleteAddress(this.deletingAddress.id)
          if (response.success) {
            this.showNotification('地址删除成功')
            await this.loadAddresses() // 重新加载地址列表
          } else {
            this.showNotification('地址删除失败：' + response.message)
          }
        } catch (error) {
          console.error('删除地址失败:', error)
          this.showNotification('删除地址失败')
        }
      }
      this.closeDeleteDialog()
    },

    async setDefaultAddress(addressId) {
      try {
        const response = await setDefaultAddress(addressId)
        if (response.success) {
          this.showNotification('默认地址设置成功')
          await this.loadAddresses() // 重新加载地址列表
        } else {
          this.showNotification('设置默认地址失败：' + response.message)
        }
      } catch (error) {
        console.error('设置默认地址失败:', error)
        this.showNotification('设置默认地址失败')
      }
    },

    onProvinceChange() {
      // 省份改变时清空城市和区县
      this.addressForm.city = ''
      this.addressForm.district = ''
    },

    onCityChange() {
      // 城市改变时清空区县
      this.addressForm.district = ''
    },

    cancelAddressForm() {
      this.closeAddressDialog()
    },

    resetAddressForm() {
      this.addressForm = {
        recipientName: '',
        phone: '',
        province: '',
        city: '',
        district: '',
        detailAddress: '',
        isDefault: false
      }
    },

    async saveAddress() {
      // 验证表单
      if (!this.addressForm.recipientName || !this.addressForm.phone ||
          !this.addressForm.province || !this.addressForm.city ||
          !this.addressForm.district || !this.addressForm.detailAddress) {
        this.showNotification('请填写完整的地址信息')
        return
      }

      // 验证手机号格式
      const phoneRegex = /^1[3-9]\d{9}$/
      if (!phoneRegex.test(this.addressForm.phone)) {
        this.showNotification('请输入正确的手机号码')
        return
      }

      // 如果设为默认地址，检查是否已有默认地址
      if (this.addressForm.isDefault) {
        const hasDefault = this.addresses.some(addr =>
          addr.isDefault && (!this.editingAddress || addr.id !== this.editingAddress.id)
        )
        if (hasDefault) {
          const confirmMsg = '已存在默认地址，设置此地址为默认将取消其他默认地址，是否继续？'
          if (!confirm(confirmMsg)) {
            return
          }
        }
      }

      try {
        // 构建完整地址
        const fullAddress = `${this.addressForm.province}${this.addressForm.city}${this.addressForm.district}${this.addressForm.detailAddress}`

        const addressData = {
          ...this.addressForm,
          fullAddress: fullAddress
        }

        if (this.editingAddress) {
          // 编辑地址
          const response = await updateAddress(this.editingAddress.id, addressData)
          if (response.success) {
            this.showNotification('地址修改成功')
            await this.loadAddresses() // 重新加载地址列表
          } else {
            this.showNotification('地址修改失败：' + response.message)
          }
        } else {
          // 添加新地址
          const response = await addAddress(addressData)
          if (response.success) {
            this.showNotification('地址添加成功')
            await this.loadAddresses() // 重新加载地址列表
          } else {
            this.showNotification('地址添加失败：' + response.message)
          }
        }

        this.cancelAddressForm()
      } catch (error) {
        console.error('保存地址失败:', error)
        this.showNotification('保存地址失败')
      }
    }
  },

  mounted() {
    const tab = this.$route.query.tab
    if (tab && ['orders', 'cart', 'favorites', 'address', 'appointments', 'settings'].includes(tab)) {
      this.activeTab = tab
    }

    // 加载用户信息
    this.loadUserInfo()

    // 根据当前标签页加载对应数据
    if (this.activeTab === 'orders') {
      this.loadOrders()
    } else if (this.activeTab === 'cart') {
      this.loadCartItems()
    } else if (this.activeTab === 'favorites') {
      this.loadFavorites()
    } else if (this.activeTab === 'address') {
      this.loadAddresses()
    } else if (this.activeTab === 'appointments') {
      this.loadAppointments()
    }

    // 始终加载购物车数据以保持同步
    this.cartStore.loadCart()

    // 启动全局倒计时更新器
    this.startGlobalTimer()
  },

  beforeUnmount() {
    // 清理定时器
    this.stopPaymentTimer()
    this.stopGlobalTimer()
  }
}
</script>

<style scoped>
.user-center-page {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  min-height: 100vh;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 30px;
}

/* 左侧边栏 */
.sidebar {
  width: 280px;
  flex-shrink: 0;
}

/* 用户信息卡片 */
.user-info-card {
  position: relative;
  background: white;
  border-radius: 15px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  text-align: center;
}

.edit-profile-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 1;
}

.edit-profile-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
  transform: scale(1.1);
}

.edit-profile-btn svg {
  font-size: 16px;
  color: #6c757d;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9800, #ffcc80);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  margin: 0 auto 15px;
}

.user-details h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
}

.user-level {
  background: #ff9800;
  color: white;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 12px;
  display: inline-block;
  margin-bottom: 20px;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 20px;
  font-weight: bold;
  color: #ff9800;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

/* 导航菜单 */
.nav-menu {
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  overflow: hidden;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}

.nav-item:last-child {
  border-bottom: none;
}

.nav-item:hover {
  background: rgba(25, 118, 210, 0.08);
  color: #1976d2;
}

.nav-item.active {
  background: #e8f4fd;
  color: #1976d2;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: #1976d2;
}

.nav-badge {
  background: #ff4757;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  margin-left: auto;
}

.nav-item.active .nav-badge {
  background: rgba(255, 255, 255, 0.3);
}

/* 右侧主内容区 */
.main-content {
  flex: 1;
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  overflow: hidden;
}

.content-section {
  padding: 30px;
}

.content-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.content-header h2 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 24px;
}

.content-header p {
  margin: 0;
  color: #666;
}

/* 订单状态筛选栏 */
.order-filter-tabs {
  display: flex;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 4px;
  margin-bottom: 25px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.filter-tab {
  flex: 1;
  padding: 12px 16px;
  text-align: center;
  cursor: pointer;
  border-radius: 6px;
  font-size: 14px;
  color: #666;
  transition: all 0.3s ease;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.filter-tab:hover {
  color: #333;
  background: rgba(255, 255, 255, 0.7);
}

.filter-tab.active {
  background: white;
  color: #333;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(0,0,0,0.08);
}

.filter-badge {
  background: #ff6b6b;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 清空回收站按钮 */
.clear-trash-btn {
  padding: 8px 12px;
  background: #ff4757;
  color: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s ease;
  margin-left: 8px;
  white-space: nowrap;
}

.clear-trash-btn:hover {
  background: #ff3742;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 71, 87, 0.3);
}

/* 按钮样式 */
.action-btn {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.action-btn-primary {
  background: #ff9800;
  color: white;
}

.action-btn-primary:hover {
  background: #f57c00;
  transform: translateY(-2px);
}

.action-btn-secondary {
  background: white;
  color: #ff9800;
  border: 2px solid #ff9800;
}

.action-btn-secondary:hover {
  background: rgba(255, 152, 0, 0.1);
}

.action-btn-danger {
  background: white;
  color: #f44336;
  border: 2px solid #f44336;
}

.action-btn-danger:hover {
  background: rgba(244, 67, 54, 0.1);
}

/* 订单列表 */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: #f8f9fa;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  overflow: hidden;
  transition: all 0.3s;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.order-header {
  background: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  border-bottom: 1px solid #eee;
}

.order-info {
  display: flex;
  gap: 30px;
}

.order-info-item {
  display: flex;
  flex-direction: column;
}

.order-info-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.order-info-value {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

/* 订单状态区域 */
.order-status-section {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

/* 支付倒计时徽章样式 */
.payment-countdown-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 12px;
  color: #856404;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.payment-countdown-badge.countdown-warning {
  background: #f8d7da;
  border-color: #f5c6cb;
  color: #721c24;
  animation: pulse 1s infinite;
}

.payment-countdown-badge svg {
  font-size: 14px;
}

.order-status {
  padding: 6px 15px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
}

.status-pending {
  background: rgba(255, 152, 0, 0.1);
  color: #ff9800;
}

.status-paid {
  background: rgba(76, 175, 80, 0.1);
  color: #4caf50;
}

.status-processing {
  background: rgba(33, 150, 243, 0.1);
  color: #2196f3;
}

.status-shipped {
  background: rgba(76, 175, 80, 0.1);
  color: #4caf50;
}

.status-delivered {
  background: rgba(76, 175, 80, 0.2);
  color: #4caf50;
}

.status-cancelled {
  background: rgba(244, 67, 54, 0.1);
  color: #f44336;
}

.order-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.preview-product {
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.preview-product:hover {
  transform: translateY(-2px);
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info h4 {
  margin: 0 0 5px 0;
  color: #333;
}

.product-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.order-summary {
  text-align: right;
}

.order-summary p {
  margin: 0 0 5px 0;
  color: #666;
  font-size: 14px;
}

.order-summary h3 {
  margin: 0;
  color: #ff9800;
}

/* 超时订单提示 */
.expired-order-notice {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
  color: #856404;
  font-size: 14px;
  font-weight: 500;
}

.expired-order-notice svg {
  color: #ff9800;
}

.order-actions {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

/* 订单详情展开 */
.order-detail-expanded {
  border-top: 1px solid #eee;
  background: #f9f9f9;
}

.detail-section {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.detail-section:last-child {
  border-bottom: none;
}

.detail-section h4 {
  margin: 0 0 15px 0;
  color: #333;
}

.detail-products {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.detail-product-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.detail-product-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.detail-product-item .product-image {
  width: 60px;
  height: 60px;
}

.product-details {
  flex: 1;
}

.product-details h5 {
  margin: 0 0 5px 0;
  color: #333;
}

.product-details p {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 12px;
}

.price-quantity {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-weight: bold;
  color: #ff9800;
}

.quantity {
  font-size: 12px;
  color: #666;
}

.order-summary-detail {
  background: white;
  border-radius: 8px;
  padding: 15px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px dashed #ddd;
}

.summary-row:last-child {
  border-bottom: none;
}

.summary-row.total {
  font-weight: bold;
  color: #ff9800;
  font-size: 16px;
}

/* 购物车样式 */
.cart-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cart-items-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 15px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
  width: 100%;
}

.cart-item:last-child {
  border-bottom: none;
}

/* 全选控制栏 */
.cart-select-all {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 12px;
  background: #f8f9fa;
  border-bottom: 2px solid #e9ecef;
  font-size: 14px;
}

.select-all-text {
  font-weight: 600;
  color: #333;
}

.selected-info {
  color: #666;
  margin-left: auto;
}

.cart-checkbox {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.cart-item-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.cart-item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cart-item-details {
  flex: 1;
  min-width: 0;
}

.cart-item-details h4 {
  margin: 0 0 4px 0;
  color: #333;
  font-size: 14px;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cart-item-details p {
  margin: 0;
  color: #666;
  font-size: 12px;
}

.cart-item-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  margin-left: auto;
}

.quantity-control {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.quantity-btn {
  background: #f5f5f5;
  border: none;
  width: 28px;
  height: 28px;
  cursor: pointer;
  color: #666;
  font-size: 14px;
}

.quantity-btn:hover {
  background: #e9e9e9;
}

.quantity-input {
  width: 40px;
  height: 28px;
  text-align: center;
  border: none;
  font-size: 12px;
}

.cart-item-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff9800;
  min-width: 70px;
  text-align: right;
  flex-shrink: 0;
}

.cart-item-remove {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 4px;
  font-size: 16px;
  flex-shrink: 0;
}

.cart-item-remove:hover {
  color: #f44336;
}

.cart-summary-card {
  background: white;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.summary-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
}

.summary-total-price {
  font-size: 20px;
  font-weight: bold;
  color: #ff9800;
}

.summary-details {
  margin-bottom: 15px;
}

.promo-section {
  margin: 15px 0;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
}

.promo-section h4 {
  margin: 0 0 8px 0;
  color: #333;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.promo-input {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.promo-input input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
}

.promo-input button {
  background: #ff9800;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.promo-message {
  font-size: 14px;
  padding: 8px;
  border-radius: 4px;
}

.promo-message.success {
  background: rgba(76, 175, 80, 0.1);
  color: #4caf50;
}

.promo-message.error {
  background: rgba(244, 67, 54, 0.1);
  color: #f44336;
}

.promo-message.info {
  background: rgba(33, 150, 243, 0.1);
  color: #2196f3;
}

.cart-actions {
  display: flex;
  gap: 10px;
}

.cart-action-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-size: 13px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.3s;
}

.continue-shopping {
  background: white;
  color: #ff9800;
  border: 2px solid #ff9800;
}

.continue-shopping:hover {
  background: rgba(255, 152, 0, 0.1);
}

.checkout-btn {
  background: #ff9800;
  color: white;
}

.checkout-btn:hover {
  background: #f57c00;
  transform: translateY(-2px);
}

/* 加载和空状态 */
.loading-state, .empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #f8f9fa;
  border-radius: 12px;
  margin: 20px 0;
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  color: #ff9800;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.empty-icon {
  font-size: 60px;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.empty-description {
  color: #666;
  margin-bottom: 30px;
}

.empty-action {
  background: #ff9800;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.empty-action:hover {
  background: #f57c00;
  transform: translateY(-2px);
}

/* 对话框样式 - 简洁白色设计 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dialog-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  max-width: 520px;
  width: 90%;
  max-height: 80vh;
  overflow: hidden;
  animation: slideUp 0.3s ease-out;
}

.dialog-container.small {
  max-width: 400px;
}

.dialog-header {
  background: white;
  color: #333;
  padding: 24px 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.dialog-close-btn {
  background: #f5f5f5;
  border: none;
  color: #666;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  transition: all 0.2s ease;
}

.dialog-close-btn:hover {
  background: #e0e0e0;
  color: #333;
}

.dialog-form {
  padding: 28px;
  max-height: calc(80vh - 120px);
  overflow-y: auto;
}

.dialog-form .form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.dialog-form .form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.dialog-form .form-group label {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.dialog-form .required {
  color: #ff4757;
}

.dialog-form .form-group input,
.dialog-form .form-group select,
.dialog-form .form-group textarea {
  padding: 12px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s ease;
  background: white;
}

.dialog-form .form-group input:focus,
.dialog-form .form-group select:focus,
.dialog-form .form-group textarea:focus {
  outline: none;
  border-color: #ff9800;
  box-shadow: 0 0 0 2px rgba(255, 152, 0, 0.1);
}

.dialog-form .form-group textarea {
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
}

.dialog-form .checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-size: 14px;
  margin: 16px 0;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.dialog-form .checkbox-label:hover {
  background: #f0f1f2;
}

.dialog-form .checkbox-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: #ff9800;
}

.dialog-content {
  padding: 28px;
  text-align: center;
}

.dialog-content p {
  margin: 0 0 16px 0;
  color: #666;
  font-size: 15px;
  line-height: 1.5;
}

.delete-address-info {
  background: #fff5f5;
  padding: 16px;
  border-radius: 8px;
  border-left: 3px solid #ff4757;
  color: #333;
  line-height: 1.5;
  text-align: left;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 20px 28px;
  background: #fff8e1;
  border-top: 1px solid #f0f0f0;
}

.btn-cancel {
  background: white;
  color: #666;
  border: 1px solid #ddd;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-cancel:hover {
  background: #f5f5f5;
  border-color: #ccc;
}

.btn-save {
  background: #ff9800;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-save:hover {
  background: #f57c00;
}

.btn-danger {
  background: #ff4757;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-danger:hover {
  background: #ff3742;
}

/* 响应式对话框 */
@media (max-width: 768px) {
  .dialog-container {
    width: 95%;
    max-height: 85vh;
  }

  .dialog-header {
    padding: 20px 24px;
  }

  .dialog-form {
    padding: 24px 20px;
  }

  .dialog-form .form-row {
    flex-direction: column;
    gap: 16px;
  }

  .dialog-actions {
    flex-direction: column;
    padding: 16px 20px;
  }

  .dialog-actions button {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
    gap: 20px;
  }

  .sidebar {
    width: 100%;
  }

  .user-stats {
    justify-content: center;
    gap: 30px;
  }

  .nav-menu {
    display: flex;
    overflow-x: auto;
    border-radius: 10px;
  }

  .nav-item {
    flex-shrink: 0;
    min-width: 120px;
    justify-content: center;
    border-bottom: none;
    border-right: 1px solid #f0f0f0;
  }

  .nav-item:last-child {
    border-right: none;
  }

  /* 收藏网格响应式 */
  .favorites-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }

  .order-preview {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .order-summary {
    text-align: left;
  }

  .cart-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .cart-item-controls {
    width: 100%;
    justify-content: space-between;
  }
}

/* 设置页面样式 */
.settings-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.setting-group {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  border: 1px solid #f0f0f0;
}

.setting-group h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.setting-group h3 svg {
  font-size: 20px;
  color: #ff9800;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #f8f9fa;
}

.setting-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.setting-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.setting-title {
  font-size: 16px;
  font-weight: 500;
  color: #2c3e50;
}

.setting-desc {
  font-size: 14px;
  color: #6c757d;
  line-height: 1.4;
}

.setting-action-btn {
  padding: 8px 16px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  background: #f8f9fa;
  color: #495057;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.setting-action-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.setting-action-btn.premium {
  background: linear-gradient(135deg, #ff9800, #f57c00);
  color: white;
  border-color: #ff9800;
}

.setting-action-btn.premium:hover {
  background: linear-gradient(135deg, #f57c00, #ef6c00);
  transform: translateY(-1px);
}

.setting-action-btn.danger {
  background: #dc3545;
  color: white;
  border-color: #dc3545;
}

.setting-action-btn.danger:hover {
  background: #c82333;
  border-color: #bd2130;
}

.setting-select {
  padding: 8px 12px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  background: white;
  color: #495057;
  font-size: 14px;
  min-width: 120px;
}

.setting-select:focus {
  outline: none;
  border-color: #ff9800;
  box-shadow: 0 0 0 2px rgba(255, 152, 0, 0.2);
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.3s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #ff9800;
}

input:checked + .slider:before {
  transform: translateX(26px);
}

/* 危险操作区域 */
.danger-zone {
  border-color: #dc3545;
  background: #fff5f5;
}

.danger-zone h3 {
  color: #dc3545;
}

.danger-zone h3 svg {
  color: #dc3545;
}

/* 设置保存按钮 */
.settings-actions {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

.save-settings-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #ff9800, #f57c00);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
}

.save-settings-btn:hover {
  background: linear-gradient(135deg, #f57c00, #ef6c00);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 152, 0, 0.4);
}

/* 验证码输入框 */
.verification-input {
  display: flex;
  gap: 8px;
}

.verification-input input {
  flex: 1;
}

.send-code-btn {
  padding: 8px 16px;
  background: #ff9800;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.send-code-btn:hover {
  background: #f57c00;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .setting-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .setting-action-btn {
    align-self: flex-end;
  }

  .verification-input {
    flex-direction: column;
  }

  .send-code-btn {
    align-self: flex-end;
  }
}

/* 地址管理样式 */
.content-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.content-header .header-info {
  flex: 1;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  border: 2px solid transparent;
  transition: all 0.3s;
}

.address-card:hover {
  border-color: #1976d2;
  transform: translateY(-2px);
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.address-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.recipient-name {
  font-weight: bold;
  color: #333;
  font-size: 16px;
}

.recipient-phone {
  color: #666;
  font-size: 14px;
}

.default-tag {
  background: #1976d2;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.address-actions {
  display: flex;
  gap: 15px;
}

.action-link {
  background: none;
  border: none;
  color: #1976d2;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s;
}

.action-link:hover {
  color: #0d47a1;
}

.action-link.danger {
  color: #f44336;
}

.action-link.danger:hover {
  color: #d32f2f;
}

.address-detail {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 15px;
}

.address-footer {
  display: flex;
  justify-content: flex-end;
}

.set-default-btn {
  background: none;
  border: 1px solid #1976d2;
  color: #1976d2;
  padding: 6px 15px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.set-default-btn:hover {
  background: #1976d2;
  color: white;
}

/* 购物车搜索样式 */
.cart-search {
  position: relative;
  margin-top: 15px;
}

.search-input {
  width: 100%;
  padding: 10px 40px 10px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #ff9800;
  box-shadow: 0 0 0 2px rgba(255, 152, 0, 0.1);
}

.search-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  font-size: 16px;
}

/* 购物车分页样式 */
.cart-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}

/* 购物车底部分页样式 */
.cart-pagination-bottom {
  margin-top: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.pagination-info {
  text-align: center;
  margin-bottom: 15px;
  color: #6c757d;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.page-numbers {
  display: flex;
  align-items: center;
  gap: 5px;
}

.page-number {
  min-width: 32px;
  height: 32px;
  border: 1px solid #dee2e6;
  background: white;
  color: #495057;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.page-number:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.page-number.active {
  background: #ff9800;
  border-color: #ff9800;
  color: white;
}

.page-btn {
  background: #f5f5f5;
  border: 1px solid #ddd;
  color: #666;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background: #ff9800;
  color: white;
  border-color: #ff9800;
}

.page-btn:disabled {
  background: #f0f0f0;
  color: #ccc;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

/* 收藏搜索样式 */
.favorites-search {
  position: relative;
  margin-top: 15px;
}

/* 收藏分页样式 */
.favorites-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

/* 收藏列表横条样式 */
/* 收藏页面样式 */
.favorites-content {
  display: flex;
  flex-direction: column;
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.favorite-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  border: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
  position: relative;
}

.favorite-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
  border-color: #ff9800;
}

.favorite-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.favorite-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.favorite-image svg {
  font-size: 32px;
  color: #ccc;
}

.favorite-details {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.favorite-main-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.favorite-name {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.3;
}

.favorite-description {
  margin: 0;
  color: #6c757d;
  font-size: 14px;
  line-height: 1.4;
}

.favorite-meta-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-row {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}

.meta-label {
  color: #8e8e93;
  font-weight: 500;
}

.favorite-type {
  background: #e3f2fd;
  color: #1565c0;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.favorite-time {
  color: #8e8e93;
}

.product-id {
  color: #666;
  font-family: monospace;
}

.product-brand {
  color: #333;
  font-weight: 500;
}

.favorite-actions-area {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 16px;
  min-width: 180px;
}

.favorite-price-section {
  text-align: right;
}

.current-price {
  font-size: 20px;
  font-weight: 700;
  color: #ff9800;
  line-height: 1;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
  margin-top: 4px;
}

.favorite-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.btn-view-detail,
.btn-add-cart,
.btn-remove-favorite {
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-view-detail {
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #dee2e6;
}

.btn-view-detail:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.btn-add-cart {
  background: #ff9800;
  color: white;
}

.btn-add-cart:hover {
  background: #f57c00;
  transform: translateY(-1px);
}

.btn-remove-favorite {
  background: white;
  color: #dc3545;
  border: 1px solid #dc3545;
}

.btn-remove-favorite:hover {
  background: #dc3545;
  color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .favorite-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 16px;
  }

  .favorite-image {
    align-self: center;
  }

  .favorite-details {
    width: 100%;
  }

  .favorite-main-info {
    text-align: center;
  }

  .meta-row {
    justify-content: center;
    gap: 16px;
  }

  .favorite-actions-area {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    min-width: auto;
  }

  .favorite-actions {
    flex-direction: row;
    gap: 8px;
    width: auto;
  }

  .btn-view-detail,
  .btn-add-cart,
  .btn-remove-favorite {
    font-size: 12px;
    padding: 6px 12px;
  }
}

@media (max-width: 480px) {
  .favorite-item {
    padding: 12px;
  }

  .favorite-actions-area {
    flex-direction: column;
    gap: 12px;
  }

  .favorite-actions {
    width: 100%;
  }

  .btn-view-detail,
  .btn-add-cart,
  .btn-remove-favorite {
    flex: 1;
  }
}

/* 无宠物提示样式 */
.no-pets-tip {
  margin-top: 10px;
  padding: 12px;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #856404;
}

.no-pets-tip svg {
  color: #ff9800;
}

.btn-link {
  background: none;
  border: none;
  color: #1976d2;
  text-decoration: underline;
  cursor: pointer;
  font-size: 14px;
  margin-left: 8px;
}

.btn-link:hover {
  color: #0d47a1;
}

/* 预约筛选栏样式 */
.appointment-filter-tabs {
  display: flex;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 4px;
  margin-bottom: 25px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

/* 预约列表横条样式 */
.appointments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.appointment-row {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  border-left: 4px solid #1976d2;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s;
}

.appointment-row:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.appointment-left {
  flex: 1;
  min-width: 0;
}

.appointment-main-info {
  margin-bottom: 12px;
}

.service-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.order-no {
  font-size: 12px;
  color: #666;
  font-family: monospace;
}

.appointment-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #666;
}

.meta-item svg {
  font-size: 16px;
  color: #1976d2;
}

.special-notes {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  color: #666;
  background: #f8f9fa;
  padding: 8px 12px;
  border-radius: 6px;
  margin-top: 8px;
}

.special-notes svg {
  margin-top: 2px;
  color: #1976d2;
}

.appointment-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  min-width: 120px;
}

.appointment-price {
  font-size: 20px;
  font-weight: bold;
  color: #ff9800;
}

.appointment-right {
  min-width: 200px;
}

.appointment-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

/* 预约详情对话框样式 */
.dialog-container.large {
  max-width: 700px;
}

.detail-section {
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 15px 0;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.detail-label {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.detail-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.service-detail {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.service-detail .service-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.service-detail .service-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff9800;
}

.pet-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.pet-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.pet-breed {
  font-size: 14px;
  color: #666;
}

.special-notes-detail {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  line-height: 1.5;
  color: #333;
}

.time-records {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.time-record {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: #f8f9fa;
  border-radius: 6px;
}

.record-label {
  font-size: 14px;
  color: #666;
}

.record-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .appointment-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .appointment-center {
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
    min-width: auto;
  }

  .appointment-right {
    width: 100%;
    min-width: auto;
  }

  .appointment-actions {
    justify-content: flex-start;
  }

  .appointment-meta {
    flex-direction: column;
    gap: 8px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .service-detail {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}

/* 预约列表样式 */
.appointments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.appointment-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  border: 2px solid transparent;
  transition: all 0.3s;
}

.appointment-card:hover {
  border-color: #1976d2;
  transform: translateY(-2px);
}

.appointment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.appointment-info .order-no {
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.appointment-time {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
  font-size: 14px;
}

.appointment-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.appointment-status.pending {
  background: rgba(255, 152, 0, 0.1);
  color: #ff9800;
}

.appointment-status.confirmed {
  background: rgba(33, 150, 243, 0.1);
  color: #2196f3;
}

.appointment-status.completed {
  background: rgba(76, 175, 80, 0.1);
  color: #4caf50;
}

.appointment-status.cancelled {
  background: rgba(244, 67, 54, 0.1);
  color: #f44336;
}

.appointment-details {
  margin-bottom: 15px;
}

.service-info h4 {
  margin: 0 0 5px 0;
  color: #333;
}

.service-info p {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
}

.appointment-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff9800;
}

.special-notes {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-top: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
  font-size: 14px;
  color: #666;
}

.appointment-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.form-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  transition: all 0.3s;
}

.close-btn:hover {
  background: #f5f5f5;
  color: #666;
}

.address-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.required {
  color: #f44336;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-size: 14px;
}

.checkbox-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
}

.checkmark {
  color: #666;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 10px;
}

.btn-cancel,
.btn-save {
  padding: 12px 25px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel {
  background: white;
  border: 1px solid #ddd;
  color: #666;
}

.btn-cancel:hover {
  background: #f5f5f5;
  border-color: #999;
}

.btn-save {
  background: #1976d2;
  border: none;
  color: white;
}

.btn-save:hover {
  background: #0d47a1;
  transform: translateY(-1px);
}

@media (max-width: 480px) {
  .container {
    padding: 0 15px;
  }

  .order-actions {
    flex-direction: column;
  }

  .cart-actions {
    flex-direction: column;
  }

  /* 手机端收藏网格 */
  .favorites-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
}

/* 支付倒计时样式 */
.payment-countdown {
  font-weight: 600;
  color: #ff9800;
}

.payment-countdown.countdown-warning {
  color: #dc3545;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
}

/* 支付对话框样式 */
.payment-overlay {
  backdrop-filter: blur(4px);
}

.compact-payment {
  max-width: 600px;
  max-height: 85vh;
}

.compact-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 支付摘要信息 */
.payment-summary {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-label {
  font-size: 12px;
  color: #6c757d;
}

.order-number {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  font-family: monospace;
}

.payment-amount {
  text-align: right;
}

.amount-label {
  display: block;
  color: #6c757d;
  font-size: 12px;
  margin-bottom: 4px;
}

.amount-value {
  font-size: 24px;
  font-weight: 700;
  color: #ff9800;
}

/* 支付主要内容区域 */
.payment-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  align-items: start;
}

/* 支付方式选择区域 */
.payment-methods-section h4 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 16px;
  text-align: left;
}

.compact-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.compact-option {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.compact-option.active {
  border-color: #ff9800;
  background: #fff8f0;
}

.compact-option input[type="radio"] {
  display: none;
}

.compact-option .payment-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 16px;
  flex-shrink: 0;
}

.compact-option .payment-icon.wechat {
  background: #07c160;
  color: white;
}

.compact-option .payment-icon.alipay {
  background: #1677ff;
  color: white;
}

.compact-option .payment-icon.unionpay {
  background: #e60012;
  color: white;
}

.compact-option span {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

/* 二维码区域 */
.compact-qr {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.compact-qr .qr-code {
  width: 120px;
  height: 120px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  background: #f8f9fa;
}

.compact-qr .qr-code svg {
  font-size: 80px;
  color: #ccc;
}

.compact-qr .qr-instruction {
  color: #6c757d;
  font-size: 13px;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.compact-timer {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
  color: #856404;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.compact-timer.timer-warning {
  background: #f8d7da;
  border-color: #f5c6cb;
  color: #721c24;
  animation: pulse 1s infinite;
}

.compact-timer .warning-text {
  margin-left: 4px;
  font-size: 11px;
  color: #dc3545;
}

/* 支付成功状态 */
.payment-success {
  text-align: center;
  padding: 40px 20px;
}

.success-icon {
  font-size: 64px;
  color: #28a745;
  margin-bottom: 16px;
}

.payment-success h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 20px;
}

.payment-success p {
  color: #6c757d;
  font-size: 14px;
  margin: 0;
}

/* 支付提示 */
.compact-tips {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 12px;
}

.tips-content {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6c757d;
  font-size: 13px;
  line-height: 1.4;
}

.tips-content svg {
  flex-shrink: 0;
  color: #17a2b8;
}

/* 操作按钮区域 */
.compact-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #e9ecef;
}

.compact-actions .btn-confirm-payment,
.compact-actions .btn-view-order {
  flex: 2;
  padding: 14px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.compact-actions .btn-confirm-payment {
  background: #ff9800;
  color: white;
  box-shadow: 0 2px 8px rgba(255, 152, 0, 0.3);
}

.compact-actions .btn-confirm-payment:hover:not(:disabled) {
  background: #f57c00;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.4);
}

.compact-actions .btn-confirm-payment:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.compact-actions .btn-view-order {
  background: #28a745;
  color: white;
  box-shadow: 0 2px 8px rgba(40, 167, 69, 0.3);
}

.compact-actions .btn-view-order:hover {
  background: #218838;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.4);
}

.compact-actions .btn-cancel-payment {
  flex: 1;
  background: #6c757d;
  color: white;
  border: none;
  padding: 14px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
}

.compact-actions .btn-cancel-payment:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

/* 响应式设计 - 移动端优化 */
@media (max-width: 768px) {
  .compact-payment {
    max-width: 95%;
    max-height: 95vh;
  }

  .payment-main {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .payment-summary {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .compact-actions {
    flex-direction: column;
  }

  .compact-actions .btn-confirm-payment,
  .compact-actions .btn-view-order,
  .compact-actions .btn-cancel-payment {
    flex: none;
  }
}
</style>
