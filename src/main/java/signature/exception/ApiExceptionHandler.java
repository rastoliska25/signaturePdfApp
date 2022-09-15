package signature.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestExceptionInternalServerError(ApiRequestException e) {

        HttpStatus internalServerError = HttpStatus.NOT_FOUND;

        ApiException apiException = new ApiException(
                e.getMessage(),
                e,
                internalServerError,
                ZonedDateTime.now()
        );

        return new ResponseEntity<>(apiException, internalServerError);
    }

    @ExceptionHandler({IOException.class})
    public String IOExceptionError(IOException e) {
        return "IOExceptionError: " + e;
    }
}
