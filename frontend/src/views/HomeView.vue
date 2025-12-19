<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const navigateToAuth = () => {
  router.push('/auth')
}

const navigateToProfile = () => {
  router.push('/profile')
}

const handleLogout = () => {
  userStore.logout()
  router.push('/auth')
}
</script>

<template>
  <div class="home">
    <!-- 英雄区域 -->
    <section class="hero">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">
            欢迎来到
            <span class="highlight">宠物之家</span>
          </h1>
          <p class="hero-subtitle">专业的宠物服务平台，为您的爱宠提供全方位的关爱</p>
          <div class="hero-buttons">
            <button v-if="!userStore.isLoggedIn" @click="navigateToAuth" class="cta-button primary">
              立即加入 🐾
            </button>
            <button v-else @click="navigateToProfile" class="cta-button primary">
              进入个人中心 🏠
            </button>
            <button class="cta-button secondary">了解更多 📖</button>
          </div>
        </div>
        <div class="hero-image">
          <div class="pet-showcase">
            <div class="pet-item">🐕</div>
            <div class="pet-item">🐱</div>
            <div class="pet-item">🐰</div>
            <div class="pet-item">🐹</div>
            <div class="floating-hearts">
              <span class="heart">💕</span>
              <span class="heart">💖</span>
              <span class="heart">💝</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 服务特色 -->
    <section class="features">
      <div class="container">
        <h2 class="section-title">我们的服务</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">📋</div>
            <h3>宠物档案管理</h3>
            <p>为您的爱宠建立详细的健康档案，记录疫苗、体重、成长里程碑等重要信息。</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🛁</div>
            <h3>专业洗护服务</h3>
            <p>提供专业的宠物洗护、美容、SPA等服务，让您的爱宠保持最佳状态。</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🛒</div>
            <h3>优质宠物用品</h3>
            <p>精选优质宠物食品、用品、玩具等，为您的爱宠提供最好的生活用品。</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🤖</div>
            <h3>AI智能咨询</h3>
            <p>24小时AI宠物专家在线，为您解答宠物饲养、健康、行为等各种问题。</p>
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
              <div class="author-avatar">🐕</div>
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
              <div class="author-avatar">🐱</div>
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
              <div class="author-avatar">🐰</div>
              <div class="author-info">
                <h4>小张</h4>
                <p>兔子主人</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 行动号召 -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-content">
          <h2>准备好给您的爱宠最好的关爱了吗？</h2>
          <p>加入宠物之家，让我们一起为您的毛孩子创造更美好的生活！</p>
          <button v-if="!userStore.isLoggedIn" @click="navigateToAuth" class="cta-button large">
            立即注册 🚀
          </button>
          <button v-else @click="navigateToProfile" class="cta-button large">开始使用 🎉</button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
}



.hero {
  background: var(--pet-primary-light);
  padding: 80px 0;
  position: relative;
  overflow: hidden;
  border-bottom: 3px solid var(--pet-primary);
}

.hero-content {
  max-width: 100%;
  margin: 0 auto;
  padding: 0 40px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.hero-text {
  color: var(--pet-text-primary);
}

.hero-title {
  font-size: 48px;
  font-weight: bold;
  margin: 0 0 20px 0;
  line-height: 1.2;
}

.highlight {
  color: var(--pet-primary-dark);
  font-weight: 800;
}

.hero-subtitle {
  font-size: 20px;
  margin: 0 0 40px 0;
  opacity: 0.9;
  line-height: 1.6;
}

.hero-buttons {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.cta-button {
  padding: 16px 32px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.cta-button.primary {
  background: var(--pet-primary);
  color: white;
  box-shadow: var(--pet-shadow);
}

.cta-button.secondary {
  background: transparent;
  color: var(--pet-text-primary);
  border: 2px solid var(--pet-primary);
}

.cta-button.large {
  padding: 20px 40px;
  font-size: 18px;
}

.cta-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.hero-image {
  display: flex;
  justify-content: center;
  align-items: center;
}

.pet-showcase {
  position: relative;
  width: 300px;
  height: 300px;
}

.pet-item {
  position: absolute;
  font-size: 60px;
  animation: float 4s ease-in-out infinite;
}

.pet-item:nth-child(1) {
  top: 20%;
  left: 20%;
  animation-delay: 0s;
}

.pet-item:nth-child(2) {
  top: 20%;
  right: 20%;
  animation-delay: 1s;
}

.pet-item:nth-child(3) {
  bottom: 20%;
  left: 20%;
  animation-delay: 2s;
}

.pet-item:nth-child(4) {
  bottom: 20%;
  right: 20%;
  animation-delay: 3s;
}

.floating-hearts {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.heart {
  position: absolute;
  font-size: 24px;
  animation: heartbeat 2s ease-in-out infinite;
}

.heart:nth-child(1) {
  animation-delay: 0s;
}

.heart:nth-child(2) {
  animation-delay: 0.7s;
  left: 20px;
}

.heart:nth-child(3) {
  animation-delay: 1.4s;
  left: -20px;
}

.container {
  max-width: 100%;
  margin: 0 auto;
  padding: 0 40px;
}

.section-title {
  text-align: center;
  font-size: 36px;
  font-weight: bold;
  color: var(--pet-text-primary);
  margin: 0 0 60px 0;
}

.features {
  padding: 100px 0;
  background: white;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 40px;
}

.feature-card {
  text-align: center;
  padding: 40px 20px;
  border-radius: 20px;
  background: white;
  border: 2px solid var(--pet-primary-light);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: var(--pet-shadow-dark);
  border-color: var(--pet-primary);
}

.feature-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 24px;
  color: var(--pet-text-primary);
  margin: 0 0 15px 0;
}

.feature-card p {
  color: var(--pet-text-secondary);
  line-height: 1.6;
  margin: 0;
}

.testimonials {
  padding: 100px 0;
  background: var(--pet-primary-lighter);
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
}

.testimonial-card {
  background: white;
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
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

.author-info h4 {
  margin: 0 0 5px 0;
  color: var(--pet-text-primary);
}

.author-info p {
  margin: 0;
  color: var(--pet-text-secondary);
  font-size: 14px;
}

.cta-section {
  padding: 100px 0;
  background: var(--pet-primary);
  text-align: center;
}

.cta-content {
  color: white;
}

.cta-content h2 {
  font-size: 36px;
  margin: 0 0 20px 0;
}

.cta-content p {
  font-size: 18px;
  margin: 0 0 40px 0;
  opacity: 0.9;
}

.cta-section .cta-button {
  background: white;
  color: var(--pet-primary-dark);
  box-shadow: 0 6px 20px rgba(255, 255, 255, 0.3);
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
@media (max-width: 1200px) {
  .hero-content {
    gap: 40px;
  }

  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .testimonials-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero {
    padding: 60px 0;
  }

  .hero-content {
    grid-template-columns: 1fr;
    gap: 30px;
    text-align: center;
    padding: 0 20px;
  }

  .container {
    padding: 0 20px;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-subtitle {
    font-size: 18px;
  }

  .hero-buttons {
    justify-content: center;
    gap: 15px;
  }

  .pet-showcase {
    width: 220px;
    height: 220px;
  }

  .pet-item {
    font-size: 35px;
  }

  .section-title {
    font-size: 28px;
    margin-bottom: 40px;
  }

  .features,
  .testimonials,
  .cta-section {
    padding: 60px 0;
  }

  .features-grid,
  .testimonials-grid {
    grid-template-columns: 1fr;
    gap: 25px;
  }

  .feature-card,
  .testimonial-card {
    padding: 30px 20px;
  }

  .cta-content h2 {
    font-size: 28px;
  }

  .cta-content p {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .hero {
    padding: 40px 0;
  }

  .hero-title {
    font-size: 28px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .hero-buttons {
    flex-direction: column;
    width: 100%;
  }

  .cta-button {
    padding: 14px 24px;
    font-size: 14px;
    width: 100%;
  }

  .cta-button.large {
    padding: 16px 32px;
    font-size: 16px;
  }

  .pet-showcase {
    width: 180px;
    height: 180px;
  }

  .pet-item {
    font-size: 28px;
  }

  .section-title {
    font-size: 24px;
  }

  .features,
  .testimonials,
  .cta-section {
    padding: 40px 0;
  }

  .feature-card {
    padding: 25px 15px;
  }

  .feature-icon {
    font-size: 48px;
  }

  .testimonial-card {
    padding: 25px 15px;
  }
}

@media (max-width: 360px) {
  .hero-title {
    font-size: 24px;
  }

  .hero-subtitle {
    font-size: 14px;
  }

  .section-title {
    font-size: 22px;
  }

  .cta-content h2 {
    font-size: 24px;
  }
}
</style>
