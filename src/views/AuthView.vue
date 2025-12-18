<template>
  <div class="auth-view">
    <!-- 背景爪印装饰 -->
    <div class="paw-print"><i class="fas fa-paw"></i></div>
    <div class="paw-print"><i class="fas fa-paw"></i></div>
    <div class="paw-print"><i class="fas fa-paw"></i></div>
    <div class="paw-print"><i class="fas fa-paw"></i></div>

    <div class="container">
      <!-- 左侧欢迎区域 -->
      <div class="welcome-section">
        <div class="logo">
          <div class="logo-icon"><i class="fas fa-paw"></i></div>
          <div class="logo-text">宠物之家</div>
        </div>

        <h1 class="welcome-title">欢迎回到宠物之家</h1>
        <p class="welcome-text">
          在这里，我们为您的宠物提供最温馨的家。无论您是宠物主人还是宠物爱好者，都可以在这里找到归属感。
        </p>

        <div class="features">
          <div class="feature">
            <i class="fas fa-check-circle"></i>
            <span>专业宠物健康咨询</span>
          </div>
          <div class="feature">
            <i class="fas fa-check-circle"></i>
            <span>预约洗护服务</span>
          </div>
          <div class="feature">
            <i class="fas fa-check-circle"></i>
            <span>宠物商城购物</span>
          </div>
        </div>

        <!-- 宠物装饰 -->
        <div class="pet-dog"><i class="fas fa-dog"></i></div>
        <div class="pet-cat"><i class="fas fa-cat"></i></div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="form-section">
        <!-- 登录表单 -->
        <div class="form-container login-form">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">请登录您的账户继续使用宠物之家</p>

          <form @submit.prevent="handleLogin">
            <div class="form-group">
              <label class="form-label" for="loginEmail">用户名</label>
              <div class="input-with-icon">
                <input
                  v-model="loginForm.email"
                  type="text"
                  id="loginEmail"
                  :class="[
                    'form-input',
                    { error: loginForm.emailError, success: loginForm.emailSuccess },
                  ]"
                  placeholder="请输入您的用户名"
                  required
                  @input="clearLoginEmailError"
                />
                <div class="input-icon"><i class="fas fa-user"></i></div>
              </div>
              <div v-if="loginForm.emailError" class="error-message">请输入用户名</div>
            </div>

            <div class="form-group">
              <label class="form-label" for="loginPassword">密码</label>
              <div class="input-with-icon">
                <input
                  v-model="loginForm.password"
                  :type="showLoginPassword ? 'text' : 'password'"
                  id="loginPassword"
                  :class="[
                    'form-input',
                    { error: loginForm.passwordError, success: loginForm.passwordSuccess },
                  ]"
                  placeholder="请输入您的密码"
                  required
                  @input="clearLoginPasswordError"
                />
                <div class="input-icon"><i class="fas fa-lock"></i></div>
                <div class="toggle-password" @click="toggleLoginPasswordVisibility">
                  <i :class="showLoginPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </div>
              </div>
              <div v-if="loginForm.passwordError" class="error-message">密码不能少于6个字符</div>
            </div>

            <div class="remember-forgot">
              <div class="remember-me">
                <input v-model="loginForm.remember" type="checkbox" id="rememberMe" />
                <label for="rememberMe">记住我</label>
              </div>
              <a href="#" class="forgot-password">忘记密码?</a>
            </div>

            <button type="submit" class="submit-btn" :disabled="isLoading">
              <i v-if="isLoading" class="fas fa-spinner fa-spin"></i>
              {{ loginButtonText }}
            </button>
          </form>

          <div class="switch-form">
            还没有账户？ <router-link to="/register" class="register-link">立即注册</router-link>
          </div>
        </div>


      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 状态管理
const isLoading = ref(false)
const showLoginPassword = ref(false)

// 表单数据
const loginForm = reactive({
  email: '',
  password: '',
  remember: false,
  emailError: false,
  emailSuccess: false,
  passwordError: false,
  passwordSuccess: false,
})



// 按钮文本计算属性
const loginButtonText = computed(() => {
  return isLoading.value ? '登录中...' : '登录'
})

// 重置表单
const resetForm = () => {
  Object.assign(loginForm, {
    email: '',
    password: '',
    remember: false,
    emailError: false,
    emailSuccess: false,
    passwordError: false,
    passwordSuccess: false,
  })
}

// 切换密码可见性
const toggleLoginPasswordVisibility = () => {
  showLoginPassword.value = !showLoginPassword.value
}

// 清除错误状态的方法
const clearLoginEmailError = () => {
  loginForm.emailError = false
}

const clearLoginPasswordError = () => {
  loginForm.passwordError = false
}

// 邮箱验证正则
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

// 处理登录
const handleLogin = async () => {
  let isValid = true

  // 验证用户名（改为用户名而不是邮箱）
  if (!loginForm.email.trim()) {
    loginForm.emailError = true
    loginForm.emailSuccess = false
    isValid = false
  } else {
    loginForm.emailError = false
    loginForm.emailSuccess = true
  }

  // 验证密码
  if (loginForm.password.length < 6) {
    loginForm.passwordError = true
    loginForm.passwordSuccess = false
    isValid = false
  } else {
    loginForm.passwordError = false
    loginForm.passwordSuccess = true
  }

  if (!isValid) return

  isLoading.value = true

  try {
    // 调用真实的登录API
    const loginData = {
      username: loginForm.email, // 这里用email字段存储用户名
      password: loginForm.password
    }

    await userStore.login(loginData, loginForm.remember)

    // 登录成功，跳转到首页
    router.push('/')
  } catch (error) {
    // 登录失败，显示错误信息
    alert(error.message || '登录失败，请检查用户名和密码')
    isLoading.value = false
  }
}

// 处理注册
const handleRegister = async () => {
  let isValid = true

  // 验证用户名
  if (registerForm.username.trim().length < 3 || registerForm.username.trim().length > 50) {
    registerForm.usernameError = true
    registerForm.usernameSuccess = false
    isValid = false
  } else {
    registerForm.usernameError = false
    registerForm.usernameSuccess = true
  }

  // 验证邮箱
  if (!registerForm.email.trim() || !emailRegex.test(registerForm.email)) {
    registerForm.emailError = true
    registerForm.emailSuccess = false
    isValid = false
  } else {
    registerForm.emailError = false
    registerForm.emailSuccess = true
  }

  // 验证密码
  if (registerForm.password.length < 6) {
    registerForm.passwordError = true
    registerForm.passwordSuccess = false
    isValid = false
  } else {
    registerForm.passwordError = false
    registerForm.passwordSuccess = true
  }

  // 验证确认密码
  if (registerForm.confirmPassword !== registerForm.password) {
    registerForm.confirmPasswordError = true
    registerForm.confirmPasswordSuccess = false
    isValid = false
  } else {
    registerForm.confirmPasswordError = false
    registerForm.confirmPasswordSuccess = true
  }

  // 验证条款同意
  if (!registerForm.agreeTerms) {
    alert('请同意服务条款和隐私政策')
    isValid = false
  }

  if (!isValid) return

  isLoading.value = true

  try {
    // 调用真实的注册API
    const registerData = {
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.username, // 默认昵称为用户名
      email: registerForm.email
    }

    await userStore.register(registerData)

    // 注册成功，显示提示并切换到登录
    alert('注册成功！请登录您的账户')
    switchToLogin()
    isLoading.value = false
  } catch (error) {
    // 注册失败，显示错误信息
    alert(error.message || '注册失败，请重试')
    isLoading.value = false
  }
}
</script>

<style scoped>
/* 组件根元素样式 */
.auth-view {
  background-color: #fef8f0;
  background-image:
    radial-gradient(#ffd7b5 1.5px, transparent 1.5px), radial-gradient(#ffd7b5 1.5px, #fef8f0 1.5px);
  background-size: 60px 60px;
  background-position:
    0 0,
    30px 30px;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  overflow-x: hidden;
  font-family: 'Noto Sans SC', sans-serif;
}

body {
  background-color: #fef8f0;
  background-image:
    radial-gradient(#ffd7b5 1.5px, transparent 1.5px), radial-gradient(#ffd7b5 1.5px, #fef8f0 1.5px);
  background-size: 60px 60px;
  background-position:
    0 0,
    30px 30px;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  overflow-x: hidden;
}

.paw-print {
  position: absolute;
  color: #ffb284;
  font-size: 24px;
  opacity: 0.3;
  z-index: 0;
}

.paw-print:nth-child(1) {
  top: 10%;
  left: 5%;
}
.paw-print:nth-child(2) {
  top: 20%;
  right: 8%;
}
.paw-print:nth-child(3) {
  bottom: 15%;
  left: 7%;
}
.paw-print:nth-child(4) {
  bottom: 25%;
  right: 5%;
}

.container {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  background-color: white;
  border-radius: 25px;
  overflow: hidden;
  box-shadow: 0 15px 35px rgba(255, 150, 100, 0.15);
  position: relative;
  z-index: 1;
}

/* 左侧欢迎区域 - 调整为柔和渐变橙色 */
.welcome-section {
  flex: 1;
  background: linear-gradient(135deg, #ffcc99 0%, #ff9966 50%, #ff7b3d 100%);
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.welcome-section::before {
  content: '';
  position: absolute;
  width: 200px;
  height: 200px;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  top: -50px;
  left: -50px;
}

.welcome-section::after {
  content: '';
  position: absolute;
  width: 150px;
  height: 150px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  bottom: -30px;
  right: -30px;
}

.logo {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  z-index: 2;
  position: relative;
}

.logo-icon {
  font-size: 42px;
  margin-right: 15px;
  color: white;
}

/* 宠物之家字体加大 */
.logo-text {
  font-family: 'Ma Shan Zheng', cursive;
  font-size: 46px;
  font-weight: normal;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.15);
}

.welcome-title {
  font-size: 36px;
  margin-bottom: 20px;
  z-index: 2;
  position: relative;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.1);
}

.welcome-text {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 30px;
  z-index: 2;
  position: relative;
  opacity: 0.95;
}

.features {
  margin-top: 20px;
  z-index: 2;
  position: relative;
}

.feature {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 16px;
  background-color: rgba(255, 255, 255, 0.15);
  padding: 12px 15px;
  border-radius: 10px;
  backdrop-filter: blur(5px);
}

.feature i {
  margin-right: 12px;
  font-size: 20px;
}

/* 右侧表单区域 */
.form-section {
  flex: 1;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-container {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.form-title {
  font-size: 28px;
  color: #ff7b3d;
  margin-bottom: 10px;
  font-weight: 700;
}

.form-subtitle {
  color: #888;
  margin-bottom: 30px;
  font-size: 15px;
}

.form-group {
  margin-bottom: 25px;
  position: relative;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
  font-size: 14px;
}

.input-with-icon {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 16px 16px 16px 50px;
  border: 2px solid #eee;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s;
  background-color: #fcfcfc;
}

.form-input:focus {
  outline: none;
  border-color: #ffb284;
  background-color: white;
  box-shadow: 0 0 0 3px rgba(255, 178, 132, 0.2);
}

.input-icon {
  position: absolute;
  left: 18px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaa;
  font-size: 18px;
}

.toggle-password {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaa;
  cursor: pointer;
  font-size: 18px;
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  font-size: 14px;
}

.remember-me {
  display: flex;
  align-items: center;
}

.remember-me input {
  margin-right: 8px;
}

.forgot-password {
  color: #ff7b3d;
  text-decoration: none;
  transition: color 0.3s;
}

.forgot-password:hover {
  color: #ff9a5c;
  text-decoration: underline;
}

.submit-btn {
  background: linear-gradient(to right, #ff9a5c, #ff7b3d);
  color: white;
  border: none;
  padding: 16px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-btn:hover:not(:disabled) {
  background: linear-gradient(to right, #ff8a4c, #ff6b2d);
  transform: translateY(-2px);
  box-shadow: 0 7px 15px rgba(255, 122, 61, 0.3);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.submit-btn i {
  margin-right: 8px;
}

.switch-form {
  text-align: center;
  color: #666;
  font-size: 14px;
}

.switch-form a,
.register-link {
  color: #ff7b3d;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
  transition: color 0.3s;
}

.switch-form a:hover,
.register-link:hover {
  color: #ff9a5c;
  text-decoration: underline;
}

.pet-images {
  position: absolute;
  z-index: 2;
}

.pet-cat {
  position: absolute;
  bottom: 20px;
  right: 30px;
  font-size: 60px;
  color: rgba(255, 255, 255, 0.9);
  filter: drop-shadow(2px 2px 4px rgba(0, 0, 0, 0.1));
}

.pet-dog {
  position: absolute;
  top: 30px;
  left: 30px;
  font-size: 50px;
  color: rgba(255, 255, 255, 0.8);
  transform: rotate(-15deg);
  filter: drop-shadow(2px 2px 4px rgba(0, 0, 0, 0.1));
}

/* 表单验证样式 */
.error-message {
  color: #ff4757;
  font-size: 13px;
  margin-top: 5px;
  display: block;
}

.success-message {
  color: #2ed573;
  font-size: 13px;
  margin-top: 5px;
  display: block;
}

.form-input.error {
  border-color: #ff4757;
}

.form-input.success {
  border-color: #2ed573;
}

/* 响应式设计 */
@media (max-width: 900px) {
  .container {
    flex-direction: column;
    max-width: 500px;
  }

  .welcome-section {
    padding: 30px;
  }

  .form-section {
    padding: 40px 30px;
  }

  .pet-cat,
  .pet-dog {
    display: none;
  }
}

@media (max-width: 480px) {
  .container {
    border-radius: 15px;
  }

  .welcome-section,
  .form-section {
    padding: 30px 20px;
  }

  .welcome-title {
    font-size: 28px;
  }

  .logo-text {
    font-size: 36px;
  }

  .logo-icon {
    font-size: 36px;
  }

  .form-title {
    font-size: 24px;
  }

  .remember-forgot {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}

/* 动画 */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
