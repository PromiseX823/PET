package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI功能控制器
 * 提供文本生成、翻译、摘要等AI能力
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 生成文本
     * @param request 包含prompt的请求体
     * @return 生成的文本
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateText(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "prompt参数不能为空"));
        }

        String result = aiService.generateText(prompt);
        return ResponseEntity.ok(ApiResponse.success(Map.of("content", result)));
    }

    /**
     * 生成宠物描述
     * @param request 包含宠物信息的请求体
     * @return 生成的描述
     */
    @PostMapping("/pet-description")
    public ResponseEntity<ApiResponse<Map<String, String>>> generatePetDescription(@RequestBody Map<String, String> request) {
        String petName = request.getOrDefault("pet_name", "宠物");
        String petType = request.getOrDefault("pet_type", "狗");
        String petBreed = request.get("pet_breed");

        String description = aiService.generatePetDescription(petName, petType, petBreed);
        return ResponseEntity.ok(ApiResponse.success(Map.of("description", description)));
    }

    /**
     * 翻译文本
     * @param request 包含text和target_language的请求体
     * @return 翻译结果
     */
    @PostMapping("/translate")
    public ResponseEntity<ApiResponse<Map<String, String>>> translateText(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String targetLanguage = request.getOrDefault("target_language", "英文");

        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "text参数不能为空"));
        }

        String translation = aiService.translateText(text, targetLanguage);
        return ResponseEntity.ok(ApiResponse.success(Map.of("translation", translation)));
    }

    /**
     * 文本摘要
     * @param request 包含text的请求体
     * @return 摘要结果
     */
    @PostMapping("/summarize")
    public ResponseEntity<ApiResponse<Map<String, String>>> summarizeText(@RequestBody Map<String, String> request) {
        String text = request.get("text");

        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "text参数不能为空"));
        }

        String summary = aiService.summarizeText(text);
        return ResponseEntity.ok(ApiResponse.success(Map.of("summary", summary)));
    }

    /**
     * 检查AI服务状态
     * @return 服务状态
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkStatus() {
        // 测试调用
        String testResult = aiService.generateText("你好");
        boolean available = !testResult.contains("暂未配置") && !testResult.contains("失败") && !testResult.contains("异常");

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "available", available,
                "message", available ? "AI服务正常" : testResult
        )));
    }
}