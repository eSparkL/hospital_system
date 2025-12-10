package com.shanzhu.hospital.utils;

import com.shanzhu.hospital.entity.po.Patient;
import com.shanzhu.hospital.entity.vo.ChatSession;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionManager {
    private static final Logger logger = LoggerFactory.getLogger(ChatSessionManager.class);
    private static final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();
    
    public static ChatSession createSession(String sessionId, Patient patient) {
        logger.info("创建新的聊天会话，会话ID: {}", sessionId);
        ChatSession session = new ChatSession(sessionId, patient);
        sessions.put(sessionId, session);
        logger.debug("聊天会话创建完成，当前会话总数: {}", sessions.size());
        return session;
    }
    
    public static ChatSession getSession(String sessionId) {
        ChatSession session = sessions.get(sessionId);
        if (session != null) {
            logger.debug("获取到会话，会话ID: {}", sessionId);
        } else {
            logger.warn("未找到会话，会话ID: {}", sessionId);
        }
        return session;
    }
    
    public static void removeSession(String sessionId) {
        logger.info("删除会话，会话ID: {}", sessionId);
        sessions.remove(sessionId);
        logger.debug("会话删除成功，剩余会话总数: {}", sessions.size());
    }
    
    public static boolean sessionExists(String sessionId) {
        boolean exists = sessions.containsKey(sessionId);
        logger.debug("检查会话是否存在，会话ID: {}, 结果: {}", sessionId, exists);
        return exists;
    }
}