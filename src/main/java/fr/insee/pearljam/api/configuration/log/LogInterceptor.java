package fr.insee.pearljam.api.configuration.log;

import fr.insee.pearljam.api.web.authentication.AuthenticationHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    private final AuthenticationHelper authenticationHelper;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        String fishTag = UUID.randomUUID().toString();
        String method = request.getMethod();
        String operationPath = request.getRequestURI();

        Authentication authentication = authenticationHelper.getAuthenticationPrincipal();

        String userId = authentication.getName();

        MDC.put("id", fishTag);
        MDC.put("path", operationPath);
        MDC.put("method", method);
        MDC.put("user", userId);

        log.info("[{}] {} {}", userId, method, operationPath);
        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler, @Nullable ModelAndView mv) {
        // no need to posthandle things for this interceptor
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception exception) {
        MDC.clear();
    }
}