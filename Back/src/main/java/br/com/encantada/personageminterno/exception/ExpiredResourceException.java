package br.com.encantada.personageminterno.exception;

public class ExpiredResourceException extends RuntimeException {
    
    public ExpiredResourceException(String message) {
        super(message);
    }
}
