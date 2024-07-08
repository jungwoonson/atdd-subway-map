package subway.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.line.exception.NotExistLineException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotExistLineException.class)
    public ResponseEntity<String> handleNotExistLineException(NotExistLineException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
}