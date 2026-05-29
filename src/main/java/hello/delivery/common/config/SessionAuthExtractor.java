package hello.delivery.common.config;

import static hello.delivery.common.config.AuthSessionAttributes.RIDER_ID;
import static hello.delivery.common.config.AuthSessionAttributes.USER_ID;
import static hello.delivery.common.config.AuthSessionAttributes.USER_ROLE;

import hello.delivery.common.exception.UnauthorizedException;
import hello.delivery.user.domain.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
public class SessionAuthExtractor {

    public HttpSession getRequiredSession(NativeWebRequest webRequest) {
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

    public Long requireUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            throw new UnauthorizedException("로그인을 해주세요.");
        }
        return userId;
    }

    public UserRole requireUserRole(HttpSession session) {
        UserRole userRole = (UserRole) session.getAttribute(USER_ROLE);
        if (userRole == null) {
            throw new UnauthorizedException("로그인을 해주세요.");
        }
        return userRole;
    }

    public Long requireRiderId(HttpSession session) {
        Long riderId = (Long) session.getAttribute(RIDER_ID);
        if (riderId == null) {
            throw new UnauthorizedException("로그인을 해주세요.");
        }
        return riderId;
    }

    public boolean hasUserSession(HttpSession session) {
        return session.getAttribute(USER_ID) != null;
    }

    public boolean hasRiderSession(HttpSession session) {
        return session.getAttribute(RIDER_ID) != null;
    }
}
