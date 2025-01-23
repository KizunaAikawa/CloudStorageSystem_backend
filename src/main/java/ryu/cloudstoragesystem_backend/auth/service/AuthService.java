package ryu.cloudstoragesystem_backend.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ryu.cloudstoragesystem_backend.auth.TokenProvider;
import ryu.cloudstoragesystem_backend.auth.exception.TokenUnavailableException;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.user.UserDAO;

@Service
public class AuthService {
    private final UserDAO userDAO;
    private final TokenProvider tokenProvider;

    @Autowired
    public AuthService(UserDAO userDAO, TokenProvider tokenProvider) {
        this.userDAO = userDAO;
        this.tokenProvider = tokenProvider;
    }

    public User getPresentUser(String token) {
        String username = tokenProvider.getUsernameByToken(token);
        return userDAO.findByUsername(username).orElseThrow(TokenUnavailableException::new);
    }
}
