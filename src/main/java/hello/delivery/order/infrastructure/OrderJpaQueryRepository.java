package hello.delivery.order.infrastructure;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface OrderJpaQueryRepository extends Repository<OrderEntity, Long> {

    @Query("""
            select new hello.delivery.order.infrastructure.OrderQueryRow(
                o.id,
                u.id,
                u.name,
                s.id,
                o.totalPrice.amount,
                o.address.address,
                s.name,
                o.orderStatus,
                o.orderedAt,
                p.id,
                p.name,
                op.quantity,
                op.price.amount
            )
            from OrderEntity o
            join o.user u
            join o.store s
            join o.orderProducts op
            join op.product p
            where o.id = :id
            order by op.id asc
            """)
    List<OrderQueryRow> findRowsById(Long id);

    @Query("""
            select new hello.delivery.order.infrastructure.OrderQueryRow(
                o.id,
                u.id,
                u.name,
                s.id,
                o.totalPrice.amount,
                o.address.address,
                s.name,
                o.orderStatus,
                o.orderedAt,
                p.id,
                p.name,
                op.quantity,
                op.price.amount
            )
            from OrderEntity o
            join o.user u
            join o.store s
            join o.orderProducts op
            join op.product p
            where u.id = :userId
            order by o.orderedAt desc, o.id desc, op.id asc
            """)
    List<OrderQueryRow> findRowsByUserId(long userId);

}
