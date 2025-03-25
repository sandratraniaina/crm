package site.easy.to.build.crm.util.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    
    private ResponseUtil() {
        
    }
    
    public static <T> ResponseEntity<Response<T>> sendResponse(HttpStatus status, boolean success, String message, T data) {
        Response<T> response = new Response<>(status.value(), success, message, data);
        return new ResponseEntity<>(response, status);
    }
    
}