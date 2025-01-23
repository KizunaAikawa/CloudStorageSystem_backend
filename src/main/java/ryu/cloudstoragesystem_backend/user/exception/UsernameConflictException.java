package ryu.cloudstoragesystem_backend.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Username already exist")
public class UsernameConflictException extends RuntimeException {
    public UsernameConflictException() {
        super("Username already exist");
    }

    public UsernameConflictException(String message) {
        super(message);
    }
}
