package hello.delivery.common.config;

import static hello.delivery.user.domain.UserRole.OWNER;

import hello.delivery.common.annotation.LoginOwnerId;
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
public class LoginOwnerArgumentResolver implements HandlerMethodArgumentResolver {

    private final SessionAuthExtractor sessionAuthExtractor;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginOwnerId.class)
                && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        HttpSession session = sessionAuthExtractor.getRequiredSession(webRequest);
        if (sessionAuthExtractor.hasRiderSession(session)) {
            throw new ForbiddenException("사장 권한이 필요합니다.");
        }

        Long userId = sessionAuthExtractor.requireUserId(session);
        UserRole userRole = sessionAuthExtractor.requireUserRole(session);

        if (userRole != OWNER) {
            throw new ForbiddenException("사장 권한이 필요합니다.");
        }

        return userId;
    }
}
