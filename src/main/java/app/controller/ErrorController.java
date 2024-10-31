package app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
public class ErrorController {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorController.class);

    @RequestMapping("/error")
    public String handleError() {
        System.out.println("Handling error...");
        return "error"; // Имя вашего шаблона ошибки
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        LOG.error("Ошибка: {}", e.getMessage(), e);
        return "error"; // Убедитесь, что этот шаблон существует
    }
}
