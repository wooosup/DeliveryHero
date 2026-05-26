package hello.delivery.rider.service.port.in;

import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.domain.RiderStatus;
import java.util.List;

public interface RiderService {

    Rider signup(RiderCreateCommand request);

    Rider login(RiderLoginCommand request);

    Rider changeStatus(Long riderId, RiderStatus status);

    List<Rider> findAvailableRiders();

}
