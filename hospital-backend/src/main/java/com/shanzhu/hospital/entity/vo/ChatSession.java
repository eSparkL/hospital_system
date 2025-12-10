package com.shanzhu.hospital.entity.vo;

import com.shanzhu.hospital.entity.po.Patient;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatSession {
    private static final Logger logger = LoggerFactory.getLogger(ChatSession.class);
    
    private String sessionId;
    private Patient patient;
    private List<ChatMessage> messages;
    private Long createdAt;
    private Long lastActiveAt;
    
    public ChatSession() {
        this.messages = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.lastActiveAt = System.currentTimeMillis();
    }
    
    public ChatSession(String sessionId, Patient patient) {
        this.sessionId = sessionId;
        this.patient = patient;
        this.messages = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.lastActiveAt = System.currentTimeMillis();
        
        // 添加初始欢迎语
        addSystemMessage("您好！我是智能导诊助手，请描述您的症状，我将为您推荐科室~");
        logger.debug("创建新的聊天会话，会话ID: {}", sessionId);
    }
    
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        this.lastActiveAt = System.currentTimeMillis();
        logger.debug("向会话 {} 添加消息，当前消息总数: {}", sessionId, messages.size());
    }
    
    public void addSystemMessage(String content) {
        this.messages.add(new ChatMessage("system", content));
        this.lastActiveAt = System.currentTimeMillis();
        logger.debug("向会话 {} 添加系统消息: {}", sessionId, content);
    }
    
    public void addUserMessage(String content) {
        this.messages.add(new ChatMessage("user", content));
        this.lastActiveAt = System.currentTimeMillis();
        logger.debug("向会话 {} 添加用户消息: {}", sessionId, content);
    }
    
    public void addAssistantMessage(String content) {
        this.messages.add(new ChatMessage("assistant", content));
        this.lastActiveAt = System.currentTimeMillis();
        logger.debug("向会话 {} 添加助手消息: {}", sessionId, content);
    }
    
    public List<ChatMessage> getRecentMessages(int limit) {
        int size = this.messages.size();
        int fromIndex = Math.max(0, size - limit);
        List<ChatMessage> recentMessages = new ArrayList<>(this.messages.subList(fromIndex, size));
        logger.debug("获取会话 {} 的近期消息，限制数量: {}, 实际返回数量: {}", sessionId, limit, recentMessages.size());
        return recentMessages;
    }
}