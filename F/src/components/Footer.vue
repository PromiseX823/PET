<template>
  <footer class="footer">
    <div class="footer-background">
      <div class="gradient-overlay"></div>
      <div class="footer-shapes">
        <div class="footer-shape shape-1"></div>
        <div class="footer-shape shape-2"></div>
      </div>
    </div>
    <div class="footer-container">
      <div class="footer-content">
        <div class="footer-section footer-about">
          <div class="footer-logo">
            <div class="logo-icon">
              <el-icon><HomeFilled /></el-icon>
            </div>
            <h3 class="logo-text">宠物领养中心</h3>
          </div>
          <p class="footer-description">
            致力于为每一只流浪宠物找到温暖的家，让爱与责任相伴一生
          </p>
          <div class="social-links">
            <a href="#" class="social-link">
              <el-icon><Share /></el-icon>
            </a>
            <a href="#" class="social-link">
              <el-icon><Camera /></el-icon>
            </a>
            <a href="#" class="social-link">
              <el-icon><Phone /></el-icon>
            </a>
            <a href="#" class="social-link">
              <el-icon><Message /></el-icon>
            </a>
          </div>
        </div>

        <div class="footer-section">
          <h4 class="footer-title">
            <el-icon><InfoFilled /></el-icon> 关于我们
          </h4>
          <ul class="footer-links">
            <li><a href="#about" class="footer-link">平台简介</a></li>
            <li><a href="#team" class="footer-link">团队成员</a></li>
            <li><a href="#partner" class="footer-link">合作伙伴</a></li>
            <li><a href="#join" class="footer-link">加入我们</a></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4 class="footer-title">
            <el-icon><QuestionFilled /></el-icon> 帮助中心
          </h4>
          <ul class="footer-links">
            <li><a href="#faq" class="footer-link">常见问题</a></li>
            <li><a href="#guide" class="footer-link">领养指南</a></li>
            <li><a href="#contact" class="footer-link">联系我们</a></li>
            <li><a href="#feedback" class="footer-link">意见反馈</a></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4 class="footer-title">
            <el-icon><Star /></el-icon> 关注我们
          </h4>
          <ul class="footer-links">
            <li><a href="#wechat" class="footer-link">微信公众号</a></li>
            <li><a href="#weibo" class="footer-link">官方微博</a></li>
            <li><a href="#app" class="footer-link">手机客户端</a></li>
            <li><a href="#newsletter" class="footer-link">订阅更新</a></li>
          </ul>
        </div>
      </div>

      <div class="footer-bottom">
        <div class="bottom-content">
          <div class="copyright">
            © 2024 宠物领养中心. 保留所有权利
          </div>
          <div class="footer-policies">
            <a href="#terms" class="policy-link">服务条款</a>
            <a href="#privacy" class="policy-link">隐私政策</a>
            <a href="#cookies" class="policy-link">Cookie 政策</a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { HomeFilled, Share, Camera, Phone, Message, InfoFilled, QuestionFilled, Star } from '@element-plus/icons-vue'
</script>

<style scoped>
.footer {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 0 30px;
  position: relative;
  overflow: hidden;
}

.footer-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.gradient-overlay {
  background: linear-gradient(135deg, 
    rgba(102, 126, 234, 0.9) 0%, 
    rgba(118, 75, 162, 0.95) 50%, 
    rgba(240, 147, 251, 0.8) 100%);
}

.footer-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
}

.footer-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  animation: float 25s ease-in-out infinite;
}

.footer-shape.shape-1 {
  width: 400px;
  height: 400px;
  background: #ff6b6b;
  top: -10%;
  left: -10%;
  animation-delay: 0s;
}

.footer-shape.shape-2 {
  width: 300px;
  height: 300px;
  background: #4ecdc4;
  bottom: -5%;
  right: -5%;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(50px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-30px, 30px) scale(0.9);
  }
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 50px;
  margin-bottom: 50px;
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.footer-about {
  max-width: 350px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.logo-icon {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  backdrop-filter: blur(10px);
}

.logo-text {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #fff, #f0f0f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-description {
  font-size: 14px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 25px;
}

.social-links {
  display: flex;
  gap: 15px;
}

.social-link {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-decoration: none;
  font-size: 16px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.social-link:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-3px) scale(1.1);
}

.footer-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.footer-links {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-link {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
}

.footer-link:hover {
  color: white;
  transform: translateX(5px);
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.15);
  padding-top: 30px;
}

.bottom-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.copyright {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.footer-policies {
  display: flex;
  gap: 25px;
}

.policy-link {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.policy-link:hover {
  color: white;
}

@media (max-width: 768px) {
  .footer {
    padding: 40px 0 20px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 30px;
    margin-bottom: 30px;
  }

  .footer-about {
    max-width: 100%;
  }

  .bottom-content {
    flex-direction: column;
    text-align: center;
  }

  .footer-policies {
    gap: 15px;
  }
}
</style>