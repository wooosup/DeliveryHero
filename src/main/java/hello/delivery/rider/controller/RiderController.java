package hello.delivery.rider.controller;

import static hello.delivery.common.config.AuthSessionAttributes.RIDER_ID;

import hello.delivery.common.annotation.LoginUser;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.rider.controller.docs.RiderControllerDocs;
import hello.delivery.rider.controller.port.RiderService;
import hello.delivery.rider.controller.response.RiderResponse;
import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.domain.RiderCreate;
import hello.delivery.rider.domain.RiderLogin;
import hello.delivery.rider.domain.RiderStatusUpdate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/riders")
@RequiredArgsConstructor
public class RiderController implements RiderControllerDocs {

    private final RiderService riderService;

    @Override
    @PostMapping("/new")
    public ApiResponse<RiderResponse> signup(@Valid @RequestBody RiderCreate request) {
        Rider rider = riderService.signup(request);
        return ApiResponse.ok(RiderResponse.of(rider));
    }

    @Override
    @PostMapping("/login")
    public ApiResponse<RiderResponse> login(@Valid @RequestBody RiderLogin request, HttpServletRequest httpServletRequest) {
        Rider rider = riderService.login(request);
        HttpSession currentSession = httpServletRequest.getSession(false);
        if (currentSession != null) {
            currentSession.invalidate();
        }

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(RIDER_ID, rider.getId());
        return ApiResponse.ok(RiderResponse.of(rider));
    }

    @Override
    @PatchMapping("/status")
    public ApiResponse<RiderResponse> changeStatus(@LoginUser Long riderId,
                                                   @Valid @RequestBody RiderStatusUpdate request) {
        Rider rider = riderService.changeStatus(riderId, request);
        return ApiResponse.ok(RiderResponse.of(rider));
    }

    @Override
    @GetMapping("/available")
    public ApiResponse<List<RiderResponse>> getAvailableRiders() {
        List<Rider> riders = riderService.findAvailableRiders();
        return ApiResponse.ok(RiderResponse.of(riders));
    }

}
