package tradeService.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignHeaderConfig {

    @Bean
    public RequestInterceptor headerForwardingInterceptor() {
        return (RequestTemplate template) -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String email = request.getHeader("X-User-Email");
                String role = request.getHeader("X-User-Role");
                if (email != null) {
                    template.header("X-User-Email", email);
                }
                if (role != null) {
                    template.header("X-User-Role", role);
                }
            }
        };
    }
}