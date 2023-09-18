package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.site.domain.Breadcrumb;
import ru.job4j.site.service.AuthService;
import ru.job4j.site.service.BreadcrumbsService;
import ru.job4j.site.service.CategoriesService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoriesControl {
    private final CategoriesService categoriesService;
    private final AuthService authService;
    private final BreadcrumbsService breadcrumbsService;

    @GetMapping("/")
    public String categories(Model model, HttpServletRequest req) throws JsonProcessingException {
        var session = req.getSession();
        var token = (String) session.getAttribute("token");
        model.addAttribute("categories", categoriesService.getAll(token));
        var userInfo = authService.userInfo(token);
        model.addAttribute("userInfo", userInfo);
        var canManage = userInfo.getRoles().stream()
                .anyMatch(role -> role.getValue().equals("ROLE_ADMIN"));
        model.addAttribute("canManage", canManage);
        breadcrumbsService.addBreadCrumb(session, new Breadcrumb("Направления", "/categories/"));
        return "categories";
    }
}
