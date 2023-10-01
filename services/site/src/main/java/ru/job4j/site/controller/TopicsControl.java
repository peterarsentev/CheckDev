package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.site.service.AuthService;
import ru.job4j.site.service.CategoriesService;
import ru.job4j.site.service.TopicsService;

import javax.servlet.http.HttpServletRequest;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

@Controller
@RequestMapping("/topics")
@AllArgsConstructor
public class TopicsControl {
    private final TopicsService topicsService;
    private final AuthService authService;
    private final CategoriesService categoriesService;

    @GetMapping("/{categoryName}/{categoryId}")
    public String getByCategory(@PathVariable String categoryName,
                                @PathVariable int categoryId,
                                Model model,
                                HttpServletRequest req) throws JsonProcessingException {
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("topics", topicsService.getByCategory(categoryId));
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Направления", "/categories/",
                String.format("%s. Темы", categoryName),
                String.format("/topics/%s/%d", categoryName, categoryId));
        var token = getToken(req);
        if (token != null) {
            var userInfo = authService.userInfo(token);
            model.addAttribute("userInfo", userInfo);
            RequestResponseTools.addAttrCanManage(model, userInfo);
            categoriesService.updateStatistic(token, categoryId);
        }
        return "topic/topics";
    }
}