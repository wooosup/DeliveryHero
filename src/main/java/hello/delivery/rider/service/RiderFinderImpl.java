package hello.delivery.rider.service;

import hello.delivery.common.exception.RiderNotFound;
import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.service.port.out.RiderFinder;
import hello.delivery.rider.service.port.out.RiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RiderFinderImpl implements RiderFinder {

    private final RiderRepository riderRepository;

    @Override
    public Rider findByRider(Long id) {
        return riderRepository.findById(id)
                .orElseThrow(RiderNotFound::new);
    }

}
