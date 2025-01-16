package ryu.cloudstoragesystem_backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.user.UserDAO;
import ryu.cloudstoragesystem_backend.user.exception.UserNotExistException;

@Service
public class UserService {
    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setPassword(Long id, String password) {
        User user = userDAO.findById(id).orElseThrow(UserNotExistException::new);
        user.setPassword(password);
        userDAO.save(user);
    }

    public String setUsername(Long id, String username) {
        User user = userDAO.findById(id).orElseThrow(UserNotExistException::new);
        user.setUsername(username);
        userDAO.save(user);
        return username;
    }

    public boolean isPasswordEmpty(Long id) {
        User user = userDAO.findById(id).orElseThrow(UserNotExistException::new);
        return user.getPassword().isEmpty();
    }
}
