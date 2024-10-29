package app.controller;

import app.model.User;
import app.service.RoleService;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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
    public String homepage(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("isAdmin", false); // Для отображения администраторских функций
        model.addAttribute("roles", roleService.findAll()); // Передаем список ролей
        return "user/users"; // Имя шаблона для списка пользователей
    }


    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("isAdmin", false); // Для отображения администраторских функций
        model.addAttribute("roles", roleService.findAll()); // Передаем список ролей

        return "user/users"; // Имя шаблона для списка пользователей
    }
}
