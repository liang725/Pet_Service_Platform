<!-- src/views/admin/AdminLayout.vue -->
<template>
  <div class="admin-layout">
    <!-- 左侧导航栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo">
          <div class="logo-icon">
            <Icon icon="mdi:paw" />
          </div>
          <div class="logo-text">
            <h1>宠物之家</h1>
            <p>管理端系统</p>
          </div>
        </div>
      </div>

      <div class="user-info">
        <div class="user-avatar">
          <Icon icon="mdi:cog" />
        </div>
        <div class="user-details">
          <h3>{{ userStore.getDisplayName }}</h3>
          <p>系统管理员</p>
        </div>
      </div>

      <ul class="nav-menu">
        <!-- 移除 Dashboard 菜单项，直接从用户管理开始 -->
        <li class="nav-item">
          <router-link to="/admin/users" class="nav-link" :class="{ active: activeNav === 'users' }" @click="setActiveNav('users')">
            <Icon icon="mdi:account-group" class="nav-icon" />
            <span class="nav-text">用户管理</span>
          </router-link>
        </li>
        <li class="nav-item">
          <router-link to="/admin/pets" class="nav-link" :class="{ active: activeNav === 'pets' }" @click="setActiveNav('pets')">
            <Icon icon="mdi:paw" class="nav-icon" />
            <span class="nav-text">宠物档案</span>
          </router-link>
        </li>
        <li class="nav-item">
          <router-link to="/admin/appointments" class="nav-link" :class="{ active: activeNav === 'appointments' }" @click="setActiveNav('appointments')">
            <Icon icon="mdi:calendar-check" class="nav-icon" />
            <span class="nav-text">预约管理</span>
          </router-link>
        </li>
        <li class="nav-item">
          <router-link to="/admin/products" class="nav-link" :class="{ active: activeNav === 'products' }" @click="setActiveNav('products')">
            <Icon icon="mdi:cart" class="nav-icon" />
            <span class="nav-text">商城管理</span>
          </router-link>
        </li>
      </ul>

      <div class="sidebar-footer">
        <p style="color: #8B5A2B; font-size: 0.85rem; margin-bottom: 15px;">
          <Icon icon="mdi:shield-check" />
          安全登录
        </p>
        <button class="logout-btn" @click="handleLogout">
          <Icon icon="mdi:logout" />
          <span>退出登录</span>
        </button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Icon } from '@iconify/vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const activeNav = ref('users') // 默认选中用户管理

const setActiveNav = (nav) => {
  activeNav.value = nav
}

// 监听路由变化，更新活动导航
watch(() => route.name, (newRouteName) => {
  if (newRouteName === 'adminUsers') activeNav.value = 'users'
  else if (newRouteName === 'adminPets') activeNav.value = 'pets'
  else if (newRouteName === 'adminAppointments') activeNav.value = 'appointments'
  else if (newRouteName === 'adminProducts' || newRouteName === 'adminProductAdd' || newRouteName === 'adminProductEdit') activeNav.value = 'products'
})

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    userStore.logout()
    router.push('/auth')
  }
}
</script>

<style scoped>
.admin-layout {
  background-color: #fafaf9;
  color: #44403c;
  display: flex;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.6;
}

.sidebar {
  width: 260px;
  background: linear-gradient(180deg, #fef3e2 0%, #fde8c8 100%);
  box-shadow: 2px 0 20px rgba(251, 191, 36, 0.15);
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  z-index: 100;
  border-right: 1px solid rgba(251, 191, 36, 0.2);
}

.sidebar-header {
  padding: 24px 20px;
  background: rgba(251, 191, 36, 0.08);
  border-bottom: 1px solid rgba(251, 191, 36, 0.15);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 2.2rem;
  color: #f59e0b;
  filter: drop-shadow(0 2px 4px rgba(245, 158, 11, 0.3));
}

.logo-text h1 {
  font-size: 1.5rem;
  color: #92400e;
  margin: 0;
  font-weight: 700;
  letter-spacing: 0.3px;
}

.logo-text p {
  font-size: 0.75rem;
  color: #b45309;
  margin: 2px 0 0 0;
  font-weight: 500;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  margin: 12px 15px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid rgba(251, 191, 36, 0.2);
  box-shadow: 0 2px 8px rgba(251, 191, 36, 0.1);
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.user-details h3 {
  color: #78350f;
  font-size: 1rem;
  margin: 0 0 4px 0;
  font-weight: 600;
}

.user-details p {
  color: #a16207;
  font-size: 0.8rem;
  margin: 0;
}

.nav-menu {
  list-style: none;
  padding: 15px 10px;
  flex-grow: 1;
  margin: 0;
}

.nav-item {
  margin-bottom: 4px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #78350f;
  text-decoration: none;
  font-weight: 500;
  padding: 12px 16px;
  border-radius: 10px;
  transition: all 0.3s ease;
  position: relative;
}

.nav-link:hover {
  background: rgba(251, 191, 36, 0.15);
  color: #92400e;
  transform: translateX(4px);
}

.nav-link.active {
  background: linear-gradient(90deg, rgba(251, 191, 36, 0.25) 0%, rgba(251, 191, 36, 0.1) 100%);
  color: #92400e;
  font-weight: 600;
  border-left: 4px solid #f59e0b;
  padding-left: 12px;
}

.nav-icon {
  font-size: 1.3rem;
  min-width: 24px;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(251, 191, 36, 0.15);
  background: rgba(251, 191, 36, 0.05);
}

.sidebar-footer p {
  color: #a16207;
  font-size: 0.8rem;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.logout-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(239, 68, 68, 0.4);
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
}

.logout-btn:active {
  transform: translateY(0);
}

.main-content {
  flex: 1;
  margin-left: 260px;
  padding: 20px;
  background-color: #fafaf9;
}

@media (max-width: 992px) {
  .sidebar { width: 70px; }
  .main-content { margin-left: 70px; }
  .logo-text, .user-details, .nav-text, .sidebar-footer p { display: none; }
  .nav-link { justify-content: center; padding: 15px 0; }
  .user-info { justify-content: center; }
}
</style>
