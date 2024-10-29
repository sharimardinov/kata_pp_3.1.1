package app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MVCController {

    private static final Logger LOG = LoggerFactory.getLogger(MVCController.class);

    @GetMapping(value = "/login")
    public String login(@RequestParam(value = "logout", required = false) String logout,
                        Model model,
                        HttpServletRequest request) {
        LOG.info("/login");
        LOG.info("Return login");

        // Получение CSRF токена
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "Вы вышли из системы.");
        }
        return "login"; // Возвращает имя вашего шаблона для входа
    }
}
