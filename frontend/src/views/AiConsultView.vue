<template>
  <div class="ai-consult-page">
    <div class="chat-container">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="ai-avatar">
          <i class="fas fa-robot"></i>
        </div>
        <div class="ai-info">
          <h2>宠物小助手</h2>
          <span class="status online">在线</span>
        </div>
      </div>

      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <!-- 欢迎消息 -->
        <div class="message ai-message">
          <div class="avatar">
            <i class="fas fa-robot"></i>
          </div>
          <div class="content">
            <p>你好！我是宠物小助手 🐾</p>
            <p>我可以帮你解答关于宠物饲养、健康、训练等方面的问题。有什么可以帮到你的吗？</p>
          </div>
        </div>

        <!-- 推荐问题 -->
        <div class="suggestions" v-if="messages.length === 0">
          <p class="suggestions-title">你可以问我：</p>
          <div class="suggestion-tags">
            <span
              v-for="(suggestion, index) in suggestions"
              :key="index"
              class="suggestion-tag"
              @click="askQuestion(suggestion)"
            >
              {{ suggestion }}
            </span>
          </div>
        </div>

        <!-- 聊天记录 -->
        <template v-for="(msg, index) in messages" :key="index">
          <div :class="['message', msg.role === 'user' ? 'user-message' : 'ai-message']">
            <div class="avatar">
              <i :class="msg.role === 'user' ? 'fas fa-user' : 'fas fa-robot'"></i>
            </div>
            <div class="content">
              <p v-html="formatMessage(msg.content)"></p>
            </div>
          </div>
        </template>

        <!-- 加载中 -->
        <div class="message ai-message" v-if="loading">
          <div class="avatar">
            <i class="fas fa-robot"></i>
          </div>
          <div class="content typing">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <input
          v-model="inputMessage"
          type="text"
          placeholder="输入你的问题..."
          @keyup.enter="sendMessage"
          :disabled="loading"
        />
        <button @click="sendMessage" :disabled="loading || !inputMessage.trim()">
          <i class="fas fa-paper-plane"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { chatWithAi, getAiSuggestions } from '@/api/ai'

export default {
  name: 'AiConsultView',
  data() {
    return {
      inputMessage: '',
      messages: [],
      loading: false,
      suggestions: [
        '如何给猫咪选择合适的猫粮？',
        '狗狗不吃东西怎么办？',
        '新手养猫需要准备什么？',
        '如何训练狗狗定点大小便？'
      ]
    }
  },
  async mounted() {
    await this.loadSuggestions()
  },
  methods: {
    async loadSuggestions() {
      try {
        const res = await getAiSuggestions()
        if (res.code === 200 && res.data) {
          this.suggestions = res.data
        }
      } catch (e) {
        console.error('加载推荐问题失败', e)
      }
    },

    askQuestion(question) {
      this.inputMessage = question
      this.sendMessage()
    },

    async sendMessage() {
      const message = this.inputMessage.trim()
      if (!message || this.loading) return

      // 添加用户消息
      this.messages.push({
        role: 'user',
        content: message
      })
      this.inputMessage = ''
      this.scrollToBottom()

      // 调用AI接口
      this.loading = true
      try {
        const res = await chatWithAi(message)
        if (res.code === 200 && res.data) {
          this.messages.push({
            role: 'assistant',
            content: res.data.reply
          })
        } else {
          this.messages.push({
            role: 'assistant',
            content: res.message || '抱歉，我暂时无法回答，请稍后再试。'
          })
        }
      } catch (e) {
        console.error('AI请求失败', e)
        this.messages.push({
          role: 'assistant',
          content: '网络出错了，请检查网络后重试。'
        })
      } finally {
        this.loading = false
        this.scrollToBottom()
      }
    },

    formatMessage(content) {
      // 简单的换行处理
      return content.replace(/\n/g, '<br>')
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    }
  }
}
</script>

<style scoped>
.ai-consult-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff8e1 0%, #ffe0b2 100%);
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.chat-container {
  width: 100%;
  max-width: 800px;
  height: 85vh;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部 */
.chat-header {
  background: linear-gradient(135deg, #ffb347, #e69500);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.ai-avatar {
  width: 50px;
  height: 50px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #ffb347;
}

.ai-info h2 {
  color: white;
  margin: 0;
  font-size: 20px;
}

.status {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
}

.status.online::before {
  content: '';
  display: inline-block;
  width: 8px;
  height: 8px;
  background: #4caf50;
  border-radius: 50%;
  margin-right: 5px;
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message {
  display: flex;
  gap: 10px;
  max-width: 80%;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.ai-message {
  align-self: flex-start;
}

.message .avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ai-message .avatar {
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
}

.user-message .avatar {
  background: #5a4a42;
  color: white;
}

.message .content {
  padding: 12px 16px;
  border-radius: 18px;
  line-height: 1.5;
}

.ai-message .content {
  background: #f5f5f5;
  color: #333;
  border-bottom-left-radius: 4px;
}

.user-message .content {
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  border-bottom-right-radius: 4px;
}

.message .content p {
  margin: 0;
}

.message .content p + p {
  margin-top: 8px;
}

/* 打字动画 */
.typing {
  display: flex;
  gap: 4px;
  padding: 15px 20px !important;
}

.typing span {
  width: 8px;
  height: 8px;
  background: #999;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing span:nth-child(1) { animation-delay: 0s; }
.typing span:nth-child(2) { animation-delay: 0.2s; }
.typing span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-10px); }
}

/* 推荐问题 */
.suggestions {
  background: #fff8e1;
  padding: 15px;
  border-radius: 12px;
  margin-top: 10px;
}

.suggestions-title {
  color: #5a4a42;
  font-size: 14px;
  margin: 0 0 10px 0;
}

.suggestion-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.suggestion-tag {
  background: white;
  color: #e69500;
  padding: 8px 14px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  border: 1px solid #ffcc80;
  transition: all 0.3s;
}

.suggestion-tag:hover {
  background: #ffb347;
  color: white;
  border-color: #ffb347;
}

/* 输入区域 */
.chat-input {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}

.chat-input input {
  flex: 1;
  padding: 12px 20px;
  border: 2px solid #ffecb3;
  border-radius: 25px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.3s;
}

.chat-input input:focus {
  border-color: #ffb347;
}

.chat-input button {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.3s;
}

.chat-input button:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 5px 15px rgba(255, 179, 71, 0.4);
}

.chat-input button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 滚动条 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f5f5f5;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #ffcc80;
  border-radius: 3px;
}

/* 响应式 */
@media (max-width: 768px) {
  .ai-consult-page {
    padding: 10px;
  }

  .chat-container {
    height: 90vh;
    border-radius: 15px;
  }

  .message {
    max-width: 90%;
  }
}
</style>
