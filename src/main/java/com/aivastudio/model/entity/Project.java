package com.aivastudio.model.entity;

import java.util.HashSet;
import java.util.Set;

import com.aivastudio.model.base.BaseEntity;
import com.aivastudio.model.embeddable.ConfiguracionVideo;
import com.aivastudio.model.enums.EstadoProyecto;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class Project extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ai_model_id", nullable = false)
    private AiModel aiModel;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 1000)
    private String prompt;

    @Column(nullable = false)
    private long creditCost;

    @Embedded
    private ConfiguracionVideo configuracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProyecto estado = EstadoProyecto.EN_COLA;

    @ManyToMany
    @JoinTable(
            name = "project_tags",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private final Set<Tag> tags = new HashSet<>();

    protected Project() {
    }

    public Project(User user, AiModel aiModel, String name, String prompt,
                   long creditCost, ConfiguracionVideo configuracion) {
        this.user = user;
        this.aiModel = aiModel;
        this.name = name;
        this.prompt = prompt;
        this.creditCost = creditCost;
        this.configuracion = configuracion;
        this.estado = EstadoProyecto.EN_COLA;
    }

    public User getUser() {
        return user; }
    public AiModel getAiModel() { return aiModel; }
    public String getName() { return name; }
    public String getPrompt() { return prompt; }
    public long getCreditCost() { return creditCost; }
    public ConfiguracionVideo getConfiguracion() { return configuracion; }
    public EstadoProyecto getEstado() { return estado; }
    public void setEstado(EstadoProyecto estado) { this.estado = estado; }
    public Set<Tag> getTags() { return tags; }
    public void addTag(Tag tag) { this.tags.add(tag); }
}