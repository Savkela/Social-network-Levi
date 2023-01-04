package com.levi9.socialnetwork.Exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public abstract class BaseExceptionHandler {
    private static final ExceptionMapping DEFAULT_ERROR = new ExceptionMapping(INTERNAL_SERVER_ERROR);

    private final Map<Class<?>, ExceptionMapping> exceptionMappings = new HashMap<>();

    public BaseExceptionHandler() {
        registerMapping(MissingServletRequestParameterException.class, BAD_REQUEST);
        registerMapping(MethodArgumentTypeMismatchException.class, BAD_REQUEST);
        registerMapping(HttpRequestMethodNotSupportedException.class, METHOD_NOT_ALLOWED);
        registerMapping(ServletRequestBindingException.class, BAD_REQUEST);
        registerMapping(BadRequestException.class, BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleThrowable(final Throwable ex, final HttpServletResponse response) {
        ExceptionMapping mapping = exceptionMappings.getOrDefault(ex.getClass(), DEFAULT_ERROR);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();

        return new ResponseEntity<>(new ErrorResponse(mapping.status, ex.getMessage(), stackTrace), mapping.status);
    }

    protected void registerMapping(final Class<?> clazz, final HttpStatus status) {
        exceptionMappings.put(clazz, new ExceptionMapping(status));
    }

    private static class ExceptionMapping {
        private final HttpStatus status;

        public ExceptionMapping(HttpStatus status) {
            super();
            this.status = status;
        }
    }

}