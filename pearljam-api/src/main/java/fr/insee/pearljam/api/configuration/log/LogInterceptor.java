package fr.insee.pearljam.api.configuration.log;

import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final AuthenticatedUserService authenticationHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        injectLogContext(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) {
        // no need to posthandle things for this interceptor
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception exception) throws Exception {
        clearLogContext();
    }

    public void injectLogContext(HttpServletRequest request) {
        injectLogContext(request, authenticationHelper.getCurrentUserId());
    }

    public void injectLogContext(HttpServletRequest request, String userId) {
        String fishTag = UUID.randomUUID().toString();
        String method = request.getMethod();
        String operationPath = request.getRequestURI();

        MDC.put("id", fishTag);
        MDC.put("path", operationPath);
        MDC.put("method", method);
        MDC.put("user", userId.toUpperCase());

        log.info("[{}] - [{}] - [{}]", userId.toUpperCase(), method, operationPath);
    }

    public void clearLogContext() {
        MDC.clear();
    }
}