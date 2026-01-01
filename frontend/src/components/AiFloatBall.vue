<template>
  <div class="ai-float-wrapper">
    <!-- 悬浮球 -->
    <div
      class="ai-float-ball"
      :style="{ left: position.x + 'px', top: position.y + 'px' }"
      @mousedown="startDrag"
      @touchstart="startDrag"
      @click="toggleChat"
    >
      <i class="fas fa-robot"></i>
      <span class="ball-text">AI</span>
    </div>

    <!-- 聊天窗口 -->
    <transition name="chat-popup">
      <div v-if="showChat" class="ai-chat-popup" :style="chatPosition">
        <div class="chat-header">
          <div class="header-info">
            <i class="fas fa-robot"></i>
            <span>宠物小助手</span>
          </div>
          <button class="close-btn" @click="showChat = false">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="chat-messages" ref="messagesBox">
          <!-- 欢迎消息 -->
          <div class="message ai">
            <p>你好！我是宠物小助手 🐾 有什么可以帮你的吗？</p>
          </div>

          <!-- 快捷问题 -->
          <div class="quick-questions" v-if="messages.length === 0">
            <span
              v-for="(q, i) in quickQuestions"
              :key="i"
              @click="askQuestion(q)"
            >{{ q }}</span>
          </div>

          <!-- 聊天记录 -->
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.role]"
          >
            <p v-html="formatMsg(msg.content)"></p>
          </div>

          <!-- 加载中 -->
          <div class="message ai loading" v-if="loading">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>

        <div class="chat-input">
          <input
            v-model="inputMsg"
            placeholder="输入问题..."
            @keyup.enter="sendMessage"
            :disabled="loading"
          />
          <button @click="sendMessage" :disabled="loading || !inputMsg.trim()">
            <i class="fas fa-paper-plane"></i>
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { chatWithAi } from '@/api/ai'

export default {
  name: 'AiFloatBall',
  data() {
    return {
      position: { x: window.innerWidth - 80, y: window.innerHeight - 200 },
      isDragging: false,
      hasMoved: false,
      startPos: { x: 0, y: 0 },
      showChat: false,
      inputMsg: '',
      messages: [],
      loading: false,
      quickQuestions: [
        '猫咪不吃东西怎么办？',
        '狗狗疫苗怎么打？',
        '如何训练宠物？'
      ]
    }
  },
  computed: {
    chatPosition() {
      const chatWidth = 350
      const chatHeight = 450
      let x = this.position.x - chatWidth - 10
      let y = this.position.y - chatHeight / 2

      // 边界检测
      if (x < 10) x = this.position.x + 70
      if (y < 10) y = 10
      if (y + chatHeight > window.innerHeight - 10) {
        y = window.innerHeight - chatHeight - 10
      }

      return { left: x + 'px', top: y + 'px' }
    }
  },
  methods: {
    startDrag(e) {
      this.isDragging = true
      this.hasMoved = false
      const event = e.touches ? e.touches[0] : e
      this.startPos = {
        x: event.clientX - this.position.x,
        y: event.clientY - this.position.y
      }
      document.addEventListener('mousemove', this.onDrag)
      document.addEventListener('mouseup', this.stopDrag)
      document.addEventListener('touchmove', this.onDrag)
      document.addEventListener('touchend', this.stopDrag)
    },
    onDrag(e) {
      if (!this.isDragging) return
      const event = e.touches ? e.touches[0] : e
      const newX = event.clientX - this.startPos.x
      const newY = event.clientY - this.startPos.y

      // 检测是否移动了
      if (Math.abs(newX - this.position.x) > 5 || Math.abs(newY - this.position.y) > 5) {
        this.hasMoved = true
      }

      // 边界限制
      this.position.x = Math.max(0, Math.min(newX, window.innerWidth - 60))
      this.position.y = Math.max(0, Math.min(newY, window.innerHeight - 60))
    },
    stopDrag() {
      this.isDragging = false
      document.removeEventListener('mousemove', this.onDrag)
      document.removeEventListener('mouseup', this.stopDrag)
      document.removeEventListener('touchmove', this.onDrag)
      document.removeEventListener('touchend', this.stopDrag)
    },
    toggleChat() {
      if (!this.hasMoved) {
        this.showChat = !this.showChat
      }
    },
    askQuestion(q) {
      this.inputMsg = q
      this.sendMessage()
    },
    async sendMessage() {
      const msg = this.inputMsg.trim()
      if (!msg || this.loading) return

      this.messages.push({ role: 'user', content: msg })
      this.inputMsg = ''
      this.scrollToBottom()

      this.loading = true
      try {
        const res = await chatWithAi(msg)
        if (res.code === 200 && res.data) {
          this.messages.push({ role: 'ai', content: res.data.reply })
        } else {
          this.messages.push({ role: 'ai', content: res.message || '抱歉，请稍后再试' })
        }
      } catch (e) {
        this.messages.push({ role: 'ai', content: '网络出错，请重试' })
      } finally {
        this.loading = false
        this.scrollToBottom()
      }
    },
    formatMsg(content) {
      return content.replace(/\n/g, '<br>')
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const box = this.$refs.messagesBox
        if (box) box.scrollTop = box.scrollHeight
      })
    }
  }
}
</script>

<style scoped>
.ai-float-wrapper {
  position: fixed;
  z-index: 9999;
}

.ai-float-ball {
  position: fixed;
  width: 55px;
  height: 55px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(255, 179, 71, 0.5);
  transition: transform 0.2s, box-shadow 0.2s;
  user-select: none;
}

.ai-float-ball:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(255, 179, 71, 0.6);
}

.ai-float-ball i {
  font-size: 20px;
}

.ball-text {
  font-size: 10px;
  font-weight: bold;
  margin-top: 2px;
}

/* 聊天窗口 */
.ai-chat-popup {
  position: fixed;
  width: 350px;
  height: 450px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: bold;
}

.close-btn {
  background: rgba(255,255,255,0.2);
  border: none;
  color: white;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  transition: background 0.3s;
}

.close-btn:hover {
  background: rgba(255,255,255,0.3);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message {
  max-width: 85%;
  padding: 10px 14px;
  border-radius: 16px;
  line-height: 1.4;
  font-size: 14px;
}

.message p {
  margin: 0;
}

.message.ai {
  align-self: flex-start;
  background: #fff8e1;
  color: #5a4a42;
  border-bottom-left-radius: 4px;
}

.message.user {
  align-self: flex-end;
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  border-bottom-right-radius: 4px;
}

.message.loading {
  display: flex;
  gap: 4px;
  padding: 15px;
}

.dot {
  width: 8px;
  height: 8px;
  background: #e69500;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
}

.dot:nth-child(1) { animation-delay: 0s; }
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-8px); }
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 5px;
}

.quick-questions span {
  background: #fff8e1;
  color: #e69500;
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  cursor: pointer;
  border: 1px solid #ffcc80;
  transition: all 0.3s;
}

.quick-questions span:hover {
  background: #ffb347;
  color: white;
  border-color: #ffb347;
}

.chat-input {
  padding: 12px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 8px;
}

.chat-input input {
  flex: 1;
  padding: 10px 15px;
  border: 2px solid #ffecb3;
  border-radius: 20px;
  outline: none;
  font-size: 14px;
}

.chat-input input:focus {
  border-color: #ffb347;
}

.chat-input button {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #ffb347, #e69500);
  color: white;
  cursor: pointer;
  transition: transform 0.2s;
}

.chat-input button:hover:not(:disabled) {
  transform: scale(1.05);
}

.chat-input button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 动画 */
.chat-popup-enter-active,
.chat-popup-leave-active {
  transition: all 0.3s ease;
}

.chat-popup-enter-from,
.chat-popup-leave-to {
  opacity: 0;
  transform: scale(0.8);
}

/* 滚动条 */
.chat-messages::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #ffcc80;
  border-radius: 2px;
}
</style>
