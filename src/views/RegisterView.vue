<template>
  <div class="register-view">
    <!-- 背景装饰 -->
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

        <h1 class="welcome-title">加入宠物之家大家庭</h1>
        <p class="welcome-text">
          创建您的专属账户，开启美好的宠物之旅。在这里，您可以为您的爱宠找到最贴心的服务和最温暖的社区。
        </p>

        <div class="features">
          <div class="feature">
            <i class="fas fa-heart"></i>
            <span>专业的宠物护理服务</span>
          </div>
          <div class="feature">
            <i class="fas fa-users"></i>
            <span>活跃的宠物爱好者社区</span>
          </div>
          <div class="feature">
            <i class="fas fa-shopping-cart"></i>
            <span>优质的宠物用品商城</span>
          </div>
          <div class="feature">
            <i class="fas fa-calendar-alt"></i>
            <span>便捷的预约服务系统</span>
          </div>
        </div>

        <!-- 宠物装饰 -->
        <div class="pet-dog"><i class="fas fa-dog"></i></div>
        <div class="pet-cat"><i class="fas fa-cat"></i></div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="form-section">
        <div class="form-container">
          <h2 class="form-title">创建新账户</h2>
          <p class="form-subtitle">填写以下信息完成注册</p>

          <form @submit.prevent="handleRegister">
            <div class="form-group">
              <label class="form-label" for="username">用户名 *</label>
              <div class="input-with-icon">
                <input
                  v-model="registerForm.username"
                  type="text"
                  id="username"
                  :class="[
                    'form-input',
                    { error: registerForm.usernameError, success: registerForm.usernameSuccess },
                  ]"
                  placeholder="请输入用户名（3-50个字符）"
                  required
                  @input="clearUsernameError"
                />
                <div class="input-icon"><i class="fas fa-user"></i></div>
              </div>
              <div v-if="registerForm.usernameError" class="error-message">
                {{ registerForm.usernameErrorMsg }}
              </div>
            </div>

            <div class="form-group">
              <label class="form-label" for="email">电子邮箱 *</label>
              <div class="input-with-icon">
                <input
                  v-model="registerForm.email"
                  type="email"
                  id="email"
                  :class="[
                    'form-input',
                    { error: registerForm.emailError, success: registerForm.emailSuccess },
                  ]"
                  placeholder="请输入邮箱地址"
                  required
                  @input="clearEmailError"
                />
                <div class="input-icon"><i class="fas fa-envelope"></i></div>
              </div>
              <div v-if="registerForm.emailError" class="error-message">
                {{ registerForm.emailErrorMsg }}
              </div>
            </div>



            <div class="form-group">
              <label class="form-label" for="password">密码 *</label>
              <div class="input-with-icon">
                <input
                  v-model="registerForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  id="password"
                  :class="[
                    'form-input',
                    { error: registerForm.passwordError, success: registerForm.passwordSuccess },
                  ]"
                  placeholder="请输入密码（至少6位）"
                  required
                  @input="clearPasswordError"
                />
                <div class="input-icon"><i class="fas fa-lock"></i></div>
                <div class="toggle-password" @click="togglePasswordVisibility">
                  <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </div>
              </div>
              <div v-if="registerForm.passwordError" class="error-message">
                {{ registerForm.passwordErrorMsg }}
              </div>
            </div>

            <div class="form-group">
              <label class="form-label" for="confirmPassword">确认密码 *</label>
              <div class="input-with-icon">
                <input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  id="confirmPassword"
                  :class="[
                    'form-input',
                    {
                      error: registerForm.confirmPasswordError,
                      success: registerForm.confirmPasswordSuccess,
                    },
                  ]"
                  placeholder="请再次输入密码"
                  required
                  @input="clearConfirmPasswordError"
                />
                <div class="input-icon"><i class="fas fa-lock"></i></div>
              </div>
              <div v-if="registerForm.confirmPasswordError" class="error-message">
                {{ registerForm.confirmPasswordErrorMsg }}
              </div>
            </div>

            <div class="form-group">
              <div class="checkbox-group">
                <input v-model="registerForm.agreeTerms" type="checkbox" id="agreeTerms" required />
                <label for="agreeTerms" class="checkbox-label">
                  我已阅读并同意
                  <a href="#" class="link">《用户服务协议》</a>
                  和
                  <a href="#" class="link">《隐私政策》</a>
                </label>
              </div>
            </div>

            <button type="submit" class="submit-btn" :disabled="isLoading">
              <i v-if="isLoading" class="fas fa-spinner fa-spin"></i>
              {{ isLoading ? '注册中...' : '立即注册' }}
            </button>
          </form>

          <div class="switch-form">
            已有账户？
            <router-link to="/auth" class="link">立即登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 状态管理
const isLoading = ref(false)
const showPassword = ref(false)

// 表单数据
const registerForm = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  agreeTerms: false,

  // 错误状态
  usernameError: false,
  usernameSuccess: false,
  usernameErrorMsg: '',

  emailError: false,
  emailSuccess: false,
  emailErrorMsg: '',

  phoneError: false,
  phoneSuccess: false,
  phoneErrorMsg: '',

  passwordError: false,
  passwordSuccess: false,
  passwordErrorMsg: '',

  confirmPasswordError: false,
  confirmPasswordSuccess: false,
  confirmPasswordErrorMsg: '',
})

// 正则表达式
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const phoneRegex = /^1[3-9]\d{9}$/

// 切换密码可见性
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

// 清除错误状态的方法
const clearUsernameError = () => {
  registerForm.usernameError = false
  registerForm.usernameErrorMsg = ''
}

const clearEmailError = () => {
  registerForm.emailError = false
  registerForm.emailErrorMsg = ''
}

const clearPhoneError = () => {
  registerForm.phoneError = false
  registerForm.phoneErrorMsg = ''
}

const clearPasswordError = () => {
  registerForm.passwordError = false
  registerForm.passwordErrorMsg = ''
}

const clearConfirmPasswordError = () => {
  registerForm.confirmPasswordError = false
  registerForm.confirmPasswordErrorMsg = ''
}

// 处理注册
const handleRegister = async () => {
  let isValid = true

  // 验证用户名
  if (registerForm.username.trim().length < 3 || registerForm.username.trim().length > 50) {
    registerForm.usernameError = true
    registerForm.usernameSuccess = false
    registerForm.usernameErrorMsg = '用户名长度必须在3-50个字符之间'
    isValid = false
  } else {
    registerForm.usernameError = false
    registerForm.usernameSuccess = true
  }

  // 验证邮箱
  if (!registerForm.email.trim() || !emailRegex.test(registerForm.email)) {
    registerForm.emailError = true
    registerForm.emailSuccess = false
    registerForm.emailErrorMsg = '请输入有效的邮箱地址'
    isValid = false
  } else {
    registerForm.emailError = false
    registerForm.emailSuccess = true
  }

  // 验证手机号（可选）
  if (registerForm.phone.trim() && !phoneRegex.test(registerForm.phone)) {
    registerForm.phoneError = true
    registerForm.phoneSuccess = false
    registerForm.phoneErrorMsg = '请输入有效的手机号'
    isValid = false
  } else {
    registerForm.phoneError = false
    registerForm.phoneSuccess = registerForm.phone.trim() !== ''
  }

  // 验证密码
  if (registerForm.password.length < 6) {
    registerForm.passwordError = true
    registerForm.passwordSuccess = false
    registerForm.passwordErrorMsg = '密码长度不能少于6个字符'
    isValid = false
  } else {
    registerForm.passwordError = false
    registerForm.passwordSuccess = true
  }

  // 验证确认密码
  if (registerForm.confirmPassword !== registerForm.password) {
    registerForm.confirmPasswordError = true
    registerForm.confirmPasswordSuccess = false
    registerForm.confirmPasswordErrorMsg = '两次输入的密码不一致'
    isValid = false
  } else {
    registerForm.confirmPasswordError = false
    registerForm.confirmPasswordSuccess = true
  }

  // 验证条款同意
  if (!registerForm.agreeTerms) {
    alert('请同意用户服务协议和隐私政策')
    isValid = false
  }

  if (!isValid) return

  isLoading.value = true

  try {
    // 调用注册API
    const registerData = {
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.username, // 默认昵称为用户名
      email: registerForm.email,
      phone: registerForm.phone || null
    }

    await userStore.register(registerData)

    // 注册成功，显示提示并跳转到登录页
    alert('注册成功！请登录您的账户')
    router.push('/auth')
  } catch (error) {
    // 注册失败，显示错误信息
    alert(error.message || '注册失败，请重试')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* 使用与AuthView相同的样式，但针对注册页面进行了优化 */
.register-view {
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
  max-width: 1200px;
  min-height: 700px;
  background-color: white;
  border-radius: 25px;
  overflow: hidden;
  box-shadow: 0 15px 35px rgba(255, 150, 100, 0.15);
  position: relative;
  z-index: 1;
}

/* 左侧欢迎区域 */
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
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow-y: auto;
}

.form-container {
  width: 100%;
  max-width: 450px;
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
  margin-bottom: 20px;
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

.checkbox-group {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.checkbox-label {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.link {
  color: #ff7b3d;
  text-decoration: none;
  transition: color 0.3s;
}

.link:hover {
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

.form-input.error {
  border-color: #ff4757;
}

.form-input.success {
  border-color: #2ed573;
}

/* 响应式设计 */
@media (max-width: 1000px) {
  .container {
    flex-direction: column;
    max-width: 600px;
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
}
</style>
