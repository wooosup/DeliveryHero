package hello.delivery.rider.controller.request;

import hello.delivery.rider.domain.RiderStatus;
import jakarta.validation.constraints.NotNull;

public record RiderStatusUpdate(
        @NotNull
        RiderStatus status
) {
}
