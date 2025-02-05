package ryu.cloudstoragesystem_backend.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Unavailable password")
public class PasswordUnavailableException extends RuntimeException {
    public PasswordUnavailableException() {
        super("Wrong password");
    }

    public PasswordUnavailableException(String message) {
        super(message);
    }
}
