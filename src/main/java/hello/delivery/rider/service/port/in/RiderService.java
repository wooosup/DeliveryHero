package hello.delivery.rider.service.port.in;

import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.domain.RiderCreate;
import hello.delivery.rider.domain.RiderLogin;
import hello.delivery.rider.domain.RiderStatusUpdate;
import java.util.List;

public interface RiderService {

    Rider signup(RiderCreate request);

    Rider login(RiderLogin request);

    Rider changeStatus(Long riderId, RiderStatusUpdate request);

    List<Rider> findAvailableRiders();
}
