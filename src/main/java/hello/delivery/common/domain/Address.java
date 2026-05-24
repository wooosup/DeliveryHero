package hello.delivery.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;

    @Builder
    private Address(@JsonProperty("address") String address) {
        this.address = address;
    }

    public static Address of(String address) {
        return Address.builder()
                .address(address)
                .build();
    }

}
