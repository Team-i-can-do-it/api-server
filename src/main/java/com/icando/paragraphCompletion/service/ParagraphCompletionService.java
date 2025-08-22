package com.icando.paragraphCompletion.service;

import com.icando.paragraphCompletion.entity.WordSetItem;
import com.icando.paragraphCompletion.repository.ParagraphCompletionRepository;
import com.icando.paragraphCompletion.repository.WordSetItemRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ParagraphCompletionService {
    private final ParagraphCompletionRepository paragraphCompletionRepository;
    private final WordSetItemRepository wordSetItemRepository;
    private final ChatClient ai;

    
    public ParagraphCompletionService(ParagraphCompletionRepository paragraphCompletionRepository, WordSetItemRepository wordSetItemRepository, ChatClient.Builder chatClient) {
        this.paragraphCompletionRepository = paragraphCompletionRepository;
        this.wordSetItemRepository = wordSetItemRepository;
        this.ai = chatClient.build();
    }

    public List<String> generateWords(int count) {
        return wordSetItemRepository.getRandomWords(count).stream().map(WordSetItem::getWord).toList();
    }
}
