package com.levi9.socialnetwork.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ResourceExistsException extends Exception {
    private static final long serialVersionUID = 1L;

    public ResourceExistsException(String message){
        super(message);
    }
}
