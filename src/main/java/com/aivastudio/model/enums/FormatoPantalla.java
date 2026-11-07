package com.aivastudio.model.enums;

import com.aivastudio.exception.ReglaDeNegocioException;

public enum FormatoPantalla {
    HORIZONTAL_16_9("16:9"),
    VERTICAL_9_16("9:16"),
    CUADRADO_1_1("1:1");

    private final String valor;

    FormatoPantalla(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static FormatoPantalla fromValor(String valor) {
        for (FormatoPantalla formato : values()) {
            if (formato.valor.equals(valor)) {
                return formato;
            }
        }
        throw new ReglaDeNegocioException("El formato debe ser 16:9, 9:16 o 1:1");
    }
}