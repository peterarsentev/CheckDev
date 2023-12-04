package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.site.component.safety.StringShieldXSSInspector;
import ru.job4j.site.dto.CredentialDTO;
import ru.job4j.site.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@Slf4j
public class LoginControl {
    private final AuthService authService;
    private final StringShieldXSSInspector stringShieldXSSInspector = new StringShieldXSSInspector();

    @Value("${botUserName}")
    private String botUserName;

    public LoginControl(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(@ModelAttribute("redirectUri") String redirectUri,
                            @ModelAttribute("topicId") String topicId,
                            @RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "interviewId", required = false) String interviewId,
                            Model model) {
        topicId = stringShieldXSSInspector.defuse(topicId);
        error = stringShieldXSSInspector.defuse(error);
        interviewId = stringShieldXSSInspector.defuse(interviewId);
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/",
                "Авторизация", "/login"
        );
        String errorMessage = null;
        if (error != null) {
            errorMessage = "Email или пароль введены неверно!";
        }
        model.addAttribute("authPing", authService.getPing());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("redirectUri", redirectUri);
        model.addAttribute("topicId", topicId);
        model.addAttribute("interviewId", interviewId);
        return "login";
    }

    @PostMapping("/signIn")
    public String signIn(@ModelAttribute CredentialDTO credentialDTO,
                         @ModelAttribute("redirectUri") String redirectUri,
                         @ModelAttribute("topicId") String topicId,
                         @ModelAttribute("interviewId") String interviewId,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest req) throws JsonProcessingException {
        topicId = stringShieldXSSInspector.defuse(topicId);
        interviewId = stringShieldXSSInspector.defuse(interviewId);
        var isLogin = authService.token(
                Map.of("username", credentialDTO.getEmail(),
                        "password", credentialDTO.getPassword()));
        if (isLogin.isEmpty()) {
            return "redirect:/login?error=true";
        }
        req.getSession().setAttribute("token", isLogin);
        if (!redirectUri.isEmpty()) {
            if (!topicId.isEmpty()) {
                redirectAttributes.addFlashAttribute("topicId", topicId);
            } else if (!interviewId.isEmpty()) {
                redirectAttributes.addFlashAttribute("interviewId", interviewId);
            }
            return "redirect:" + redirectUri;
        }
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
        model.addAttribute("botUserName", botUserName);
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
