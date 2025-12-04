package com.shanzhu.hospital.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 大语言模型服务接口
 */
public interface LlmService {

    /**
     * 调用大语言模型获取响应
     *
     * @param prompt 用户输入的提示词
     * @return 模型返回的内容
     */
    String getModelResponse(String prompt);
    
    /**
     * 调用大语言模型获取流式响应
     *
     * @param prompt 用户输入的提示词
     * @param emitter SSE发射器
     */
    void getModelResponseStream(String prompt, SseEmitter emitter);
    
    /**
     * 获取带上下文的模型响应
     *
     * @param sessionId 会话ID
     * @param prompt 用户输入的提示词
     * @return 模型返回的内容
     */
    String getModelResponseWithContext(String sessionId, String prompt);
    
    /**
     * 流式获取带上下文的模型响应
     *
     * @param sessionId 会话ID
     * @param prompt 用户输入的提示词
     * @param emitter SSE发射器
     */
    void getModelResponseStreamWithContext(String sessionId, String prompt, SseEmitter emitter);
}