package ru.job4j.site.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.site.dto.FeedbackDTO;
import ru.job4j.site.service.FeedbackService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * FeedbackController контроллер обработки отзывов
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 25.10.2023
 */
@Controller
@RequestMapping("/interview")
@AllArgsConstructor
public class FeedbackController {

    private FeedbackService feedbackService;

    /**
     * Отображение формы для сохранения отзыв на собеседование.
     *
     * @param param Map<String, String>
     * @param model Model
     * @return String page.
     */
    @GetMapping("/feedback/")
    public String getFeedbackForm(@RequestParam Map<String, String> param, Model model) {
        var interviewId = Integer.parseInt(param.get("interviewId"));
        var submitterId = Integer.parseInt(param.get("submitterId"));
        var userId = Integer.parseInt(param.get("userId"));
        var mode = Integer.parseInt(param.get("mode"));
        int roleInInterview = mode;
        if (userId != submitterId) {
            roleInInterview = (mode == 1) ? 2 : 1;
        }
        var feedback = new FeedbackDTO();
        feedback.setInterviewId(interviewId);
        feedback.setUserId(userId);
        feedback.setRoleInInterview(roleInInterview);
        model.addAttribute("feedback", feedback);
        return "/interview/feedbackForm";
    }

    /**
     * Метод пост сохраняет новый комментарий.
     *
     * @param feedbackDTO FeedbackDTO
     * @param request     HttpServletRequest
     * @return String page.
     */
    @PostMapping("/createFeedback")
    public String saveFeedback(@ModelAttribute FeedbackDTO feedbackDTO,
                               HttpServletRequest request) {
        var token = RequestResponseTools.getToken(request);
        feedbackService.save(token, feedbackDTO);
        return "redirect:/interview/" + feedbackDTO.getInterviewId();
    }
}
