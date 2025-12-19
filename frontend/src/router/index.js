import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/auth',
      name: 'auth',
      component: () => import('../views/LoginView.vue'),
      meta: {
        requiresGuest: true, // 只有未登录用户可以访问
        title: '用户登录',
      },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
      meta: {
        requiresGuest: true, // 只有未登录用户可以访问
        title: '用户注册',
      },
    },
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: {
        requiresAuth: true, // 需要登录才能访问
        title: '首页',
      },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: {
        requiresAuth: true, // 需要登录才能访问
        title: '个人中心',
      },
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 检查登录状态
  userStore.checkAuthStatus()

  // 需要登录的页面
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'auth' })
    return
  }

  // 已登录用户访问登录页面，重定向到首页
  if (to.meta.requiresGuest && userStore.isLoggedIn) {
    next({ name: 'home' })
    return
  }

  next()
})

export default router
