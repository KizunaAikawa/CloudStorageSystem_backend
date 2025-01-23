package ryu.cloudstoragesystem_backend;

import lombok.Data;

@Data
public class ErrorResponseBody {
    private String status;
    private String message;

    public ErrorResponseBody(String status, String message) {
        this.status = status;
        this.message = message;
    }

}
