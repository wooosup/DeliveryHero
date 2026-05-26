package hello.delivery.product.service.port.out;

import hello.delivery.product.domain.Product;

public interface ProductFinder {

    Product findByProduct(Long id);

}
