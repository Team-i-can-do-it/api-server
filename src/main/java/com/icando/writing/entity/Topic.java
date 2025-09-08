package com.icando.writing.entity;


import com.icando.referenceMaterial.entity.ReferenceMaterial;
import com.icando.writing.enums.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id", nullable = false)
    private Long Id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Column(name = "topic_content")
    private String topicContent;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ReferenceMaterial> referenceMaterials = new ArrayList<>();

    public Topic(Category category, String topicContent) {
        this.category = category;
        this.topicContent = topicContent;
    }

    public static Topic of(Category category, String topic) {
        return new Topic(category, topic);
    }
}
