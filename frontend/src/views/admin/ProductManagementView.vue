<template>
  <div class="product-management">
    <div class="page-header">
      <h1>商城管理</h1>
      <p>管理商品信息、库存和状态</p>
    </div>

    <!-- 空白页面，准备重新构建 -->
    <div class="content-card">
      <div class="empty-state">
        <Icon icon="mdi:package-variant" class="empty-icon" />
        <h3>商品管理功能重构中</h3>
        <p>正在重新构建商品管理功能，敬请期待...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Icon } from '@iconify/vue'
</script>

<style scoped>
.product-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 26px;
}

.page-header p {
  margin: 0;
  color: #666;
}

.content-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  opacity: 0.5;
  margin-bottom: 15px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  color: #666;
}

.empty-state p {
  margin: 0;
}
</style>
