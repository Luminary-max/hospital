package com.bear.hospital.service.serviceImpl;

import com.bear.hospital.service.DeepSeekService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DeepSeekServiceImpl implements DeepSeekService {
    @Value("${deepseek.api-key:${DEEPSEEK_API_KEY:}}")
    private String apiKey;

    @Value("${deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${deepseek.model:deepseek-chat}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return fallback(userPrompt);
        }
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", model);
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(message("system", systemPrompt));
            messages.add(message("user", userPrompt));
            body.put("messages", messages);
            body.put("temperature", 0.2);
            body.put("max_tokens", 900);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey.trim());
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/chat/completions",
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    String.class
            );
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (!content.isMissingNode()) {
                return cleanOutput(content.asText());
            }
        } catch (Exception e) {
            return cleanOutput(fallback(userPrompt) + "\n\nAI接口调用失败，已返回本地规则建议：" + e.getMessage());
        }
        return cleanOutput(fallback(userPrompt));
    }

    private Map<String, String> message(String role, String content) {
        Map<String, String> item = new HashMap<>();
        item.put("role", role);
        item.put("content", content);
        return item;
    }

    private String fallback(String text) {
        String src = text == null ? "" : text;
        String risk = "低";
        if (src.contains("胸痛") || src.contains("呼吸困难") || src.contains("昏迷") || src.contains("高热") || src.contains("危急")) {
            risk = "高危";
        } else if (src.contains("发热") || src.contains("疼痛") || src.contains("异常") || src.contains("过敏")) {
            risk = "中";
        }
        return "【本地规则建议】风险等级：" + risk
                + "\n可能方向：请结合生命体征、既往史、检查结果和医生面诊判断。"
                + "\n处理建议：完善必要检查，出现高危症状时优先急诊处理。";
    }

    private String cleanOutput(String text) {
        if (text == null) return "";
        return text.replace("**", "");
    }
}
