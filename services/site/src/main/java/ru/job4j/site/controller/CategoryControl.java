package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.service.AuthService;
import ru.job4j.site.service.CategoriesService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryControl {
    private final CategoriesService categoriesService;
    private final AuthService authService;

    @GetMapping("/createForm")
    public String createForm(Model model, HttpServletRequest req) throws JsonProcessingException {
        var token = (String) req.getSession().getAttribute("token");
        var userInfo = authService.userInfo(token);
        model.addAttribute("userInfo", userInfo);
        var canManage = userInfo.getRoles().stream()
                .anyMatch(role -> role.getValue().equals("ROLE_ADMIN"));
        model.addAttribute("canManage", canManage);
        return "createForm";
    }

    @PostMapping("/")
    public String createCategory(@ModelAttribute CategoryDTO category, HttpServletRequest req) throws JsonProcessingException {
        var token = (String) req.getSession().getAttribute("token");
        categoriesService.create(token, category);
        return "redirect:/categories/";
    }
}
