package br.com.kumabe.handson02.exceptions;

public class ServicoExternoIndisponivelException extends RuntimeException {
    public ServicoExternoIndisponivelException(String message) {
        super(message);
    }

    public ServicoExternoIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
}

