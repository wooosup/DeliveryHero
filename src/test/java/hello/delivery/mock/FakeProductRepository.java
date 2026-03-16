package hello.delivery.mock;

import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductSellingStatus;
import hello.delivery.product.domain.ProductType;
import hello.delivery.product.service.port.ProductRepository;
import hello.delivery.store.domain.Store;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeProductRepository implements ProductRepository {

    private final AtomicLong autoIncrement = new AtomicLong(1);
    private final List<Product> data = new ArrayList<>();

    @Override
    public Optional<Product> findById(Long id) {
        return data.stream()
                .filter(product -> product.getId().equals(id))
                .findAny();
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            Product newProduct = Product.builder()
                    .id(autoIncrement.getAndIncrement())
                    .store(product.getStore())
                    .productType(product.getProductType())
                    .productSellingStatus(product.getProductSellingStatus())
                    .name(product.getName())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .build();
            data.add(newProduct);
            return newProduct;
        } else {
            data.removeIf(p -> p.getId().equals(product.getId()));
            data.add(product);
            return product;
        }
    }

    @Override
    public void deleteById(Long productId) {
        data.removeIf(product -> product.getId().equals(productId));
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        return products.stream()
                .map(this::save)
                .toList();
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public List<Product> findByProductType(Long id, ProductType type) {
        return data.stream()
                .filter(product -> product.getProductType().equals(type))
                .toList();
    }

    @Override
    public List<Product> findByProductSellingStatusIs(Long id, ProductSellingStatus status) {
        return data.stream()
                .filter(product -> product.getProductSellingStatus().equals(status))
                .toList();
    }

    @Override
    public Optional<Product> findByStoreAndNameWithLock(Store store, String name) {
        return data.stream()
                .filter(product -> product.getStore().getId().equals(store.getId()) &&
                        product.getName().equals(name))
                .findAny();
    }

    @Override
    public boolean existsByStoreAndName(Store store, String name) {
        return data.stream()
                .anyMatch(product -> product.getStore().getId().equals(store.getId()) &&
                        product.getName().equals(name));
    }

    @Override
    public List<Product> findByStore(Store store) {
        return data.stream()
                .filter(product -> product.getStore().getId().equals(store.getId()))
                .toList();
    }

    @Override
    public Optional<Product> findByIdWithLock(Long productId) {
        return findById(productId);
    }

}
