package hello.delivery.common.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import hello.delivery.common.exception.DeliveryAppException;
import hello.delivery.common.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse invalidRequestHandler(MethodArgumentNotValidException e) {
        log.error("잘못된 요청: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return response;
    }

    @ExceptionHandler(DeliveryAppException.class)
    public ResponseEntity<ErrorResponse> deliveryException(DeliveryAppException e) {
        log.error("비즈니스 오류: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .code(String.valueOf(e.getStatus().value()))
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(response, e.getStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> dataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("데이터 무결성 위반: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of("409", "이미 존재하는 데이터입니다.");

        return new ResponseEntity<>(response, CONFLICT);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse generalServerError(Exception e) {
        log.error("서버 오류: {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .code("500")
                .message("서버에 오류가 발생했습니다.")
                .build();
    }
}
