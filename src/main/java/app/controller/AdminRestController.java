package app.controller;

import app.model.Role;
import app.model.User;
import app.service.RoleService;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRestController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Получение списка всех пользователей
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);  // Возвращаем OK статус с пользователями
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }


    // Получение пользователя по ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return ResponseEntity.ok(user);  // Возвращаем OK статус с пользователем
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Возвращаем статус 404, если пользователь не найден
        }
    }

    // Создание нового пользователя
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);  // Возвращаем статус 201 и созданного пользователя
    }

    // Обновление существующего пользователя
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.update(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);  // Возвращаем OK статус с обновленным пользователем
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Возвращаем статус 404, если пользователь не найден
        }
    }

    // удаление юзера
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.delete(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();  // Возвращаем статус 204, если пользователь был удален
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Возвращаем статус 404, если пользователь не найден
        }
    }
}
