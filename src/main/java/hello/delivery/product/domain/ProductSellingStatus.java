package hello.delivery.product.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductSellingStatus {
    SELLING("판매중"),
    STOP_SELLING("판매중지"),
    SOLD_OUT("품절");

    private final String text;

    public ProductSellingStatus changeStatus(ProductSellingStatus status) {
        if (this == status) {
            throw new IllegalArgumentException("이미 " + status.text + " 상태입니다.");
        }
        return status;
    }
}
