package org.cresplanex.nova.websocket.exception;

public abstract class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    abstract public AuthServerError getErrorCode();
}
