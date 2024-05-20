package com.mourtzounis.cards.exception;

public class ExpiredCredentialsException extends RuntimeException {

    public ExpiredCredentialsException(String message) {
        super(message);
    }
}
