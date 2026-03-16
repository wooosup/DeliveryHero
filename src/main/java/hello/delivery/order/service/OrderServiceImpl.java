package hello.delivery.order.service;

import hello.delivery.common.exception.OrderNotFound;
import hello.delivery.common.exception.ProductNotFound;
import hello.delivery.common.service.port.ClockHolder;
import hello.delivery.common.service.port.FinderPort;
import hello.delivery.delivery.controller.port.DeliveryService;
import hello.delivery.order.controller.port.OrderService;
import hello.delivery.order.domain.Order;
import hello.delivery.order.domain.OrderCreate;
import hello.delivery.order.domain.OrderProduct;
import hello.delivery.order.domain.OrderProductRequest;
import hello.delivery.order.service.port.OrderRepository;
import hello.delivery.product.domain.Product;
import hello.delivery.product.service.port.ProductRepository;
import hello.delivery.store.controller.port.StoreService;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final DeliveryService deliveryService;
    private final FinderPort finder;
    private final ClockHolder clockHolder;

    public Order order(Long userId, OrderCreate request) {
        User user = finder.findByUser(userId);
        Store store = finder.findByStoreName(request.getStoreName());

        List<OrderProduct> orderProducts = createOrderProducts(store, request.getOrderProducts());
        Order order = Order.order(user, store, orderProducts, request.getAddress(), clockHolder.nowDateTime());

        return orderRepository.save(order);
    }

    public Order accept(Long ownerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFound::new);

        order.validateOwner(ownerId);
        Order acceptedOrder = order.accept();

        storeService.addTotalSales(acceptedOrder.getStore().getId(), acceptedOrder.getTotalPrice());
        deliveryService.createDeliveryForOrder(acceptedOrder);

        return orderRepository.save(acceptedOrder);
    }

    @Override
    public Order cancel(Long customerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFound::new);
        order.validateCustomer(customerId);
        Order cancelledOrder = order.cancel();

        for (OrderProduct op : cancelledOrder.getOrderProducts()) {
            Product product = op.getProduct();
            Product restoredProduct = product.increaseStock(op.getQuantity());
            productRepository.save(restoredProduct);
        }

        return orderRepository.save(cancelledOrder);
    }

    @Override
    public Order complete(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFound::new);
        Order completedOrder = order.complete();

        return orderRepository.save(completedOrder);
    }

    @Transactional(readOnly = true)
    public List<Order> findOrdersByUserId(Long userId) {
        User user = finder.findByUser(userId);
        return orderRepository.findOrdersByUserId(user.getId());
    }

    private List<OrderProduct> createOrderProducts(Store store, List<OrderProductRequest> orderProducts) {
        return orderProducts.stream()
                .map(req -> createOrderProduct(store, req))
                .toList();
    }

    private OrderProduct createOrderProduct(Store store, OrderProductRequest request) {
        Product product = productRepository.findByStoreAndNameWithLock(store, request.getProductName())
                .orElseThrow(ProductNotFound::new);

        Product decreasedProduct = product.decreaseStock(request.getQuantity());
        productRepository.save(decreasedProduct);

        return OrderProduct.create(decreasedProduct, request.getQuantity());
    }

}
