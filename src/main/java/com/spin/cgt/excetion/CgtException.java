package com.spin.cgt.excetion;

public class CgtException extends RuntimeException {
    public CgtException(String text) {
        super(text);
    }

    public CgtException(Throwable e) {
        super(e);
    }
}
