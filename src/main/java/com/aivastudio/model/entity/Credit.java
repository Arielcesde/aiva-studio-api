package com.aivastudio.model.entity;

import com.aivastudio.model.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "credits")
public class Credit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false, length = 160)
    private String description;

    protected Credit() {
    }

    public Credit(User user, long amount, String description) {
        this.user = user;
        this.amount = amount;
        this.description = description;
    }
    public User getUser() {
        return user;
    }

    public long getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}