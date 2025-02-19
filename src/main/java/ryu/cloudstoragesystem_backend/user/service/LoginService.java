package ryu.cloudstoragesystem_backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ryu.cloudstoragesystem_backend.auth.KeyPairProvider;
import ryu.cloudstoragesystem_backend.auth.TokenProvider;
import ryu.cloudstoragesystem_backend.auth.exception.LoginFailException;

@Service
public class LoginService {
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisStringTemplate;
    private final KeyPairProvider keyPairProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginService(TokenProvider tokenProvider, RedisTemplate<String, String> redisStringTemplate, KeyPairProvider keyPairProvider, AuthenticationManager authenticationManager) {
        this.tokenProvider = tokenProvider;
        this.redisStringTemplate = redisStringTemplate;
        this.keyPairProvider = keyPairProvider;
        this.authenticationManager = authenticationManager;
    }

    public String login(String username, String password) {
        String rawPassword = keyPairProvider.decrypt(password);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, rawPassword));
        } catch (AuthenticationException e) {
            throw new LoginFailException();
        }
        String token = tokenProvider.generateToken(username);
        redisStringTemplate.opsForValue().set(token, username);
        return token;
    }
}
