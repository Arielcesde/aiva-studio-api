package com.aivastudio.exception;

/**
 * Se lanza cuando una operación incumple una regla del negocio de Aiva Studio
 * (ej: créditos insuficientes, duración no permitida, correo ya registrado).
 * El GlobalExceptionHandler la traduce a HTTP 400 Bad Request.
 */
public class ReglaDeNegocioException extends RuntimeException {

    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}