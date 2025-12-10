package com.shanzhu.hospital.service.serviceImpl;

import com.shanzhu.hospital.entity.vo.ChatMessage;
import com.shanzhu.hospital.entity.vo.ChatSession;
import com.shanzhu.hospital.service.LlmService;
import com.shanzhu.hospital.utils.ChatSessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LlmServiceImpl implements LlmService {
    
    private static final Logger logger = LoggerFactory.getLogger(LlmServiceImpl.class);

    @Value("${llm.api.key:sk-TMe6dYqpxvJgQXfK82mhCpqPcOcxzMVjr3EylpcfowuKipR1}")
    private String API_KEY;

    @Value("${llm.api.url:https://api.chatanywhere.tech/v1/chat/completions}")
    private String API_URL;

    @Value("${llm.model:gpt-3.5-turbo}")
    private String MODEL;

    @Override
    public String getModelResponse(String prompt) {
        try {
            logger.info("调用大语言模型API，提示词: {}", prompt);
            
            // 创建URL对象
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法和请求头
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setDoOutput(true);

            // 构造请求体 (使用字符串拼接替代JSON库)
            String jsonBody = "{"
                    + "\"model\": \"" + MODEL + "\","
                    + "\"messages\": ["
                    + "{\"role\": \"user\", \"content\": \"" + escapeJsonString(prompt) + "\"}"
                    + "],"
                    + "\"stream\": false"
                    + "}";

            // 发送请求体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 解析JSON响应并提取content字段
                String result = extractContentFromResponse(response.toString());
                logger.info("大语言模型API调用成功，响应: {}", result);
                return result;
            } else {
                String errorMsg = "Error: HTTP " + responseCode;
                logger.error("大语言模型API调用失败，HTTP状态码: {}", responseCode);
                return errorMsg;
            }
        } catch (Exception e) {
            logger.error("调用大语言模型时发生异常: ", e);
            return "Error: " + e.getMessage();
        }
    }
        
    @Override
    public void getModelResponseStream(String prompt, SseEmitter emitter) {
        try {
            logger.info("调用大语言模型API(流式)，提示词: {}", prompt);
            
            // 创建URL对象
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    
            // 设置请求方法和请求头
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setDoOutput(true);
            
            // 构造请求体 (使用字符串拼接替代JSON库)
            String jsonBody = "{"
                    + "\"model\": \"" + MODEL + "\","
                    + "\"messages\": ["
                    + "{\"role\": \"user\", \"content\": \"" + escapeJsonString(prompt) + "\"}"
                    + "],"
                    + "\"stream\": true"
                    + "}";
    
            // 发送请求体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
    
            // 读取流式响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("data: ")) continue;
                    
                    String data = line.substring(6);
                    if ("[DONE]".equals(data)) break;
                    
                    // 尝试从中提取content字段
                    String content = extractContentFromStreamData(data);
                    if (content != null && !content.isEmpty()) {
                        emitter.send(content);
                    }
                }
                reader.close();
                logger.info("大语言模型API(流式)调用完成");
            }
            emitter.complete();
        } catch (Exception e) {
            logger.error("调用大语言模型(流式)时发生异常: ", e);
            try {
                emitter.send("Error: " + e.getMessage());
            } catch (Exception ignored) {}
            emitter.complete();
        }
    }

    /**
     * 获取带上下文的模型响应
     *
     * @param sessionId 会话ID
     * @param prompt 用户输入
     * @return 模型响应
     */
    public String getModelResponseWithContext(String sessionId, String prompt) {
        try {
            logger.info("调用大语言模型API(带上下文)，会话ID: {}, 提示词: {}", sessionId, prompt);
            
            // 获取会话
            ChatSession session = ChatSessionManager.getSession(sessionId);
            if (session == null) {
                String errorMsg = "会话不存在或已过期";
                logger.warn(errorMsg);
                return errorMsg;
            }
            
            // 添加用户消息到会话历史
            session.addUserMessage(prompt);
            logger.debug("已将会话ID {} 的用户消息添加到历史记录", sessionId);
            
            // 创建URL对象
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法和请求头
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setDoOutput(true);

            // 构建消息历史
            List<ChatMessage> recentMessages = session.getRecentMessages(10); // 限制最近10条消息
            StringBuilder messagesBuilder = new StringBuilder();
            for (int i = 0; i < recentMessages.size(); i++) {
                ChatMessage msg = recentMessages.get(i);
                if (i > 0) messagesBuilder.append(",");
                messagesBuilder.append("{")
                        .append("\"role\": \"").append(msg.getRole()).append("\",")
                        .append("\"content\": \"").append(escapeJsonString(msg.getContent())).append("\"")
                        .append("}");
            }
            
            // 构造请求体
            String jsonBody = "{"
                    + "\"model\": \"" + MODEL + "\","
                    + "\"messages\": [" + messagesBuilder.toString() + "],"
                    + "\"stream\": false"
                    + "}";

            // 发送请求体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 解析JSON响应并提取content字段
                String content = extractContentFromResponse(response.toString());
                
                // 将助手回复添加到会话历史
                session.addAssistantMessage(content);
                logger.info("大语言模型API(带上下文)调用成功，会话ID: {}, 响应: {}", sessionId, content);
                
                return content;
            } else {
                String errorMsg = "Error: HTTP " + responseCode;
                logger.error("大语言模型API(带上下文)调用失败，会话ID: {}, HTTP状态码: {}", sessionId, responseCode);
                return errorMsg;
            }
        } catch (Exception e) {
            logger.error("调用大语言模型(带上下文)时发生异常，会话ID: " + sessionId, e);
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * 流式获取带上下文的模型响应
     *
     * @param sessionId 会话ID
     * @param prompt 用户输入
     * @param emitter SSE发射器
     */
    public void getModelResponseStreamWithContext(String sessionId, String prompt, SseEmitter emitter) {
        try {
            logger.info("调用大语言模型API(流式带上下文)，会话ID: {}, 提示词: {}", sessionId, prompt);
            
            // 获取会话
            ChatSession session = ChatSessionManager.getSession(sessionId);
            if (session == null) {
                String errorMsg = "会话不存在或已过期";
                logger.warn(errorMsg);
                emitter.send(errorMsg);
                emitter.complete();
                return;
            }
            
            // 添加用户消息到会话历史
            session.addUserMessage(prompt);
            logger.debug("已将会话ID {} 的用户消息添加到历史记录", sessionId);
            
            // 创建URL对象
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法和请求头
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setDoOutput(true);

            // 构建消息历史
            List<ChatMessage> recentMessages = session.getRecentMessages(10); // 限制最近10条消息
            StringBuilder messagesBuilder = new StringBuilder();
            for (int i = 0; i < recentMessages.size(); i++) {
                ChatMessage msg = recentMessages.get(i);
                if (i > 0) messagesBuilder.append(",");
                messagesBuilder.append("{")
                        .append("\"role\": \"").append(msg.getRole()).append("\",")
                        .append("\"content\": \"").append(escapeJsonString(msg.getContent())).append("\"")
                        .append("}");
            }
            
            // 构造请求体
            String jsonBody = "{"
                    + "\"model\": \"" + MODEL + "\","
                    + "\"messages\": [" + messagesBuilder.toString() + "],"
                    + "\"stream\": true"
                    + "}";

            // 发送请求体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 读取流式响应
            int responseCode = connection.getResponseCode();
            StringBuilder fullResponse = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("data: ")) continue;
                    
                    String data = line.substring(6);
                    if ("[DONE]".equals(data)) break;
                    
                    // 尝试从中提取content字段
                    String content = extractContentFromStreamData(data);
                    if (content != null && !content.isEmpty()) {
                        emitter.send(content);
                        fullResponse.append(content);
                    }
                }
                reader.close();
                
                // 将完整响应添加到会话历史
                session.addAssistantMessage(fullResponse.toString());
                logger.info("大语言模型API(流式带上下文)调用完成，会话ID: {}", sessionId);
            }
            emitter.complete();
        } catch (Exception e) {
            logger.error("调用大语言模型(流式带上下文)时发生异常，会话ID: " + sessionId, e);
            try {
                emitter.send("Error: " + e.getMessage());
            } catch (Exception ignored) {}
            emitter.complete();
        }
    }

    /**
     * 从API响应中提取content字段
     *
     * @param jsonResponse API的完整JSON响应
     * @return 提取的content内容
     */
    private String extractContentFromResponse(String jsonResponse) {
        try {
            // 使用正则表达式提取content字段
            Pattern pattern = Pattern.compile("\"content\":\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(jsonResponse);
            
            if (matcher.find()) {
                String content = matcher.group(1);
                // 处理转义字符
                return content.replace("\\n", "\n").replace("\\\"", "\"");
            }
            return "No content found in response";
        } catch (Exception e) {
            return "Failed to parse response: " + e.getMessage();
        }
    }
    
    /**
     * 从流式API响应中提取content字段
     *
     * @param streamData 流式数据片段
     * @return 提取的content内容
     */
    private String extractContentFromStreamData(String streamData) {
        try {
            // 使用正则表达式提取content字段
            Pattern pattern = Pattern.compile("\"content\":\"([^\"]*)\"");
            Matcher matcher = pattern.matcher(streamData);
            
            if (matcher.find()) {
                String content = matcher.group(1);
                // 处理转义字符
                return content.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * 转义JSON字符串中的特殊字符
     * 
     * @param str 原始字符串
     * @return 转义后的字符串
     */
    private String escapeJsonString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}