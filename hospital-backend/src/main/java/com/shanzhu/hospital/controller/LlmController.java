package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.Patient;
import com.shanzhu.hospital.entity.vo.ChatSession;
import com.shanzhu.hospital.service.LlmService;
import com.shanzhu.hospital.utils.ChatSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 大语言模型控制器
 */
@RestController
@RequestMapping("/llm")
@CrossOrigin
public class LlmController {
    
    private static final Logger logger = LoggerFactory.getLogger(LlmController.class);

    @Autowired
    private LlmService llmService;

    /**
     * 调用大语言模型接口
     *
     * @param prompt 用户输入的提示词
     * @return 模型响应结果
     */
    @PostMapping("/ask")
    public R<String> askModel(@RequestParam("prompt") String prompt) {
        try {
            logger.info("收到普通模型调用请求，提示词: {}", prompt);
            String response = llmService.getModelResponse(prompt);
            logger.info("普通模型调用完成，响应: {}", response);
            return R.ok(response);
        } catch (Exception e) {
            logger.error("调用大语言模型失败", e);
            return R.error("调用大语言模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 流式调用大语言模型接口
     *
     * @param prompt 用户输入的提示词
     * @return SSE流式响应
     */
    @GetMapping(value = "/ask/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter askModelStream(@RequestParam("prompt") String prompt) {
        logger.info("收到流式模型调用请求，提示词: {}", prompt);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        llmService.getModelResponseStream(prompt, emitter);
        logger.info("流式模型调用已启动");
        return emitter;
    }
    
    /**
     * 创建新的聊天会话
     *
     * @param request HTTP请求
     * @return 会话ID
     */
    @PostMapping("/session/create")
    public R<String> createSession(HttpServletRequest request) {
        try {
            logger.info("收到创建会话请求");
            // 从请求中获取患者信息（实际项目中可能需要从token中解析）
            // 这里简化处理，实际应用中应该从JWT token或其他认证机制中获取患者信息
            Patient patient = new Patient(); // 示例患者对象
            
            // 生成会话ID（实际项目中可以使用UUID或者其他唯一标识）
            String sessionId = "session_" + System.currentTimeMillis();
            
            // 创建会话
            ChatSession session = ChatSessionManager.createSession(sessionId, patient);
            logger.info("会话创建成功，会话ID: {}", sessionId);
            
            return R.ok(sessionId);
        } catch (Exception e) {
            logger.error("创建会话失败", e);
            return R.error("创建会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 带上下文的调用大语言模型接口
     *
     * @param sessionId 会话ID
     * @param prompt 用户输入的提示词
     * @return 模型响应结果
     */
    @PostMapping("/ask/context")
    public R<String> askModelWithContext(@RequestParam("sessionId") String sessionId,
                                         @RequestParam("prompt") String prompt) {
        try {
            logger.info("收到带上下文模型调用请求，会话ID: {}, 提示词: {}", sessionId, prompt);
            String response = llmService.getModelResponseWithContext(sessionId, prompt);
            logger.info("带上下文模型调用完成，会话ID: {}, 响应: {}", sessionId, response);
            return R.ok(response);
        } catch (Exception e) {
            logger.error("调用大语言模型失败，会话ID: " + sessionId, e);
            return R.error("调用大语言模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 流式带上下文的调用大语言模型接口
     *
     * @param sessionId 会话ID
     * @param prompt 用户输入的提示词
     * @return SSE流式响应
     */
    @GetMapping(value = "/ask/context/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter askModelStreamWithContext(@RequestParam("sessionId") String sessionId,
                                                @RequestParam("prompt") String prompt) {
        logger.info("收到流式带上下文模型调用请求，会话ID: {}, 提示词: {}", sessionId, prompt);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        llmService.getModelResponseStreamWithContext(sessionId, prompt, emitter);
        logger.info("流式带上下文模型调用已启动，会话ID: {}", sessionId);
        return emitter;
    }
}