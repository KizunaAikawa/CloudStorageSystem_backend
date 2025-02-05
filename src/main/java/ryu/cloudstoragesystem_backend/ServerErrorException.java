package ryu.cloudstoragesystem_backend;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Sever error")
public class ServerErrorException extends RuntimeException {
    public ServerErrorException() {
        super("Server error");
    }

    public ServerErrorException(String message) {
        super(message);
    }
}
