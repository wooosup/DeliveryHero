package hello.delivery.rider.controller.docs;

import hello.delivery.common.annotation.LoginRiderId;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.rider.controller.request.RiderCreate;
import hello.delivery.rider.controller.request.RiderLogin;
import hello.delivery.rider.controller.request.RiderStatusUpdate;
import hello.delivery.rider.controller.response.RiderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "라이더")
public interface RiderControllerDocs {

    @Operation(summary = "라이더 회원가입", description = "새로운 라이더를 등록합니다.")
    ApiResponse<RiderResponse> signup(@Valid @RequestBody RiderCreate request);

    @Operation(summary = "라이더 로그인", description = "전화번호로 라이더 로그인을 처리하고 세션을 생성합니다.")
    ApiResponse<RiderResponse> login(
            @Valid @RequestBody RiderLogin request,
            @Parameter(hidden = true) HttpServletRequest httpServletRequest);

    @Operation(summary = "라이더 상태 변경", description = "로그인한 라이더의 상태를 변경합니다.")
    ApiResponse<RiderResponse> changeStatus(
            @Parameter(hidden = true) @LoginRiderId Long riderId,
            @Valid @RequestBody RiderStatusUpdate request);

    @Operation(summary = "배달 가능 라이더 조회", description = "현재 배달 가능한 라이더 목록을 조회합니다.")
    ApiResponse<List<RiderResponse>> getAvailableRiders();

}
