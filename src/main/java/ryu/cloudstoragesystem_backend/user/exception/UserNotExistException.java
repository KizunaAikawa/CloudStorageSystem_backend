package ryu.cloudstoragesystem_backend.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not exist")
public class UserNotExistException extends RuntimeException {
    public UserNotExistException() {
        super("User not exist");
    }

    public UserNotExistException(String message) {
        super(message);
    }
}
