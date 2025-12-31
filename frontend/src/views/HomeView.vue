<!-- src/views/HomeView.vue -->
<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
// 移除这行：import Icon from '@/components/Icon.vue' ← 已全局注册，不需要导入

const router = useRouter()
const userStore = useUserStore()

const navigateToAuth = () => {
  router.push('/auth')
}

const navigateToProfile = () => {
  router.push('/profile')
}

const navigateToPetManage = () => {
  router.push('/pets')
}

// 新增：导航到预约洗护页面
const navigateToAppointment = () => {
  router.push('/service/appointment')
}

// 新增：导航到宠物商店页面
const navigateToShop = () => {
  router.push('/products')
}

const handleLogout = () => {
  userStore.logout()
  router.push('/auth')
}
</script>


<template>
  <div class="home">
    <!-- 英雄区域 -->
    <section class="hero" id="home">
      <div class="container">
        <h2>欢迎来到宠物之家</h2>
        <p>为您和您的爱宠提供一站式贴心服务，从日常护理到健康管理，我们用心呵护每一个毛孩子</p>
        <div class="hero-image">
          <div class="pet-showcase">
            <div class="pet-item">
              <Icon icon="mdi:dog" />
            </div>
            <div class="pet-item">
              <Icon icon="mdi:cat" />
            </div>
            <div class="pet-item">
              <Icon icon="mdi:rabbit" />
            </div>
            <div class="pet-item">
              <Icon icon="mdi:hamster" />
            </div>
            <div class="pet-item">
              <Icon icon="mdi:bird" />
            </div>
            <div class="pet-item">
              <Icon icon="mdi:fish" />
            </div>
            <div class="floating-hearts">
              <span class="heart">
                <Icon icon="mdi:heart" />
              </span>
              <span class="heart">
                <Icon icon="mdi:heart" />
              </span>
              <span class="heart">
                <Icon icon="mdi:heart" />
              </span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 功能展示区 -->
    <section class="features" id="features">
      <div class="container">
        <h2 class="section-title">我们的核心功能</h2>
        <div class="features-grid">
          <!-- 用户账户系统 -->
          <div class="feature-card">
            <div class="feature-icon">
              <Icon icon="mdi:account-circle" />
            </div>
            <h3>用户账户系统</h3>
            <p>安全便捷的账户管理，支持多方式登录，个人信息管理，消息通知等功能。</p>
            <button v-if="!userStore.isLoggedIn" @click="navigateToAuth" class="feature-btn">
              立即体验
            </button>
            <button v-else @click="navigateToProfile" class="feature-btn">
              管理账户
            </button>
          </div>

          <!-- 预约洗护系统 -->
          <div class="feature-card">
            <div class="feature-icon">
              <Icon icon="mdi:shower" />
            </div>
            <h3>预约洗护系统</h3>
            <p>在线预约宠物洗护、美容服务，选择时间、查看评价，轻松管理预约记录。</p>
            <button @click="navigateToAppointment" class="feature-btn">
              立即预约
            </button>
          </div>

          <!-- 宠物档案管理 -->
          <div class="feature-card">
            <div class="feature-icon">
              <Icon icon="mdi:card-account-details" />
            </div>
            <h3>宠物档案管理</h3>
            <p>创建宠物专属档案，记录健康信息、疫苗接种、成长历程，随时分享给服务商。</p>
            <button @click="navigateToPetManage" class="feature-btn">创建档案</button>
          </div>

          <!-- 宠物商店 -->
          <div class="feature-card">
            <div class="feature-icon">
              <Icon icon="mdi:store" />
            </div>
            <h3>宠物商店</h3>
            <p>精选宠物食品、玩具、用品，一站式购物，便捷支付，送货上门。</p>
            <button @click="navigateToShop" class="feature-btn">去逛逛</button>
          </div>

          <!-- AI咨询 -->
          <div class="feature-card">
            <div class="feature-icon">
              <Icon icon="mdi:robot"/>
            </div>
            <h3>AI咨询</h3>
            <p>获得专业的AI咨询服务，为您的宠物提供健康、行为和护理方面的指导。</p>
            <button class="feature-btn">立即咨询</button>
          </div>

          <!-- 评价与反馈系统 -->
          <div class="feature-card">
            <div class="feature-icon">
              <Icon icon="mdi:star-half" />
            </div>
            <h3>评价反馈系统</h3>
            <p>对服务进行评价，提交反馈建议，帮助我们不断改进服务质量。</p>
            <button class="feature-btn">查看评价</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 用户评价 -->
    <section class="testimonials">
      <div class="container">
        <h2 class="section-title">用户评价</h2>
        <div class="testimonials-grid">
          <div class="testimonial-card">
            <div class="testimonial-content">
              <p>
                "宠物之家的服务真的很棒！我家的小金毛在这里洗护后变得更加可爱了，工作人员也很专业。"
              </p>
            </div>
            <div class="testimonial-author">
              <div class="author-avatar">
                <Icon icon="mdi:dog" />
              </div>
              <div class="author-info">
                <h4>小王</h4>
                <p>金毛主人</p>
              </div>
            </div>
          </div>
          <div class="testimonial-card">
            <div class="testimonial-content">
              <p>"AI咨询功能太实用了！半夜小猫咪不舒服，立即就能得到专业建议，真的很安心。"</p>
            </div>
            <div class="testimonial-author">
              <div class="author-avatar">
                <Icon icon="mdi:cat" />
              </div>
              <div class="author-info">
                <h4>小李</h4>
                <p>猫咪主人</p>
              </div>
            </div>
          </div>
          <div class="testimonial-card">
            <div class="testimonial-content">
              <p>
                "宠物档案功能帮我记录了小兔子的所有成长过程，每次看都很感动，推荐给所有宠物主人！"
              </p>
            </div>
            <div class="testimonial-author">
              <div class="author-avatar">
                <Icon icon="mdi:rabbit" />
              </div>
              <div class="author-info">
                <h4>小张</h4>
                <p>兔子主人</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 样式保持不变 */
.home {
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 英雄区域样式 */
.hero {
  padding: 60px 0 40px;
  text-align: center;
}

.hero h2 {
  font-size: 2.8rem;
  color: var(--pet-primary-dark);
  margin-bottom: 20px;
  text-shadow: 2px 2px 0 var(--pet-primary-light);
}

.hero p {
  font-size: 1.3rem;
  max-width: 800px;
  margin: 0 auto 40px;
  color: var(--pet-text-secondary);
}

.hero-image {
  display: flex;
  justify-content: center;
  align-items: center;
}

.pet-showcase {
  position: relative;
  width: 400px;
  height: 400px;
}

.pet-item {
  position: absolute;
  font-size: 60px;
  animation: float 4s ease-in-out infinite;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pet-item :deep(svg) {
  width: 60px;
  height: 60px;
  color: var(--pet-primary);
}

.pet-item:nth-child(1) {
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.pet-item:nth-child(2) {
  top: 10%;
  right: 10%;
  animation-delay: 0.5s;
}

.pet-item:nth-child(3) {
  top: 50%;
  left: 5%;
  animation-delay: 1s;
}

.pet-item:nth-child(4) {
  top: 50%;
  right: 5%;
  animation-delay: 1.5s;
}

.pet-item:nth-child(5) {
  bottom: 20%;
  left: 20%;
  animation-delay: 2s;
}

.pet-item:nth-child(6) {
  bottom: 20%;
  right: 20%;
  animation-delay: 2.5s;
}

.floating-hearts {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.heart {
  position: absolute;
  font-size: 32px;
  animation: heartbeat 2s ease-in-out infinite;
  display: flex;
  align-items: center;
  justify-content: center;
}

.heart :deep(svg) {
  width: 32px;
  height: 32px;
  color: #ff4757;
}

.heart:nth-child(1) {
  animation-delay: 0s;
}

.heart:nth-child(2) {
  animation-delay: 0.7s;
  left: 30px;
}

.heart:nth-child(3) {
  animation-delay: 1.4s;
  left: -30px;
}

/* 功能展示区样式 */
.features {
  padding: 60px 0;
}

.section-title {
  text-align: center;
  font-size: 2.2rem;
  color: var(--pet-primary-dark);
  margin-bottom: 40px;
  position: relative;
}

.section-title:after {
  content: "";
  display: block;
  width: 100px;
  height: 6px;
  background-color: var(--pet-primary-light);
  border-radius: 3px;
  margin: 15px auto;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.feature-card {
  background-color: var(--pet-bg-secondary);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 20px rgba(255, 200, 100, 0.2);
  transition: all 0.3s ease;
  border-top: 8px solid var(--pet-primary-light);
  text-align: center;
  cursor: pointer;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(255, 200, 100, 0.3);
}

.feature-card:nth-child(2) {
  border-top-color: var(--pet-primary);
}

.feature-card:nth-child(3) {
  border-top-color: #FFB347;
}

.feature-card:nth-child(4) {
  border-top-color: var(--pet-primary-light);
}

.feature-card:nth-child(5) {
  border-top-color: var(--pet-primary);
}

.feature-card:nth-child(6) {
  border-top-color: #FFB347;
}

.feature-icon {
  font-size: 2.5rem;
  color: var(--pet-primary);
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.feature-icon :deep(svg) {
  width: 2.5rem;
  height: 2.5rem;
}

.feature-card h3 {
  font-size: 1.5rem;
  color: var(--pet-primary-dark);
  margin-bottom: 15px;
}

.feature-card p {
  color: var(--pet-text-secondary);
  margin-bottom: 20px;
  line-height: 1.6;
}

.feature-btn {
  display: inline-block;
  background-color: var(--pet-primary-lighter);
  color: var(--pet-text-secondary);
  padding: 10px 20px;
  border-radius: 30px;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}

.feature-btn:hover {
  background-color: var(--pet-primary-light);
  color: var(--pet-text-light);
  transform: translateY(-2px);
}

/* 用户评价样式 */
.testimonials {
  padding: 60px 0;
  background-color: var(--pet-primary-lighter);
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
}

.testimonial-card {
  background: var(--pet-bg-secondary);
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 8px 20px rgba(255, 200, 100, 0.2);
  border-top: 8px solid var(--pet-primary);
}

.testimonial-content p {
  font-size: 16px;
  line-height: 1.6;
  color: var(--pet-text-primary);
  margin: 0 0 20px 0;
  font-style: italic;
}

.testimonial-author {
  display: flex;
  align-items: center;
  gap: 15px;
}

.author-avatar {
  font-size: 40px;
  width: 60px;
  height: 60px;
  background: var(--pet-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.author-avatar :deep(svg) {
  width: 30px;
  height: 30px;
  color: var(--pet-text-light);
}

.author-info h4 {
  margin: 0 0 5px 0;
  color: var(--pet-text-primary);
}

.author-info p {
  margin: 0;
  color: var(--pet-text-secondary);
  font-size: 14px;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes heartbeat {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

/* 响应式设计 */
@media (max-width: 992px) {
  .hero h2 {
    font-size: 2.2rem;
  }

  .hero p {
    font-size: 1.1rem;
  }

  .pet-showcase {
    width: 300px;
    height: 300px;
  }

  .pet-item {
    font-size: 45px;
  }

  .pet-item :deep(svg) {
    width: 45px;
    height: 45px;
  }

  .heart {
    font-size: 24px;
  }

  .heart :deep(svg) {
    width: 24px;
    height: 24px;
  }
}

@media (max-width: 768px) {
  .hero h2 {
    font-size: 1.8rem;
  }

  .hero p {
    font-size: 1rem;
  }

  .section-title {
    font-size: 1.8rem;
  }

  .features-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }

  .pet-showcase {
    width: 250px;
    height: 250px;
  }

  .pet-item {
    font-size: 35px;
  }

  .pet-item :deep(svg) {
    width: 35px;
    height: 35px;
  }

  .testimonials-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .hero {
    padding: 40px 0;
  }

  .hero h2 {
    font-size: 1.5rem;
  }

  .hero p {
    font-size: 0.9rem;
  }

  .features,
  .testimonials {
    padding: 40px 0;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .feature-card {
    padding: 25px 20px;
  }

  .feature-icon {
    font-size: 2rem;
  }

  .feature-icon :deep(svg) {
    width: 2rem;
    height: 2rem;
  }

  .pet-showcase {
    width: 200px;
    height: 200px;
  }

  .pet-item {
    font-size: 28px;
  }

  .pet-item :deep(svg) {
    width: 28px;
    height: 28px;
  }
}
</style>
