package com.icando.paragraphCompletion.service;

import com.icando.paragraphCompletion.entity.WordSetItem;
import com.icando.paragraphCompletion.repository.ParagraphCompletionRepository;
import com.icando.paragraphCompletion.repository.WordSetItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ParagraphCompletionService {
    private final ParagraphCompletionRepository paragraphCompletionRepository;
    private final WordSetItemRepository wordSetItemRepository;
    private final ChatClient ai;

    @Autowired
    public ParagraphCompletionService(ParagraphCompletionRepository paragraphCompletionRepository, WordSetItemRepository wordSetItemRepository, ChatClient.Builder chatClient) {
        this.paragraphCompletionRepository = paragraphCompletionRepository;
        this.wordSetItemRepository = wordSetItemRepository;
        this.ai = chatClient.build();
    }

    public List<String> generateWords(int count) {
        List<String> words = new ArrayList<>(count);
        List<Integer> ids = new ArrayList<>(count + 1);
        ids.add(0);
        for (int i = 0; i < count; i++) { // order by random 보다 빠른 쿼리 100번 호출하는게 더 나을 수 있다고 생각함. 이건 테스트 해봐야 알 듯
            WordSetItem item = wordSetItemRepository.getRandomWord(ids);
            if (item == null) { // 코드 자체는 무한 루프 위험이 있지만 데이터가 5000개 이상이고 count는 100개로 제한되어 있으므로 무한루프는 발생하지 않음
                i--;
                continue;
            }
            words.add(item.getWord());
            ids.add(item.getId());
        }
        return words;
    }
}
