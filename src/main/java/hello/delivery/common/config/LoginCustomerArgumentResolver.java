package hello.delivery.common.config;

import static hello.delivery.common.config.AuthSessionAttributes.RIDER_ID;
import static hello.delivery.common.config.AuthSessionAttributes.USER_ID;
import static hello.delivery.common.config.AuthSessionAttributes.USER_ROLE;
import static hello.delivery.user.domain.UserRole.CUSTOMER;

import hello.delivery.common.annotation.LoginCustomerId;
import hello.delivery.common.exception.ForbiddenException;
import hello.delivery.common.exception.UnauthorizedException;
import hello.delivery.user.domain.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginCustomerArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginCustomerId.class)
                && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        HttpSession session = getSession(webRequest);
        if (session.getAttribute(RIDER_ID) != null) {
            throw new ForbiddenException("고객 권한이 필요합니다.");
        }

        Long userId = (Long) session.getAttribute(USER_ID);
        UserRole userRole = (UserRole) session.getAttribute(USER_ROLE);

        if (userId == null || userRole == null) {
            throw new UnauthorizedException("로그인을 해주세요.");
        }

        if (userRole != CUSTOMER) {
            throw new ForbiddenException("고객 권한이 필요합니다.");
        }

        return userId;
    }

    private HttpSession getSession(NativeWebRequest webRequest) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new UnauthorizedException("잘못된 요청입니다.");
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new UnauthorizedException("로그인을 해주세요.");
        }
        return session;
    }
}
