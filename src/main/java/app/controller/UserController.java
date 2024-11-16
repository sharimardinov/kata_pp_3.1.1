package app.controller;

import app.model.Role;
import app.model.User;
import app.service.RoleService;
import app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    public String homepage(Model model, Principal principal, HttpServletRequest request) {
        String email = principal.getName();
        User user = userService.findByEmail(email);
        String role = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("USER");

        model.addAttribute("userEmail", email);
        model.addAttribute("userRole", role);
        model.addAttribute("isAdmin", true);
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("requestURI", request.getRequestURI());

        return "userPage";
    }

}

