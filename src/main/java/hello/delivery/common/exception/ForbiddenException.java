package hello.delivery.common.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class ForbiddenException extends DeliveryAppException {

    public ForbiddenException(String message) {
        super(message, FORBIDDEN);
    }
}
