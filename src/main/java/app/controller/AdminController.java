package app.controller;

import app.model.Role;
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
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Главная страница админки со списком пользователей, формой создания и редактирования
    @GetMapping
    public String homepage(Model model, Principal principal) {
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

        // Новый пользователь для формы создания
        model.addAttribute("newUser", new User());

        return "admin/adminPage"; // Страница управления пользователями
    }

    // Создание нового пользователя
    @PostMapping("/create")
    public String createUser(@ModelAttribute("newUser") User user, Model model) {
        try {
            userService.save(user);
            return "redirect:/admin"; // Возврат на страницу списка пользователей
        } catch (RuntimeException e) {
            model.addAttribute("emailError", e.getMessage());
            model.addAttribute("roles", roleService.findAll());
            return "admin/adminPage"; // Остаемся на той же странице, чтобы отобразить ошибки
        }
    }

    // Показ формы редактирования (используется в модальном окне)
    @GetMapping("/edit")
    public String showEditUserForm(@RequestParam("id") Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user); // Передаем пользователя в форму редактирования
        model.addAttribute("roles", roleService.findAll()); // Передаем список ролей
        return "admin/edit"; // Возвращаем шаблон модального окна редактирования
    }
    // Обновление пользователя
    @PostMapping("/edit")
    public String editUser(@ModelAttribute("user") User user, Model model) {
        try {
            userService.update(user.getId(), user);
            return "redirect:/admin"; // Возврат на страницу списка пользователей
        } catch (RuntimeException e) {
            model.addAttribute("emailError", e.getMessage());
            model.addAttribute("roles", roleService.findAll());
            return "admin/adminPage"; // Остаемся на странице, чтобы отобразить ошибки
        }
    }

    // Удаление пользователя
    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin"; // Возврат на страницу списка пользователей
    }
}
