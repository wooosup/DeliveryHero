package hello.delivery.rider.service;

import static hello.delivery.rider.domain.RiderStatus.AVAILABLE;

import hello.delivery.common.exception.RiderException;
import hello.delivery.common.exception.RiderNotFound;
import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.domain.RiderRegistration;
import hello.delivery.rider.domain.RiderStatus;
import hello.delivery.rider.service.port.in.RiderCreateCommand;
import hello.delivery.rider.service.port.in.RiderLoginCommand;
import hello.delivery.rider.service.port.in.RiderService;
import hello.delivery.rider.service.port.out.RiderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RiderServiceImpl implements RiderService {

    private final RiderRepository riderRepository;

    @Transactional
    public Rider signup(RiderCreateCommand command) {
        RiderRegistration registration = command.toRegistration();
        validate(registration);
        Rider rider = Rider.signup(registration);

        return riderRepository.save(rider);
    }

    @Transactional
    public Rider login(RiderLoginCommand command) {
        Rider rider = riderRepository.findByPhone(command.phone())
                .orElseThrow(RiderNotFound::new);

        rider = rider.login();
        return riderRepository.save(rider);
    }

    @Transactional
    public Rider changeStatus(Long riderId, RiderStatus status) {
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(RiderNotFound::new);

        rider = rider.changeStatus(status);
        return riderRepository.save(rider);
    }

    public List<Rider> findAvailableRiders() {
        return riderRepository.findByStatus(AVAILABLE);
    }

    private void validate(RiderRegistration registration) {
        if (riderRepository.existsByPhone(registration.phone())) {
            throw new RiderException("이미 등록된 전화번호입니다.");
        }
    }

}
