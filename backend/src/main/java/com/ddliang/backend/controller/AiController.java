package com.ddliang.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * AI咨询控制器 - 调用DeepSeek API
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    @Value("${deepseek.api.key:sk-289f877b60784fd2b0453f3ccb71e44a}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * AI聊天接口
     */
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> request) {
        try {
            String userMessage = (String) request.get("message");
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "消息不能为空"
                ));
            }

            // 构建DeepSeek API请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 系统提示词 - 宠物咨询专家
            String systemPrompt = "你是一个专业的宠物咨询专家，名叫'宠物小助手'。你精通各种宠物的饲养、健康、训练等知识。" +
                "请用友好、专业的语气回答用户关于宠物的问题。如果用户问的不是宠物相关问题，请礼貌地引导他们询问宠物相关话题。" +
                "回答要简洁明了，必要时可以分点列出。";

            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");  // 最便宜的模型
            body.put("messages", Arrays.asList(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
            ));
            body.put("temperature", 0.7);
            body.put("max_tokens", 500);  // 限制token数量节省费用

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // 调用DeepSeek API
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map responseBody = response.getBody();
                List<Map> choices = (List<Map>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map message = (Map) choices.get(0).get("message");
                    String aiResponse = (String) message.get("content");
                    
                    return ResponseEntity.ok(Map.of(
                        "code", 200,
                        "data", Map.of(
                            "reply", aiResponse
                        )
                    ));
                }
            }

            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "AI服务暂时不可用，请稍后再试"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "AI服务出错: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取AI推荐的宠物问题
     */
    @GetMapping("/suggestions")
    public ResponseEntity<?> getSuggestions() {
        List<String> suggestions = Arrays.asList(
            "如何给猫咪选择合适的猫粮？",
            "狗狗不吃东西怎么办？",
            "新手养猫需要准备什么？",
            "如何训练狗狗定点大小便？",
            "猫咪掉毛严重是什么原因？",
            "狗狗疫苗应该怎么打？"
        );
        
        return ResponseEntity.ok(Map.of(
            "code", 200,
            "data", suggestions
        ));
    }
}
