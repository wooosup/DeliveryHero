package hello.delivery.user.service.port.in;

public record AddressUpdateCommand(
        String address
) {

    public static AddressUpdateCommand from(String address) {
        return new AddressUpdateCommand(address);
    }

}
