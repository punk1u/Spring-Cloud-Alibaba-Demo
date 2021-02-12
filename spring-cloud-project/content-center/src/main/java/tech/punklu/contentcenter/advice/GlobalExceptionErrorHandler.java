package tech.punklu.contentcenter.advice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionErrorHandler {

    /**
     * 统一处理用户未登录的异常
     * @param e
     * @return
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorBody> error(SecurityException e){
        log.warn("发生SecurityException异常",e);
        ResponseEntity<ErrorBody> response = new ResponseEntity<ErrorBody>(
                ErrorBody.builder()
                .body("Token非法，用户不允许访问!~")
                .status(HttpStatus.UNAUTHORIZED.value())
                .build(),
                HttpStatus.UNAUTHORIZED
        );
        return response;
    }
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ErrorBody{
    private String body;
    private int status;
}
