package ryu.cloudstoragesystem_backend.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ryu.cloudstoragesystem_backend.auth.service.AuthService;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.user.exception.PasswordNotEmptyException;
import ryu.cloudstoragesystem_backend.user.exception.PasswordUnavailableException;
import ryu.cloudstoragesystem_backend.user.service.LoginService;
import ryu.cloudstoragesystem_backend.user.service.RegisterService;
import ryu.cloudstoragesystem_backend.user.service.UserService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final LoginService loginService;
    private final RegisterService registerService;
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public UserController(LoginService loginService, RegisterService registerService, AuthService authService, UserService userService) {
        this.loginService = loginService;
        this.registerService = registerService;
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestParam @NotBlank String username,
                                        @RequestParam @NotBlank String password,
                                        HttpServletResponse response) throws Exception {
        String token = registerService.register(username, password);
        response.setHeader("Authorization", token);
        Map<String, String> responseBody = new LinkedHashMap<>();
        responseBody.put("username", username);
        return responseBody;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam @NotBlank String username,
                                     @RequestParam @NotBlank String password,
                                     HttpServletResponse response) {
        String token = loginService.login(username, password);
        response.setHeader("Authorization", token);
        Map<String, String> responseBody = new LinkedHashMap<>();
        responseBody.put("username", username);
        return responseBody;
    }

    @PutMapping("/me/password")
    public void setPassword(@RequestHeader @NotBlank String token,
                            @RequestParam @NotBlank String password) {
        Long userId = authService.getPresentUser(token).getUserId();
        if (userService.isPasswordEmpty(userId)) {
            userService.setPassword(userId, password);
        } else throw new PasswordNotEmptyException();
    }

    @PostMapping("/me/name")
    public void updateName(@RequestHeader @NotBlank String token,
                           @RequestParam @NotBlank String name) {
        Long userId = authService.getPresentUser(token).getUserId();
        userService.setUsername(userId, name);
    }

    @PostMapping("/me/password")
    public void resetPassword(@RequestHeader @NotBlank String token,
                              @RequestParam @NotBlank String oldPassword,
                              @RequestParam @NotBlank String newPassword) {
        User user = authService.getPresentUser(token);
        if (user.getPassword().equals(oldPassword)) {
            userService.setPassword(user.getUserId(), newPassword);
        } else throw new PasswordUnavailableException();
    }
}
