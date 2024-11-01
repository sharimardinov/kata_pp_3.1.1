package app.controller;

import app.model.User;
import app.service.RoleService;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAnyRole('USER','ADMIN')") // Доступ только для пользователей
public class UserController {

    private final UserService userService;
    private final RoleService roleService; // Сервис ролей

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService; // Инициализация сервиса ролей
    }

    @GetMapping
    public String homepage(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userService.findByEmail(currentUsername);
        model.addAttribute("users", List.of(currentUser)); // Массив из одного пользователя
        return "user/users"; // Имя шаблона для списка пользователей
    }
}
