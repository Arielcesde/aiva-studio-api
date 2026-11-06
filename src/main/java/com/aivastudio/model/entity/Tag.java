package com.aivastudio.model.entity;

import java.util.HashSet;
import java.util.Set;

import com.aivastudio.model.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String nombre;

    @ManyToMany(mappedBy = "tags")
    private final Set<Project> projects = new HashSet<>();

    public Tag() {}

    public Tag(String nombre) { this.nombre = nombre; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Set<Project> getProjects() { return projects; }
}