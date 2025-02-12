package ryu.cloudstoragesystem_backend.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Empty file")
public class EmptyFileException extends RuntimeException {
    public EmptyFileException() {
        super("Empty file");
    }
    public EmptyFileException(String message) {
        super(message);
    }
}
