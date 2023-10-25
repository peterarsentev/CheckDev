package ru.job4j.site.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.site.dto.FeedbackDTO;
import ru.job4j.site.service.FeedbackService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FeedbackController TEST
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 25.10.2023
 */
@SpringBootTest(classes = FeedbackController.class)
@AutoConfigureMockMvc
class FeedbackControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    FeedbackService feedbackService;

    @Test
    void injectedIsNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(feedbackService).isNotNull();
    }

    @Test
    void whenGetFeedbackFormThenReturnFeedbackPage() throws Exception {
        var interviewId = "1";
        var submitterId = "2";
        var userId = "2";
        var mode = "1";
        var feedbackDto = new FeedbackDTO(0, Integer.parseInt(interviewId),
                Integer.parseInt(userId),
                Integer.parseInt(mode),
                null, 0);
        this.mockMvc.perform(get("/interview/feedback/")
                        .param("interviewId", interviewId)
                        .param("submitterId", submitterId)
                        .param("userId", userId)
                        .param("mode", mode))
                .andDo(print())
                .andExpect(model().attribute("feedback", feedbackDto))
                .andExpect(status().isOk())
                .andExpect(view().name("/interview/feedbackForm"));
    }

    @Test
    void whenGetFeedbackFormThenReturnFeedbackPageNewRoleInInterview() throws Exception {
        var interviewId = "1";
        var submitterId = "2";
        var userId = "1";
        var mode = "1";
        var feedbackDto = new FeedbackDTO(0, Integer.parseInt(interviewId),
                Integer.parseInt(userId),
                2,
                null, 0);
        this.mockMvc.perform(get("/interview/feedback/")
                        .param("interviewId", interviewId)
                        .param("submitterId", submitterId)
                        .param("userId", userId)
                        .param("mode", mode))
                .andDo(print())
                .andExpect(model().attribute("feedback", feedbackDto))
                .andExpect(status().isOk())
                .andExpect(view().name("/interview/feedbackForm"));
    }

    @Test
    void whenPostSaveFeedbackThenRedirectInterviewPage() throws Exception {
        var token = "1234";
        var feedbackDTO = new FeedbackDTO(1, 1, 1, 1, "text", 5);
        when(feedbackService.save(token, feedbackDTO)).thenReturn(true);
        mockMvc.perform(post("/interview/createFeedback")
                        .flashAttr("feedbackDTO", feedbackDTO)
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/interview/" + feedbackDTO.getInterviewId()));
    }
}