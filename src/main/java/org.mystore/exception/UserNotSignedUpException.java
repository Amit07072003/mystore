package org.mystore.exception;

public class UserNotSignedUpException extends RuntimeException {

    public UserNotSignedUpException(String message) {
        super(message);
    }
}
