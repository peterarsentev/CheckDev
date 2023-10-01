package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.site.service.CategoriesService;
import ru.job4j.site.service.TopicsService;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {
    private final CategoriesService categoriesService;
    private final TopicsService topicsService;

    @GetMapping({"/", "index"})
    public String getIndexPage(Model model) throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/",
                "Категории", "/categories/"
        );
        model.addAttribute("categories", categoriesService.getAllWithTopics(topicsService));
        return "index";
    }
}
