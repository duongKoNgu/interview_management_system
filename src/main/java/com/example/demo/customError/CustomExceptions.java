package com.example.demo.customError;

public class CustomExceptions {

    public static class EmailRequiredException extends RuntimeException {
        public EmailRequiredException(String message) {
            super(message);
        }
    }

    public static class MailSendException extends RuntimeException {
        public MailSendException(String e) {super(e);}
    }
}
