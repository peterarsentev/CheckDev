package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.site.SiteApplication;
import ru.job4j.site.domain.Breadcrumb;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.service.AuthService;
import ru.job4j.site.service.CategoriesService;
import ru.job4j.site.service.TopicsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private AuthService authService;

    @MockBean
    private TopicsService topicsService;

    private IndexController indexController;

    @BeforeEach
    void initTest() {
        this.indexController = new IndexController(categoriesService, topicsService, authService);
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
        var catDTO1 = new CategoryDTO(1, "name1");
        var catDTO2 = new CategoryDTO(2, "name2");
        var listCatDTO = List.of(catDTO1, catDTO2);
        when(categoriesService.getAll()).thenReturn(listCatDTO);
        when(categoriesService.getAllWithTopics(topicsService)).thenReturn(listCatDTO);
        var listBread = List.of(new Breadcrumb("Главная", "/"));
        var model = new ConcurrentModel();
        var req = Mockito.mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(Mockito.mock(HttpSession.class));
        var view = indexController.getIndexPage(model, req);
        var actualCategories = model.getAttribute("categories");
        var actualBreadCrumbs = model.getAttribute("breadcrumbs");
        var actualUserInfo = model.getAttribute("userInfo");

        assertThat(view).isEqualTo("index");
        assertThat(actualCategories).usingRecursiveComparison().isEqualTo(listCatDTO);
        assertThat(actualBreadCrumbs).usingRecursiveComparison().isEqualTo(listBread);
        assertThat(actualUserInfo).isNull();
    }
}