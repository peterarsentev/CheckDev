package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.site.service.CategoriesService;
import ru.job4j.site.service.InterviewsService;
import java.util.ArrayList;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {
    private final CategoriesService categoriesService;
    private final InterviewsService interviewsService;

    @GetMapping({"/", "index"})
    public String getIndexPage(Model model) throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/"
        );
        try {
            model.addAttribute("categories", categoriesService.getMostPopular());
        } catch (Exception e) {
            model.addAttribute("categories", new ArrayList<>());
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }
        model.addAttribute("new_interviews", interviewsService.getByType(1));
        return "index";
    }
}