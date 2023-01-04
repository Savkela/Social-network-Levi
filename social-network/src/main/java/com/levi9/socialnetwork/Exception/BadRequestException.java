package com.levi9.socialnetwork.Exception;

public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public BadRequestException(final String msg) {
        super(msg);
    }

}
