package hello.delivery.rider.infrastructure;

import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.domain.RiderStatus;
import hello.delivery.rider.service.port.out.RiderRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RiderRepositoryImpl implements RiderRepository {

    private final RiderJpaRepository riderJpaRepository;

    @Override
    public Rider save(Rider rider) {
        return riderJpaRepository.save(RiderEntity.of(rider)).toDomain();
    }

    @Override
    public Optional<Rider> findById(Long id) {
        return riderJpaRepository.findById(id).map(RiderEntity::toDomain);
    }

    @Override
    public List<Rider> findByStatus(RiderStatus riderStatus) {
        return riderJpaRepository.findByStatus(riderStatus)
                .stream()
                .map(RiderEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Rider> findByPhone(String phone) {
        return riderJpaRepository.findByPhone(phone).map(RiderEntity::toDomain);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return riderJpaRepository.existsByPhone(phone);
    }
}
