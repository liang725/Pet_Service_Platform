<template>
  <div class="user-management">
    <div class="page-header">
      <h1>用户管理</h1>
      <p>管理系统用户信息和权限</p>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <div class="search-box">
        <Icon icon="mdi:magnify" class="search-icon" />
        <input
          v-model="searchKeyword"
          placeholder="搜索用户名..."
          @keyup.enter="searchUsers"
        />
        <button class="search-btn" @click="searchUsers">搜索</button>
      </div>
      <button class="add-btn" @click="openAddDialog">
        <Icon icon="mdi:plus" /> 添加用户
      </button>
    </div>

    <!-- 用户列表 -->
    <div v-if="loading" class="loading-state">
      <Icon icon="mdi:loading" class="spin" />
      <span>加载中...</span>
    </div>

    <div v-else-if="users.length === 0" class="empty-state">
      <Icon icon="mdi:account-group" class="empty-icon" />
      <h3>暂无用户</h3>
      <p>点击上方按钮添加第一个用户</p>
    </div>

    <div v-else class="table-container">
      <table class="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>角色</th>
            <th>状态</th>
            <th>注册时间</th>
            <th>最后登录</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>
              <div class="user-info">
                <Icon icon="mdi:account-circle" class="user-icon" />
                <span>{{ user.username }}</span>
              </div>
            </td>
            <td>
              <span class="role-badge" :class="user.role">
                <Icon v-if="user.role === 'admin'" icon="mdi:shield-crown" />
                <Icon v-else icon="mdi:account" />
                {{ user.role === 'admin' ? '管理员' : '普通用户' }}
              </span>
            </td>
            <td>
              <span class="status-badge" :class="{ active: user.status === 1 }">
                {{ user.status === 1 ? '正常' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td>{{ user.lastLoginAt ? formatDate(user.lastLoginAt) : '未登录' }}</td>
            <td>
              <div class="action-buttons">
                <button class="action-btn edit" @click="openEditDialog(user)" title="编辑">
                  <Icon icon="mdi:pencil" />
                </button>
                <button
                  class="action-btn password"
                  @click="openResetPasswordDialog(user)"
                  title="重置密码"
                >
                  <Icon icon="mdi:lock-reset" />
                </button>
                <button
                  v-if="user.role !== 'admin'"
                  class="action-btn toggle"
                  @click="toggleUserStatus(user)"
                  :title="user.status === 1 ? '禁用' : '启用'"
                >
                  <Icon :icon="user.status === 1 ? 'mdi:account-off' : 'mdi:account-check'" />
                </button>
                <button
                  v-if="user.role !== 'admin'"
                  class="action-btn delete"
                  @click="deleteUser(user)"
                  title="删除"
                >
                  <Icon icon="mdi:delete" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination">
      <div class="page-size-selector">
        <span>每页显示</span>
        <select v-model.number="pageSize" @change="changePageSize">
          <option :value="5">5 条</option>
          <option :value="10">10 条</option>
          <option :value="20">20 条</option>
          <option :value="50">50 条</option>
          <option :value="100">100 条</option>
        </select>
      </div>
      <div class="page-controls">
        <button :disabled="page === 1" @click="changePage(page - 1)">上一页</button>
        <span class="page-info">第 {{ page }} / {{ totalPages }} 页，共 {{ total }} 条</span>
        <button :disabled="page >= totalPages" @click="changePage(page + 1)">下一页</button>
      </div>
    </div>

    <!-- 添加/编辑用户对话框 -->
    <div v-if="showDialog" class="dialog-overlay" @click="closeDialog">
      <div class="dialog" @click.stop>
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑用户' : '添加用户' }}</h3>
          <button class="close-btn" @click="closeDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>
        <form class="dialog-body" @submit.prevent="saveUser">
          <div class="form-group">
            <label>用户名 <span class="required">*</span></label>
            <input
              v-model="form.username"
              :disabled="isEdit"
              required
              placeholder="请输入用户名"
            />
            <small v-if="isEdit" class="hint">用户名不可修改</small>
          </div>

          <div v-if="!isEdit" class="form-group">
            <label>密码 <span class="required">*</span></label>
            <input
              v-model="form.password"
              type="password"
              required
              minlength="6"
              placeholder="请输入密码（至少6位）"
            />
          </div>

          <div v-if="!isEdit" class="form-group">
            <label>确认密码 <span class="required">*</span></label>
            <input
              v-model="form.confirmPassword"
              type="password"
              required
              minlength="6"
              placeholder="请再次输入密码"
            />
          </div>

          <div class="form-group">
            <label>角色 <span class="required">*</span></label>
            <select v-model="form.role" required>
              <option value="user">普通用户</option>
              <option value="admin">管理员</option>
            </select>
          </div>

          <div class="form-group">
            <label>状态 <span class="required">*</span></label>
            <select v-model.number="form.status" required>
              <option :value="1">正常</option>
              <option :value="0">禁用</option>
            </select>
          </div>

          <div class="dialog-actions">
            <button type="button" class="cancel-btn" @click="closeDialog">取消</button>
            <button type="submit" class="submit-btn" :disabled="saving">
              <Icon v-if="saving" icon="mdi:loading" class="spin" />
              {{ saving ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 重置密码对话框 -->
    <div v-if="showResetPasswordDialog" class="dialog-overlay" @click="closeResetPasswordDialog">
      <div class="dialog small" @click.stop>
        <div class="dialog-header">
          <h3>重置密码</h3>
          <button class="close-btn" @click="closeResetPasswordDialog">
            <Icon icon="mdi:close" />
          </button>
        </div>
        <div class="dialog-body">
          <div class="reset-password-confirm">
            <Icon icon="mdi:alert-circle" class="warning-icon" />
            <p class="confirm-message">确定要将该用户的密码重置为默认密码吗？</p>
            <p class="default-password-hint">默认密码：<strong>123456</strong></p>
            <p class="security-hint">建议用户登录后立即修改密码</p>
          </div>

          <div class="dialog-actions">
            <button type="button" class="cancel-btn" @click="closeResetPasswordDialog">
              取消
            </button>
            <button type="button" class="submit-btn" :disabled="saving" @click="resetPassword">
              <Icon v-if="saving" icon="mdi:loading" class="spin" />
              {{ saving ? '重置中...' : '确认重置' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Icon } from '@iconify/vue'
import request from '@/utils/request'

const users = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const showDialog = ref(false)
const isEdit = ref(false)
const saving = ref(false)

const showResetPasswordDialog = ref(false)
const currentUserId = ref(null)

const form = ref({
  username: '',
  password: '',
  role: 'user',
  status: 1
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

onMounted(() => {
  loadUsers()
})

async function loadUsers() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    const res = await request({ url: '/api/admin/users', method: 'GET', params })
    if (res.code === 200) {
      users.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载用户失败:', error)
    alert('加载用户失败')
  } finally {
    loading.value = false
  }
}

function searchUsers() {
  page.value = 1
  loadUsers()
}

function changePage(newPage) {
  page.value = newPage
  loadUsers()
}

function changePageSize() {
  page.value = 1 // 重置到第一页
  loadUsers()
}

function openAddDialog() {
  isEdit.value = false
  form.value = {
    username: '',
    password: '',
    confirmPassword: '',
    role: 'user',
    status: 1
  }
  showDialog.value = true
}

function openEditDialog(user) {
  isEdit.value = true
  form.value = {
    id: user.id,
    username: user.username,
    role: user.role,
    status: user.status
  }
  showDialog.value = true
}

function closeDialog() {
  showDialog.value = false
}

async function saveUser() {
  // 添加用户时验证密码
  if (!isEdit.value) {
    if (!form.value.password || form.value.password.trim().length === 0) {
      alert('密码不能为空')
      return
    }
    if (form.value.password.length < 6) {
      alert('密码长度不能少于6位')
      return
    }
    if (form.value.password !== form.value.confirmPassword) {
      alert('两次输入的密码不一致')
      return
    }
  }

  saving.value = true
  try {
    let res
    if (isEdit.value) {
      res = await request({
        url: `/api/admin/users/${form.value.id}`,
        method: 'PUT',
        data: form.value
      })
    } else {
      // 创建用户时，不发送 confirmPassword 字段
      const {  ...userData } = form.value
      res = await request({
        url: '/api/admin/users',
        method: 'POST',
        data: userData
      })
    }

    if (res.code === 200) {
      alert(isEdit.value ? '更新成功' : '添加成功')
      closeDialog()
      loadUsers()
    } else {
      alert(res.message || '操作失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  } finally {
    saving.value = false
  }
}

function openResetPasswordDialog(user) {
  currentUserId.value = user.id
  showResetPasswordDialog.value = true
}

function closeResetPasswordDialog() {
  showResetPasswordDialog.value = false
  currentUserId.value = null
}

async function resetPassword() {
  saving.value = true
  try {
    const res = await request({
      url: `/api/admin/users/${currentUserId.value}/reset-password`,
      method: 'PUT',
      data: { password: '123456' }
    })

    if (res.code === 200) {
      alert('密码已重置为：123456')
      closeResetPasswordDialog()
    } else {
      alert(res.message || '密码重置失败')
    }
  } catch (error) {
    console.error('密码重置失败:', error)
    alert('密码重置失败')
  } finally {
    saving.value = false
  }
}

async function toggleUserStatus(user) {
  const newStatus = user.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'

  if (!confirm(`确定要${action}用户"${user.username}"吗？`)) {
    return
  }

  try {
    const res = await request({
      url: `/api/admin/users/${user.id}/status`,
      method: 'PUT',
      data: { status: newStatus }
    })

    if (res.code === 200) {
      alert(`${action}成功`)
      loadUsers()
    } else {
      alert(res.message || `${action}失败`)
    }
  } catch (error) {
    console.error(`${action}失败:`, error)
    alert(`${action}失败`)
  }
}

async function deleteUser(user) {
  if (!confirm(`确定要删除用户"${user.username}"吗？此操作不可恢复！`)) {
    return
  }

  try {
    const res = await request({
      url: `/api/admin/users/${user.id}`,
      method: 'DELETE'
    })

    if (res.code === 200) {
      alert('删除成功')
      loadUsers()
    } else {
      alert(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败')
  }
}

function formatDate(dateString) {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

</script>
<style scoped>
.user-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 24px;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
}

.search-box {
  flex: 1;
  max-width: 500px;
  display: flex;
  align-items: center;
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  padding: 0 12px;
  transition: all 0.3s ease;
}

.search-box:focus-within {
  border-color: #e17055;
  box-shadow: 0 0 0 3px rgba(225, 112, 85, 0.1);
}

.search-icon {
  color: #999;
  font-size: 20px;
  margin-right: 8px;
}

.search-box input {
  flex: 1;
  border: none;
  outline: none;
  padding: 10px 8px;
  font-size: 14px;
}

.search-btn {
  padding: 8px 20px;
  background: #e17055;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.search-btn:hover {
  background: #d35400;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: #e17055;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.add-btn:hover {
  background: #d35400;
  transform: translateY(-1px);
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 10px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #666;
}

.empty-icon {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 16px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  color: #333;
}

.empty-state p {
  margin: 0;
  color: #999;
}

.table-container {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table thead {
  background: #f8f9fa;
}

.user-table th {
  padding: 14px 12px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  font-size: 13px;
  border-bottom: 2px solid #dee2e6;
}

.user-table td {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 13px;
  color: #333;
}

.user-table tbody tr:hover {
  background: #f8f9fa;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-icon {
  font-size: 28px;
  color: #adb5bd;
}

.role-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.role-badge.admin {
  background: #fff3cd;
  color: #856404;
}

.role-badge.user {
  background: #d1ecf1;
  color: #0c5460;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  background: #f8d7da;
  color: #721c24;
}

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.action-buttons {
  display: flex;
  gap: 6px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  font-size: 16px;
}

.action-btn.edit {
  background: #e3f2fd;
  color: #1976d2;
}

.action-btn.edit:hover {
  background: #1976d2;
  color: white;
}

.action-btn.password {
  background: #fff3e0;
  color: #f57c00;
}

.action-btn.password:hover {
  background: #f57c00;
  color: white;
}

.action-btn.toggle {
  background: #f3e5f5;
  color: #7b1fa2;
}

.action-btn.toggle:hover {
  background: #7b1fa2;
  color: white;
}

.action-btn.delete {
  background: #ffebee;
  color: #c62828;
}

.action-btn.delete:hover {
  background: #c62828;
  color: white;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-top: 20px;
  padding: 16px;
  background: white;
  border-radius: 8px;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.page-size-selector select {
  padding: 6px 10px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.page-size-selector select:hover {
  border-color: #e17055;
}

.page-size-selector select:focus {
  outline: none;
  border-color: #e17055;
  box-shadow: 0 0 0 3px rgba(225, 112, 85, 0.1);
}

.page-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #dee2e6;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.pagination button:hover:not(:disabled) {
  background: #e17055;
  color: white;
  border-color: #e17055;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 14px;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.dialog.small {
  max-width: 400px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f8f9fa;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  font-size: 20px;
  color: #666;
}

.close-btn:hover {
  background: #e9ecef;
  color: #333;
}

.dialog-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #333;
  font-size: 13px;
}

.required {
  color: #e74c3c;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #e17055;
  box-shadow: 0 0 0 3px rgba(225, 112, 85, 0.1);
}

.form-group input:disabled {
  background: #f8f9fa;
  cursor: not-allowed;
}

.hint {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e9ecef;
}

.cancel-btn {
  padding: 10px 20px;
  border: 1px solid #dee2e6;
  background: #f8f9fa;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #495057;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.submit-btn {
  padding: 10px 20px;
  border: none;
  background: #e17055;
  color: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.submit-btn:hover {
  background: #d35400;
}

.submit-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 重置密码确认对话框样式 */
.reset-password-confirm {
  text-align: center;
  padding: 20px 0;
}

.warning-icon {
  font-size: 48px;
  color: #f57c00;
  margin-bottom: 16px;
}

.confirm-message {
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
}

.default-password-hint {
  font-size: 18px;
  color: #e17055;
  margin-bottom: 8px;
}

.default-password-hint strong {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 2px;
}

.security-hint {
  font-size: 13px;
  color: #999;
  margin-top: 12px;
}
</style>
