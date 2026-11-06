package com.aivastudio.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Centraliza el manejo de errores de la API para que los controllers no
 * necesiten try/catch. Toda excepción de negocio lanzada desde la capa service
 * termina aquí y se convierte en una respuesta JSON con el código HTTP adecuado.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ReglaDeNegocioException.class)
    public ResponseEntity<ApiError> manejarReglaDeNegocio(ReglaDeNegocioException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ApiError> manejarCredencialesInvalidas(CredencialesInvalidasException ex) {
        return construir(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        String mensaje = errores.values().stream()
                .filter(valor -> valor != null && !valor.isBlank())
                .findFirst()
                .orElse("La solicitud contiene datos inválidos");
        return construir(HttpStatus.BAD_REQUEST, mensaje, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> manejarCuerpoInvalido(HttpMessageNotReadableException ex) {
        return construir(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es válido", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        String mensaje = "El valor del parámetro '" + ex.getName() + "' no es válido";
        return construir(HttpStatus.BAD_REQUEST, mensaje, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> manejarIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos", ex);
        return construir(HttpStatus.CONFLICT,
                "La operación viola una restricción de datos (registro duplicado o dato inválido)", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> manejarAccesoDenegado(AccessDeniedException ex) {
        return construir(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> manejarInesperado(Exception ex) {
        // Los errores propios de Spring MVC (ruta inexistente 404, método no permitido 405,
        // tipo de contenido 415...) ya traen su código HTTP: se respeta en vez de convertirlo en 500.
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatus status = HttpStatus.valueOf(errorResponse.getStatusCode().value());
            return construir(status, status.getReasonPhrase(), null);
        }
        log.error("Error inesperado", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado", null);
    }

    private ResponseEntity<ApiError> construir(HttpStatus status, String mensaje, Map<String, String> errores) {
        ApiError cuerpo = new ApiError(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensaje, errores);
        return ResponseEntity.status(status).body(cuerpo);
    }
}