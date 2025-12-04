package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.service.LlmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 大语言模型测试控制器
 */
@RestController
@RequestMapping("/test/llm")
@CrossOrigin
public class LlmTestController {

    @Autowired
    private LlmService llmService;

    /**
     * 测试调用大语言模型接口
     *
     * @param prompt 用户输入的提示词
     * @return 模型响应结果
     */
    @GetMapping("/ask")
    public R<String> askModel(@RequestParam("prompt") String prompt) {
        try {
            String response = llmService.getModelResponse(prompt);
            return R.ok(response);
        } catch (Exception e) {
            return R.error("调用大语言模型失败: " + e.getMessage());
        }
    }
}