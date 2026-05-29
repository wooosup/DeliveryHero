package hello.delivery.common.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;

import hello.delivery.common.exception.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

class ExceptionControllerTest {

    private final ExceptionController exceptionController = new ExceptionController();

    @Test
    @DisplayName("데이터 무결성 위반은 409 응답으로 변환한다.")
    void dataIntegrityViolationException() {
        // when
        ResponseEntity<ErrorResponse> response =
                exceptionController.dataIntegrityViolationException(new DataIntegrityViolationException("duplicate"));

        // then
        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("409");
    }
}
