package app.controller;

import app.model.Role; // Импортируем класс Role
import app.model.User;
import app.service.RoleService;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Обеспечивает доступ только для администраторов
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String homepage(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("isAdmin", true);
        model.addAttribute("roles", roleService.findAll());
        return "admin/users";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("isAdmin", true);
        model.addAttribute("roles", roleService.findAll());
        return "admin/users"; // Имя шаблона для списка пользователей
    }


    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll()); // Передаем список ролей
        return "admin/create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute User user, Model model) {
        try {
            model.addAttribute("user", user);
            model.addAttribute("roles",  roleService.findAll()); // Передаем список ролей
            userService.save(user);
            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            model.addAttribute("emailError", e.getMessage());
            return "admin/create";
        }
    }


    @GetMapping("/edit")
    public String showEditUserForm(@RequestParam("id") Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles",  roleService.findAll()); // Передаем список ролей
        return "admin/edit"; // Имя шаблона для редактирования пользователя
    }


    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user, Model model) {
        try {
            userService.update(user.getId(), user);
            model.addAttribute("user", user);
            model.addAttribute("roles",  roleService.findAll()); // Передаем список ролей

            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            model.addAttribute("emailError", e.getMessage());
            model.addAttribute("user", user); // Возвращаем пользователя, чтобы заполнить форму
            return "admin/edit"; // Вернуться к форме редактирования с сообщением об ошибке
        }
    }


    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin/users"; // Перенаправление на список пользователей
    }
}
