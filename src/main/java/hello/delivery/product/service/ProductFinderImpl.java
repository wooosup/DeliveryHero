package hello.delivery.product.service;

import hello.delivery.common.exception.ProductNotFound;
import hello.delivery.product.domain.Product;
import hello.delivery.product.service.port.out.ProductFinder;
import hello.delivery.product.service.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFinderImpl implements ProductFinder {

    private final ProductRepository productRepository;

    @Override
    public Product findByProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(ProductNotFound::new);
    }

}
