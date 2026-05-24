package hello.delivery.rider.service.port.out;

import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.domain.RiderStatus;
import java.util.List;
import java.util.Optional;

public interface RiderRepository {

    Rider save(Rider rider);

    Optional<Rider> findById(Long id);

    List<Rider> findByStatus(RiderStatus riderStatus);

    Optional<Rider> findByPhone(String phone);

    boolean existsByPhone(String phone);
}
