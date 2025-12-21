<!-- src/App.vue -->
<script setup>
import { RouterView, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { onMounted, computed } from 'vue'
import ShoppingCart from '@/components/ShoppingCart.vue'

const userStore = useUserStore()
const route = useRoute()

// 计算是否显示导航栏
const showHeader = computed(() => {
  return !route.meta.hideHeader
})

const showFooter = computed(() => {
  return !route.meta.hideFooter
})

onMounted(() => {
  userStore.checkAuthStatus() // 使用我们store中确实存在的方法
})
</script>

<template>
  <div class="app">
    <!-- 条件渲染头部导航栏 -->
    <header v-if="showHeader" class="app-header">
      <div class="container">
        <div class="header-content">
          <!-- Logo和标题 -->
          <div class="logo">
            <div class="logo-icon">
              <Icon icon="mdi:paw" />
            </div>
            <div class="logo-text">
              <h1>宠物之家</h1>
              <p>一站式宠物服务平台</p>
            </div>
          </div>

          <!-- 导航菜单 -->
          <nav class="nav-container">
            <ul class="nav-menu">
              <li class="nav-item">
                <router-link to="/" class="nav-link">
                  <Icon icon="mdi:home" class="nav-icon" />
                  <span>首页</span>
                </router-link>
              </li>
              <li class="nav-item">
                <router-link to="/service/appointment" class="nav-link">
                  <Icon icon="mdi:calendar-check" class="nav-icon" />
                  <span>预约洗护</span>
                </router-link>
              </li>
              <li class="nav-item">
                <router-link to="/pets" class="nav-link">
                  <Icon icon="mdi:paw" class="nav-icon" />
                  <span>宠物档案</span>
                </router-link>
              </li>
              <li class="nav-item">
                <router-link to="/products" class="nav-link">
                  <Icon icon="mdi:cart" class="nav-icon" />
                  <span>宠物商店</span>
                </router-link>
              </li>
              <li class="nav-item">
                <a href="#" class="nav-link">
                  <Icon icon="mdi:robot" class="nav-icon" />
                  <span>AI咨询</span>
                </a>
              </li>
              <li class="nav-item">
                <router-link to="/service/orders" class="nav-link">
                  <Icon icon="mdi:clipboard-list" class="nav-icon" />
                  <span>我的预约</span>
                </router-link>
              </li>
            </ul>
          </nav>

          <!-- 用户区域 -->
          <div class="user-area">
            <div v-if="userStore.isLoggedIn" class="user-info">
              <router-link to="/profile" class="user-profile">
                <span class="user-avatar">{{ userStore.getAvatar }}</span>
                <span class="user-name">{{ userStore.getDisplayName }}</span>
              </router-link>
            </div>
            <div v-else class="auth-buttons">
              <router-link to="/auth" class="btn btn-login">
                <Icon icon="mdi:login" />
                用户登录
              </router-link>
              <router-link to="/auth?mode=register" class="btn btn-register">
                <Icon icon="mdi:account-plus" />
                用户注册
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main class="app-main">
      <RouterView />
    </main>

    <!-- 购物车悬浮组件 -->
    <ShoppingCart />

    <!-- 条件渲染页脚 -->
    <footer v-if="showFooter" class="app-footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-column">
            <h3>宠物之家</h3>
            <p>我们致力于为宠物主人提供最优质、最便捷的一站式宠物服务，让每一只宠物都能得到最好的照顾。</p>
            <div class="pet-icons">
              <Icon icon="mdi:paw" />
              <Icon icon="mdi:cat" />
              <Icon icon="mdi:dog" />
            </div>
          </div>

          <div class="footer-column">
            <h3>快速链接</h3>
            <ul class="footer-links">
              <li><router-link to="/"><Icon icon="mdi:chevron-right" /> 返回首页</router-link></li>
              <li><router-link to="/service/appointment"><Icon icon="mdi:chevron-right" /> 预约洗护</router-link></li>
              <li><router-link to="/pets"><Icon icon="mdi:chevron-right" /> 宠物档案</router-link></li>
              <li><router-link to="/service/orders"><Icon icon="mdi:chevron-right" /> 我的预约</router-link></li>
            </ul>
          </div>

          <div class="footer-column">
            <h3>联系我们</h3>
            <ul class="footer-links">
              <li><a href="#"><Icon icon="mdi:map-marker" /> 杭州市钱塘区宠物之家大厦</a></li>
              <li><a href="tel:400-123-4567"><Icon icon="mdi:phone" /> 400-123-4567</a></li>
              <li><a href="mailto:service@pethome.com"><Icon icon="mdi:email" /> PetHome@pethome.com</a></li>
              <li><a href="#"><Icon icon="mdi:wechat" /> 微信公众号: 宠物之家</a></li>
            </ul>
          </div>
        </div>

        <div class="copyright">
          <p>
            <Icon icon="mdi:paw" class="paw-print" />
            版权所有 © 2025 宠物之家 一站式宠物服务平台
            <Icon icon="mdi:paw" class="paw-print" />
          </p>
          <p class="footer-slogan">让每一只宠物都感受到家的温暖</p>
        </div>
      </div>
    </footer>
  </div>
</template>


<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 头部样式 */
.app-header {
  background-color: var(--pet-primary-lighter);
  padding: 15px 0;
  box-shadow: 0 4px 12px rgba(255, 200, 100, 0.2);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 30px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-shrink: 0;
}

.logo-icon {
  font-size: 2.4rem;
  color: var(--pet-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon :deep(svg) {
  width: 2.4rem;
  height: 2.4rem;
  color: var(--pet-primary);
}

.logo-text h1 {
  font-size: 1.8rem;
  color: var(--pet-primary-dark);
  text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
  margin: 0;
  white-space: nowrap;
}

.logo-text p {
  font-size: 0.8rem;
  color: #B55A3C;
  margin-top: 2px;
  margin: 0;
  white-space: nowrap;
}

/* 导航菜单样式 */
.nav-container {
  flex: 1;
  max-width: 600px;
}

.nav-menu {
  display: flex;
  list-style: none;
  justify-content: center;
  padding: 0;
  margin: 0;
  gap: 5px;
}

.nav-item {
  position: relative;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--pet-text-secondary);
  text-decoration: none;
  font-weight: bold;
  padding: 8px 16px;
  border-radius: 30px;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  white-space: nowrap;
}

.nav-link.router-link-active {
  background-color: var(--pet-primary);
  color: var(--pet-text-light);
}

.nav-link:hover {
  background-color: var(--pet-primary);
  color: var(--pet-text-light);
  transform: translateY(-2px);
}

.nav-icon {
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-icon :deep(svg) {
  width: 1.1rem;
  height: 1.1rem;
}

/* 用户区域样式 */
.user-area {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.btn {
  padding: 8px 16px;
  border-radius: 50px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  font-size: 0.9rem;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.btn :deep(svg) {
  width: 0.9rem;
  height: 0.9rem;
}

.btn-login {
  background-color: var(--pet-primary-light);
  color: var(--pet-text-secondary);
}

.btn-register {
  background-color: var(--pet-primary);
  color: var(--pet-text-light);
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(255, 159, 67, 0.3);
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--pet-text-secondary);
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 30px;
  background-color: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
  font-size: 0.9rem;
  white-space: nowrap;
}

.user-profile:hover {
  background-color: rgba(255, 179, 71, 0.2);
  transform: translateY(-2px);
}

.user-avatar {
  font-size: 1.2rem;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--pet-primary);
  color: white;
}

.user-name {
  font-weight: bold;
  font-size: 0.9rem;
  color: var(--pet-text-secondary);
}

.app-main {
  flex: 1;
}

/* 页脚样式 */
.app-footer {
  background-color: var(--pet-primary-light);
  padding: 40px 0;
  margin-top: 60px;
  border-top-left-radius: 30px;
  border-top-right-radius: 30px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 40px;
}

.footer-column {
  flex: 1;
  min-width: 250px;
}

.footer-column h3 {
  color: var(--pet-primary-dark);
  margin-bottom: 20px;
  font-size: 1.5rem;
}

.footer-column p {
  color: var(--pet-text-secondary);
  line-height: 1.6;
}

.pet-icons {
  margin-top: 20px;
  font-size: 2rem;
  color: var(--pet-primary-dark);
  display: flex;
  gap: 15px;
}

.pet-icons :deep(svg) {
  width: 2rem;
  height: 2rem;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 12px;
}

.footer-links a {
  color: var(--pet-text-secondary);
  text-decoration: none;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 10px;
}

.footer-links a:hover {
  color: var(--pet-primary-dark);
  transform: translateX(5px);
}

.footer-links :deep(svg) {
  width: 1rem;
  height: 1rem;
}

.copyright {
  text-align: center;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 2px solid var(--pet-primary);
  color: var(--pet-text-secondary);
}

.paw-print {
  color: var(--pet-primary);
  font-size: 1.5rem;
  margin: 0 5px;
  vertical-align: middle;
}

.paw-print :deep(svg) {
  width: 1.5rem;
  height: 1.5rem;
  color: var(--pet-primary);
}

.footer-slogan {
  margin-top: 10px;
  font-style: italic;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .header-content {
    gap: 20px;
  }

  .nav-menu {
    gap: 3px;
  }

  .nav-link {
    padding: 8px 12px;
    font-size: 0.85rem;
  }

  .logo-text h1 {
    font-size: 1.6rem;
  }

  .logo-icon {
    font-size: 2rem;
  }

  .logo-icon :deep(svg) {
    width: 2rem;
    height: 2rem;
  }
}

@media (max-width: 900px) {
  .header-content {
    flex-wrap: wrap;
    justify-content: center;
    gap: 15px;
  }

  .logo {
    order: 1;
    flex: 1;
  }

  .nav-container {
    order: 3;
    width: 100%;
    max-width: 100%;
    margin-top: 10px;
  }

  .nav-menu {
    justify-content: center;
    flex-wrap: wrap;
    gap: 5px;
  }

  .user-area {
    order: 2;
    flex: 1;
    justify-content: flex-end;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }

  .app-header {
    padding: 10px 0;
  }

  .nav-link {
    padding: 6px 10px;
    font-size: 0.8rem;
  }

  .btn {
    padding: 6px 12px;
    font-size: 0.8rem;
  }

  .logo-text h1 {
    font-size: 1.4rem;
  }

  .logo-text p {
    font-size: 0.7rem;
  }

  .footer-content {
    flex-direction: column;
    gap: 30px;
  }
}

@media (max-width: 480px) {
  .header-content {
    flex-direction: column;
    gap: 10px;
  }

  .logo {
    order: 1;
    width: 100%;
    justify-content: center;
  }

  .nav-container {
    order: 2;
    width: 100%;
  }

  .nav-menu {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
    gap: 5px;
  }

  .nav-link {
    padding: 6px 10px;
    font-size: 0.75rem;
  }

  .nav-icon {
    font-size: 1rem;
  }

  .nav-icon :deep(svg) {
    width: 1rem;
    height: 1rem;
  }

  .user-area {
    order: 3;
    width: 100%;
    justify-content: center;
    margin-top: 10px;
  }

  .btn {
    padding: 8px 16px;
    font-size: 0.8rem;
  }

  .logo-text h1 {
    font-size: 1.3rem;
  }

  .logo-icon {
    font-size: 1.8rem;
  }

  .logo-icon :deep(svg) {
    width: 1.8rem;
    height: 1.8rem;
  }
}
</style>
