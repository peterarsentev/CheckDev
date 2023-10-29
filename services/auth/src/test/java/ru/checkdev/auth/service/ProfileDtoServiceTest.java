package ru.checkdev.auth.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.auth.dto.ProfileDTO;
import ru.checkdev.auth.repository.ProfileRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * CheckDev пробное собеседование
 * ProfileServiceTest тестирование слоя бизнес логики обработки модели ProfileDTO
 *
 * @author Dmitry Stepanov
 * @version 01:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfileDtoServiceTest {
    private static final int ID_OK = 1;
    @MockBean
    private ProfileRepository personRepository;
    @Autowired
    private ProfileDtoService profileDtoService;
    private final ProfileDTO profileDTO1 = new ProfileDTO(
            1, "name1", "experience1", 1, null, null);
    private final ProfileDTO profileDTO2 = new ProfileDTO(
            2, "name2", "experience2", 2, null, null);

    @Test
    public void whenFindByIDThenReturnOptionalProfileDTO() {
        when(personRepository.findProfileById(ID_OK)).thenReturn(profileDTO1);
        var actual = profileDtoService.findProfileByID(ID_OK);
        assertThat(actual, is(Optional.of(profileDTO1)));
    }

    @Test
    public void whenFindByIDThenReturnEmpty() {
        when(personRepository.findProfileById(anyInt())).thenReturn(null);
        var actual = profileDtoService.findProfileByID(anyInt());
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void whenFindProfilesOrderByCreatedDescThenReturnEmptyList() {
        when(personRepository.findProfileOrderByCreatedDesc()).thenReturn(Collections.emptyList());
        var actual = profileDtoService.findProfilesOrderByCreatedDesc();
        assertThat(actual, is(Collections.emptyList()));
    }

    @Test
    public void whenFindProfilesOrderByCreatedDescThenReturnListProfileDTO() {
        var expected = List.of(profileDTO1, profileDTO2);
        when(personRepository.findProfileOrderByCreatedDesc()).thenReturn(expected);
        var actual = profileDtoService.findProfilesOrderByCreatedDesc();
        assertThat(actual, is(expected));
    }
}