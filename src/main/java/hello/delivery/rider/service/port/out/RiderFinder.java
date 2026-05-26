package hello.delivery.rider.service.port.out;

import hello.delivery.rider.domain.Rider;

public interface RiderFinder {

    Rider findByRider(Long id);

}
