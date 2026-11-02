package com.aivastudio.model.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "aiva_projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 1000)
    private String prompt;

    @Column(nullable = false, length = 80)
    private String modelName;

    @Column(nullable = false, length = 40)
    private String modelId;

    @Column(nullable = false)
    private long creditCost;

    @Column(nullable = false)
    private int durationSeconds;

    @Column(nullable = false, length = 5)
    private String format;

    @Column(nullable = false, length = 40)
    private String style;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Project() {
    }

    public Project(
            User user,
            String name,
            String prompt,
            String modelId,
            String modelName,
            long creditCost,
            int durationSeconds,
            String format,
            String style) {
        this.user = user;
        this.name = name;
        this.prompt = prompt;
        this.modelId = modelId;
        this.modelName = modelName;
        this.creditCost = creditCost;
        this.durationSeconds = durationSeconds;
        this.format = format;
        this.style = style;
        this.status = "EN_COLA";
    }

    @PrePersist
    @SuppressWarnings("unused")
    void setCreatedAt() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now(ZoneOffset.UTC);
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelId() {
        return modelId;
    }

    public long getCreditCost() {
        return creditCost;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public String getFormat() {
        return format;
    }

    public String getStyle() {
        return style;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}