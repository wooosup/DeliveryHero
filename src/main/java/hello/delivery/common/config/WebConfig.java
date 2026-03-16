package hello.delivery.common.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginUserArgumentResolver loginUserArgumentResolver;
    private final LoginCustomerArgumentResolver loginCustomerArgumentResolver;
    private final LoginOwnerArgumentResolver loginOwnerArgumentResolver;
    private final LoginRiderArgumentResolver loginRiderArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
        resolvers.add(loginCustomerArgumentResolver);
        resolvers.add(loginOwnerArgumentResolver);
        resolvers.add(loginRiderArgumentResolver);
    }

}
