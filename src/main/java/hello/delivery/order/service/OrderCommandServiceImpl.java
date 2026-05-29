package hello.delivery.order.service;

import hello.delivery.common.exception.OrderNotFound;
import hello.delivery.common.exception.ProductException;
import hello.delivery.common.exception.ProductNotFound;
import hello.delivery.common.service.port.out.ClockHolder;
import hello.delivery.order.domain.Order;
import hello.delivery.order.domain.OrderProduct;
import hello.delivery.order.domain.event.OrderAcceptedEvent;
import hello.delivery.order.service.port.in.OrderCommandService;
import hello.delivery.order.service.port.in.OrderCreateCommand;
import hello.delivery.order.service.port.in.OrderProductCommand;
import hello.delivery.order.service.port.out.OrderEventPublisher;
import hello.delivery.order.service.port.out.OrderRepository;
import hello.delivery.product.domain.Product;
import hello.delivery.product.service.port.out.ProductRepository;
import hello.delivery.store.domain.Store;
import hello.delivery.store.service.port.in.StoreService;
import hello.delivery.store.service.port.out.StoreFinder;
import hello.delivery.user.domain.User;
import hello.delivery.user.service.port.out.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderCommandServiceImpl implements OrderCommandService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final StoreFinder storeFinder;
    private final UserFinder userFinder;
    private final ClockHolder clockHolder;
    private final OrderEventPublisher eventPublisher;

    public Order order(Long userId, OrderCreateCommand command) {
        User user = userFinder.findByUser(userId);
        Store store = storeFinder.findByStore(command.storeId());

        List<OrderProduct> orderProducts = createOrderProducts(store, command.orderProducts());
        Order order = Order.order(user, store, orderProducts, command.address(), clockHolder.nowDateTime());

        return orderRepository.save(order);
    }

    public Order accept(Long ownerId, Long orderId) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(OrderNotFound::new);

        order.validateOwner(ownerId);
        Order acceptedOrder = order.accept();

        Order savedOrder = orderRepository.save(acceptedOrder);
        storeService.addTotalSales(savedOrder.getStore().getId(), savedOrder.getTotalPrice().getAmount());

        eventPublisher.publish(new OrderAcceptedEvent(savedOrder.getId()));

        return savedOrder;
    }

    @Override
    public Order reject(Long ownerId, Long orderId) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(OrderNotFound::new);

        order.validateOwner(ownerId);
        Order rejectedOrder = order.reject();
        restoreStock(rejectedOrder);

        return orderRepository.save(rejectedOrder);
    }

    @Override
    public Order cancel(Long customerId, Long orderId) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(OrderNotFound::new);
        order.validateCustomer(customerId);
        Order cancelledOrder = order.cancel();
        restoreStock(cancelledOrder);

        return orderRepository.save(cancelledOrder);
    }

    @Override
    public Order complete(Long orderId) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(OrderNotFound::new);
        Order completedOrder = order.complete();

        return orderRepository.save(completedOrder);
    }

    private List<OrderProduct> createOrderProducts(Store store, List<OrderProductCommand> orderProducts) {
        return orderProducts.stream()
                .map(req -> createOrderProduct(store, req))
                .toList();
    }

    private OrderProduct createOrderProduct(Store store, OrderProductCommand command) {
        Product product = productRepository.findByIdWithLock(command.productId())
                .orElseThrow(ProductNotFound::new);
        validateProductBelongsToStore(store, product);

        Product decreasedProduct = product.decreaseStock(command.quantity());
        productRepository.save(decreasedProduct);

        return OrderProduct.create(decreasedProduct, command.quantity());
    }

    private void validateProductBelongsToStore(Store store, Product product) {
        if (!product.getStore().getId().equals(store.getId())) {
            throw new ProductException("주문한 가게의 상품만 주문할 수 있습니다.");
        }
    }

    private void restoreStock(Order order) {
        for (OrderProduct op : order.getOrderProducts()) {
            Product product = op.getProduct();
            Product restoredProduct = product.increaseStock(op.getQuantity());
            productRepository.save(restoredProduct);
        }
    }

}
