package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/pet-description")
    public ApiResponse<String> generatePetDescription(@RequestBody Map<String, String> request) {
        String petInfo = request.get("petInfo");
        if (petInfo == null || petInfo.trim().isEmpty()) {
            return ApiResponse.error("请提供宠物信息");
        }
        String description = aiService.generatePetDescription(petInfo);
        return ApiResponse.success(description);
    }

    @PostMapping("/adoption-analysis")
    public ApiResponse<String> analyzeAdoptionApplication(@RequestBody Map<String, String> request) {
        String applicationInfo = request.get("applicationInfo");
        if (applicationInfo == null || applicationInfo.trim().isEmpty()) {
            return ApiResponse.error("请提供申请信息");
        }
        String analysis = aiService.analyzeAdoptionApplication(applicationInfo);
        return ApiResponse.success(analysis);
    }

    @PostMapping("/pet-question")
    public ApiResponse<String> answerPetQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        if (question == null || question.trim().isEmpty()) {
            return ApiResponse.error("请提出问题");
        }
        String answer = aiService.answerPetQuestion(question);
        return ApiResponse.success(answer);
    }

    @PostMapping("/summarize-comments")
    public ApiResponse<String> summarizeComments(@RequestBody Map<String, String> request) {
        String comments = request.get("comments");
        if (comments == null || comments.trim().isEmpty()) {
            return ApiResponse.error("请提供评论内容");
        }
        String summary = aiService.summarizeComments(comments);
        return ApiResponse.success(summary);
    }

    @PostMapping("/qwen")
    public ApiResponse<String> chatWithQwen(@RequestBody Map<String, Object> request) {
        List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
        if (messages == null || messages.isEmpty()) {
            return ApiResponse.error("请提供对话消息");
        }
        String response = aiService.chat(messages);
        return ApiResponse.success(response);
    }
}