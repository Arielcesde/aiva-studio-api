package com.aivastudio.model.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.aivastudio.model.base.BaseEntity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_models")
@AttributeOverride(name = "id", column = @Column(name = "id_modelo"))
public class AiModel extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String proveedor;

    @Column(nullable = false, length = 30)
    private String version;

    @Column(name = "costo_por_segundo", nullable = false, precision = 10, scale = 4)
    private BigDecimal costoPorSegundo;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "aiModel")
    private List<Project> projects = new ArrayList<>();

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;          // slug que usa el frontend, ej. "lumina-v2"

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(nullable = false, length = 300)
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String color;

    public List<Project> getProjects() { return projects; }

    public AiModel() {
    }

    public AiModel(String codigo, String nombre, String proveedor, String version,
                   BigDecimal costoPorSegundo, String categoria, String descripcion, String color) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.proveedor = proveedor;
        this.version = version;
        this.costoPorSegundo = costoPorSegundo;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.color = color;
    }
    // + getters y setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public BigDecimal getCostoPorSegundo() {
        return costoPorSegundo;
    }

    public void setCostoPorSegundo(BigDecimal costoPorSegundo) {
        this.costoPorSegundo = costoPorSegundo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getCodigo() {return codigo; }

    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }
}
