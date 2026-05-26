package hello.delivery.user.service.port.in;

public record PasswordUpdateCommand(
        String password
) {

    public static PasswordUpdateCommand from(String password) {
        return new PasswordUpdateCommand(password);
    }

}
