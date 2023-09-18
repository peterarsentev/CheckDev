package ru.job4j.site.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.site.domain.Breadcrumb;
import ru.job4j.site.service.BreadcrumbsService;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
public class IndexController {

    private final BreadcrumbsService breadcrumbsService;

    @GetMapping({"/", "index"})
    public String getIndexPage(@RequestParam(value = "error", required = false) String error,
                               Model model,
                               HttpSession session) {
        String errorMessage = null;
        if (error != null) {
            errorMessage = "Email or Password is incorrect !!";
        }
        model.addAttribute("errorMessage", errorMessage);
        breadcrumbsService.addBreadCrumb(session, new Breadcrumb("Главная", "/index"));
        return "index";
    }
}
