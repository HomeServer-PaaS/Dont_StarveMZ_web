package kr.co.guljima.portal.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("portal_jwt".equals(cookie.getName())) {
                        Long userId = jwtService.subjectFrom(cookie.getValue());
                        if (userId != null) {
                            var authentication = new UsernamePasswordAuthenticationToken(
                                Long.toString(userId),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                            );
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                        break;
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
