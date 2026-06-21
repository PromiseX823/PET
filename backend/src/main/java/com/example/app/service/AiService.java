package com.example.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiService {

    @Value("${ai.dashscope.api-key}")
    private String apiKey;

    @Value("${ai.dashscope.base-url:https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public AiService() {
        this.restTemplate = new RestTemplate();
    }

    public String generatePetDescription(String petInfo) {
        String prompt = """
            请根据以下宠物信息，生成一段吸引人的领养描述：
            %s
            
            要求：
            1. 语言生动有趣，突出宠物的可爱之处
            2. 包含宠物的性格特点和适合的领养家庭
            3. 字数在200-300字左右
            4. 语气亲切友好
            """.formatted(petInfo);

        return callDashScope(prompt);
    }

    public String analyzeAdoptionApplication(String applicationInfo) {
        String prompt = """
            请分析以下宠物领养申请：
            %s
            
            请评估：
            1. 申请人是否适合领养该宠物
            2. 申请理由是否充分
            3. 给出审核建议（通过/拒绝/需补充信息）
            4. 说明理由
            """.formatted(applicationInfo);

        return callDashScope(prompt);
    }

    public String answerPetQuestion(String question) {
        String prompt = """
            请回答关于宠物养护的问题：
            %s
            
            要求：
            1. 回答准确专业
            2. 语言通俗易懂
            3. 如有必要，给出具体建议
            """.formatted(question);

        return callDashScope(prompt);
    }

    public String summarizeComments(String comments) {
        String prompt = """
            请总结以下评论内容：
            %s
            
            要求：
            1. 提取主要观点和情感倾向
            2. 总结用户反馈的关键点
            3. 给出改进建议
            """.formatted(comments);

        return callDashScope(prompt);
    }

    public String chat(List<Map<String, String>> messages) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-turbo");

            List<Map<String, Object>> formattedMessages = new ArrayList<>();
            for (Map<String, String> message : messages) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("role", message.get("role"));
                msg.put("content", message.get("content"));
                formattedMessages.add(msg);
            }

            requestBody.put("input", Map.of("messages", formattedMessages));
            requestBody.put("parameters", Map.of("temperature", 0.7, "max_tokens", 2048));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> result = response.getBody();
                if (result != null && result.containsKey("output")) {
                    Map<String, Object> output = (Map<String, Object>) result.get("output");
                    if (output.containsKey("text")) {
                        return (String) output.get("text");
                    }
                }
            }

            log.error("DashScope API call failed: {}", response);
            return "AI服务暂时不可用，请稍后重试";
        } catch (HttpClientErrorException e) {
            log.error("DashScope API HTTP error: {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return "API密钥无效，请检查配置";
            }
            return "AI服务调用失败: " + e.getMessage();
        } catch (Exception e) {
            log.error("Error calling DashScope API", e);
            return "AI服务暂时不可用，请稍后重试";
        }
    }

    private String callDashScope(String prompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        return chat(messages);
    }
}