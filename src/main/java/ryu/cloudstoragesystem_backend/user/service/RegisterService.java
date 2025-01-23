package ryu.cloudstoragesystem_backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ryu.cloudstoragesystem_backend.auth.TokenProvider;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.user.UserDAO;
import ryu.cloudstoragesystem_backend.user.exception.UsernameConflictException;

@Service
public class RegisterService {
    //private final TokenDAO tokenDAO;
    private final UserDAO userDAO;
    private final TokenProvider tokenProvider;

    @Autowired
    public RegisterService(UserDAO userDAO, TokenProvider tokenProvider) {
        this.userDAO = userDAO;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public String register(String username, String password) {
        if (!userDAO.existsByUsername(username)) {
            User user = new User(username, password);
            userDAO.save(user);
            return tokenProvider.generateToken(user);
        } else throw new UsernameConflictException();
    }

    @Transactional
    public String register(User user) {
        return register(user.getUsername(), user.getPassword());
    }
}
