package hello.delivery.rider.service;

import static hello.delivery.rider.domain.RiderStatus.AVAILABLE;

import hello.delivery.common.exception.RiderException;
import hello.delivery.common.exception.RiderNotFound;
import hello.delivery.rider.service.port.in.RiderService;
import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.domain.RiderCreate;
import hello.delivery.rider.domain.RiderLogin;
import hello.delivery.rider.domain.RiderStatusUpdate;
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
    public Rider signup(RiderCreate request) {
        validate(request);
        Rider rider = Rider.signup(request);

        return riderRepository.save(rider);
    }

    @Transactional
    public Rider login(RiderLogin request) {
        Rider rider = riderRepository.findByPhone(request.getPhone())
                .orElseThrow(RiderNotFound::new);

        rider = rider.login(request);
        return riderRepository.save(rider);
    }

    @Transactional
    public Rider changeStatus(Long riderId, RiderStatusUpdate request) {
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(RiderNotFound::new);

        rider = rider.changeStatus(request);
        return riderRepository.save(rider);
    }

    public List<Rider> findAvailableRiders() {
        return riderRepository.findByStatus(AVAILABLE);
    }

    private void validate(RiderCreate request) {
        if (riderRepository.existsByPhone(request.getPhone())) {
            throw new RiderException("이미 등록된 전화번호입니다.");
        }
    }

}