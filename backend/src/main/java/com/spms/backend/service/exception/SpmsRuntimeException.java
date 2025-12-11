package com.spms.backend.service.exception;

public class SpmsRuntimeException extends RuntimeException{

    public SpmsRuntimeException(String message,Exception ex) {
        super(message, ex);
    }
}
