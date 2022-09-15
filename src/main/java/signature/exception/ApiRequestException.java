package signature.exception;

import java.io.IOException;

public class ApiRequestException extends RuntimeException{

    public ApiRequestException(String message) {
        super(message);
    }

    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiRequestException(Class<IOException> ioExceptionClass) {
    }
}
