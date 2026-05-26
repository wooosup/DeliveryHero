package hello.delivery.rider.service.port.in;

import hello.delivery.rider.domain.RiderRegistration;

public record RiderCreateCommand(
        String name,
        String phone
) {

    public static RiderCreateCommand of(String name, String phone) {
        return new RiderCreateCommand(name, phone);
    }

    public RiderRegistration toRegistration() {
        return new RiderRegistration(name, phone);
    }

}
