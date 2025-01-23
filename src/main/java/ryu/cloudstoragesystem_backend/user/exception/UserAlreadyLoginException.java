package ryu.cloudstoragesystem_backend.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "User already login ")
public class UserAlreadyLoginException extends RuntimeException {
    public UserAlreadyLoginException() {
        super("User already login");
    }

    public UserAlreadyLoginException(String message) {
        super(message);
    }
}
