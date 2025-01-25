package ryu.cloudstoragesystem_backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ryu.cloudstoragesystem_backend.auth.TokenProvider;
import ryu.cloudstoragesystem_backend.auth.exception.LoginFailException;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.user.UserDAO;
import ryu.cloudstoragesystem_backend.user.exception.UserNotExistException;

import java.util.concurrent.TimeUnit;

@Service
public class LoginService {
    private final UserDAO userDAO;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisStringTemplate;

    @Autowired
    public LoginService(UserDAO userDAO, TokenProvider tokenProvider, RedisTemplate<String, String> redisStringTemplate) {
        this.userDAO = userDAO;
        this.tokenProvider = tokenProvider;
        this.redisStringTemplate = redisStringTemplate;
    }

    public String login(String username, String password) {
        User user = userDAO.findByUsername(username).orElseThrow(UserNotExistException::new);
        if (user.getPassword().equals(password)) {
            String token = tokenProvider.generateToken(user);
            redisStringTemplate.opsForValue().set(username, token, tokenProvider.getTokenExpiration(), TimeUnit.MILLISECONDS);
            return token;
        } else throw new LoginFailException();
    }
}
