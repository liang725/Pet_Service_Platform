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

// 新增：导航到AI咨询页面
const navigateToAIConsult = () => {
  router.push('/ai-consult')
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
        <p>为您和您的爱宠提供一站式贴心服务，我们用心呵护每一个毛孩子</p>
        <!-- 修改为双图片并排 -->
        <div class="hero-images">
          <div class="image-wrapper">
            <img 
              src="https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/pet_image/image/%E7%8B%974.jpg" 
              alt="可爱的宠物狗" 
              class="hero-pet-image"
            />
          </div>
          <div class="image-wrapper">
            <img 
              src="https://wuyingyunshang.oss-cn-shanghai.aliyuncs.com/pet/pet_image/image/%E7%8C%AB2.jpg" 
              alt="可爱的宠物猫" 
              class="hero-pet-image"
            />
          </div>
        </div>
      </div>
    </section>

    <!-- 功能展示区 -->
    <section class="features" id="features">
      <div class="container">
        <h2 class="section-title">我们的核心功能</h2>
        <div class="features-grid">

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
            <button @click="navigateToAIConsult" class="feature-btn">立即咨询</button>
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
                <p>布偶主人</p>
              </div>
            </div>
          </div>
          <div class="testimonial-card">
            <div class="testimonial-content">
              <p>
                "宠物商城推出了很多优质商品，价格实惠，物品丰富多样，发货也特别快，购物体验非常好，强烈推荐！"
              </p>
            </div>
            <div class="testimonial-author">
              <div class="author-avatar">
                <Icon icon="mdi:cat" />
              </div>
              <div class="author-info">
                <h4>小张</h4>
                <p>暹罗主人</p>
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

/* 修改图片区域样式为双图片并排 */
.hero-images {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 两列并排 */
  gap: 20px;
  margin-top: 30px;
  width: 100%;
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
}

.image-wrapper {
  position: relative;
  overflow: hidden;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.image-wrapper:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
}

.hero-pet-image {
  width: 100%;
  height: 350px;
  object-fit: cover;
  display: block;
  transition: transform 0.3s ease;
}

.image-wrapper:hover .hero-pet-image {
  transform: scale(1.05);
}

.image-label {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
  padding: 20px 15px 15px;
  font-size: 1.2rem;
  font-weight: bold;
  text-align: center;
}

/* 功能展示区样式 - 修改为并排排列 */
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

/* 修改这里：将grid-template-columns改为4列并排 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 改为4列并排 */
  gap: 30px;
  margin-top: 40px;
}

.feature-card {
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 20px rgba(255, 200, 100, 0.2);
  transition: all 0.3s ease;
  border-top: 8px solid var(--pet-primary-light);
  text-align: center;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%; /* 确保所有卡片高度一致 */
  border: 1px solid rgba(255, 179, 71, 0.1);
  backdrop-filter: blur(10px);
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(255, 200, 100, 0.3);
  border-color: #ffcc80;
}

.feature-card:nth-child(1) {
  border-top-color: var(--pet-primary);
}

.feature-card:nth-child(2) {
  border-top-color: #FFB347;
}

.feature-card:nth-child(3) {
  border-top-color: var(--pet-primary-light);
}

.feature-card:nth-child(4) {
  border-top-color: #4CAF50; /* 添加第四个卡片的边框颜色 */
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
  flex-grow: 1; /* 让段落填充剩余空间，确保按钮对齐 */
}

.feature-btn {
  display: inline-block;
  background-color: var(--pet-primary);
  color: var(--pet-text-light);
  padding: 10px 20px;
  border-radius: 30px;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  margin-top: auto; /* 确保按钮在底部对齐 */
}

.feature-btn:hover {
  background-color: var(--pet-primary-dark);
  color: var(--pet-text-light);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(255, 179, 71, 0.3);
}

/* 用户评价样式 - 删除背景色 */
.testimonials {
  padding: 60px 0;
  /* 删除这行：background-color: var(--pet-primary-lighter); */
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
}

.testimonial-card {
  background: rgba(255, 255, 255, 0.95);
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 8px 20px rgba(255, 200, 100, 0.2);
  border-top: 8px solid var(--pet-primary);
  border: 1px solid rgba(255, 179, 71, 0.1);
  backdrop-filter: blur(10px);
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr); /* 中等屏幕改为2列 */
  }
}

@media (max-width: 992px) {
  .hero h2 {
    font-size: 2.2rem;
  }

  .hero p {
    font-size: 1.1rem;
  }

  .hero-images {
    max-width: 800px;
  }
  
  .hero-pet-image {
    height: 300px;
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

  /* 在中等屏幕改为单列图片 */
  .hero-images {
    grid-template-columns: 1fr;
    max-width: 500px;
  }
  
  .hero-pet-image {
    height: 280px;
  }
  
  .features-grid {
    grid-template-columns: 1fr; /* 小屏幕改为1列 */
    gap: 20px;
  }

  .testimonials-grid {
    grid-template-columns: 1fr;
  }
  
  .image-label {
    font-size: 1.1rem;
    padding: 15px 10px 10px;
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

  .hero-images {
    gap: 15px;
  }
  
  .hero-pet-image {
    height: 220px;
  }
  
  .features,
  .testimonials {
    padding: 40px 0;
  }

  .feature-card {
    padding: 25px 20px;
  }

  .testimonial-card {
    padding: 25px 20px;
  }

  .feature-icon {
    font-size: 2rem;
  }

  .feature-icon :deep(svg) {
    width: 2rem;
    height: 2rem;
  }
  
  .image-label {
    font-size: 1rem;
    padding: 12px 8px 8px;
  }
}

@media (max-width: 360px) {
  .hero-pet-image {
    height: 180px;
  }
}
</style>