package ryu.cloudstoragesystem_backend;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ryu.cloudstoragesystem_backend.auth.exception.LoginFailException;
import ryu.cloudstoragesystem_backend.auth.exception.TokenUnavailableException;
import ryu.cloudstoragesystem_backend.user.exception.UsernameConflictException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getBindingResult().toString());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseBody> handleExpiredJwtException(ExpiredJwtException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .body(new ErrorResponseBody("401", "Token expired"));
    }

    @ExceptionHandler(TokenUnavailableException.class)
    public ResponseEntity<ErrorResponseBody> handleTokenUnavailableException(TokenUnavailableException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .body(new ErrorResponseBody("401", "Token unavailable"));
    }

    @ExceptionHandler(UsernameConflictException.class)
    public ResponseEntity<ErrorResponseBody> handleUsernameConflictException(UsernameConflictException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .body(new ErrorResponseBody("409", "Username already exists"));
    }

    @ExceptionHandler(LoginFailException.class)
    public ResponseEntity<ErrorResponseBody> handleLoginFailException(LoginFailException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .body(new ErrorResponseBody("403", "Login fail"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseBody> handleUnknownException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .body(new ErrorResponseBody("500", "Unknown Error"));
    }
}
