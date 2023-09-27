package ru.job4j.site.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.site.dto.PersonDTO;
import ru.job4j.site.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Calendar;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

/**
 * CheckDev пробное собеседование
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 18.09.2023
 */
@Controller
@RequestMapping("/persons")
@AllArgsConstructor
@Slf4j
public class PersonController {
    private final PersonService personService;

    /**
     * Метод GET отображения страницы для просмотра данных пользователя.
     *
     * @param request HttpServletRequest
     * @param model   Model
     * @return String
     */
    @GetMapping("/")
    public String getViewPerson(HttpServletRequest request, Model model) {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/",
                "Профиль", "/persons"
        );
        var personDTO = getPersonDTO(request);
        if (personDTO == null) {
            return "redirect:/";
        }
        model.addAttribute("personDto", personDTO);
        return "/persons/personView";
    }

    /**
     * Метод GET отображения страницы для редактирования пользователя.
     *
     * @param request HttpServletRequest
     * @param model   Model
     * @return String
     */
    @GetMapping("/edit")
    public String getEditPerson(HttpServletRequest request, Model model) {
        var personDTO = getPersonDTO(request);
        if (personDTO == null) {
            return "redirect:/";
        }
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/",
                "Профиль", "/persons/",
                "Редактирование", "/edit"
        );
        model.addAttribute("personDto", personDTO);
        return "/persons/personEdit";
    }

    /**
     * Post метод редактирования пользователя
     */
    @PostMapping("/edit")
    public String updatePerson(@ModelAttribute PersonDTO personDTO,
                               MultipartFile file,
                               HttpServletRequest request) {
        var token = getToken(request);
        personDTO.setUpdated(Calendar.getInstance());
        try {
            personService.postUpdatePerson(token, personDTO, file);
        } catch (IOException e) {
            log.error("API post method error: {}", e.getMessage());
        }
        return "redirect:/";
    }


    private PersonDTO getPersonDTO(HttpServletRequest request) {
        var token = (String) request.getSession().getAttribute("token");
        if (token == null) {
            return null;
        }
        try {
            return personService.getPerson(token);
        } catch (Exception e) {
            log.error("PersonDTO data available. {}", e.getMessage());
            return null;
        }
    }
}
