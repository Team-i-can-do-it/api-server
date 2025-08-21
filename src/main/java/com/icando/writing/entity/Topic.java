package com.icando.writing.entity;


import com.icando.writing.enums.Category;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id", nullable = false)
    private Long Id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Column(name = "topic")
    private String topic;

    public Topic(Category category, String topic) {
        this.category = category;
        this.topic = topic;
    }
}
