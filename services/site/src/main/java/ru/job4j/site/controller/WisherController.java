package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.job4j.site.component.safety.StringShieldXSSInspector;
import ru.job4j.site.component.safety.WisherApprovedDtoInspector;
import ru.job4j.site.component.safety.WisherNotifyDtoXSSInspector;
import ru.job4j.site.dto.WisherApprovedDTO;
import ru.job4j.site.dto.WisherNotifyDTO;
import ru.job4j.site.enums.StatusInterview;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.WisherDto;
import ru.job4j.site.service.InterviewService;
import ru.job4j.site.service.NotificationService;
import ru.job4j.site.service.WisherService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Контроллер обработки запросов на подписку на собеседование.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.10.2023
 */
@Controller
@RequestMapping("/wisher")
@AllArgsConstructor
public class WisherController {
    private final WisherService wisherService;
    private final InterviewService interviewService;
    private final NotificationService notificationService;
    private final WisherNotifyDtoXSSInspector wisherNotifyDtoXSSInspector;
    private final WisherApprovedDtoInspector wisherApprovedDtoInspector;

    /**
     * Подать заявку на участие в собеседовании.
     *
     * @param wisherNotifyDTO WisherNotifyDTO
     * @param request   HttpServletRequest
     * @return String page.
     */
    @PostMapping("/create")
    public String createWisher(@ModelAttribute WisherNotifyDTO wisherNotifyDTO,
                               HttpServletRequest request) {
        var token = RequestResponseTools.getToken(request);
        wisherNotifyDtoXSSInspector.defuse(wisherNotifyDTO);
        int interviewId = wisherNotifyDTO.getInterviewId();
        int userId = wisherNotifyDTO.getUserId();
        var contactBy = wisherNotifyDTO.getContactBy();
        var wisherDto = new WisherDto(0, interviewId, userId, contactBy, false);
        wisherService.saveWisherDto(token, wisherDto);
        notificationService.sendParticipateAuthor(token, wisherNotifyDTO);
        return "redirect:/interview/" + interviewId;
    }

    /**
     * Одобрить участника собеседование, а остальных участников отклонить
     *
     * @param param   Map<String, String>
     * @param request HttpServletRequest
     * @return String page.
     */
    @PostMapping("/dismissed")
    public String dismissedWisher(@RequestParam Map<String, String> param,
                                  @ModelAttribute WisherApprovedDTO wisherApprovedDTO,
                                  HttpServletRequest request) throws JsonProcessingException {
        var token = RequestResponseTools.getToken(request);
        var interviewId = param.get("interviewId");
        var wisherId = param.get("wisherId");
        var wisherUserId = param.get("wisherUserId");
        wisherApprovedDtoInspector.defuse(wisherApprovedDTO);
        wisherService.setNewApproveByWisherInterview(
                token, interviewId, wisherId, true);
        InterviewDTO interviewDto = interviewService.getById(token, Integer.parseInt(interviewId));
        interviewDto.setAgreedWisherId(Integer.parseInt(wisherUserId));
        interviewDto.setStatusId(StatusInterview.IN_PROGRESS.getId());
        interviewService.update(token, interviewDto);
        interviewService.updateStatus(token, interviewDto);
        wisherApprovedDTO.setInterviewLink(
                String.format("https://checkdev.ru/interview/%d", Integer.parseInt(interviewId)));
        notificationService.approvedWisher(token, wisherApprovedDTO);
        return "redirect:/interview/" + interviewId;
    }
}
