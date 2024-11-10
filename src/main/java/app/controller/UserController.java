package app.controller;

import app.model.Role;
import app.model.User;
import app.service.RoleService;
import app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public String homepage(Model model, Authentication authentication, Principal principal, HttpServletRequest request) {
        String email = principal.getName();
        User user = userService.findByEmail(email);
        String role = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("USER");

        // Добавляем основные атрибуты
        model.addAttribute("userEmail", email);
        model.addAttribute("userRole", role);
        model.addAttribute("isAdmin", true);
        model.addAttribute("roles", roleService.findAll());

        // Передаем пользователей для отображения в таблице
        List<User> users = userService.findAll();
        model.addAttribute("users", users);

        // Новый пользователь для формы
        model.addAttribute("newUser", new User());
        String currentUsername = authentication.getName();
        User currentUser = userService.findByEmail(currentUsername);
        model.addAttribute("users", List.of(currentUser));
        model.addAttribute("requestURI", request.getRequestURI());
        return "user/userPage"; // Имя шаблона для списка пользователей
    }
}
