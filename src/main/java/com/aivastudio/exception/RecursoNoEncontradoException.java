package com.aivastudio.exception;

/**
 * Se lanza cuando se busca por id o código un registro que no existe
 * (ej: usuario o modelo de IA inexistente).
        * El GlobalExceptionHandler la traduce a HTTP 404 Not Found.
        */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}