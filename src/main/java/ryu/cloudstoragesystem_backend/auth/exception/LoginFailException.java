package ryu.cloudstoragesystem_backend.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Login fail")
public class LoginFailException extends RuntimeException {
    public LoginFailException() {
        super("Login fail");
    }
}
