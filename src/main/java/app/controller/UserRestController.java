package app.controller;

import app.model.User;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    // Получение данных о текущем пользователе
    @GetMapping("/profile")
    public User getProfile(Principal principal) {
        return userService.findByEmail(principal.getName());
    }

    // Обновление профиля текущего пользователя
    @PutMapping("/profile")
    public User updateProfile(Principal principal, @RequestBody User updatedUser) {
        User currentUser = userService.findByEmail(principal.getName());
        currentUser.setName(updatedUser.getName());
        currentUser.setSurname(updatedUser.getSurname());
        currentUser.setAge(updatedUser.getAge());
        return userService.update(currentUser.getId(), currentUser);
    }
}
