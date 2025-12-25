<template>
  <div class="profile-container">
    <div class="profile-header">
      <div class="user-avatar">{{ userStore.getAvatar }}</div>
      <div class="user-info">
        <h2 class="user-name">{{ userStore.getDisplayName }}</h2>
        <p class="user-title">宠物爱好者</p>
      </div>
      <button @click="handleLogout" class="logout-btn">
        <Icon icon="mdi:logout" class="logout-icon" />
        退出登录
      </button>
    </div>

    <div class="profile-content">
      <div class="welcome-message">
        <h3>
          <Icon icon="mdi:home" class="welcome-icon" />
          欢迎来到宠物之家！
        </h3>
        <p>
          在这里，您可以为您的爱宠创建档案、预约洗护服务、购买宠物用品，还能获得专业的AI宠物咨询。
        </p>
      </div>

      <div class="quick-actions">
        <div class="action-card">
          <div class="action-icon">
            <Icon icon="mdi:file-document-multiple" />
          </div>
          <h4>宠物档案</h4>
          <p>管理您的宠物信息</p>
          <button class="action-btn">创建档案</button>
        </div>

        <div class="action-card">
          <div class="action-icon">
            <Icon icon="mdi:shower-head" />
          </div>
          <h4>预约洗护</h4>
          <p>为爱宠预约专业服务</p>
          <button class="action-btn">立即预约</button>
        </div>

        <div class="action-card">
          <div class="action-icon">
            <Icon icon="mdi:cart" />
          </div>
          <h4>宠物商店</h4>
          <p>购买优质宠物用品</p>
          <button class="action-btn" @click="router.push('/products')">去购物</button>
        </div>

        <div class="action-card">
          <div class="action-icon">
            <Icon icon="mdi:robot" />
          </div>
          <h4>AI咨询</h4>
          <p>获得专业宠物建议</p>
          <button class="action-btn">开始咨询</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    userStore.logout()
    router.push('/')
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 100%;
  margin: 0 auto;
  padding: 20px 40px;
  background: #fff9e6;
  min-height: 100vh;
}

.profile-header {
  display: flex;
  align-items: center;
  background: #ffd93d;
  padding: 30px;
  border-radius: 20px;
  color: white;
  margin-bottom: 30px;
  box-shadow: 0 8px 24px rgba(255, 193, 7, 0.3);
}

.user-avatar {
  font-size: 60px;
  margin-right: 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  flex: 1;
}

.user-name {
  margin: 0 0 5px 0;
  font-size: 28px;
  font-weight: bold;
}

.user-title {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 12px 24px;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.logout-icon :deep(svg) {
  width: 16px;
  height: 16px;
  color: white;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.welcome-message {
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.welcome-icon {
  margin-right: 8px;
  vertical-align: middle;
}

.welcome-icon :deep(svg) {
  width: 24px;
  height: 24px;
  color: #ffd93d;
}

.welcome-message h3 {
  color: #2d3436;
  margin: 0 0 15px 0;
  font-size: 24px;
}

.welcome-message p {
  color: #636e72;
  line-height: 1.6;
  margin: 0;
  font-size: 16px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.action-card {
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: all 0.3s ease;
}

.action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.action-icon {
  font-size: 48px;
  margin-bottom: 15px;
  display: flex;
  justify-content: center;
}

.action-icon :deep(svg) {
  width: 48px;
  height: 48px;
  color: #ffd93d;
}

.action-card h4 {
  color: #2d3436;
  margin: 0 0 10px 0;
  font-size: 20px;
}

.action-card p {
  color: #636e72;
  margin: 0 0 20px 0;
  font-size: 14px;
  line-height: 1.5;
}

.action-btn {
  background: #ffd93d;
  border: none;
  color: white;
  padding: 12px 24px;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 193, 7, 0.3);
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 193, 7, 0.4);
  background: #ffcd02;
}

@media (max-width: 1024px) {
  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .profile-container {
    padding: 15px;
  }

  .profile-header {
    flex-direction: column;
    text-align: center;
    gap: 20px;
    padding: 25px;
  }

  .user-avatar {
    margin-right: 0;
    font-size: 50px;
    width: 70px;
    height: 70px;
  }

  .user-name {
    font-size: 24px;
  }

  .quick-actions {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .action-card {
    padding: 25px 20px;
  }

  .action-icon :deep(svg) {
    width: 40px;
    height: 40px;
  }
}

@media (max-width: 480px) {
  .profile-container {
    padding: 10px;
  }

  .profile-header {
    padding: 20px;
    margin-bottom: 20px;
  }

  .user-avatar {
    font-size: 40px;
    width: 60px;
    height: 60px;
  }

  .user-name {
    font-size: 20px;
  }

  .welcome-message {
    padding: 20px;
  }

  .welcome-message h3 {
    font-size: 20px;
  }

  .action-card {
    padding: 20px 15px;
  }

  .action-icon {
    font-size: 40px;
  }

  .action-icon :deep(svg) {
    width: 32px;
    height: 32px;
  }
}
</style>
