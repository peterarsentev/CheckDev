package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.site.dto.CredentialDTO;
import ru.job4j.site.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@AllArgsConstructor
@Slf4j
public class LoginControl {
    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            Model model) {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/",
                "Авторизация", "/login"
        );
        String errorMessage = null;
        if (error != null) {
            errorMessage = "Email or Password is incorrect !!";
        }
        model.addAttribute("authPing", authService.getPing());
        model.addAttribute("errorMessage", errorMessage);
        return "login";
    }

    @PostMapping("/signIn")
    public String signIn(@ModelAttribute CredentialDTO credentialDTO,
                         HttpServletRequest req) throws JsonProcessingException {
        var isLogin = authService.token(
                Map.of("username", credentialDTO.getEmail(),
                        "password", credentialDTO.getPassword()));
        if (isLogin.isEmpty()) {
            return "redirect:/login?error=true";
        }
        req.getSession().setAttribute("token", isLogin);
        return "redirect:/";
    }

    /**
     * Метод GET отображения страницы регистрации.
     *
     * @param model Model
     * @return String.
     */
    @GetMapping("/registration")
    public String registration(Model model) {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/",
                "Регистрация", "/registration"
        );
        return "registration";
    }

    /**
     * Метод Get очищает сессию и осуществляет выход пользователя.
     *
     * @param request HttpServletRequest
     * @return "/index"
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
