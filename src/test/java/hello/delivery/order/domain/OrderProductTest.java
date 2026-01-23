package hello.delivery.order.domain;

import static org.assertj.core.api.Assertions.assertThat;

import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderProductTest {

    @Test
    @DisplayName("주문 상품을 생성할 수 있다.")
    void create() throws Exception {
        // given
        Product product = Product.builder()
                .id(1L)
                .store(null)
                .name("아메리카노")
                .price(5000)
                .build();

        // when
        OrderProduct orderProduct = OrderProduct.create(product, 2);

        // then
        assertThat(orderProduct.getProduct().getName()).isEqualTo("아메리카노");
    }

    @Test
    @DisplayName("재고가 있는 주문 상품을 생성할 수 있다.")
    void createWithStock() throws Exception {
        // given
        Product product = Product.builder()
                .id(1L)
                .store(null)
                .name("아메리카노")
                .price(5000)
                .stock(Stock.of(10))
                .build();

        // when
        OrderProduct orderProduct = OrderProduct.create(product, 2);

        // then
        assertThat(orderProduct.getProduct().getName()).isEqualTo("아메리카노");
    }

}