package hello.delivery.product.service;

import static hello.delivery.product.domain.ProductSellingStatus.SELLING;

import hello.delivery.common.exception.ProductException;
import hello.delivery.common.service.port.FinderPort;
import hello.delivery.product.controller.port.ProductService;
import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductCreate;
import hello.delivery.product.domain.ProductSellingStatus;
import hello.delivery.product.domain.ProductType;
import hello.delivery.product.service.port.ProductRepository;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FinderPort finder;

    @Transactional
    public Product create(Long userId, ProductCreate request) {
        User owner = finder.findByUser(userId);
        owner.validateOwnerRole();

        Store store = finder.findByStoreName(request.getStoreName());
        store.validateIsOwner(owner);

        validateProductDuplicate(store, request.getName());

        Product product = Product.of(request, store, owner);
        return productRepository.save(product);
    }

    @Transactional
    public List<Product> creates(Long userId, List<ProductCreate> requests) {
        validateList(requests);
        String storeName = requests.get(0).getStoreName();
        validateSameStore(requests, storeName);

        User owner = finder.findByUser(userId);
        owner.validateOwnerRole();
        Store store = finder.findByStoreName(storeName);
        store.validateIsOwner(owner);


        for (ProductCreate request : requests) {
            validateProductDuplicate(store, request.getName());
        }

        List<Product> products = getProductList(store, owner, requests);
        return productRepository.saveAll(products);
    }

    @Transactional
    public Product changeSellingStatus(Long id, Long userId, ProductSellingStatus status) {
        Product product = finder.findByProduct(id);
        User owner = finder.findByUser(userId);

        owner.validateOwnerRole();
        product.validateOwner(owner.getId());

        product = product.changeSellingStatus(status);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteById(Long ownerId, Long productId) {
        Product product = finder.findByProduct(productId);
        User owner = finder.findByUser(ownerId);

        owner.validateOwnerRole();
        product.validateOwner(owner.getId());

        productRepository.deleteById(productId);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByType(Long storeId, ProductType type) {
        Store store = finder.findByStore(storeId);
        return productRepository.findByProductType(store.getId(), type);
    }

    public List<Product> findBySelling(Long storeId) {
        Store store = finder.findByStore(storeId);
        return productRepository.findByProductSellingStatusIs(store.getId(), SELLING);
    }

    public List<Product> findByStoreId(Long storeId) {
        Store store = finder.findByStore(storeId);
        return productRepository.findByStore(store);
    }

    private void validateProductDuplicate(Store store, String name) {
        if (productRepository.existsByStoreAndName(store, name)) {
            throw new ProductException("이미 존재하는 상품입니다.");
        }
    }

    private static void validateList(List<ProductCreate> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ProductException("상품 목록이 비어 있습니다.");
        }
    }

    private static void validateSameStore(List<ProductCreate> requests, String storeName) {
        boolean sameStore = requests.stream().allMatch(r -> storeName.equals(r.getStoreName()));
        if (!sameStore) {
            throw new ProductException("모든 상품은 동일한 매장에 속해야 합니다.");
        }
    }

    private static List<Product> getProductList(Store store, User owner, List<ProductCreate> request) {
        return request.stream()
                .map(r -> Product.of(r, store, owner))
                .toList();
    }
}
