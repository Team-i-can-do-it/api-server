package com.icando.writing.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/writing")
public class WritingController {

    private final ChatClient ai;

    public WritingController(ChatClient.Builder ai) {
        this.ai = ai.build();
    }

    /*@GetMapping("/topics/{category}")
    public ResponseEntity<String> getTopicByCategory(@PathVariable String category) {

        switch (category) {
            // 일상 취미
            case "daily-life":
                break;

            // 사회 정치
            case "social-politics":
                break;

            // 경제 비즈니스
            case "economy-business":
                break;

            // 기술 미래
            case "tech-future":
                break;

            // 문화 예술
            case "culture-arts":
                break;

            // 랜덤
            case "random":
                break;
        }
    }*/

    @GetMapping("/topics/feedback")
    public ResponseEntity<String> topicsDaily(
        @RequestParam(
            name = "text",
            defaultValue = ""
        ) String text
    ) {
        String feedbackPrompt = """
            너는 사용자가 즐겁게 글을 쓸 수 있도록 돕는 친절한 글쓰기 코치 AI야. 너의 역할은 한 가지야.
            
            사용자가 직접 쓴 글을 전달할 경우(사용자 입력이 비어있지 않은 경우), 글을 분석하고 아래 5가지 항목에 따라 구체적인 피드백을 제공해야 해. 피드백은 항상 긍정적이고 성장을 격려하는 방식으로 작성해 줘. 평가는 반드시 아래의 JSON 형식에 맞춰서 응답해야 해.
            
            ---
            [피드백 제공 시 준수할 JSON 형식]
            {
              "content": "글의 내용에 대한 1~2문장의 총평. (예: '오늘 하루의 소소한 행복을 잘 포착해주셨네요. 글을 읽는 내내 미소가 지어졌어요.')",
              "score": "글의 전체적인 완성도에 대한 점수 (100점 만점, 숫자만 포함).",
              "expression": "인상적인 표현이나 어휘, 문장 스타일에 대한 구체적인 칭찬과 발전 방향 제시.",
              "narrative": "글의 시작, 중간, 끝의 흐름과 구조에 대한 분석 및 피드백.",
              "tone": "글에서 느껴지는 전반적인 어조와 분위기에 대한 분석."
            }
            ---
            
            이제 아래 사용자 글에 대해 피드백을 생성해 줘.
            """;
        String response = ai.prompt()
            .system(feedbackPrompt)
            .user(text)
            .call()
            .content();

        return ResponseEntity.ok(response);
    }

}
