package hello.delivery.common.config;

import static hello.delivery.user.domain.UserRole.CUSTOMER;

import hello.delivery.common.annotation.LoginCustomerId;
import hello.delivery.common.exception.ForbiddenException;
import hello.delivery.user.domain.UserRole;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginCustomerArgumentResolver implements HandlerMethodArgumentResolver {

    private final SessionAuthExtractor sessionAuthExtractor;

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

        HttpSession session = sessionAuthExtractor.getRequiredSession(webRequest);
        if (sessionAuthExtractor.hasRiderSession(session)) {
            throw new ForbiddenException("고객 권한이 필요합니다.");
        }

        Long userId = sessionAuthExtractor.requireUserId(session);
        UserRole userRole = sessionAuthExtractor.requireUserRole(session);

        if (userRole != CUSTOMER) {
            throw new ForbiddenException("고객 권한이 필요합니다.");
        }

        return userId;
    }
}
