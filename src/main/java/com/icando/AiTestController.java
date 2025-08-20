package com.icando;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiTestController {

    private final ChatClient ai;

    public AiTestController(ChatClient.Builder ai) {
        this.ai = ai.build();
    }

    @GetMapping("/ai-test")
    public ResponseEntity<String> aiTest(@RequestParam(required = false) String text) {
        String response = ai
                .prompt("Now you are a speech mentor. "
                        + "Recognize my text and give me feedback in JSON format. "
                        + "Score is from 0 to 100. "
                        + "format: { \"feedback\": \"string\", score: number }\n"
                )
                .user(text)
                .call()
                .content();
        return ResponseEntity.ok(response);
    }
}