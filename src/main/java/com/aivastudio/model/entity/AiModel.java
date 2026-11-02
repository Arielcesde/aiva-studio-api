package com.aivastudio.model.entity;

import java.math.BigDecimal;
import com.aivastudio.model.base.BaseEntity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "modelos_ia")
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

    public AiModel() {
    }

    public AiModel(Long id, String nombre, String proveedor, String version,
            BigDecimal costoPorSegundo, Boolean activo) {
        super(id);
        this.nombre = nombre;
        this.proveedor = proveedor;
        this.version = version;
        this.costoPorSegundo = costoPorSegundo;
        this.activo = (activo != null) ? activo : Boolean.TRUE;
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
}
