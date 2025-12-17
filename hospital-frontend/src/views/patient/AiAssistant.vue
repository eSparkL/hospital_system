<template>
  <div class="ai-assistant-container">
    <div 
      class="ai-assistant-float" 
      v-show="visible"
      :style="{ 
        bottom: floatingPosition.y + 'px', 
        right: floatingPosition.x + 'px',
        transform: isDragging ? 'scale(1.02)' : 'scale(1)'
      }"
    >
      <div class="ai-header">
        <span>智能导诊助手</span>
        <i class="el-icon-close" @click="close"></i>
      </div>
      <div class="ai-content">
        <div class="chat-history" ref="chatHistory">
          <div v-for="(message, index) in messages" :key="index" :class="['message', message.type]">
            <div class="message-text">{{ message.content }}</div>
            <!-- 查看排班按钮 -->
            <div v-if="message.showScheduleButton" class="schedule-button">
              <el-button size="mini" type="primary" @click="handleViewSchedule">查看今日排班</el-button>
            </div>
            <!-- 挂号确认信息 -->
            <div v-if="message.showRegistrationConfirm" class="registration-confirm">
              <!-- 时间选择界面 -->
              <div v-if="!message.timeSelected">
                <p>请选择挂号时间：</p>
                <div class="time-buttons">
                  <el-button 
                    v-for="timeSlot in message.availableTimes" 
                    :key="timeSlot"
                    size="mini" 
                    type="primary" 
                    plain
                    @click="selectTime(message, timeSlot)"
                    style="margin: 5px;"
                  >
                    {{ timeSlot }}
                  </el-button>
                </div>
              </div>
              <!-- 已选择时间，显示挂号按钮 -->
              <div v-else>
                <p>已选择时间：{{ message.selectedTime }}</p>
                <el-button size="mini" type="primary" @click="confirmRegistration(message.registrationData)">确认挂号</el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <el-input
            ref="inputRef"
            v-model="inputMessage"
            placeholder="请输入您的问题..."
            @keyup.enter.native="sendMessage"
            :disabled="loading"
          ></el-input>
          <el-button type="primary" @click="sendMessage" :loading="loading">发送</el-button>
        </div>
      </div>
      <!-- 将拖动处理移到一个专门的拖动手柄区域，但排除关闭按钮 -->
      <div class="drag-handle" @mousedown="startDrag"></div>
    </div>
    
    <!-- 圆形悬浮按钮 -->
    <div 
      class="ai-toggle-circle" 
      v-show="!visible"
      @click="toggle"
      :style="{ 
        bottom: toggleButtonPosition.y + 'px', 
        right: toggleButtonPosition.x + 'px',
        cursor: isToggleDragging ? 'grabbing' : 'grab'
      }"
      @mousedown="startDragToggle"
    >
      <i class="el-icon-message"></i>
    </div>


  </div>
</template>

<script>
import { getToken } from "@/utils/storage";
import request from "@/utils/request";
import jwtDecode from "jwt-decode";

export default {
  name: "AiAssistant",
  data() {
    return {
      visible: true,
      loading: false,
      inputMessage: "",
      messages: [
        {
          type: "received",
          content: "您好！我是智能导诊助手，请问有什么我可以帮您的吗？",
          showScheduleButton: true
        }
      ],
      abortController: null,
      sessionId: null,
      // 时间段设置，与OrderOperate.vue保持一致
      timeSlots: [
        "08:30-09:30",
        "09:30-10:30", 
        "10:30-11:30",
        "14:30-15:30", 
        "15:30-16:30", 
        "16:30-17:30"
      ],
      // 拖动相关
      isDragging: false,
      isToggleDragging: false,
      dragStartX: 0,
      dragStartY: 0,
      startX: 0,
      startY: 0,
      floatingPosition: { x: 20, y: 0 }, // 默认位置
      toggleButtonPosition: { x: 20, y: 20 }, // 收起按钮位置
      dragElement: null,
      dragThreshold: 5, // 拖动阈值，单位像素
      // 挂号相关
      // 当前用户的排班信息缓存
      currentSchedule: null,
      // 存储最近一次获取的排班信息
      lastScheduleInfo: "",
      // 当前时间是否已过某个时间点
      isTimeAfterTarget(targetTime) {
        const currentTime = new Date();
        const [targetHour, targetMinute] = targetTime.split(':');
        const targetDateTime = new Date();
        targetDateTime.setHours(parseInt(targetHour), parseInt(targetMinute), 0, 0);
        return currentTime > targetDateTime;
      }
    };
  },
  methods: {
    toggle() {
      // 只有在非拖动状态下才响应点击事件
      if (!this.isDragging && !this.isToggleDragging) {
        this.visible = !this.visible;
      }
    },
    close() {
      // 关闭时将按钮位置设置为窗口中心位置
      // 窗口宽度400px，按钮宽度60px，所以中心位置是向右偏移170px (400-60)/2
      // 窗口高度500px，按钮高度60px，所以中心位置是向上偏移220px (500-60)/2
      this.toggleButtonPosition.x = this.floatingPosition.x + 170;
      this.toggleButtonPosition.y = this.floatingPosition.y + 220;
      this.visible = false;
    },
    
    // 选择时间
    selectTime(message, timeSlot) {
      message.timeSelected = true;
      message.selectedTime = timeSlot;
      
      // 更新注册数据中的开始时间
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      const day = String(today.getDate()).padStart(2, '0');
      const dateString = `${year}-${month}-${day}`;
      
      message.registrationData.oStart = `${dateString} ${timeSlot}`;
      
      // 刷新界面
      this.$forceUpdate();
    },

    async sendMessage() {
      if (!this.inputMessage.trim() || this.loading) return;
      
      // 添加用户消息到聊天记录
      this.messages.push({
        type: "sent",
        content: this.inputMessage
      });
      
      // 特殊指令处理：查看排班
      if (this.inputMessage.trim() === "查看排班") {
        this.handleViewSchedule();
        return;
      }
      
      // 检查是否是挂号请求 - 支持多种表达方式
      const registerPatterns = [
        /我想挂(.+)医生的号/i,
        /我要挂(.+)医生的号/i,
        /帮我挂(.+)医生的号/i,
        /预约(.+)医生/i,
        /挂号(.+)医生/i,
        /挂(.+)医生的号/i,
        /我要预约(.+)医生/i,
        /我想预约(.+)医生/i
      ];
      
      let doctorName = null;
      for (const pattern of registerPatterns) {
        const match = this.inputMessage.trim().match(pattern);
        if (match) {
          doctorName = match[1];
          break;
        }
      }
      
      if (doctorName && this.currentSchedule) {
        // 查找匹配的医生
        const matchedDoctor = this.currentSchedule.find(doctor => 
          doctor.dName.includes(doctorName) || doctorName.includes(doctor.dName)
        );
        
        if (matchedDoctor) {
          this.handleDoctorRegistration(matchedDoctor);
          this.inputMessage = "";
          return;
        }
      }
      
      // 保存用户输入并清空输入框
      const prompt = this.inputMessage;
      this.inputMessage = "";
      
      // 显示加载状态
      this.loading = true;
      
      // 滚动到底部
      this.$nextTick(() => {
        this.scrollToBottom();
      });
      
      // 如果还没有会话ID，先创建会话
      if (!this.sessionId) {
        try {
          await this.createSession();
        } catch (error) {
          this.loading = false;
          // 添加错误消息到聊天记录
          this.messages.push({
            type: "received",
            content: "创建会话失败: " + error.message
          });
          return;
        }
      }
      
      // 调用带上下文的流式API，如果存在排班信息则将其与用户输入一起发送
      if (this.lastScheduleInfo) {
        this.callStreamApiWithContext(`${this.lastScheduleInfo}\n\n基于以上排班信息，请根据患者症状推荐合适的科室和医生。如果您满意推荐结果，可以告诉我进行挂号，或者补充更多症状信息:\n${prompt}`);
      } else {
        this.callStreamApiWithContext(`请根据患者症状推荐合适的科室和医生。如果您满意推荐结果，可以告诉我您想要挂号的医生姓名，或者补充更多症状信息：\n${prompt}`);
      }
    },
    
    // 处理医生挂号请求
    handleDoctorRegistration(doctor) {
      // 构造挂号确认信息
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      const day = String(today.getDate()).padStart(2, '0');
      const dateString = `${year}-${month}-${day}`;
      
      // 过滤出当前时间之后的时间段
      const availableTimes = this.timeSlots.filter(time => {
        const timePart = time.split('-')[0].trim(); // 获取开始时间，如 "08:30"
        return !this.isTimeAfterTarget(timePart);
      });
      
      // 如果没有可用时间段，显示提示
      if (availableTimes.length === 0) {
        this.messages.push({
          type: "received",
          content: "抱歉，今天已无可用挂号时间段，请选择明天就诊。"
        });
        return;
      }
      
      const registrationInfo = `我根据您的情况，为您推荐：
医生工号：${doctor.dId}
医生姓名：${doctor.dName}
科室：${doctor.dSection}
职位：${doctor.dPost}
排班ID：${doctor.arId}
`;
      
      // 添加挂号确认消息到聊天记录
      this.messages.push({
        type: "received",
        content: registrationInfo,
        showRegistrationConfirm: true,
        timeSelected: false,
        selectedTime: '',
        availableTimes: availableTimes,
        registrationData: {
          dId: doctor.dId,
          arId: doctor.arId,
          oStart: ''
        }
      });
      
      this.$nextTick(() => {
        this.scrollToBottom();
      });
    },
    
    // 处理查看排班指令
    async handleViewSchedule() {
      // 如果是通过按钮点击触发，清除输入框
      this.inputMessage = "";
      this.loading = true;
      
      try {
        // 获取今天的日期
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        const currentDate = `${year}-${month}-${day}`;
        
        // 调用API获取所有科室的排班信息
        const departments = [
          "简易门诊（慢病）",
          "精神科门诊",
          "呼吸（心血管、神经）内科门诊",
          "消化内科门诊",
          "肝胆、肠胃外科",
          "肛肠科",
          "甲状腺、乳腺外科",
          "胸部外科",
          "骨科门诊",
          "手足创伤外科门诊",
          "妇产科门诊",
          "儿科门诊",
          "眼科、耳鼻喉科门诊",
          "口腔科门诊",
          "皮肤科门诊"
        ];
        
        let scheduleInfo = "今日排班信息：\n\n";
        let hasSchedule = false;
        let scheduleData = []; // 缓存排班数据供后续使用
        
        // 遍历所有科室获取排班信息
        for (const dept of departments) {
          try {
            const response = await request.get("/arrange/findByTime", {
              params: {
                arTime: currentDate,
                dSection: dept
              }
            });
            
            if (response.data.data && response.data.data.length > 0) {
              hasSchedule = true;
              scheduleInfo += `${dept}：\n`;
              response.data.data.forEach(item => {
                if (item.doctor) {
                  scheduleInfo += `  ${item.doctor.dName} (${item.doctor.dPost})\n`;
                  // 缓存医生信息
                  scheduleData.push({
                    dId: item.doctor.dId,
                    dName: item.doctor.dName,
                    dPost: item.doctor.dPost,
                    dSection: dept,
                    arId: item.arId
                  });
                }
              });
              scheduleInfo += "\n";
            }
          } catch (err) {
            console.error(`获取${dept}排班信息失败:`, err);
          }
        }
        
        if (!hasSchedule) {
          scheduleInfo += "今日暂无排班信息。";
        }
        
        // 缓存排班数据和信息文本
        this.currentSchedule = scheduleData;
        this.lastScheduleInfo = scheduleInfo;
        
        this.messages.push({
          type: "received",
          content: scheduleInfo + "\n☺️您可以描述症状，我将为您推荐合适的医生",
          showScheduleButton: false // 隐藏查看排班按钮
        });
      } catch (error) {
        console.error("获取排班信息失败:", error);
        this.messages.push({
          type: "received",
          content: "获取排班信息失败，请稍后重试。"
        });
      } finally {
        this.loading = false;
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      }
    },
    // 确认挂号
    async confirmRegistration(registrationData) {
      this.loading = true;
      
      try {
        // 验证是否已选择时间
        if (!registrationData.oStart) {
          this.$notify({
            title: '提示',
            message: '请先选择挂号时间',
            type: 'warning',
            duration: 3000
          });
          return;
        }
        
        // 解码token获取用户信息
        const token = getToken();
        const userData = jwtDecode(token);
        
        // 构造挂号参数
        const params = {
          pId: userData.pId,
          dId: registrationData.dId,
          oStart: registrationData.oStart,
          arId: registrationData.arId
        };
        
        // 调用挂号API
        const response = await request.get("patient/addOrder", { params });
        
        if (response.data.status === 200) {
          // 挂号成功
          this.$notify({
            title: '挂号成功',
            message: '挂号成功！',
            type: 'success',
            duration: 3000
          });
          
          // 在聊天记录中添加成功消息
          this.messages.push({
            type: "received",
            content: "挂号成功，我将继续为您服务"
          });
        } else {
          // 挂号失败
          this.$notify({
            title: '挂号失败',
            message: '挂号失败：' + (response.data.msg || "未知错误"),
            type: 'error',
            duration: 3000
          });
          
          // 在聊天记录中添加失败消息
          this.messages.push({
            type: "received",
            content: "挂号失败，请重试或联系管理员"
          });
        }
      } catch (error) {
        console.error("挂号请求失败:", error);
        this.$notify({
          title: '挂号失败',
          message: '挂号请求失败，请稍后重试',
          type: 'error',
          duration: 3000
        });
        
        this.messages.push({
          type: "received",
          content: "挂号请求失败，请重试或联系管理员"
        });
      } finally {
        this.loading = false;
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      }
    },
    // 创建新的会话
    async createSession() {
      try {
        const token = getToken();
        if (!token) {
          throw new Error("未找到身份验证令牌");
        }
        
        // 使用相对路径，通过Vue代理转发请求
        const url = `/llm/session/create`;
        
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'token': token,
            'Content-Type': 'application/json'
          }
        });
        
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const result = await response.json();
        if (result.code === 200) {
          this.sessionId = result.data;
        } else {
          // 检查msg字段是否包含会话ID
          if (result.msg && result.msg.startsWith('session_')) {
            // 如果msg字段包含会话ID，则使用它
            this.sessionId = result.msg;
          } else {
            // 否则抛出错误
            throw new Error(result.msg || "创建会话失败");
          }
        }
      } catch (error) {
        console.error("创建会话失败:", error);
        throw error;
      }
    },
    async callStreamApiWithContext(prompt) {
      // 添加初始的AI回复消息
      const aiMessageIndex = this.messages.length;
      this.messages.push({
        type: "received",
        content: ""
      });
      
      // 检查token
      const token = getToken();
      
      if (!token) {
        this.loading = false;
        this.messages[aiMessageIndex].content = "身份验证失败，请重新登录后再试。";
        return;
      }
      
      // 确保有会话ID
      if (!this.sessionId) {
        this.loading = false;
        this.messages[aiMessageIndex].content = "会话创建失败，请刷新页面重试。";
        return;
      }
      
      // 取消之前的请求（如果有的话）
      if (this.abortController) {
        this.abortController.abort();
      }
      
      // 创建新的 AbortController
      this.abortController = new AbortController();
      
      try {
        // 构建带上下文的流式请求URL，使用相对路径
        // 如果是特殊的挂号确认消息，直接处理挂号逻辑
        if (prompt.startsWith && prompt.startsWith('挂号确认:')) {
          // 这是一个内部生成的挂号确认消息，直接处理挂号
          const registrationData = JSON.parse(prompt.substring('挂号确认:'.length));
          await this.confirmRegistration(registrationData);
          return;
        }
        
        const url = `/llm/ask/context/stream?sessionId=${encodeURIComponent(this.sessionId)}&prompt=${encodeURIComponent(prompt)}`;
        
        // 使用 fetch 发起请求，只包含必要的headers
        const response = await fetch(url, {
          method: 'GET',
          headers: {
            'token': token
          },
          signal: this.abortController.signal
        });
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        if (!response.body) throw new Error("No stream body");
        
        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = ""; // 累积缓冲区
        
        // 打字机效果相关变量
        let fullResponse = ""; // 存储完整响应
        let currentIndex = 0;  // 当前显示的字符索引
        
        // 打字机效果函数
        const typeNextChar = () => {
          if (currentIndex < fullResponse.length) {
            this.messages[aiMessageIndex].content = fullResponse.substring(0, currentIndex + 1);
            currentIndex++;
            this.$nextTick(() => {
              this.scrollToBottom();
            });
            // 继续打字机效果
            setTimeout(typeNextChar, 50); // 50ms间隔，更明显的打字机效果
          } else {
            // 打字机效果完成后，将焦点设置回输入框
            this.$nextTick(() => {
              if (this.$refs.inputRef && this.$refs.inputRef.focus) {
                this.$refs.inputRef.focus();
              }
            });
          }
        };
        
        // eslint-disable-next-line no-constant-condition
        while (true) {
          const { done, value } = await reader.read();
          if (done) break;
          
          // 累积解码数据并保留未完成字节
          buffer += decoder.decode(value, { stream: true });
          
          // 处理 SSE 格式数据
          const lines = buffer.split("\n");
          buffer = lines.pop() || ""; // 保留未完成部分
          
          for (const line of lines) {
            if (line.startsWith("data:")) {
              const data = line.substring(5).trim(); // 移除 'data:' 前缀
              
              if (data === "[DONE]") {
                // 收到结束信号，启动打字机效果显示剩余内容
                if (currentIndex < fullResponse.length) {
                  typeNextChar();
                }
                this.loading = false;
              } else if (data) {
                // 累积完整响应
                fullResponse += data;
                
                // 立即开始打字机效果
                if (currentIndex === 0) {
                  typeNextChar();
                }
                
                // 检查是否包含挂号推荐关键词
                if (fullResponse.includes("为您推荐") && fullResponse.includes("确定挂号")) {
                  // 解析挂号信息
                  const registrationData = this.parseRegistrationInfo(fullResponse);
                  if (registrationData) {
                    // 更新消息以显示挂号确认按钮
                    this.$set(this.messages[aiMessageIndex], 'showRegistrationConfirm', true);
                    this.$set(this.messages[aiMessageIndex], 'registrationData', registrationData);
                  }
                }
                
                // 如果检测到AI提供了明确的科室和医生推荐，也可以添加挂号按钮
                if (fullResponse.includes("医生工号") && fullResponse.includes("排班ID")) {
                  const registrationData = this.parseRegistrationInfo(fullResponse);
                  if (registrationData) {
                    this.$set(this.messages[aiMessageIndex], 'showRegistrationConfirm', true);
                    this.$set(this.messages[aiMessageIndex], 'registrationData', registrationData);
                  }
                }
              }
            }
          }
        }
        
        // 处理最后剩余数据（如果有）
        if (buffer) {
          console.warn("未完成数据:", buffer);
        }
        
        // 确保所有内容都被显示
        if (currentIndex < fullResponse.length) {
          typeNextChar();
        }
        this.loading = false;
        // 回答完成后，将焦点设置回输入框
        this.$nextTick(() => {
          if (this.$refs.inputRef && this.$refs.inputRef.focus) {
            this.$refs.inputRef.focus();
          }
        });
      } catch (error) {
        if (error.name === 'AbortError') {
          console.log('请求已取消');
        } else {
          console.error('请求出错:', error);
          this.loading = false;
          
          // 显示错误消息
          if (this.messages[aiMessageIndex].content === "") {
            this.messages[aiMessageIndex].content = "抱歉，我在处理您的问题时遇到了一些困难，请稍后再试。";
          }
        }
      }
    },
    // 解析挂号信息
    parseRegistrationInfo(content) {
      // 解析AI回复中的挂号信息
      // 查找类似"医生工号：1001"和"挂号时间：2023-12-15 09:30-10:30"的模式
      const dIdMatch = content.match(/医生工号[：:]\s*(\d+)/);
      const arIdMatch = content.match(/排班ID[：:]\s*(\d+)/);
      const timeMatch = content.match(/挂号时间[：:]\s*([\d\-:\s]+[\d:])/);
      
      if (dIdMatch && arIdMatch && timeMatch) {
        return {
          dId: parseInt(dIdMatch[1]),
          arId: parseInt(arIdMatch[1]),
          oStart: timeMatch[1].trim()
        };
      }
      
      // 如果没有找到明确的信息，使用原来的简单逻辑
      if (this.currentSchedule && this.currentSchedule.length > 0) {
        // 简单示例：选择第一个医生作为默认推荐
        const doctor = this.currentSchedule[0];
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        const dateString = `${year}-${month}-${day}`;
        
        return {
          dId: doctor.dId,
          arId: doctor.arId,
          oStart: `${dateString} 09:00:00` // 示例时间
        };
      }
      
      return null;
    },
    scrollToBottom() {
      const chatHistory = this.$refs.chatHistory;
      if (chatHistory) {
        chatHistory.scrollTop = chatHistory.scrollHeight;
      }
    },
    // 拖动功能相关方法
    startDrag(event) {
      // 阻止在关闭按钮上触发拖动
      if (event.target.classList.contains('el-icon-close')) return;
      
      this.isDragging = false; // 初始时不进入拖动状态
      this.dragElement = 'floating';
      this.startX = event.clientX;
      this.startY = event.clientY;
      this.dragStartX = event.clientX + this.floatingPosition.x;
      this.dragStartY = event.clientY + this.floatingPosition.y;
      
      document.addEventListener('mousemove', this.onDrag);
      document.addEventListener('mouseup', this.stopDrag);
      event.preventDefault();
    },
    startDragToggle(event) {
      this.isToggleDragging = false; // 初始时不进入拖动状态
      this.dragElement = 'toggle';
      this.startX = event.clientX;
      this.startY = event.clientY;
      this.dragStartX = event.clientX + this.toggleButtonPosition.x;
      this.dragStartY = event.clientY + this.toggleButtonPosition.y;
      
      document.addEventListener('mousemove', this.onDrag);
      document.addEventListener('mouseup', this.stopDrag);
      event.preventDefault();
    },
    onDrag(event) {
      const deltaX = Math.abs(event.clientX - this.startX);
      const deltaY = Math.abs(event.clientY - this.startY);
      
      // 只有当移动距离超过阈值时才进入拖动状态
      if (!this.isDragging && !this.isToggleDragging && 
          (deltaX > this.dragThreshold || deltaY > this.dragThreshold)) {
        if (this.dragElement === 'floating') {
          this.isDragging = true;
        } else if (this.dragElement === 'toggle') {
          this.isToggleDragging = true;
        }
      }
      
      // 如果处于拖动状态，则更新位置
      if (this.isDragging || this.isToggleDragging) {
        const newX = this.dragStartX - event.clientX;
        const newY = this.dragStartY - event.clientY;
        
        // 边界检测
        const maxX = window.innerWidth - (this.dragElement === 'floating' ? 400 : 60);
        const maxY = window.innerHeight - (this.dragElement === 'floating' ? 500 : 60);
        
        const boundedX = Math.max(0, Math.min(newX, maxX));
        const boundedY = Math.max(0, Math.min(newY, maxY));
        
        if (this.dragElement === 'floating') {
          this.floatingPosition.x = boundedX;
          this.floatingPosition.y = boundedY;
        } else if (this.dragElement === 'toggle') {
          this.toggleButtonPosition.x = boundedX;
          this.toggleButtonPosition.y = boundedY;
        }
      }
    },
    stopDrag() {
      // 延迟重置拖动状态，避免mouseup触发click事件
      setTimeout(() => {
        this.isDragging = false;
        this.isToggleDragging = false;
        this.dragElement = null;
      }, 100);
      
      document.removeEventListener('mousemove', this.onDrag);
      document.removeEventListener('mouseup', this.stopDrag);
    }
  },
  beforeDestroy() {
    // 组件销毁前取消未完成的请求
    if (this.abortController) {
      this.abortController.abort();
    }
    
    // 清理事件监听器
    document.removeEventListener('mousemove', this.onDrag);
    document.removeEventListener('mouseup', this.stopDrag);
  }
};
</script>

<style scoped lang="scss">
.ai-assistant-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1000;
}

// 添加时间选择按钮样式
.time-buttons {
  margin: 10px 0;
  
  .el-button {
    margin: 5px;
  }
}

.ai-assistant-float {
  position: absolute;
  width: 400px;
  height: 500px;
  background: white;
  border: 1px solid #dcdfe6;
  border-radius: 8px 8px 0 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease;
  pointer-events: auto;
  
  .ai-header {
    height: 40px;
    background: #409eff;
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 15px;
    border-radius: 8px 8px 0 0;
    
    .el-icon-close {
      cursor: pointer;
      font-size: 18px;
      z-index: 2; /* 确保关闭按钮在拖动手柄之上 */
    }
  }
  
  .ai-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    
    .chat-history {
      flex: 1;
      padding: 15px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      
      .message {
        max-width: 80%;
        padding: 10px;
        margin-bottom: 10px;
        border-radius: 4px;
        
        &.sent {
          align-self: flex-end;
          background-color: #409eff;
          color: white;
        }
        
        &.received {
          align-self: flex-start;
          background-color: #f5f5f5;
          color: #333;
        }
        
        .message-text {
          word-wrap: break-word;
          word-break: break-all;
          white-space: pre-wrap; /* 保持换行符 */
        }
        
        .schedule-button,
        .registration-confirm {
          margin-top: 10px;
        }
      }
    }
    
    .chat-input {
      display: flex;
      padding: 10px;
      border-top: 1px solid #eee;
      
      .el-input {
        flex: 1;
        margin-right: 10px;
      }
    }
  }

  // 拖动手柄，覆盖在标题栏上但不影响关闭按钮
  .drag-handle {
    position: absolute;
    top: 0;
    left: 0;
    width: calc(100% - 40px); /* 留出关闭按钮的空间 */
    height: 40px;
    cursor: grab;
    z-index: 1;
    
    &:active {
      cursor: grabbing;
    }
  }
}

.ai-toggle-circle {
  position: absolute;
  width: 60px;
  height: 60px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  pointer-events: auto;
  font-size: 24px;
  transition: transform 0.2s ease, background 0.2s ease;
  
  &:hover {
    background: #66b1ff;
    transform: scale(1.05);
  }
  
  &:active {
    cursor: grabbing;
  }
}
</style>