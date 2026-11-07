package com.aivastudio.model.embeddable;

import com.aivastudio.model.enums.FormatoPantalla;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class ConfiguracionVideo {

    @Column(name = "estilo_visual", nullable = false, length = 50)
    private String estiloVisual;

    @Enumerated(EnumType.STRING)
    @Column(name = "formato_pantalla", nullable = false, length = 30)
    private FormatoPantalla formatoPantalla;

    @Column(name = "resolucion", nullable = false, length = 20)
    private String resolucion;

    @Column(name = "duracion_segundos", nullable = false)
    private Integer duracionSegundos;

    public ConfiguracionVideo() {}

    public ConfiguracionVideo(String estiloVisual, FormatoPantalla formatoPantalla,
                              String resolucion, Integer duracionSegundos) {
        this.estiloVisual = estiloVisual;
        this.formatoPantalla = formatoPantalla;
        this.resolucion = resolucion;
        this.duracionSegundos = duracionSegundos;
    }

    public String getEstiloVisual() { return estiloVisual; }
    public void setEstiloVisual(String estiloVisual) { this.estiloVisual = estiloVisual; }

    public FormatoPantalla getFormatoPantalla() { return formatoPantalla; }
    public void setFormatoPantalla(FormatoPantalla formatoPantalla) { this.formatoPantalla = formatoPantalla; }

    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }

    public Integer getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(Integer duracionSegundos) { this.duracionSegundos = duracionSegundos; }
}