package ryu.cloudstoragesystem_backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ryu.cloudstoragesystem_backend.auth.TokenProvider;
import ryu.cloudstoragesystem_backend.auth.exception.LoginFailException;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.user.UserDAO;
import ryu.cloudstoragesystem_backend.user.exception.UserNotExistException;

@Service
public class LoginService {
    //private final TokenDAO tokenDAO;
    private final UserDAO userDAO;
    private final TokenProvider tokenProvider;

    @Autowired
    public LoginService(UserDAO userDAO, TokenProvider tokenProvider) {
        this.userDAO = userDAO;
        this.tokenProvider = tokenProvider;
    }

    public String login(String username, String password) {
        User user = userDAO.findByUsername(username).orElseThrow(UserNotExistException::new);
        if (user.getPassword().equals(password)) {
            return tokenProvider.generateToken(user);
        } else throw new LoginFailException();
    }
}
