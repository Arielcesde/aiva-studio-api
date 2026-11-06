package com.aivastudio.model.entity;

import com.aivastudio.model.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscriptions")
public class Subscription extends BaseEntity {

    @Column(nullable = false, length = 30)
    private String plan = "GRATUITO";

    @Column(nullable = false)
    private boolean activa = true;

    public Subscription() {}

    public Subscription(String plan, boolean activa) {
        this.plan = plan;
        this.activa = activa;
    }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}