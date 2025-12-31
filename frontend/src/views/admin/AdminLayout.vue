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
        <li class="nav-item">
          <router-link to="/admin/dashboard" class="nav-link" :class="{ active: activeNav === 'dashboard' }" @click="setActiveNav('dashboard')">
            <Icon icon="mdi:view-dashboard" class="nav-icon" />
            <span class="nav-text">控制面板</span>
          </router-link>
        </li>
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
const activeNav = ref('dashboard')

const setActiveNav = (nav) => {
  activeNav.value = nav
}

// 监听路由变化，更新活动导航
watch(() => route.name, (newRouteName) => {
  if (newRouteName === 'adminDashboard') activeNav.value = 'dashboard'
  else if (newRouteName === 'adminUsers') activeNav.value = 'users'
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
  background-color: #FFF9E6;
  color: #5A4B3A;
  display: flex;
  min-height: 100vh;
  font-family: 'Arial Rounded MT Bold', 'Arial', sans-serif;
  line-height: 1.6;
}

.sidebar {
  width: 250px;
  background-color: #FFD166;
  box-shadow: 4px 0 15px rgba(255, 159, 67, 0.2);
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  z-index: 100;
}

.sidebar-header {
  padding: 25px 20px;
  background-color: #FFEAA7;
  border-bottom: 3px solid #FFB347;
}

.logo {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo-icon {
  font-size: 2.5rem;
  color: #FF9F43;
}

.logo-text h1 {
  font-size: 1.8rem;
  color: #E17055;
  margin: 0;
}

.logo-text p {
  font-size: 0.85rem;
  color: #B55A3C;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  border-bottom: 2px solid #FFB347;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #FF9F43;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  color: white;
}

.user-details h3 {
  color: #E17055;
  font-size: 1.2rem;
  margin: 0 0 5px 0;
}

.user-details p {
  color: #8B5A2B;
  font-size: 0.9rem;
  margin: 0;
}

.nav-menu {
  list-style: none;
  padding: 20px 0;
  flex-grow: 1;
  margin: 0;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #8B5A2B;
  text-decoration: none;
  font-weight: bold;
  padding: 15px 20px;
  border-left: 5px solid transparent;
  transition: all 0.3s ease;
}

.nav-link:hover, .nav-link.active {
  background-color: #FFB347;
  color: white;
  border-left-color: #FF9F43;
}

.nav-icon {
  font-size: 1.3rem;
}

.sidebar-footer {
  padding: 20px;
  border-top: 2px solid #FFB347;
  background-color: #FFEAA7;
}

.logout-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  padding: 12px;
  background-color: #FF9F43;
  color: white;
  border: none;
  border-radius: 30px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background-color: #E17055;
  transform: translateY(-3px);
}

.main-content {
  flex: 1;
  margin-left: 250px;
  padding: 20px;
  background-color: #FFF9E6;
}

@media (max-width: 992px) {
  .sidebar { width: 70px; }
  .main-content { margin-left: 70px; }
  .logo-text, .user-details, .nav-text, .sidebar-footer p { display: none; }
  .nav-link { justify-content: center; padding: 15px 0; }
}
</style>
