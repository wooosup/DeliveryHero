package hello.delivery.mock;

import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.domain.RiderStatus;
import hello.delivery.rider.service.port.out.RiderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeRiderRepository implements RiderRepository {

    private final AtomicLong autoIncrement = new AtomicLong(1);
    private final List<Rider> data = new ArrayList<>();

    @Override
    public Rider save(Rider rider) {
        if (rider.getId() == null) {
            Rider newRider = Rider.builder()
                    .id(autoIncrement.getAndIncrement())
                    .name(rider.getName())
                    .phone(rider.getPhone())
                    .status(rider.getStatus())
                    .build();
            data.add(newRider);
            return newRider;
        } else {
            data.removeIf(r -> r.getId().equals(rider.getId()));
            data.add(rider);
            return rider;
        }
    }

    @Override
    public Optional<Rider> findById(Long id) {
        return data.stream()
                .filter(rider -> rider.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Rider> findByStatus(RiderStatus riderStatus) {
        return data.stream()
                .filter(rider -> rider.getStatus() == riderStatus)
                .toList();
    }

    @Override
    public Optional<Rider> findByPhone(String phone) {
        return data.stream()
                .filter(rider -> rider.getPhone().equals(phone))
                .findAny();
    }

    @Override
    public boolean existsByPhone(String phone) {
        return data.stream()
                .anyMatch(rider -> rider.getPhone().equals(phone));
    }

}
