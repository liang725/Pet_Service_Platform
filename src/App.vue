<script setup>
import { RouterLink, RouterView } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { onMounted } from 'vue'

const userStore = useUserStore()

onMounted(() => {
  userStore.initializeAuth()
})
</script>

<template>
  <div class="app">
    <header class="app-header">
      <div class="header-content">
        <!-- Logo和标题 -->
        <div class="brand">
          <div class="brand-icon">🐾</div>
          <div class="brand-text">
            <h1 class="brand-title">宠物之家</h1>
            <p class="brand-subtitle">给爱宠最温暖的家</p>
          </div>
        </div>

        <!-- 导航菜单 -->
        <nav class="nav">
          <RouterLink to="/" class="nav-link">
            <span class="nav-icon">🏠</span>
            首页
          </RouterLink>
          <RouterLink to="/about" class="nav-link">
            <span class="nav-icon">ℹ️</span>
            关于我们
          </RouterLink>
        </nav>

        <!-- 用户区域 -->
        <div class="user-area">
          <div v-if="userStore.isLoggedIn" class="user-info">
            <RouterLink to="/profile" class="user-profile">
              <span class="user-avatar">{{ userStore.getAvatar }}</span>
              <span class="user-name">{{ userStore.getDisplayName }}</span>
            </RouterLink>
          </div>
          <div v-else class="auth-buttons">
            <RouterLink to="/auth" class="auth-btn"> 登录/注册 </RouterLink>
          </div>
        </div>
      </div>
    </header>

    <main class="app-main">
      <RouterView />
    </main>

    <footer class="app-footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>宠物之家</h3>
          <p>专业的宠物服务平台，为您的爱宠提供全方位的关爱。</p>
        </div>
        <div class="footer-section">
          <h4>服务项目</h4>
          <ul>
            <li>宠物档案管理</li>
            <li>洗护预约服务</li>
            <li>宠物用品商店</li>
            <li>AI智能咨询</li>
          </ul>
        </div>
        <div class="footer-section">
          <h4>联系我们</h4>
          <p>📞 400-888-8888</p>
          <p>📧 service@pethome.com</p>
          <p>🏠 北京市朝阳区宠物大街123号</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2024 宠物之家. 用爱守护每一个小生命 💕</p>
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

.app-header {
  background: var(--pet-primary);
  box-shadow: var(--pet-shadow);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 100%;
  margin: 0 auto;
  padding: 0 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 80px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-icon {
  font-size: 36px;
  animation: bounce 2s infinite;
}

.brand-text {
  color: white;
}

.brand-title {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  line-height: 1.2;
}

.brand-subtitle {
  margin: 0;
  font-size: 12px;
  opacity: 0.9;
  line-height: 1;
}

.nav {
  display: flex;
  gap: 20px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: white;
  text-decoration: none;
  padding: 10px 16px;
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
}

.nav-link:hover,
.nav-link.router-link-active {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.nav-icon {
  font-size: 16px;
}

.user-area {
  display: flex;
  align-items: center;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.user-profile:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.user-avatar {
  font-size: 24px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-name {
  font-weight: 500;
  font-size: 14px;
}

.auth-btn {
  color: white;
  text-decoration: none;
  padding: 10px 20px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  font-weight: 500;
  transition: all 0.3s ease;
}

.auth-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.app-main {
  flex: 1;
  background: var(--pet-primary-lighter);
}

.app-footer {
  background: var(--pet-black-soft);
  color: white;
  margin-top: auto;
}

.footer-content {
  max-width: 100%;
  margin: 0 auto;
  padding: 40px 40px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.footer-section h3,
.footer-section h4 {
  margin: 0 0 15px 0;
  color: var(--pet-primary);
}

.footer-section p,
.footer-section li {
  margin: 5px 0;
  color: var(--pet-text-light);
  line-height: 1.6;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-bottom {
  border-top: 1px solid #636e72;
  padding: 20px;
  text-align: center;
  color: #b2bec3;
}

@keyframes bounce {
  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-8px);
  }
  60% {
    transform: translateY(-4px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 15px;
    padding: 15px 20px;
    min-height: auto;
  }

  .brand {
    order: 1;
  }

  .nav {
    order: 3;
    gap: 10px;
  }

  .user-area {
    order: 2;
  }

  .nav-link {
    padding: 8px 12px;
    font-size: 14px;
  }

  .brand-title {
    font-size: 20px;
  }

  .brand-icon {
    font-size: 30px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    text-align: center;
    padding: 40px 20px;
  }
}

@media (max-width: 480px) {
  .nav {
    flex-direction: column;
    width: 100%;
  }

  .nav-link {
    justify-content: center;
  }
}
</style>
