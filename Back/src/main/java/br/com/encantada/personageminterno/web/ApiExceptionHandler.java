package br.com.encantada.personageminterno.web;

import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ConflictException;
import br.com.encantada.personageminterno.exception.ForbiddenException;
import br.com.encantada.personageminterno.exception.InvalidParameterException;
import br.com.encantada.personageminterno.exception.PreconditionFailedException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.exception.UnauthorizedException;
import br.com.encantada.personageminterno.web.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    // ==================== EXCEÇÕES CUSTOMIZADAS ====================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {
        log.warn("Recurso não encontrado: {} - Path: {}", exception.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException exception,
            HttpServletRequest request) {
        log.warn("Erro de negócio: {} - Path: {}", exception.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException exception,
            HttpServletRequest request) {
        log.warn("Conflito de dados: {} - Path: {}", exception.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException exception,
            HttpServletRequest request) {
        log.warn("Acesso negado (customizado): {} - Path: {}", exception.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException exception,
            HttpServletRequest request) {
        log.warn("Não autorizado: {} - Path: {}", exception.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(
            InvalidParameterException exception,
            HttpServletRequest request) {
        log.warn("Parâmetro inválido: {} - Path: {}", exception.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(PreconditionFailedException.class)
    public ResponseEntity<ErrorResponse> handlePreconditionFailed(
            PreconditionFailedException exception,
            HttpServletRequest request) {
        log.warn("Pré-condição falhou: {} - Path: {}", exception.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.PRECONDITION_FAILED, exception.getMessage(), request);
    }

    // ==================== EXCEÇÕES SPRING SECURITY ====================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException exception,
            HttpServletRequest request) {
        log.warn("Credenciais inválidas - Path: {}", request.getRequestURI());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request) {
        log.warn("Acesso negado (Spring Security): {} - Path: {}", exception.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este recurso", request);
    }

    // ==================== VALIDAÇÕES ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            fields.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Erro de validação - Path: {} - Campos: {}", request.getRequestURI(), fields.keySet());

        String correlationId = UUID.randomUUID().toString();
        
        ErrorResponse errorResponse = new ErrorResponse(
            OffsetDateTime.now(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Erro de validação",
            "Um ou mais campos estão inválidos",
            request.getRequestURI(),
            fields,
            correlationId
        );

        return ResponseEntity.unprocessableEntity()
                .header("X-Correlation-ID", correlationId)
                .body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request) {
        
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            fields.put(fieldName, message);
        });

        log.warn("Violação de constraint - Path: {} - Campos: {}", request.getRequestURI(), fields.keySet());

        String correlationId = UUID.randomUUID().toString();
        
        ErrorResponse errorResponse = new ErrorResponse(
            OffsetDateTime.now(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Violação de restrições",
            "Dados fornecidos não atendem às restrições",
            request.getRequestURI(),
            fields,
            correlationId
        );

        return ResponseEntity.unprocessableEntity()
                .header("X-Correlation-ID", correlationId)
                .body(errorResponse);
    }

    // ==================== REQUISIÇÕES MALFORMADAS ====================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        log.warn("JSON malformado - Path: {}", request.getRequestURI());
        return buildResponse(HttpStatus.BAD_REQUEST,
                "Requisição inválida. Verifique o formato dos dados enviados", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request) {
        log.warn("Método não suportado: {} - Path: {}",
                exception.getMethod(), request.getRequestURI());
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED,
                "Método HTTP '" + exception.getMethod() + "' não é suportado para esta rota", request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request) {
        log.warn("Tipo de mídia não suportado - Path: {}", request.getRequestURI());
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Tipo de mídia não suportado. Use 'application/json'", request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request) {
        log.warn("Parâmetro obrigatório ausente: {} - Path: {}",
                exception.getParameterName(), request.getRequestURI());
        return buildResponse(HttpStatus.BAD_REQUEST,
                "Parâmetro obrigatório ausente: " + exception.getParameterName(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {
        log.warn("Tipo de argumento inválido: {} - Path: {}",
                exception.getName(), request.getRequestURI());
        String message = String.format("Parâmetro '%s' deve ser do tipo %s",
                exception.getName(),
                exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName() : "válido");
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // ==================== BANCO DE DADOS ====================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {
        
        log.warn("Violação de integridade de dados - Path: {}", request.getRequestURI(), exception);

        String message = "Operação viola restrições de integridade do banco de dados";
        
        // Mensagens mais amigáveis para casos comuns
        String rootMessage = exception.getRootCause() != null 
            ? exception.getRootCause().getMessage() 
            : exception.getMessage();

        if (rootMessage != null) {
            if (rootMessage.contains("Duplicate entry") || rootMessage.contains("duplicate key")) {
                message = "Já existe um registro com estes dados";
            } else if (rootMessage.contains("foreign key constraint")) {
                message = "Não é possível realizar a operação pois existem dados relacionados";
            } else if (rootMessage.contains("not-null")) {
                message = "Campos obrigatórios não foram preenchidos";
            }
        }

        return buildResponse(HttpStatus.CONFLICT, message, request);
    }

    // ==================== ERRO GENÉRICO ====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request) {
        
        log.error("Erro inesperado - Path: " + request.getRequestURI(), exception);
        
        return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro interno do servidor. Contate o suporte.",
            request
        );
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request) {
        
        String correlationId = UUID.randomUUID().toString();
        
        ErrorResponse errorResponse = new ErrorResponse(
            OffsetDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI(),
            correlationId
        );

        return ResponseEntity.status(status)
                .header("X-Correlation-ID", correlationId)
                .body(errorResponse);
    }
}