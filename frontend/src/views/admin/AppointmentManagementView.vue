<!-- src/views/admin/AppointmentManagementView.vue -->
<template>
  <div class="appointment-management">
    <div class="page-header">
      <h1>预约管理</h1>
      <p>管理所有预约订单</p>
    </div>
    <div class="content-card">
      <div class="card-empty">
        <Icon icon="mdi:calendar-check" class="empty-icon" />
        <h3>预约管理功能开发中</h3>
        <p>正在开发预约列表、筛选、状态管理和统计功能</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Icon } from '@iconify/vue'
</script>

<style scoped>
.appointment-management { padding: 20px; }
.page-header { margin-bottom: 30px; }
.page-header h1 { margin: 0 0 10px 0; color: #333; font-size: 28px; }
.page-header p { margin: 0; color: #666; }
.content-card {
  background: white;
  border-radius: 15px;
  padding: 50px 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  text-align: center;
}
.card-empty { display: flex; flex-direction: column; align-items: center; gap: 20px; }
.empty-icon { font-size: 80px; color: #ffb347; opacity: 0.5; }
.card-empty h3 { margin: 0; color: #666; font-size: 24px; }
.card-empty p { margin: 0; color: #999; }
</style>
