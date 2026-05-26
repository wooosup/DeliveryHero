package hello.delivery.product.service;

import static hello.delivery.product.domain.ProductSellingStatus.SELLING;

import hello.delivery.common.exception.ProductException;
import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductSellingStatus;
import hello.delivery.product.domain.ProductType;
import hello.delivery.product.service.port.in.ProductCreateCommand;
import hello.delivery.product.service.port.in.ProductService;
import hello.delivery.product.service.port.out.ProductFinder;
import hello.delivery.product.service.port.out.ProductRepository;
import hello.delivery.store.domain.Store;
import hello.delivery.store.service.port.out.StoreFinder;
import hello.delivery.user.domain.User;
import hello.delivery.user.service.port.out.UserFinder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductFinder productFinder;
    private final StoreFinder storeFinder;
    private final UserFinder userFinder;

    @Transactional
    public Product create(Long userId, ProductCreateCommand command) {
        User owner = userFinder.findByUser(userId);
        owner.validateOwnerRole();

        Store store = storeFinder.findByStoreName(command.storeName());
        store.validateIsOwner(owner);

        validateProductDuplicate(store, command.name());

        Product product = Product.of(
                command.storeName(),
                command.name(),
                command.price(),
                command.type(),
                command.stock(),
                store,
                owner
        );
        return productRepository.save(product);
    }

    @Transactional
    public List<Product> creates(Long userId, List<ProductCreateCommand> commands) {
        validateList(commands);
        String storeName = commands.get(0).storeName();
        validateSameStore(commands, storeName);
        validateDuplicateNamesInRequest(commands);

        User owner = userFinder.findByUser(userId);
        owner.validateOwnerRole();
        Store store = storeFinder.findByStoreName(storeName);
        store.validateIsOwner(owner);

        for (ProductCreateCommand command : commands) {
            validateProductDuplicate(store, command.name());
        }

        List<Product> products = getProductList(store, owner, commands);
        return productRepository.saveAll(products);
    }

    @Transactional
    public Product changeSellingStatus(Long id, Long userId, ProductSellingStatus status) {
        Product product = productFinder.findByProduct(id);
        User owner = userFinder.findByUser(userId);

        owner.validateOwnerRole();
        product.validateOwner(owner.getId());

        product = product.changeSellingStatus(status);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteById(Long ownerId, Long productId) {
        Product product = productFinder.findByProduct(productId);
        User owner = userFinder.findByUser(ownerId);

        owner.validateOwnerRole();
        product.validateOwner(owner.getId());

        productRepository.deleteById(productId);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByType(Long storeId, ProductType type) {
        Store store = storeFinder.findByStore(storeId);
        return productRepository.findByProductType(store.getId(), type);
    }

    public List<Product> findBySelling(Long storeId) {
        Store store = storeFinder.findByStore(storeId);
        return productRepository.findByProductSellingStatusIs(store.getId(), SELLING);
    }

    public List<Product> findByStoreId(Long storeId) {
        Store store = storeFinder.findByStore(storeId);
        return productRepository.findByStore(store);
    }

    private void validateProductDuplicate(Store store, String name) {
        if (productRepository.existsByStoreAndName(store, name)) {
            throw new ProductException("이미 존재하는 상품입니다.");
        }
    }

    private static void validateDuplicateNamesInRequest(List<ProductCreateCommand> requests) {
        Set<String> names = new HashSet<>();
        boolean hasDuplicateName = requests.stream()
                .map(ProductCreateCommand::name)
                .filter(name -> name != null && !name.isBlank())
                .anyMatch(name -> !names.add(name));

        if (hasDuplicateName) {
            throw new ProductException("이미 존재하는 상품입니다.");
        }
    }

    private static void validateList(List<ProductCreateCommand> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ProductException("상품 목록이 비어 있습니다.");
        }
    }

    private static void validateSameStore(List<ProductCreateCommand> requests, String storeName) {
        boolean sameStore = requests.stream().allMatch(r -> storeName.equals(r.storeName()));
        if (!sameStore) {
            throw new ProductException("모든 상품은 동일한 매장에 속해야 합니다.");
        }
    }

    private static List<Product> getProductList(Store store, User owner, List<ProductCreateCommand> request) {
        return request.stream()
                .map(r -> Product.of(r.storeName(), r.name(), r.price(), r.type(), r.stock(), store, owner))
                .toList();
    }
}
