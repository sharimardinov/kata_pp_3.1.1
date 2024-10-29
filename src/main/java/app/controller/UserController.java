package app.controller;

import app.model.Role;
import app.model.User;
import app.repository.RoleRepository;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model, Principal principal) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("isAdmin", userService.isAdmin(principal.getName()));
        return "list"; // Возвращаем одно и то же представление, но на основе роли будем управлять отображением
    }

    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "create";
        }
        // Установка ролей
        Role selectedRole = roleRepository.findByName(user.getRoles().iterator().next().getName());
        user.setRoles(new HashSet<>(Set.of(selectedRole)));
        // Сохранение пароля (необходимо хешировать пароль перед сохранением)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/edit")
    public String showEditUserForm(@RequestParam Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll()); // Список всех ролей
        return "edit";
    }

    @PostMapping("/edit")
    public String editUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "edit";
        }
        Role selectedRole = roleRepository.findByName(user.getRoles().iterator().next().getName());
        user.setRoles(new HashSet<>(Set.of(selectedRole)));

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userService.update(user.getId(), user);
        return "redirect:/users";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(id);
        return "redirect:/users";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        e.printStackTrace();
        return "error";
    }


}
