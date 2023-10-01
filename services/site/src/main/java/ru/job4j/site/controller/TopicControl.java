package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.site.dto.TopicDTO;
import ru.job4j.site.dto.TopicLiteDTO;
import ru.job4j.site.service.AuthService;
import ru.job4j.site.service.TopicsService;

import javax.servlet.http.HttpServletRequest;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

@Controller
@RequestMapping("/topic")
@AllArgsConstructor
public class TopicControl {
    private final TopicsService topicsService;
    private final AuthService authService;

    @GetMapping("/{categoryName}/{categoryId}/{topicId}")
    public String details(@PathVariable String categoryName,
                          @PathVariable int categoryId,
                          @PathVariable int topicId,
                          Model model,
                          HttpServletRequest req) throws JsonProcessingException {
        var token = getToken(req);
        var topic = new TopicDTO();
        if (token != null) {
            var userInfo = authService.userInfo(token);
            model.addAttribute("userInfo", token);
            RequestResponseTools.addAttrCanManage(model, userInfo);
            topic = topicsService.getById(topicId, token);
        }
        model.addAttribute("topic", topic);
        model.addAttribute("categoryName", categoryName);
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Направления", "/categories/",
                String.format("%s. Темы", categoryName),
                String.format("/topics/%s/%d", categoryName, categoryId),
                topic.getName(), String.format("/topic/%s/%d/%d", categoryName, categoryId, topicId));
        return "/topic/details";
    }

    @GetMapping("/createForm/{categoryId}")
    public String createForm(@PathVariable int categoryId, Model model, HttpServletRequest req)
            throws JsonProcessingException {
        var token = getToken(req);
        if (token != null) {
            var userInfo = authService.userInfo(token);
            model.addAttribute("userInfo", token);
            RequestResponseTools.addAttrCanManage(model, userInfo);
        }
        model.addAttribute("categoryId", categoryId);
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Направления", "/categories/",
                "Темы", String.format("/topics/%d", categoryId),
                "Создание темы", String.format("/topic/createForm/%d", categoryId));
        return "topic/createForm";
    }

    @PostMapping("/create")
    public String createTopic(@ModelAttribute TopicLiteDTO topic, HttpServletRequest req)
            throws JsonProcessingException {
        topicsService.create(getToken(req), topic);
        return "redirect:/categories/";
    }

    @GetMapping("/updateForm/{categoryName}/{categoryId}/{topicId}")
    public String updateForm(Model model,
                             HttpServletRequest req,
                             @PathVariable String categoryName,
                             @PathVariable int categoryId,
                             @PathVariable int topicId)
            throws JsonProcessingException {
        var topic = new TopicDTO();
        topic.setName("");
        var token = getToken(req);
        if (token != null) {
            var userInfo = authService.userInfo(token);
            topic = topicsService.getById(topicId, token);
            RequestResponseTools.addAttrCanManage(model, userInfo);
            model.addAttribute("userInfo", userInfo);
        }
        model.addAttribute("topic", topic);
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Направления", "/categories/",
                "Темы", String.format("/topics/%s/%d", categoryName, categoryId),
                "Редактировать тему",
                String.format("/topic/updateForm/%s/%d/%d", categoryName, categoryId, topicId));
        return "topic/updateForm";
    }

    @PostMapping("/update")
    public String updateTopic(TopicDTO topic, HttpServletRequest req) throws JsonProcessingException {
        topicsService.update(getToken(req), topic);
        return "redirect:/categories/";
    }

    @PostMapping("/delete")
    public String deleteTopic(@ModelAttribute(name = "id") int id, HttpServletRequest req)
            throws JsonProcessingException {
        topicsService.delete(getToken(req), id);
        return "redirect:/categories/";
    }
}
