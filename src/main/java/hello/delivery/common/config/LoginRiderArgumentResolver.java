package hello.delivery.common.config;

import static hello.delivery.common.config.AuthSessionAttributes.RIDER_ID;
import static hello.delivery.common.config.AuthSessionAttributes.USER_ID;

import hello.delivery.common.annotation.LoginRiderId;
import hello.delivery.common.exception.ForbiddenException;
import hello.delivery.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginRiderArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginRiderId.class)
                && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        HttpSession session = getSession(webRequest);
        if (session.getAttribute(USER_ID) != null) {
            throw new ForbiddenException("라이더 권한이 필요합니다.");
        }

        Long riderId = (Long) session.getAttribute(RIDER_ID);
        if (riderId == null) {
            throw new UnauthorizedException("로그인을 해주세요.");
        }

        return riderId;
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
