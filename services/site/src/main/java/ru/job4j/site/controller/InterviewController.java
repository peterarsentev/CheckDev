package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.site.component.safety.InterviewDtoXSSInspector;
import ru.job4j.site.dto.*;
import ru.job4j.site.enums.StatusInterview;
import ru.job4j.site.service.*;

import javax.servlet.http.HttpServletRequest;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

@Controller
@RequestMapping("/interview")
@AllArgsConstructor
@Slf4j
public class InterviewController {

    private final AuthService authService;
    private final TopicsService topicsService;
    private final InterviewService interviewService;
    private final InterviewsService interviewsService;
    private final WisherService wisherService;
    private final NotificationService notifications;
    private final FeedbackService feedbackService;
    private final InterviewDtoXSSInspector interviewXSSInspector;

    @GetMapping("/createForm")
    public String createForm(@ModelAttribute("topicId") int topicId,
                             Model model,
                             HttpServletRequest request)
            throws JsonProcessingException {
        var token = getToken(request);
        if (token != null) {
            var userInfo = authService.userInfo(token);
            var interviewsNoFeedback = interviewsService.findAllIdByNoFeedback(userInfo.getId());
            model.addAttribute("noFeedback", interviewsNoFeedback);
            model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token, userInfo.getId()));
        }
        var topic = topicsService.getById(topicId);
        var categoryName = topic.getCategory().getName();
        int categoryId = topic.getCategory().getId();
        model.addAttribute("category", topic.getCategory());
        model.addAttribute("topic", topic);
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Категории", "/categories/",
                categoryName, String.format("/topics/%d", categoryId),
                topic.getName(), String.format("/topic/%d", topicId),
                "Создание собеседования", String.format("/interview/createForm?topicId=%d", topicId));
        return "interview/createForm";
    }

    @PostMapping("/create")
    public String createInterview(@ModelAttribute InterviewDTO interviewDTO,
                                  @ModelAttribute("topicId") int topicId,
                                  HttpServletRequest req)
            throws JsonProcessingException {
        var token = getToken(req);
        if (token != null) {
            var userInfo = authService.userInfo(token);
            interviewDTO.setSubmitterId(userInfo.getId());
            interviewDTO.setAuthor(userInfo.getUsername());
        }
        interviewDTO.setTopicId(topicId);
        interviewXSSInspector.defuse(interviewDTO);
        InterviewDTO createInterview = interviewService.create(getToken(req), interviewDTO);
        var categoryIdName = topicsService.getCategoryIdNameDTOByTopicId(topicId);
        var topicName = topicsService.getNameById(topicId);
        var categoryWithTopicDTO = new CategoryWithTopicDTO(
                categoryIdName.getId(), categoryIdName.getName(),
                topicId, topicName, createInterview.getId(), interviewDTO.getSubmitterId());
        notifications.notifyAboutInterviewCreation(token,
                categoryWithTopicDTO);
        var interviewNotifiDTO = InterviewNotifiDTO.of()
                .id(interviewDTO.getId())
                .submitterId(interviewDTO.getSubmitterId())
                .title(interviewDTO.getTitle())
                .topicId(interviewDTO.getTopicId())
                .topicName(topicName)
                .categoryId(categoryIdName.getId())
                .categoryName(categoryIdName.getName())
                .build();
        notifications.sendSubscribeTopic(token, interviewNotifiDTO);
        return "redirect:/interview/" + createInterview.getId();
    }

    @GetMapping("/{interviewId}")
    public String details(@PathVariable("interviewId") int interviewId,
                          Model model,
                          HttpServletRequest req) throws JsonProcessingException {
        var token = getToken(req);
        var interview = interviewService.getById(token, interviewId);
        var userInfo = authService.userInfo(token);
        var isAuthor = interviewService.isAuthor(userInfo, interview);
        var wishers = wisherService.getAllWisherDtoByInterviewId(token, String.valueOf(interview.getId()));
        var isWisher = wisherService.isWisher(userInfo.getId(), interview.getId(), wishers);
        var statisticMap = wisherService.getInterviewStatistic(wishers);
        var wishersDetail = interviewService.getAllWisherDetail(wishers);
        boolean isDismissed = wisherService.isDismissed(interviewId, wishers);
        var topicLiteDTO = topicsService.getTopicLiteDTOById(interview.getTopicId()).orElse(new TopicLiteDTO());
        boolean isUserDismissed = wisherService.isUserDismissed(interviewId, userInfo.getId(), wishers);
        var feedbacks = feedbackService.findByInterviewId(interview.getId());
        var feedbackMap = feedbackService.feedbackDTOSToMap(feedbacks);
        model.addAttribute("authService", authService);
        model.addAttribute("interview", interview);
        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("isWisher", isWisher);
        model.addAttribute("statisticMap", statisticMap);
        model.addAttribute("STATUS_IN_PROGRESS_ID", StatusInterview.IN_PROGRESS.getId());
        model.addAttribute("STATUS_IS_FEEDBACK_ID", StatusInterview.IS_FEEDBACK.getId());
        model.addAttribute("wishersDetail", wishersDetail);
        model.addAttribute("isDismissed", isDismissed);
        model.addAttribute("topicLiteDTO", topicLiteDTO);
        model.addAttribute("isUserDismissed", isUserDismissed);
        model.addAttribute("feedbackMap", feedbackMap);
        if (token != null) {
            model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token, userInfo.getId()));
        }

        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Собеседования", "/interviews/",
                String.format("%s : %s", topicLiteDTO.getCategoryName(), topicLiteDTO.getName()),
                String.format("/interview/%d", interviewId));
        return "interview/details";
    }

    /**
     * Метод отображает страницу редактирования интервью (собеседования).
     * Для редактирования собеседования авторизованный пользователь должен быть создателем интервью(собеседования)
     *
     * @param interviewId int
     * @param model       Model
     * @param request     HttpServletRequest
     * @return String view page
     */
    @GetMapping("/edit/{id}")
    public String getEditView(@PathVariable("id") int interviewId,
                              Model model,
                              HttpServletRequest request) {
        var token = getToken(request);
        InterviewDTO interview;
        UserInfoDTO userInfoDTO;
        try {
            userInfoDTO = authService.userInfo(token);
            interview = interviewService.getById(token, interviewId);
            if (interview.getSubmitterId() != userInfoDTO.getId()) {
                return "redirect:/interview/" + interviewId;
            }
        } catch (Exception e) {
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
            return "redirect:/interviews/";
        }
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Собеседования", "/interviews/",
                interview.getTitle(), String.format("/interview/edit/%d", interviewId));
        model.addAttribute("interview", interview);
        if (token != null) {
            model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token,
                    userInfoDTO.getId()));
        }
        return "interview/interviewEdit";
    }

    /**
     * Метод обновления собеседования
     *
     * @param interview InterviewDTO.class
     * @param request   HttpServletRequest
     * @return String redirect page
     */
    @PostMapping("/update")
    public String postUpdateInterview(@ModelAttribute InterviewDTO interview,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        var token = getToken(request);
        try {
            interviewService.update(token, interviewXSSInspector.defuse(interview));
        } catch (Exception e) {
            log.error("Remote application not responding. Error, {}. {}, ", e.getCause(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Собеседование не обновлено");
            return "redirect:/interview/edit/" + interview.getId();
        }
        return "redirect:/interview/" + interview.getId();
    }

    @GetMapping("/{interviewId}/participate")
    public String participate(@PathVariable("interviewId") int interviewId,
                              Model model,
                              HttpServletRequest req) throws JsonProcessingException {
        var token = getToken(req);
        var userInfoDTO = authService.userInfo(token);
        var interview = interviewService.getById(token, interviewId);
        if (token != null) {
            model.addAttribute("botMessages", notifications.findBotMessageByUserId(token, userInfoDTO.getId()));
        }
        var result = "interview/participate";
        if (userInfoDTO != null && interview.getSubmitterId() != userInfoDTO.getId()) {
            var wishers = wisherService.getAllWisherDtoByInterviewId(token, String.valueOf(interview.getId()));
            var isWisher = wisherService.isWisher(userInfoDTO.getId(), interview.getId(), wishers);
            var statisticMap = wisherService.getInterviewStatistic(wishers);
            var countWishers = wisherService.countWishers(wishers, interviewId);
            model.addAttribute("interview", interview);
            model.addAttribute("isWisher", isWisher);
            model.addAttribute("statisticMap", statisticMap);
            model.addAttribute("countWishers", countWishers);
            RequestResponseTools.addAttrBreadcrumbs(model,
                    "Главная", "/index",
                    "Собеседования", "/interviews/",
                    interview.getTitle(), String.format("/interview/%d", interviewId),
                    "принять участие в собеседовании", "/participate");
        } else {
            result = String.format("redirect:/interview/%d", interviewId);
        }
        return result;
    }
}
