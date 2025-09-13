package com.icando.referenceMaterial.service;

import com.icando.global.utils.GlobalLogger;
import com.icando.referenceMaterial.dto.ReferenceMaterialAiResponse;
import com.icando.referenceMaterial.dto.ReferenceMaterialListResponse;
import com.icando.referenceMaterial.entity.ReferenceMaterial;
import com.icando.referenceMaterial.enums.ReferenceMaterialErrorCode;
import com.icando.referenceMaterial.exception.ReferenceMaterialException;
import com.icando.referenceMaterial.repository.ReferenceMaterialRepository;
import com.icando.writing.entity.Topic;
import com.icando.writing.repository.TopicRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ReferenceMaterialService {
    private final ChatClient.Builder ai;
    private final TopicRepository topicRepository;
    private final ReferenceMaterialRepository referenceMaterialRepository;

    @Value("${reference-material.urls.prompt}")
    private String generateUrlsPrompt;

    public ReferenceMaterialService(ChatClient.Builder ai, TopicRepository topicRepository, ReferenceMaterialRepository referenceMaterialRepository) {
        this.ai = ai;
        this.topicRepository = topicRepository;
        this.referenceMaterialRepository = referenceMaterialRepository;
    }

    @Async
    @Scheduled(cron = "0 45 14 * * *")
    @Transactional
    public void generateReferenceMaterials() {
        GlobalLogger.info("Generating reference materials");
        clearUselessReferenceMaterials();
        var topics = topicRepository.findByReferenceMaterialsCount(8);
        var retryCount = 0;
        List<ReferenceMaterial> referenceMaterials = new ArrayList<>();
        for (var i = 0; i < topics.size(); i++) {
            var topic = topics.get(i);
            GlobalLogger.info("Generating reference materials for topic: {} / {} - {}", i + 1, topics.size(), topic.getTopicContent());
            try {
                referenceMaterials.addAll(getReferenceMaterials(topic.getId())
                        .stream()
                        .map(rm -> ReferenceMaterial.of(rm, topic))
                        .toList());
            } catch (Exception e) {
                if (retryCount < 3) {
                    i--;
                    retryCount++;
                    try {
                        Thread.sleep(1000L * retryCount);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    continue;
                }
            }

            retryCount = 0;
        }
        referenceMaterialRepository.saveAll(referenceMaterials);
        GlobalLogger.info("Reference materials generation completed");
    }

    private void clearUselessReferenceMaterials() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        referenceMaterialRepository.deleteAllByBookmarkCountAndPastDays(0L, cutoffDate);
    }

    public List<ReferenceMaterialAiResponse> getReferenceMaterials(long topicId) {
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid topic ID: " + topicId));

        var responses = ai
                .defaultOptions(ChatOptions.builder()
                        .model("perplexity/sonar")
                        .build())
                .build()
                .prompt(generateUrlsPrompt)
                .user(topic.getTopicContent())
                .call()
                .entity(new ParameterizedTypeReference<List<ReferenceMaterialAiResponse>>() { });

        for (ReferenceMaterialAiResponse response : Objects.requireNonNull(responses)) {
            var url = response.getUrl();
            if (url != null && !url.isEmpty()) {
                String imageUrl = extractImageFromUrl(url);
                String host = url.split("/")[2];
                imageUrl = normalizeImageUrl(imageUrl, host);
                response.setImageUrl(imageUrl);
            }
        }

        return responses;
    }

    private String normalizeImageUrl(String imageUrl, String host) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        if (imageUrl.startsWith("//")) {
            return "https:" + imageUrl;
        }

        if (imageUrl.startsWith("/")) {
            return "https://" + host + imageUrl;
        }

        return imageUrl;
    }

    private String extractImageFromUrl(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(5000)
                    .get();

            // og:image 먼저 확인
            String ogImage = doc.select("meta[property=og:image]").attr("content");
            if (!ogImage.isEmpty()) {
                return ogImage;
            }

            // twitter:image 확인
            String twitterImage = doc.select("meta[name=twitter:image]").attr("content");
            if (!twitterImage.isEmpty()) {
                return twitterImage;
            }

            // twitter:image:src 확인
            String twitterImageSrc = doc.select("meta[name=twitter:image:src]").attr("content");
            if (!twitterImageSrc.isEmpty()) {
                return twitterImageSrc;
            }

            return null;
        } catch (IOException e) {
            GlobalLogger.warn("Failed to extract image from URL: {}", url, e);
            return null;
        }
    }

    public List<ReferenceMaterialListResponse> getReferenceMaterialsByTopicId(Long topicId) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new ReferenceMaterialException(ReferenceMaterialErrorCode.TOPIC_NOT_FOUND));
        List<ReferenceMaterial> referenceMaterials = referenceMaterialRepository.findAllByTopic(topic);
        return referenceMaterials.stream()
                .map(ReferenceMaterialListResponse::of)
                .toList();
    }
}
