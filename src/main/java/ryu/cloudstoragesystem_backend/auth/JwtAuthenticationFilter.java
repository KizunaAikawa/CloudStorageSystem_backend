package ryu.cloudstoragesystem_backend.auth;

import ch.qos.logback.core.util.StringUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ryu.cloudstoragesystem_backend.auth.exception.TokenUnavailableException;
import ryu.cloudstoragesystem_backend.auth.service.AuthService;
import ryu.cloudstoragesystem_backend.auth.service.UserDetailsService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public JwtAuthenticationFilter(AuthService authService, UserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (StringUtil.isNullOrEmpty(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UserDetails user = userDetailsService.toUserDetails(authService.getPresentUser(token));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authentication.setDetails(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            handlerExceptionResolver.resolveException(request, response, null, new TokenUnavailableException("Expired JWT token"));
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, new TokenUnavailableException());
        }

    }
}
