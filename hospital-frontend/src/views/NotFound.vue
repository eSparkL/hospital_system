<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1 class="code">404</h1>
      <p class="message">抱歉，您访问的页面不存在！</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
      <el-button type="default" @click="$router.back()">返回上一页</el-button>
    </div>
    <div class="illustration">
      <!-- 可以加一个可爱的小动画或者图片 -->
      <img src="@/assets/404.png" alt="404" />
    </div>
  </div>
</template>

<script>
export default {
  name: "NotFound"
};
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  flex-direction: column;
  text-align: center;
}

.not-found-content {
  margin-bottom: 20px;
}

.code {
  font-size: 96px;
  font-weight: bold;
  color: #409EFF; /* Element Plus 主色 */
  animation: fadeIn 1s ease-in-out;
}

.message {
  font-size: 18px;
  margin: 10px 0 20px;
}

.illustration img {
  width: 200px;
  opacity: 0.7;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

@keyframes fadeIn {
  0% { opacity: 0; transform: scale(0.5); }
  100% { opacity: 1; transform: scale(1); }
}
</style>
