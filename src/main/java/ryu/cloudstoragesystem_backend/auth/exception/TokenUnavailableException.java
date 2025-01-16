package ryu.cloudstoragesystem_backend.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Token unavailable")
public class TokenUnavailableException extends RuntimeException {
    public TokenUnavailableException() {
        super("Token unavailable");
    }
}
