package br.com.encantada.personageminterno.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fields,
        String correlationId
) {
    public ErrorResponse(OffsetDateTime timestamp, int status, String error, String message, String path) {
        this(timestamp, status, error, message, path, null, null);
    }
    
    public ErrorResponse(OffsetDateTime timestamp, int status, String error, String message, String path, String correlationId) {
        this(timestamp, status, error, message, path, null, correlationId);
    }
}