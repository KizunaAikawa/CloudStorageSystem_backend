package ryu.cloudstoragesystem_backend;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad request")
public class BadRequestParamException extends RuntimeException {
    public BadRequestParamException() {
        super("Bad request");
    }

    public BadRequestParamException(String message) {
        super(message);
    }
}
