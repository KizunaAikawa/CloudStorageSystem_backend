package ryu.cloudstoragesystem_backend.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Target file not exist")
public class UploadedFileNotFoundException extends RuntimeException {
    public UploadedFileNotFoundException() {
        super("Target file not exist");
    }

    public UploadedFileNotFoundException(String message) {
        super(message);
    }
}
