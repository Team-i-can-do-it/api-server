package com.icando.referenceMaterial.entity;

import com.icando.global.BaseEntity;
import com.icando.referenceMaterial.dto.ReferenceMaterialAiResponse;
import com.icando.writing.entity.Topic;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferenceMaterial extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, length = 4096)
    private String description;

    @Column(name = "image_url", nullable = true, length = 2048)
    private String imageUrl;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    public static ReferenceMaterial of(ReferenceMaterialAiResponse rm, Topic topic) {
        ReferenceMaterial referenceMaterial = new ReferenceMaterial();
        referenceMaterial.title = rm.getTitle();
        referenceMaterial.description = rm.getDescription();
        referenceMaterial.imageUrl = rm.getImageUrl();
        referenceMaterial.url = rm.getUrl();
        referenceMaterial.topic = topic;
        return referenceMaterial;
    }
}
