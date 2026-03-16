package hello.delivery.user.controller;

import static hello.delivery.common.config.AuthSessionAttributes.USER_ID;
import static hello.delivery.common.config.AuthSessionAttributes.USER_ROLE;

import hello.delivery.common.annotation.LoginUser;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.user.controller.docs.UserControllerDocs;
import hello.delivery.user.controller.port.UserService;
import hello.delivery.user.controller.response.UserResponse;
import hello.delivery.user.domain.AddressUpdate;
import hello.delivery.user.domain.Login;
import hello.delivery.user.domain.PasswordUpdate;
import hello.delivery.user.domain.User;
import hello.delivery.user.domain.UserCreate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @Override
    @PostMapping("/signup")
    public ApiResponse<UserResponse> signupCustomer(@Valid @RequestBody UserCreate request) {
        User user = userService.signupCustomer(request);
        return ApiResponse.ok(UserResponse.of(user));
    }

    @Override
    @PostMapping("/owners/signup")
    public ApiResponse<UserResponse> signupOwner(@Valid @RequestBody UserCreate request) {
        User user = userService.signupOwner(request);
        return ApiResponse.ok(UserResponse.of(user));
    }

    @Override
    @PostMapping("/login")
    public ApiResponse<UserResponse> login(@Valid @RequestBody Login request, HttpServletRequest httpServletRequest) {
        User user = userService.login(request);
        HttpSession currentSession = httpServletRequest.getSession(false);
        if (currentSession != null) {
            currentSession.invalidate();
        }

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(USER_ID, user.getId());
        session.setAttribute(USER_ROLE, user.getRole());

        return ApiResponse.ok(UserResponse.of(user));
    }

    @Override
    @PatchMapping("/address")
    public ApiResponse<UserResponse> changeAddress(@LoginUser Long userId, @Valid @RequestBody AddressUpdate request) {
        User user = userService.changeAddress(userId, request);
        return ApiResponse.ok(UserResponse.of(user));
    }

    @Override
    @PatchMapping("/password")
    public ApiResponse<UserResponse> changePassword(@LoginUser Long userId, @Valid @RequestBody PasswordUpdate request) {
        User user = userService.changePassword(userId, request);
        return ApiResponse.ok(UserResponse.of(user));
    }
}
