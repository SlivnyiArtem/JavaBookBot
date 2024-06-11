package ru.urfu.bot.exception;

import java.util.NoSuchElementException;

public class DataNotFoundException extends NoSuchElementException {

    public DataNotFoundException() {
        super();
    }

    public DataNotFoundException(String s, Throwable cause) {
        super(s, cause);
    }

    public DataNotFoundException(Throwable cause) {
        super(cause);
    }

    public DataNotFoundException(String s) {
        super(s);
    }
}
