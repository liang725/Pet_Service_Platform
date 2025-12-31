<!-- src/views/admin/AdminHome.vue -->
<template>
  <div class="dashboard">
    <div class="header">
      <div class="header-title">
        <h2>管理控制面板</h2>
        <p>欢迎回来，系统管理员！今天是 {{ currentDate }}</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="refreshData">
          <Icon icon="mdi:refresh" /> 刷新数据
        </button>
      </div>
    </div>

    <div class="stats-cards" v-if="dashboardData">
      <div class="stat-card">
        <div class="stat-icon"><Icon icon="mdi:calendar-check" /></div>
        <div class="stat-info">
          <h3>{{ dashboardData.todayAppointments || 0 }}</h3>
          <p>今日预约</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon"><Icon icon="mdi:paw" /></div>
        <div class="stat-info">
          <h3>{{ dashboardData.totalPets || 0 }}</h3>
          <p>注册宠物</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon"><Icon icon="mdi:currency-cny" /></div>
        <div class="stat-info">
          <h3>¥{{ dashboardData.todayIncome || 0 }}</h3>
          <p>今日收入</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon"><Icon icon="mdi:account-group" /></div>
        <div class="stat-info">
          <h3>{{ dashboardData.totalUsers || 0 }}</h3>
          <p>总用户数</p>
        </div>
      </div>
    </div>

    <div class="content-section">
      <h2 class="section-title"><Icon icon="mdi:clipboard-list" /> 待处理事项</h2>
      <div class="todo-container">
        <div class="todo-section">
          <h3><Icon icon="mdi:alert-circle" /> 待处理订单 ({{ dashboardData?.pendingOrders || 0 }})</h3>
          <p class="no-data">暂无待处理订单</p>
        </div>
        <div class="todo-section">
          <h3><Icon icon="mdi:star" /> 待回复评价 ({{ dashboardData?.pendingReviews || 0 }})</h3>
          <p class="no-data">暂无待回复评价</p>
        </div>
      </div>
    </div>

    <div class="footer">
      <p><Icon icon="mdi:paw" /> 宠物之家管理端系统 © {{ currentYear }} <Icon icon="mdi:paw" /></p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { Icon } from '@iconify/vue'

const userStore = useUserStore()
const dashboardData = ref(null)
const currentDate = ref('')
const currentYear = new Date().getFullYear()

onMounted(() => {
  loadDashboardData()
  setCurrentDate()
})

const setCurrentDate = () => {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
  })
}

const loadDashboardData = async () => {
  try {
    dashboardData.value = await userStore.getAdminDashboard()
  } catch (error) {
    console.error('加载仪表板数据失败:', error)
  }
}

const refreshData = () => loadDashboardData()
</script>

<style scoped>
.dashboard { padding: 0; }

.header {
  background-color: #FFEAA7;
  padding: 20px;
  border-radius: 15px;
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title h2 { color: #E17055; font-size: 1.8rem; margin: 0; }
.header-title p { color: #8B5A2B; margin: 5px 0 0 0; }

.btn {
  padding: 10px 20px;
  border-radius: 50px;
  font-weight: bold;
  cursor: pointer;
  border: none;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary { background-color: #FF9F43; color: white; }
.btn-primary:hover { transform: translateY(-3px); }

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}

.stat-card {
  background-color: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 8px 20px rgba(255, 200, 100, 0.2);
  display: flex;
  align-items: center;
  gap: 20px;
  border-top: 8px solid #FF9F43;
}

.stat-card:nth-child(2) { border-top-color: #FFD166; }
.stat-card:nth-child(3) { border-top-color: #FFB347; }
.stat-card:nth-child(4) { border-top-color: #E17055; }

.stat-icon {
  font-size: 2.5rem;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background-color: #FF9F43;
}

.stat-card:nth-child(2) .stat-icon { background-color: #FFD166; }
.stat-card:nth-child(3) .stat-icon { background-color: #FFB347; }
.stat-card:nth-child(4) .stat-icon { background-color: #E17055; }

.stat-info h3 { font-size: 2rem; color: #E17055; margin: 0 0 5px 0; }
.stat-info p { color: #8B5A2B; margin: 0; }

.content-section {
  background-color: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 8px 20px rgba(255, 200, 100, 0.2);
  margin-bottom: 30px;
}

.section-title {
  font-size: 1.5rem;
  color: #E17055;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 3px solid #FFD166;
  display: flex;
  align-items: center;
  gap: 10px;
}

.todo-container { display: flex; flex-wrap: wrap; gap: 20px; }
.todo-section { flex: 1; min-width: 300px; }
.todo-section h3 { color: #E17055; margin-bottom: 15px; display: flex; align-items: center; gap: 8px; }
.no-data { color: #8B5A2B; font-style: italic; text-align: center; }

.footer {
  text-align: center;
  padding: 20px;
  color: #8B5A2B;
  border-top: 2px solid #FFD166;
  margin-top: 30px;
}
</style>
