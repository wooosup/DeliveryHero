package hello.delivery.common.exception;

import static org.springframework.http.HttpStatus.*;

public class StockException extends DeliveryAppException{

    public StockException(String message) {
        super(message, BAD_REQUEST);
    }
}
