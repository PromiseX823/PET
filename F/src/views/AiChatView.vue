﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<template>
  <div class="ai-chat-container">
    <!-- 顶部导航 -->
    <div class="chat-header">
      <div class="header-content">
        <div class="pet-avatar">
          <div class="avatar-bg"></div>
          <div class="avatar-icon">🐾</div>
          <div class="status-dot"></div>
        </div>
        <div class="info">
          <h2 class="title">宠物小助手</h2>
          <p class="subtitle">
            <span class="online-indicator"></span>
            在线 · 随时为您解答养宠问题
          </p>
        </div>
      </div>
      <div class="header-actions">
        <el-button class="action-btn" @click="showSettings = true">
          <span>⚙️</span>
        </el-button>
        <el-button class="action-btn" @click="clearChat">
          <span>🗑️</span>
        </el-button>
      </div>
    </div>

    <!-- 消息区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <!-- 欢迎卡片 -->
      <div v-if="messages.length === 0" class="welcome-section">
        <div class="welcome-card">
          <div class="welcome-illustration">
            <div class="pet-emoji-large">🐕</div>
            <div class="floating-paws">
              <span class="paw paw-1">🐾</span>
              <span class="paw paw-2">🐾</span>
              <span class="paw paw-3">🐾</span>
            </div>
          </div>
          <h3 class="welcome-title">你好呀！我是你的宠物小助手</h3>
          <p class="welcome-desc">我可以帮你解决养宠过程中的各种问题，让我们一起为毛孩子们创造更好的生活～</p>
          
          <div class="feature-grid">
            <div class="feature-item" @click="sendQuickMessage('我家狗狗最近食欲不好怎么办？')">
              <div class="feature-icon">🍖</div>
              <div class="feature-text">
                <div class="feature-title">健康咨询</div>
                <div class="feature-desc">解答宠物健康问题</div>
              </div>
            </div>
            <div class="feature-item" @click="sendQuickMessage('帮我写一段可爱的领养描述')">
              <div class="feature-icon">📝</div>
              <div class="feature-text">
                <div class="feature-title">领养文案</div>
                <div class="feature-desc">生成动人领养故事</div>
              </div>
            </div>
            <div class="feature-item" @click="sendQuickMessage('如何训练猫咪使用猫砂？')">
              <div class="feature-icon">🎓</div>
              <div class="feature-text">
                <div class="feature-title">训练指导</div>
                <div class="feature-desc">科学训练方法</div>
              </div>
            </div>
            <div class="feature-item" @click="sendQuickMessage('新手养猫需要准备什么？')">
              <div class="feature-icon">🏠</div>
              <div class="feature-text">
                <div class="feature-title">养护指南</div>
                <div class="feature-desc">新手必看攻略</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 消息列表 -->
      <div class="message-list">
        <div v-for="(msg, index) in messages" :key="index" class="message-item" :class="msg.role === 'user' ? 'user' : 'bot'">
          <div class="message-avatar">
            <div v-if="msg.role === 'user'" class="user-avatar">
              <span>👤</span>
            </div>
            <div v-else class="bot-avatar">
              <span>🐾</span>
            </div>
          </div>
          <div class="message-wrapper">
            <div class="message-name">{{ msg.role === 'user' ? '我' : '宠物小助手' }}</div>
            <div class="message-bubble" v-html="formatMessage(msg.content)"></div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>

        <div v-if="isLoading" class="message-item bot">
          <div class="message-avatar">
            <div class="bot-avatar">
              <span>🐾</span>
            </div>
          </div>
          <div class="message-wrapper">
            <div class="message-name">宠物小助手</div>
            <div class="message-bubble loading-bubble">
              <span class="loading-dot"></span>
              <span class="loading-dot"></span>
              <span class="loading-dot"></span>
              <span class="loading-text">正在思考中...</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷问题 -->
    <div v-if="messages.length > 0" class="quick-questions">
      <div class="quick-questions-scroll">
        <div 
          v-for="(question, index) in quickQuestions" 
          :key="index" 
          class="quick-question-chip"
          @click="sendQuickMessage(question)"
        >
          {{ question }}
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-wrapper">
      <div class="chat-input-container">
        <el-input
          v-model="inputMessage"
          @keyup.enter="sendMessage"
          placeholder="说说你的宠物问题吧～"
          class="input-field"
          :disabled="isLoading"
          maxlength="500"
          show-word-limit
        />
        <el-button 
          type="primary" 
          @click="sendMessage" 
          :disabled="isLoading || !inputMessage.trim()" 
          class="send-btn"
          :class="{ 'active': inputMessage.trim() && !isLoading }"
        >
          <span v-if="!isLoading">发送</span>
          <span v-else class="loading-spinner"></span>
        </el-button>
      </div>
      <div class="input-tip">温馨提示：AI 生成的内容仅供参考，具体情况请咨询专业兽医</div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import api from '../api/index'

const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const messagesContainer = ref(null)
const showSettings = ref(false)

const quickQuestions = [
  '狗狗感冒怎么办？',
  '猫咪不吃猫粮？',
  '怎么给宠物洗澡？',
  '新手养狗准备',
  '猫咪绝育知识',
  '宠物疫苗时间'
]

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const formatMessage = (text) => {
  if (!text) return ''
  return text
    .replace(/\n/g, '<br/>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/🐾(.*?)🐾/g, '<em class="highlight">$1</em>')
    .replace(/^• (.+)$/gm, '<li>$1</li>')
    .replace(/(<li>.*<\/li>)/gs, '<ul class="message-list-items">$1</ul>')
}

const sendMessage = async () => {
  const text = inputMessage.value.trim()
  if (!text || isLoading.value) return

  const userMessage = {
    role: 'user',
    content: text,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }

  messages.value.push(userMessage)
  inputMessage.value = ''
  scrollToBottom()
  isLoading.value = true

  try {
    const messageHistory = messages.value.map(m => ({
      role: m.role,
      content: m.content
    }))

    const response = await api.aiChat(messageHistory)
    
    if (response && response.code === 200 && response.data) {
      const botMessage = {
        role: 'assistant',
        content: response.data,
        time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      }
      messages.value.push(botMessage)
    } else {
      throw new Error(response?.message || '服务异常')
    }
  } catch (error) {
    console.error('AI chat error:', error)
    const errorMessage = {
      role: 'assistant',
      content: '抱歉，小助手暂时遇到点问题，请稍后再试～ 🐾',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }
    messages.value.push(errorMessage)
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

const sendQuickMessage = (message) => {
  inputMessage.value = message
  sendMessage()
}

const clearChat = () => {
  messages.value = []
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 80px);
  background: linear-gradient(180deg, #fff5f0 0%, #ffe4d6 100%);
  position: relative;
  overflow: hidden;
}

/* 顶部导航 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: linear-gradient(135deg, #ff9966 0%, #ff5e62 100%);
  box-shadow: 0 4px 20px rgba(255, 94, 98, 0.2);
  position: relative;
  z-index: 10;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.pet-avatar {
  position: relative;
  width: 56px;
  height: 56px;
}

.avatar-bg {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-icon {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.75rem;
  z-index: 1;
}

.status-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 14px;
  height: 14px;
  background: #4ade80;
  border: 3px solid #fff;
  border-radius: 50%;
  z-index: 2;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

.info .title {
  font-size: 1.25rem;
  font-weight: 700;
  color: white;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.info .subtitle {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.9);
  margin: 0.25rem 0 0;
  display: flex;
  align-items: center;
  gap: 0.4rem;
}

.online-indicator {
  display: inline-block;
  width: 6px;
  height: 6px;
  background: #4ade80;
  border-radius: 50%;
  box-shadow: 0 0 8px #4ade80;
}

.header-actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  width: 40px !important;
  height: 40px !important;
  padding: 0 !important;
  background: rgba(255, 255, 255, 0.2) !important;
  border: none !important;
  border-radius: 50% !important;
  color: white !important;
  font-size: 1.1rem !important;
  transition: all 0.3s !important;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.35) !important;
  transform: scale(1.1);
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
  position: relative;
}

/* 欢迎卡片 */
.welcome-section {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100%;
  padding: 1rem 0;
}

.welcome-card {
  background: white;
  border-radius: 24px;
  padding: 2.5rem 2rem;
  max-width: 600px;
  width: 100%;
  box-shadow: 0 10px 40px rgba(255, 94, 98, 0.1);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.welcome-card::before {
  content: '';
  position: absolute;
  top: -50px;
  right: -50px;
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(255, 153, 102, 0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.welcome-illustration {
  position: relative;
  display: inline-block;
  margin-bottom: 1.5rem;
}

.pet-emoji-large {
  font-size: 4rem;
  animation: bounce 2s ease-in-out infinite;
  display: inline-block;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.floating-paws {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.paw {
  position: absolute;
  font-size: 1.5rem;
  opacity: 0.4;
  animation: float 3s ease-in-out infinite;
}

.paw-1 { top: 0; left: -30px; animation-delay: 0s; }
.paw-2 { top: 50%; right: -40px; animation-delay: 1s; }
.paw-3 { bottom: -10px; left: 50%; animation-delay: 2s; }

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  33% { transform: translate(10px, -10px) rotate(15deg); }
  66% { transform: translate(-5px, 5px) rotate(-10deg); }
}

.welcome-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 0.75rem;
  background: linear-gradient(135deg, #ff5e62, #ff9966);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-desc {
  font-size: 0.9rem;
  color: #718096;
  line-height: 1.6;
  margin: 0 0 2rem;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.75rem;
  text-align: left;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.875rem;
  background: linear-gradient(135deg, #fff5f0, #ffe4d6);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.feature-item:hover {
  transform: translateY(-2px);
  border-color: #ff9966;
  box-shadow: 0 4px 12px rgba(255, 153, 102, 0.2);
}

.feature-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.feature-text {
  flex: 1;
  min-width: 0;
}

.feature-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.125rem;
}

.feature-desc {
  font-size: 0.75rem;
  color: #718096;
}

/* 消息列表 */
.message-list {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  max-width: 900px;
  margin: 0 auto;
}

.message-item {
  display: flex;
  gap: 0.75rem;
  animation: fadeInUp 0.4s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.user-avatar,
.bot-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  background: linear-gradient(135deg, #fbc2eb, #a6c1ee);
}

.bot-avatar {
  background: linear-gradient(135deg, #ff9966, #ff5e62);
}

.message-wrapper {
  max-width: 75%;
  display: flex;
  flex-direction: column;
}

.message-item.user .message-wrapper {
  align-items: flex-end;
}

.message-name {
  font-size: 0.75rem;
  color: #718096;
  margin-bottom: 0.25rem;
  padding: 0 0.5rem;
}

.message-bubble {
  padding: 0.875rem 1.125rem;
  border-radius: 18px;
  font-size: 0.9rem;
  line-height: 1.6;
  word-wrap: break-word;
  position: relative;
}

.message-item.user .message-bubble {
  background: linear-gradient(135deg, #a6c1ee, #fbc2eb);
  color: #2d3748;
  border-bottom-right-radius: 4px;
  box-shadow: 0 2px 8px rgba(166, 193, 238, 0.3);
}

.message-item.bot .message-bubble {
  background: white;
  color: #2d3748;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.message-bubble :deep(.highlight) {
  background: linear-gradient(135deg, #fff5f0, #ffe4d6);
  padding: 0.125rem 0.375rem;
  border-radius: 4px;
  font-style: normal;
  color: #ff5e62;
  font-weight: 500;
}

.message-bubble :deep(.message-list-items) {
  margin: 0.5rem 0;
  padding-left: 1.25rem;
}

.message-bubble :deep(.message-list-items li) {
  margin: 0.25rem 0;
  color: #4a5568;
}

.message-time {
  font-size: 0.7rem;
  color: #a0aec0;
  margin-top: 0.375rem;
  padding: 0 0.5rem;
}

/* 加载动画 */
.loading-bubble {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  min-width: 120px;
}

.loading-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9966, #ff5e62);
  animation: loading 1.4s infinite ease-in-out;
}

.loading-dot:nth-child(1) { animation-delay: 0s; }
.loading-dot:nth-child(2) { animation-delay: 0.2s; }
.loading-dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes loading {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.5; }
  30% { transform: translateY(-6px); opacity: 1; }
}

.loading-text {
  margin-left: 0.5rem;
  font-size: 0.8rem;
  color: #718096;
}

/* 快捷问题 */
.quick-questions {
  padding: 0 1.5rem 0.75rem;
  background: linear-gradient(180deg, transparent, #fff5f0);
}

.quick-questions-scroll {
  display: flex;
  gap: 0.5rem;
  overflow-x: auto;
  padding: 0.5rem 0;
  scrollbar-width: none;
}

.quick-questions-scroll::-webkit-scrollbar {
  display: none;
}

.quick-question-chip {
  flex-shrink: 0;
  padding: 0.5rem 1rem;
  background: white;
  border: 2px solid #ffe4d6;
  border-radius: 20px;
  font-size: 0.8rem;
  color: #ff5e62;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.quick-question-chip:hover {
  background: linear-gradient(135deg, #ff9966, #ff5e62);
  color: white;
  border-color: transparent;
  transform: translateY(-1px);
}

/* 输入区域 */
.chat-input-wrapper {
  background: white;
  padding: 1rem 1.5rem 1.25rem;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 5;
}

.chat-input-container {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.input-field {
  flex: 1;
}

.input-field :deep(.el-input__wrapper) {
  background: #f7fafc;
  border-radius: 24px;
  padding: 0.5rem 1rem;
  box-shadow: none !important;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.input-field :deep(.el-input__wrapper:hover),
.input-field :deep(.el-input__wrapper.is-focus) {
  background: white;
  border-color: #ff9966;
}

.input-field :deep(.el-input__inner) {
  font-size: 0.9rem;
}

.send-btn {
  width: 48px !important;
  height: 48px !important;
  padding: 0 !important;
  border-radius: 50% !important;
  background: linear-gradient(135deg, #ff9966, #ff5e62) !important;
  border: none !important;
  color: white !important;
  font-size: 0.85rem !important;
  font-weight: 600 !important;
  opacity: 0.5;
  transition: all 0.3s !important;
}

.send-btn.active {
  opacity: 1;
  box-shadow: 0 4px 12px rgba(255, 94, 98, 0.3);
}

.send-btn.active:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(255, 94, 98, 0.4);
}

.send-btn:disabled {
  cursor: not-allowed;
}

.input-tip {
  text-align: center;
  font-size: 0.7rem;
  color: #a0aec0;
  margin-top: 0.5rem;
}

.loading-spinner {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(255, 153, 102, 0.3);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 153, 102, 0.5);
}

/* 响应式 */
@media (max-width: 768px) {
  .chat-header {
    padding: 0.75rem 1rem;
  }
  
  .info .title {
    font-size: 1.1rem;
  }
  
  .chat-messages {
    padding: 1rem;
  }
  
  .message-wrapper {
    max-width: 85%;
  }
  
  .welcome-card {
    padding: 2rem 1.5rem;
  }
  
  .feature-grid {
    grid-template-columns: 1fr;
  }
  
  .chat-input-wrapper {
    padding: 0.75rem 1rem 1rem;
  }
}
</style>