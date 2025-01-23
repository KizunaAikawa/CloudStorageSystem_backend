package ryu.cloudstoragesystem_backend.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Password not empty")
public class PasswordNotEmptyException extends RuntimeException {
    public PasswordNotEmptyException() {
        super("Password is empty");
    }

    public PasswordNotEmptyException(String message) {
        super(message);
    }
}
