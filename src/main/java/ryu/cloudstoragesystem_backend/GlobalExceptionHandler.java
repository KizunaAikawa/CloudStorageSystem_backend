package ryu.cloudstoragesystem_backend;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ryu.cloudstoragesystem_backend.auth.exception.LoginFailException;
import ryu.cloudstoragesystem_backend.auth.exception.TokenUnavailableException;
import ryu.cloudstoragesystem_backend.user.exception.UserNotExistException;
import ryu.cloudstoragesystem_backend.user.exception.UsernameConflictException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBody> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseBody("400","Bad request"));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseBody> handleMethodValidationException(HandlerMethodValidationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseBody("400","Bad request"));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseBody> handleExpiredJwtException(ExpiredJwtException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseBody("401", exception.getMessage()));
    }

    @ExceptionHandler(TokenUnavailableException.class)
    public ResponseEntity<ErrorResponseBody> handleTokenUnavailableException(TokenUnavailableException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseBody("401", exception.getMessage()));
    }

    @ExceptionHandler(UsernameConflictException.class)
    public ResponseEntity<ErrorResponseBody> handleUsernameConflictException(UsernameConflictException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseBody("409", exception.getMessage()));
    }

    @ExceptionHandler(LoginFailException.class)
    public ResponseEntity<ErrorResponseBody> handleLoginFailException(LoginFailException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseBody("403", exception.getMessage()));
    }

    @ExceptionHandler(UserNotExistException.class)
    public ResponseEntity<ErrorResponseBody> handleUserNotExistException(UserNotExistException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseBody("404", exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseBody> handleUnknownException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseBody("500", "Unknown Error"));
    }
}
