package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.job4j.site.dto.UserDTO;
import ru.job4j.site.service.NotificationService;

import javax.servlet.http.HttpServletRequest;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

@Controller
@AllArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/subscribeCategory")
    public String createSubscribeCategory(@ModelAttribute("userDTO")UserDTO user,
                                            @ModelAttribute("categoryId")int categoryId,
                                            HttpServletRequest req)
            throws JsonProcessingException {
        notificationService.addSubscribeCategory(getToken(req), user.getId(), categoryId);
        return "redirect:/categories/";
    }

    @PostMapping("/unSubscribeCategory")
    public String deleteSubscribeCategory(@ModelAttribute("userInfo")UserDTO user,
                                          @ModelAttribute("categoryId")int categoryId,
                                          HttpServletRequest req)
            throws JsonProcessingException {
        notificationService.deleteSubscribeCategory(getToken(req), user.getId(), categoryId);
        return "redirect:/categories/";
    }

    @PostMapping("/subscribeCategoryFromIndex")
    public String createSubscribeCategoryFromIndex(@ModelAttribute("userDTO")UserDTO user,
                                          @ModelAttribute("categoryId")int categoryId,
                                          HttpServletRequest req)
            throws JsonProcessingException {
        notificationService.addSubscribeCategory(getToken(req), user.getId(), categoryId);
        return "redirect:/index/";
    }

    @PostMapping("/unSubscribeCategoryFromIndex")
    public String deleteSubscribeCategoryFromIndex(@ModelAttribute("userInfo")UserDTO user,
                                          @ModelAttribute("categoryId")int categoryId,
                                          HttpServletRequest req)
            throws JsonProcessingException {
        notificationService.deleteSubscribeCategory(getToken(req), user.getId(), categoryId);
        return "redirect:/index/";
    }
}