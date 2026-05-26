package hello.delivery.rider.service.port.in;

public record RiderLoginCommand(
        String phone
) {

    public static RiderLoginCommand of(String phone) {
        return new RiderLoginCommand(phone);
    }

}
