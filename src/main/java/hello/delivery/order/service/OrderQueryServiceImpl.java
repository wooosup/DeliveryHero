package hello.delivery.order.service;

import hello.delivery.order.query.OrderQueryResult;
import hello.delivery.order.service.port.in.OrderQueryService;
import hello.delivery.order.service.port.out.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderQueryRepository orderQueryRepository;

    @Override
    public List<OrderQueryResult> findOrdersByUserId(Long userId) {
        return orderQueryRepository.findOrdersByUserId(userId);
    }

}
