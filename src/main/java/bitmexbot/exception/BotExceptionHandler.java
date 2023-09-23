package bitmexbot.exception;

import feign.FeignException;
import jakarta.validation.UnexpectedTypeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BotExceptionHandler {

    @ExceptionHandler(BotNotFoundException.class)
    protected ResponseEntity<String> botNotFound(BotNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ValidationErrorException.class)
    protected ResponseEntity<String> validationError(ValidationErrorException ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
    }

    @ExceptionHandler(BotNotCreated.class)
    protected ResponseEntity<String> validationError(BotNotCreated ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<String> handleUnexpectedTypeException(UnexpectedTypeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleUnexpectedTypeException(FeignException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleUnexpectedTypeException(MethodArgumentNotValidException ex) {
        String defaultMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(defaultMessage, httpHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
}
