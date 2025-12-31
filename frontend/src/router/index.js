// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/auth',
      name: 'auth',
      component: () => import('../views/AuthView.vue'),
      meta: {
        requiresGuest: true, // 只有未登录用户可以访问
        title: '用户认证',
        hideHeader: true,    // 隐藏导航栏
        hideFooter: true     // 隐藏页脚
      },
    },
    {
      // 保持向后兼容，将/register重定向到/auth?mode=register
      path: '/register',
      redirect: () => {
        return { path: '/auth', query: { mode: 'register' } }
      },
    },
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: {
        requiresAuth: true, // 需要登录才能访问
        title: '首页',
        hideHeader: false,   // 显示导航栏
        hideFooter: false    // 显示页脚
      },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: {
        requiresAuth: true, // 需要登录才能访问
        title: '个人中心',
        hideHeader: false,   // 显示导航栏
        hideFooter: false    // 显示页脚
      },
    },
    {
      path: '/pets',
      name: 'pets',
      component: () => import('../views/PetManageView.vue'),
      meta: {
        requiresAuth: true,
        title: '我的宠物',
        hideHeader: false,
        hideFooter: false
      },
    },
    {
      path: '/pets/add',
      name: 'addPet',
      component: () => import('../views/AddPetView.vue'),
      meta: {
        requiresAuth: true,
        title: '添加宠物',
        hideHeader: false,
        hideFooter: false
      },
    },
    {
      path: '/pets/:id/edit',
      name: 'editPet',
      component: () => import('../views/AddPetView.vue'),
      meta: {
        requiresAuth: true,
        title: '编辑宠物',
        hideHeader: false,
        hideFooter: false
      },
      props: true
    },
    {
      path: '/service/appointment',
      name: 'appointment',
      component: () => import('../views/ServiceAppointmentView.vue'),
      meta: {
        requiresAuth: true,
        title: '预约洗护',
        hideHeader: false,
        hideFooter: false
      },
    },
    {
      path: '/service/appointment/:id',
      name: 'appointmentDetail',
      component: () => import('../views/ServiceAppointmentDetailView.vue'),
      meta: {
        requiresAuth: true,
        title: '预约详情',
        hideHeader: false,
        hideFooter: false
      },
      props: true
    },
    {
      path: '/service/orders',
      name: 'serviceOrders',
      component: () => import('../views/ServiceOrdersView.vue'),
      meta: {
        requiresAuth: true,
        title: '我的预约',
        hideHeader: false,
        hideFooter: false
      },
    },
    {
      path: '/products',
      name: 'productList',
      component: () => import('../views/ProductListView.vue'),
      meta: {
        requiresAuth: false,
        title: '宠物商店',
        hideHeader: false,
        hideFooter: false
      }
    },
    {
      path: '/product/:id',
      name: 'productDetail',
      component: () => import('../views/ProductDetailView.vue'),
      meta: {
        requiresAuth: false,
        title: '商品详情',
        hideHeader: false,
        hideFooter: false
      },
      props: true
    },
    {
      path: '/user-center',
      name: 'userCenter',
      component: () => import('../views/UserCenterView.vue'),
      meta: {
        requiresAuth: true,
        title: '个人中心',
        hideHeader: false,
        hideFooter: false
      }
    },
    {
      path: '/checkout',
      name: 'checkout',
      component: () => import('../views/CheckoutView.vue'),
      meta: {
        requiresAuth: true,
        title: '结算页面',
        hideHeader: false,
        hideFooter: false
      }
    },
    // 管理后台路由
    {
      path: '/admin',
      component: () => import('@/views/admin/AdminLayout.vue'),
      meta: {
        requiresAuth: true,
        requiresAdmin: true,
        title: '管理后台',
        hideHeader: true,
        hideFooter: true
      },
      children: [
        {
          path: '',
          name: 'adminDashboard',
          component: () => import('@/views/admin/AdminHome.vue'),
          meta: { title: '控制面板' }
        },
        {
          path: 'users',
          name: 'adminUsers',
          component: () => import('@/views/admin/UserManagementView.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'pets',
          name: 'adminPets',
          component: () => import('@/views/admin/PetManagementView.vue'),
          meta: { title: '宠物档案' }
        },
        {
          path: 'appointments',
          name: 'adminAppointments',
          component: () => import('@/views/admin/AppointmentManagementView.vue'),
          meta: { title: '预约管理' }
        },
        {
          path: 'products',
          name: 'adminProducts',
          component: () => import('@/views/admin/ProductManagementView.vue'),
          meta: { title: '商城管理' }
        },
        {
          path: 'products/add',
          name: 'adminProductAdd',
          component: () => import('@/views/admin/AddProductView.vue'),
          meta: { title: '添加商品' }
        },
        {
          path: 'products/edit/:id',
          name: 'adminProductEdit',
          component: () => import('@/views/admin/AddProductView.vue'),
          meta: { title: '编辑商品' },
          props: true
        }
      ]
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

  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && (!userStore.isLoggedIn || !userStore.isAdmin)) {
    alert('您没有管理员权限')
    next({ name: 'home' })
    return
  }

  // 已登录用户访问登录页面，重定向到首页或管理后台
  if (to.meta.requiresGuest && userStore.isLoggedIn) {
    if (userStore.isAdmin) {
      next({ name: 'adminDashboard' })
    } else {
      next({ name: 'home' })
    }
    return
  }

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 宠物之家`
  }

  next()
})

export default router
