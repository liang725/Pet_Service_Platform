<!-- src/views/AuthView.vue -->
<template>
  <div class="auth-view">
    <!-- 背景爪印装饰 -->
    <div class="paw-print">
      <Icon icon="mdi:paw" />
    </div>
    <div class="paw-print">
      <Icon icon="mdi:paw" />
    </div>
    <div class="paw-print">
      <Icon icon="mdi:paw" />
    </div>
    <div class="paw-print">
      <Icon icon="mdi:paw" />
    </div>

    <div class="container">
      <!-- 左侧欢迎区域 -->
      <div class="welcome-section">
        <div class="logo">
          <div class="logo-icon">
            <Icon icon="mdi:paw" />
          </div>
          <div class="logo-text">宠物之家</div>
        </div>

        <h1 class="welcome-title">
          {{ isLoginMode ? '欢迎来到宠物之家' : '加入宠物之家' }}
        </h1>
        <p class="welcome-text">
          {{ isLoginMode ? 
            '在这里，我们为您的宠物提供最温馨的家。无论您是宠物主人还是宠物爱好者，都可以在这里找到归属感。' :
            '创建您的专属账户，开启美好的宠物之旅。在这里，您可以为您的爱宠找到最贴心的服务和最温暖的社区。'
          }}
        </p>

        <div class="features">
          <div v-if="isLoginMode" class="feature">
            <Icon icon="mdi:check-circle" class="feature-check" />
            <span>专业宠物健康咨询</span>
          </div>
          <div v-if="isLoginMode" class="feature">
            <Icon icon="mdi:check-circle" class="feature-check" />
            <span>预约洗护服务</span>
          </div>
          <div v-if="isLoginMode" class="feature">
            <Icon icon="mdi:check-circle" class="feature-check" />
            <span>宠物商城购物</span>
          </div>
          
          <div v-if="!isLoginMode" class="feature">
            <Icon icon="mdi:heart" class="feature-icon" />
            <span>专业的宠物护理服务</span>
          </div>
          <div v-if="!isLoginMode" class="feature">
            <Icon icon="mdi:account-group" class="feature-icon" />
            <span>活跃的宠物爱好者社区</span>
          </div>
          <div v-if="!isLoginMode" class="feature">
            <Icon icon="mdi:cart" class="feature-icon" />
            <span>优质的宠物用品商城</span>
          </div>
        </div>

        <!-- 宠物装饰 -->
        <div class="pet-dog">
          <Icon icon="mdi:dog" />
        </div>
        <div class="pet-cat">
          <Icon icon="mdi:cat" />
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="form-section">
        <!-- 登录表单 -->
        <div v-if="isLoginMode" class="form-container login-form">
          <h2 class="form-title">欢迎来到宠物之家</h2>
          <p class="form-subtitle">请登录您的账户继续使用宠物之家</p>

          <form @submit.prevent="handleLogin">
            <div class="form-group">
              <label class="form-label" for="loginUsername">用户名</label>
              <div class="input-with-icon">
                <input
                  v-model="loginForm.username"
                  type="text"
                  id="loginUsername"
                  :class="[
                    'form-input',
                    { error: loginForm.usernameError, success: loginForm.usernameSuccess },
                  ]"
                  placeholder="请输入您的用户名"
                  required
                  @input="clearLoginUsernameError"
                />
                <div class="input-icon">
                  <Icon icon="mdi:account" />
                </div>
              </div>
              <div v-if="loginForm.usernameError" class="error-message">请输入用户名</div>
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
                <div class="input-icon">
                  <Icon icon="mdi:lock" />
                </div>
                <div class="toggle-password" @click="toggleLoginPasswordVisibility">
                  <Icon :icon="showLoginPassword ? 'mdi:eye-off' : 'mdi:eye'" />
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
              <Icon v-if="isLoading" icon="mdi:loading" class="spinner" spin />
              {{ loginButtonText }}
            </button>
          </form>

          <div class="switch-form">
            还没有账户？
            <a href="#" class="register-link" @click.prevent="switchToRegister">立即注册</a>
          </div>
        </div>

        <!-- 注册表单 -->
        <div v-else class="form-container register-form">
          <h2 class="form-title">创建新账户</h2>
          <p class="form-subtitle">填写以下信息完成注册</p>

          <form @submit.prevent="handleRegister" class="register-form-content">
            <div class="form-row">
              <div class="form-group form-group-half">
                <label class="form-label" for="registerUsername">用户名 *</label>
                <div class="input-with-icon">
                  <input
                    v-model="registerForm.username"
                    type="text"
                    id="registerUsername"
                    :class="[
                      'form-input',
                      { error: registerForm.usernameError, success: registerForm.usernameSuccess },
                    ]"
                    placeholder="用户名（3-50字符）"
                    required
                    @input="clearRegisterUsernameError"
                  />
                  <div class="input-icon">
                    <Icon icon="mdi:account" />
                  </div>
                </div>
                <div v-if="registerForm.usernameError" class="error-message">
                  {{ registerForm.usernameErrorMsg }}
                </div>
              </div>

              <div class="form-group form-group-half">
                <label class="form-label" for="registerEmail">电子邮箱 *</label>
                <div class="input-with-icon">
                  <input
                    v-model="registerForm.email"
                    type="email"
                    id="registerEmail"
                    :class="[
                      'form-input',
                      { error: registerForm.emailError, success: registerForm.emailSuccess },
                    ]"
                    placeholder="邮箱地址"
                    required
                    @input="clearRegisterEmailError"
                  />
                  <div class="input-icon">
                    <Icon icon="mdi:email" />
                  </div>
                </div>
                <div v-if="registerForm.emailError" class="error-message">
                  {{ registerForm.emailErrorMsg }}
                </div>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group form-group-half">
                <label class="form-label" for="registerPassword">密码 *</label>
                <div class="input-with-icon">
                  <input
                    v-model="registerForm.password"
                    :type="showRegisterPassword ? 'text' : 'password'"
                    id="registerPassword"
                    :class="[
                      'form-input',
                      { error: registerForm.passwordError, success: registerForm.passwordSuccess },
                    ]"
                    placeholder="密码（至少6位）"
                    required
                    @input="clearRegisterPasswordError"
                  />
                  <div class="input-icon">
                    <Icon icon="mdi:lock" />
                  </div>
                  <div class="toggle-password" @click="toggleRegisterPasswordVisibility">
                    <Icon :icon="showRegisterPassword ? 'mdi:eye-off' : 'mdi:eye'" />
                  </div>
                </div>
                <div v-if="registerForm.passwordError" class="error-message">
                  {{ registerForm.passwordErrorMsg }}
                </div>
              </div>

              <div class="form-group form-group-half">
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
                    placeholder="确认密码"
                    required
                    @input="clearRegisterConfirmPasswordError"
                  />
                  <div class="input-icon">
                    <Icon icon="mdi:lock-check" />
                  </div>
                </div>
                <div v-if="registerForm.confirmPasswordError" class="error-message">
                  {{ registerForm.confirmPasswordErrorMsg }}
                </div>
              </div>
            </div>

            <div class="form-group terms-group">
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
              <Icon v-if="isLoading" icon="mdi:loading" class="spinner" spin />
              {{ registerButtonText }}
            </button>
          </form>

          <div class="switch-form">
            已有账户？
            <a href="#" class="link" @click.prevent="switchToLogin">立即登录</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 状态管理
const isLoading = ref(false)
const showLoginPassword = ref(false)
const showRegisterPassword = ref(false)
const isLoginMode = ref(true) // 默认显示登录表单

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  remember: false,
  usernameError: false,
  usernameSuccess: false,
  passwordError: false,
  passwordSuccess: false,
})

// 注册表单数据
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  agreeTerms: false,
  
  usernameError: false,
  usernameSuccess: false,
  usernameErrorMsg: '',
  
  emailError: false,
  emailSuccess: false,
  emailErrorMsg: '',
  
  passwordError: false,
  passwordSuccess: false,
  passwordErrorMsg: '',
  
  confirmPasswordError: false,
  confirmPasswordSuccess: false,
  confirmPasswordErrorMsg: '',
})

// 按钮文本计算属性
const loginButtonText = computed(() => {
  return isLoading.value ? '登录中...' : '登录'
})

const registerButtonText = computed(() => {
  return isLoading.value ? '注册中...' : '立即注册'
})

// 正则表达式
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

// 在组件挂载时检查路由参数
onMounted(() => {
  if (route.query.mode === 'register') {
    isLoginMode.value = false
  }
})

// 切换登录/注册模式
const switchToRegister = () => {
  isLoginMode.value = false
  resetLoginForm()
}

const switchToLogin = () => {
  isLoginMode.value = true
  resetRegisterForm()
}

// 重置表单
const resetLoginForm = () => {
  Object.assign(loginForm, {
    username: '',
    password: '',
    remember: false,
    usernameError: false,
    usernameSuccess: false,
    passwordError: false,
    passwordSuccess: false,
  })
  showLoginPassword.value = false
}

const resetRegisterForm = () => {
  Object.assign(registerForm, {
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    agreeTerms: false,
    
    usernameError: false,
    usernameSuccess: false,
    usernameErrorMsg: '',
    
    emailError: false,
    emailSuccess: false,
    emailErrorMsg: '',
    
    passwordError: false,
    passwordSuccess: false,
    passwordErrorMsg: '',
    
    confirmPasswordError: false,
    confirmPasswordSuccess: false,
    confirmPasswordErrorMsg: '',
  })
  showRegisterPassword.value = false
}

// 切换密码可见性
const toggleLoginPasswordVisibility = () => {
  showLoginPassword.value = !showLoginPassword.value
}

const toggleRegisterPasswordVisibility = () => {
  showRegisterPassword.value = !showRegisterPassword.value
}

// 清除登录表单错误状态
const clearLoginUsernameError = () => {
  loginForm.usernameError = false
}

const clearLoginPasswordError = () => {
  loginForm.passwordError = false
}

// 清除注册表单错误状态
const clearRegisterUsernameError = () => {
  registerForm.usernameError = false
  registerForm.usernameErrorMsg = ''
}

const clearRegisterEmailError = () => {
  registerForm.emailError = false
  registerForm.emailErrorMsg = ''
}

const clearRegisterPasswordError = () => {
  registerForm.passwordError = false
  registerForm.passwordErrorMsg = ''
}

const clearRegisterConfirmPasswordError = () => {
  registerForm.confirmPasswordError = false
  registerForm.confirmPasswordErrorMsg = ''
}

// 处理登录
const handleLogin = async () => {
  let isValid = true

  // 验证用户名
  if (!loginForm.username.trim()) {
    loginForm.usernameError = true
    loginForm.usernameSuccess = false
    isValid = false
  } else {
    loginForm.usernameError = false
    loginForm.usernameSuccess = true
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
    const loginData = {
      username: loginForm.username,
      password: loginForm.password
    }

    await userStore.login(loginData, loginForm.remember)

    // 登录成功，跳转到首页
    router.push('/')
  } catch (error) {
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
    const registerData = {
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.username,
      email: registerForm.email
    }

    await userStore.register(registerData)

    // 注册成功，显示提示并切换到登录
    alert('注册成功！请登录您的账户')
    switchToLogin()
    isLoading.value = false
  } catch (error) {
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

.paw-print {
  position: absolute;
  color: #ffb284;
  font-size: 24px;
  opacity: 0.3;
  z-index: 0;
}

.paw-print :deep(svg) {
  width: 24px;
  height: 24px;
  color: #ffb284;
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon :deep(svg) {
  width: 42px;
  height: 42px;
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

.feature-check,
.feature-icon {
  margin-right: 12px;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-check :deep(svg),
.feature-icon :deep(svg) {
  width: 20px;
  height: 20px;
  color: white;
}

/* 右侧表单区域 */
.form-section {
  flex: 1;
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-height: 700px;
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
  margin-bottom: 20px;
  font-size: 15px;
}

.form-group {
  margin-bottom: 15px;
  position: relative;
}

.form-label {
  display: block;
  margin-bottom: 6px;
  color: #555;
  font-weight: 500;
  font-size: 14px;
}

.input-with-icon {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 12px 12px 12px 45px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s;
  background-color: #f9f9f9;
  color: #333;
  font-family: inherit;
}

.form-input:focus {
  outline: none;
  border-color: #ff7b3d;
  background-color: white;
  box-shadow: 0 0 0 3px rgba(255, 122, 61, 0.1);
}

.form-input::placeholder {
  color: #aaa;
}

.input-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #888;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-icon :deep(svg) {
  width: 18px;
  height: 18px;
  color: #888;
}

.toggle-password {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #888;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  padding: 4px;
  transition: color 0.3s;
}

.toggle-password:hover {
  color: #ff7b3d;
}

.toggle-password :deep(svg) {
  width: 18px;
  height: 18px;
  color: inherit;
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  font-size: 14px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
}

.remember-me input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: #ff7b3d;
  cursor: pointer;
}

.remember-me label {
  color: #555;
  cursor: pointer;
  font-weight: 500;
  font-size: 13px;
}

.forgot-password {
  color: #ff7b3d;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
  font-size: 13px;
}

.forgot-password:hover {
  color: #ff9a5c;
  text-decoration: underline;
}

.submit-btn {
  background: linear-gradient(to right, #ff9a5c, #ff7b3d);
  color: white;
  border: none;
  padding: 14px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-family: inherit;
}

.submit-btn:hover:not(:disabled) {
  background: linear-gradient(to right, #ff8a4c, #ff6b2d);
  transform: translateY(-2px);
  box-shadow: 0 7px 15px rgba(255, 122, 61, 0.3);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.switch-form {
  text-align: center;
  color: #666;
  font-size: 14px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.switch-form a,
.register-link,
.link {
  color: #ff7b3d;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
  transition: color 0.3s;
  cursor: pointer;
}

.switch-form a:hover,
.register-link:hover,
.link:hover {
  color: #ff9a5c;
  text-decoration: underline;
}

.checkbox-group {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 15px;
}

.checkbox-label {
  font-size: 13px;
  color: #666;
  line-height: 1.4;
}

.checkbox-group input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: #ff7b3d;
  cursor: pointer;
  margin-top: 2px;
  flex-shrink: 0;
}

.pet-cat {
  position: absolute;
  bottom: 20px;
  right: 30px;
  font-size: 60px;
  color: rgba(255, 255, 255, 0.9);
  filter: drop-shadow(2px 2px 4px rgba(0, 0, 0, 0.1));
  display: flex;
  align-items: center;
  justify-content: center;
}

.pet-cat :deep(svg) {
  width: 60px;
  height: 60px;
  color: rgba(255, 255, 255, 0.9);
}

.pet-dog {
  position: absolute;
  top: 30px;
  left: 30px;
  font-size: 50px;
  color: rgba(255, 255, 255, 0.8);
  transform: rotate(-15deg);
  filter: drop-shadow(2px 2px 4px rgba(0, 0, 0, 0.1));
  display: flex;
  align-items: center;
  justify-content: center;
}

.pet-dog :deep(svg) {
  width: 50px;
  height: 50px;
  color: rgba(255, 255, 255, 0.8);
}

/* 表单验证样式 */
.error-message {
  color: #ff4757;
  font-size: 12px;
  margin-top: 5px;
  display: block;
  font-weight: 500;
}

.success-message {
  color: #2ed573;
  font-size: 12px;
  margin-top: 5px;
  display: block;
}

.form-input.error {
  border-color: #ff4757;
  background-color: #fff5f5;
}

.form-input.success {
  border-color: #2ed573;
  background-color: #f8fff9;
}

/* 表单切换动画 */
.login-form,
.register-form {
  transition: opacity 0.3s ease;
}

/* 注册表单特定样式 */
.register-form-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.form-row {
  display: flex;
  gap: 12px;
  width: 100%;
}

.form-group-half {
  flex: 1;
  min-width: 0;
}

.form-group-half .input-with-icon {
  width: 100%;
}

.terms-group {
  margin: 10px 0 15px;
}

/* 响应式设计 */
@media (max-width: 1000px) {
  .container {
    flex-direction: column;
    max-width: 600px;
    min-height: auto;
  }

  .welcome-section,
  .form-section {
    padding: 40px 30px;
  }

  .pet-cat,
  .pet-dog {
    display: none;
  }
  
  .form-row {
    flex-direction: column;
    gap: 10px;
  }
  
  .form-group-half {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .container {
    border-radius: 20px;
    min-height: auto;
  }

  .welcome-section,
  .form-section {
    padding: 35px 25px;
  }

  .welcome-title {
    font-size: 32px;
  }

  .logo-text {
    font-size: 40px;
  }

  .logo-icon {
    font-size: 36px;
  }

  .logo-icon :deep(svg) {
    width: 36px;
    height: 36px;
  }

  .form-title {
    font-size: 26px;
    text-align: center;
  }

  .form-subtitle {
    text-align: center;
    margin-bottom: 25px;
  }

  .remember-forgot {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
    margin-bottom: 15px;
  }
  
  .form-section {
    padding: 30px 25px;
  }
}

@media (max-width: 480px) {
  .auth-view {
    padding: 15px;
  }

  .container {
    border-radius: 15px;
    min-height: auto;
  }

  .welcome-section,
  .form-section {
    padding: 25px 20px;
  }

  .welcome-title {
    font-size: 28px;
  }

  .logo-text {
    font-size: 36px;
  }

  .logo-icon {
    font-size: 32px;
  }

  .logo-icon :deep(svg) {
    width: 32px;
    height: 32px;
  }

  .welcome-text {
    font-size: 15px;
    margin-bottom: 25px;
  }

  .form-title {
    font-size: 24px;
  }

  .form-subtitle {
    font-size: 14px;
    margin-bottom: 20px;
  }

  .form-input {
    padding: 10px 10px 10px 40px;
    font-size: 14px;
  }
  
  .input-icon {
    left: 12px;
    font-size: 16px;
  }
  
  .input-icon :deep(svg) {
    width: 16px;
    height: 16px;
  }

  .submit-btn {
    padding: 12px;
    font-size: 14px;
    margin-bottom: 15px;
  }

  .feature {
    padding: 10px 12px;
    font-size: 15px;
    margin-bottom: 10px;
  }

  .checkbox-label {
    font-size: 12px;
  }
  
  .switch-form {
    font-size: 13px;
    padding-top: 12px;
  }
  
  .toggle-password {
    right: 10px;
    font-size: 16px;
  }
  
  .toggle-password :deep(svg) {
    width: 16px;
    height: 16px;
  }
}

@media (max-width: 360px) {
  .welcome-title {
    font-size: 24px;
  }

  .logo-text {
    font-size: 32px;
  }

  .logo-icon {
    font-size: 28px;
  }

  .logo-icon :deep(svg) {
    width: 28px;
    height: 28px;
  }

  .form-title {
    font-size: 22px;
  }
  
  .auth-view {
    padding: 10px;
  }
  
  .welcome-section,
  .form-section {
    padding: 20px 15px;
  }
  
  .feature {
    padding: 8px 10px;
    font-size: 14px;
  }
}
</style>