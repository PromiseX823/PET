package com.example.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI服务 - 调用阿里云DashScope API（通义千问）
 */
@Slf4j
@Service
public class AiService {

    @Value("${ai.dashscope.api-key:}")
    private String apiKey;

    @Value("${ai.dashscope.base-url:https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation}")
    private String baseUrl;

    @Value("${ai.dashscope.model:qwen-turbo}")
    private String model;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用AI生成文本
     * @param prompt 输入提示
     * @return 生成的文本
     */
    public String generateText(String prompt) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here")) {
            log.warn("DashScope API Key未配置，返回模拟响应");
            return "AI服务暂未配置，请设置有效的API Key。提示内容：" + prompt;
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            Map<String, Object> input = new HashMap<>();
            input.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("input", input);
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("max_tokens", 500);
            parameters.put("temperature", 0.7);
            requestBody.put("parameters", parameters);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                Map<String, Object> output = (Map<String, Object>) responseMap.get("output");
                if (output != null) {
                    // 尝试从choices获取（新版API）
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return (String) message.get("content");
                    }
                    // 尝试从text获取（旧版API）
                    String text = (String) output.get("text");
                    if (text != null) {
                        return text;
                    }
                }
                log.warn("无法解析AI响应: {}", response.body());
                return "AI响应解析失败";
            } else {
                log.error("AI调用失败: status={}, body={}", response.statusCode(), response.body());
                return "AI调用失败: HTTP " + response.statusCode();
            }
        } catch (Exception e) {
            log.error("AI调用异常: {}", e.getMessage(), e);
            return "AI调用异常: " + e.getMessage();
        }
    }

    /**
     * 生成宠物描述
     * @param petName 宠物名称
     * @param petType 宠物类型
     * @param petBreed 宠物品种
     * @return 生成的描述
     */
    public String generatePetDescription(String petName, String petType, String petBreed) {
        String prompt = String.format(
                "请为一只名叫%s的%s（品种：%s）写一段简短的领养介绍，" +
                "描述它的性格特点和适合领养的原因，不超过100字。",
                petName, petType, petBreed != null ? petBreed : "未知"
        );
        return generateText(prompt);
    }

    /**
     * 翻译文本
     * @param text 待翻译文本
     * @param targetLanguage 目标语言
     * @return 翻译结果
     */
    public String translateText(String text, String targetLanguage) {
        String prompt = String.format("请将以下文本翻译成%s，只返回翻译结果，不要其他内容：\n%s", 
                targetLanguage, text);
        return generateText(prompt);
    }

    /**
     * 生成文本摘要
     * @param text 待摘要文本
     * @return 摘要结果
     */
    public String summarizeText(String text) {
        String prompt = String.format("请对以下文本进行摘要，不超过50字：\n%s", text);
        return generateText(prompt);
    }
}