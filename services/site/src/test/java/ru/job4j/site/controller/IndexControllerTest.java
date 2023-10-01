package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.site.SiteApplication;
import ru.job4j.site.domain.Breadcrumb;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.TopicDTO;
import ru.job4j.site.service.CategoriesService;
import ru.job4j.site.service.TopicsService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


/**
 * CheckDev пробное собеседование
 * IndexControllerTest тесты на контроллер IndexController
 *
 * @author Dmitry Stepanov
 * @version 24.09.2023 21:50
 */
@SpringBootTest(classes = SiteApplication.class)
@AutoConfigureMockMvc
class IndexControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoriesService categoriesService;
    @MockBean
    private TopicsService topicsService;

    private IndexController indexController;

    @BeforeEach
    void initTest() {
        this.indexController = new IndexController(categoriesService, topicsService);
    }

    @Test
    void whenGetIndexPageThenReturnIndex() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void whenGetIndexPageExpectModelAttributeThenOk() throws JsonProcessingException {
        var topicDTO1 = new TopicDTO();
        topicDTO1.setId(1);
        topicDTO1.setName("topic1");
        var topicDTO2 = new TopicDTO();
        topicDTO2.setId(2);
        topicDTO2.setName("topic2");
        var cat1 = new CategoryDTO(1, "name1");
        var cat2 = new CategoryDTO(2, "name2");
        var listCat = List.of(cat1, cat2);
        when(topicsService.getByCategory(cat1.getId())).thenReturn(List.of(topicDTO1));
        when(topicsService.getByCategory(cat2.getId())).thenReturn(List.of(topicDTO2));
        when(categoriesService.getAllWithTopics(topicsService)).thenReturn(listCat);
        var listBread = List.of(new Breadcrumb("Главная", "/"),
                new Breadcrumb("Категории", "/categories/"));
        var model = new ConcurrentModel();

        var view = indexController.getIndexPage(model);
        var actualCategories = model.getAttribute("categories");
        var actualBreadCrumbs = model.getAttribute("breadcrumbs");
        var actualUserInfo = model.getAttribute("userInfo");

        assertThat(view).isEqualTo("index");
        assertThat(actualCategories).usingRecursiveComparison().isEqualTo(listCat);
        assertThat(actualBreadCrumbs).usingRecursiveComparison().isEqualTo(listBread);
        assertThat(actualUserInfo).isNull();
    }
}