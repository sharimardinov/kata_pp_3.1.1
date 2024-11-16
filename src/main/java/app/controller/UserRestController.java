package app.controller;

import app.model.User;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<User> getProfile(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        if (user != null) {
            return ResponseEntity.ok(user);  // Возвращаем статус 200 с данными пользователя
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Возвращаем 404, если пользователь не найден
        }
    }

    // Обновление профиля текущего пользователя
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(Principal principal, @RequestBody User updatedUser) {
        User currentUser = userService.findByEmail(principal.getName());
        if (currentUser != null) {
            currentUser.setName(updatedUser.getName());
            currentUser.setSurname(updatedUser.getSurname());
            currentUser.setAge(updatedUser.getAge());
            User updated = userService.update(currentUser.getId(), currentUser);
            return ResponseEntity.ok(updated);  // Возвращаем статус 200 с обновленными данными пользователя
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Возвращаем 404, если пользователь не найден
        }
    }
}
