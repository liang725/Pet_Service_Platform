<!-- src/views/admin/UserManagementView.vue -->
<template>
  <div class="user-management">
    <div class="page-header">
      <h1>用户管理</h1>
      <p>管理所有注册用户信息</p>
    </div>
    <div class="content-card">
      <div class="card-empty">
        <Icon icon="mdi:account-group" class="empty-icon" />
        <h3>用户管理功能开发中</h3>
        <p>正在开发用户列表、搜索、编辑和删除功能</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Icon } from '@iconify/vue'
</script>

<style scoped>
.user-management { padding: 20px; }
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
.empty-icon { font-size: 80px; color: #ffd166; opacity: 0.5; }
.card-empty h3 { margin: 0; color: #666; font-size: 24px; }
.card-empty p { margin: 0; color: #999; }
</style>
