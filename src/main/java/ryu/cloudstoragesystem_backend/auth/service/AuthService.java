package ryu.cloudstoragesystem_backend.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ryu.cloudstoragesystem_backend.auth.TokenProvider;
import ryu.cloudstoragesystem_backend.auth.exception.TokenUnavailableException;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.user.UserDAO;

import java.util.Optional;

@Service
public class AuthService {
    private final UserDAO userDAO;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisStringTemplate;

    @Autowired
    public AuthService(UserDAO userDAO, TokenProvider tokenProvider, RedisTemplate<String, String> redisStringTemplate) {
        this.userDAO = userDAO;
        this.tokenProvider = tokenProvider;
        this.redisStringTemplate = redisStringTemplate;
    }

    public User getPresentUser(String token) {
        String username = tokenProvider.getUsernameByToken(token);
        validateTokenInRedis(username, token);
        return userDAO.findByUsername(username).orElseThrow(TokenUnavailableException::new);
    }

    public String validateTokenInRedis(String username, String token) {
        Optional<String> tokenCache = Optional.ofNullable(redisStringTemplate.opsForValue().get(username));
        if (tokenCache.isPresent()) {
            if (token.equals(tokenCache.get())) {
                return token;
            }
        }
        throw new TokenUnavailableException();
    }
}
