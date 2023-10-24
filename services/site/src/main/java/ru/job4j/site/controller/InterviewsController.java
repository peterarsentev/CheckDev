package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.site.domain.StatusInterview;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.ProfileDTO;
import ru.job4j.site.service.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

@Controller
@RequestMapping("/interviews")
public class InterviewsController {

    private final InterviewsService interviewsService;
    private final ProfilesService profilesService;
    private final CategoriesService categoriesService;
    private final TopicsService topicsService;
    private final AuthService authService;
    private final FilterService filterService;
    private final WisherService wisherService;

    public InterviewsController(InterviewsService interviewsService, ProfilesService profilesService,
                                CategoriesService categoriesService, TopicsService topicsService,
                                AuthService authService, FilterService filterService,
                                WisherService wisherService) {
        this.interviewsService = interviewsService;
        this.profilesService = profilesService;
        this.categoriesService = categoriesService;
        this.topicsService = topicsService;
        this.authService = authService;
        this.filterService = filterService;
        this.wisherService = wisherService;
    }

    @GetMapping("/")
    public String getAllInterviews(Model model,
                                   HttpServletRequest req,
                                   @RequestParam(required = false, defaultValue = "0") int page,
                                   @RequestParam(required = false, defaultValue = "20") int size)
            throws JsonProcessingException {
        var token = getToken(req);
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Собеседования", String.format("/interviews/?page=%d&?size=%d", page, size)
        );
        var user = authService.userInfo(token);
        var userId = user != null ? user.getId() : 0;
        var filter = userId > 0
                ? filterService.getByUserId(token, userId) : null;
        var isFiltered = filter != null && filter.getTopicId() > 0;
        Page<InterviewDTO> interviewsPage = isFiltered
                ? interviewsService.getByTopicId(filter.getTopicId(), page, size)
                : interviewsService.getAll(token, page, size);
        var categories = categoriesService.getAll();
        var categoryName = "";
        var topicName = "";
        if (isFiltered) {
            categoryName = categoriesService.getNameById(categories, filter.getCategoryId());
            topicName = topicsService.getNameById(filter.getTopicId());
        }
        Set<ProfileDTO> userList = interviewsPage.toList().stream()
                .map(x -> profilesService.getProfileById(x.getSubmitterId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        var wishers = wisherService.getAllWisherDtoByInterviewId(token, "");
        var interviewStatistic = wisherService.getInterviewStatistic(wishers);
        model.addAttribute("statisticMap", interviewStatistic);
        model.addAttribute("interviewsPage", interviewsPage);
        model.addAttribute("statuses", StatusInterview.values());
        model.addAttribute("current_page", "interviews");
        model.addAttribute("users", userList);
        model.addAttribute("categories", categories);
        model.addAttribute("filter", filter);
        model.addAttribute("userId", userId);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("topicName", topicName);
        return "interviews";
    }
}
