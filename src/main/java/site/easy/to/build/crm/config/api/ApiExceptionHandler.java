package site.easy.to.build.crm.config.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import site.easy.to.build.crm.util.response.Response;
import site.easy.to.build.crm.util.response.ResponseUtil;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response<Object>> unauthorized(AuthenticationException e) {
        return ResponseUtil.sendResponse(HttpStatus.UNAUTHORIZED, false, "Request unauthorized", null);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseUtil.sendResponse(HttpStatus.BAD_REQUEST, false, "Request method not valid", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Object>> handleException(Exception ex) {
        return ResponseUtil.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, false, ex.getMessage(), null);
    }
}