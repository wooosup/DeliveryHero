package hello.delivery.user.controller.docs;

import hello.delivery.common.annotation.LoginUser;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.user.controller.request.Login;
import hello.delivery.user.controller.request.Signup;
import hello.delivery.user.controller.response.UserResponse;
import hello.delivery.user.controller.request.AddressUpdate;
import hello.delivery.user.controller.request.PasswordUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "사용자 (user, owner)")
public interface UserControllerDocs {

    @Operation(summary = "고객 회원가입", description = "고객의 권한으로 회원가입을 진행합니다.")
    ApiResponse<UserResponse> signupCustomer(@Valid @RequestBody Signup request);

    @Operation(summary = "사장님 회원가입", description = "사장의 권한으로 회원가입을 진행합니다.")
    ApiResponse<UserResponse> signupOwner(@Valid @RequestBody Signup request);

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인을 진행하고 세션을 생성합니다.")
    ApiResponse<UserResponse> login(@Valid @RequestBody Login request, HttpServletRequest httpServletRequest);

    @Operation(summary = "주소 변경", description = "로그인한 사용자의 주소를 변경합니다.")
    ApiResponse<UserResponse> changeAddress(
            @Parameter(hidden = true) @LoginUser Long userId,
            @Valid @RequestBody AddressUpdate request
    );

    @Operation(summary = "비밀번호 변경", description = "로그인한 사용자의 비밀번호를 변경합니다.")
    ApiResponse<UserResponse> changePassword(
            @Parameter(hidden = true) @LoginUser Long userId,
            @Valid @RequestBody PasswordUpdate request
    );
}
