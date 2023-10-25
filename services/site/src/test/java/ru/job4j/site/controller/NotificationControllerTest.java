package ru.job4j.site.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.site.service.AuthService;
import ru.job4j.site.service.NotificationService;
import ru.job4j.site.service.TopicsService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.site.SiteSrv;
import ru.job4j.site.dto.*;

@SpringBootTest(classes = SiteSrv.class)
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TopicsService topicsService;
    @MockBean
    private AuthService authService;

    @MockBean
    private NotificationService notifications;

    @Test
    public void whenCreateSubscribeTopic() throws Exception {
        var token = "1410";
        int topicId = 2;
        int categoryId = 3;
        var userTopicDto = new UserTopicDTO();
        userTopicDto.setId(1);
        mockMvc.perform(post("/notification/subscribeTopic")
                        .sessionAttr("token", token)
                        .flashAttr("id", userTopicDto)
                        .param("categoryId", String.valueOf(categoryId))
                        .param("topicId", String.valueOf(topicId)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/topics/" + categoryId));
    }

    @Test
    public void whenDeleteSubscribeCategory() throws Exception {
        var token = "1410";
        int topicId = 2;
        int categoryId = 3;
        var userTopicDto = new UserTopicDTO();
        userTopicDto.setId(1);
        mockMvc.perform(post("/notification/unSubscribeTopic")
                        .sessionAttr("token", token)
                        .flashAttr("id", userTopicDto)
                        .param("categoryId", String.valueOf(categoryId))
                        .param("topicId", String.valueOf(topicId)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/topics/" + categoryId));
    }

    @Test
    public void whenCreateSubscribeTopicFromDetails() throws Exception {
        var token = "1410";
        int topicId = 2;
        var userTopicDto = new UserTopicDTO();
        userTopicDto.setId(1);
        mockMvc.perform(post("/notification/subscribeTopicFromDetails")
                        .sessionAttr("token", token)
                        .flashAttr("id", userTopicDto)
                        .param("topicId", String.valueOf(topicId)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/topic/" + topicId));
    }

    @Test
    public void whenDeleteSubscribeTopicFromDetails() throws Exception {
        var token = "1410";
        int topicId = 2;
        var userTopicDto = new UserTopicDTO();
        userTopicDto.setId(1);
        mockMvc.perform(post("/notification/unSubscribeTopicFromDetails")
                        .sessionAttr("token", token)
                        .flashAttr("id", userTopicDto)
                        .param("topicId", String.valueOf(topicId)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/topic/" + topicId));
    }
}