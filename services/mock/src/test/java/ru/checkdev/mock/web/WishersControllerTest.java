package ru.checkdev.mock.web;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.checkdev.mock.Application;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.domain.Wisher;
import ru.checkdev.mock.service.WisherService;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class WishersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WisherService wisherService;

    private Interview interview = Interview.of()
            .id(1)
            .typeInterview(2)
            .submitterId(3)
            .title("test_title")
            .description("test_description")
            .contactBy("test_contact_by")
            .approximateDate("test_approximate_date")
            .createDate("test_create_date")
            .build();

    private Wisher wisher = Wisher.of()
            .id(1)
            .interview(interview)
            .userId(1)
            .contactBy("test_contact_by")
            .approve(true)
            .build();

    private String interviewString = new GsonBuilder().create().toJson(interview);

    private String wisherString = new GsonBuilder().create().toJson(wisher);

    @Test
    @WithMockUser
    public void whenGetAll() throws Exception {
        when(wisherService.findAll()).thenReturn(List.of(wisher));
        mockMvc.perform(get("/wishers/"))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string("[" + wisherString + "]"));
    }
}