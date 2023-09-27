package ru.checkdev.mock.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.mock.Application;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.repository.InterviewRepository;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
class InterviewServiceTest {

    @MockBean
    private InterviewRepository interviewRepository;

    @Autowired
    private InterviewService interviewService;

    private Interview interview = Interview.of()
            .id(1)
            .typeInterview(2)
            .submitterId(3)
            .title("test_title")
            .description("test_description")
            .contactBy("test_contact_by")
            .approximateDate("test_approximate_date")
            .build();

    @Test
    public void whenSaveAndGetTheSame() {
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);
        var actual = interviewService.save(interview);
        assertThat(actual, is(Optional.of(interview)));
    }

    @Test
    public void whenSaveAndGetEmpty() {
        when(interviewRepository.save(any(Interview.class))).thenThrow(new DataIntegrityViolationException(""));
        var actual = interviewService.save(interview);
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void whenGetAll() {
        when(interviewRepository.findAll()).thenReturn(List.of(interview));
        var actual = interviewService.findAll();
        assertThat(actual, is(List.of(interview)));
    }

    @Test
    public void whenFindByIdIsCorrect() {
        when(interviewRepository.findById(any(Integer.class))).thenReturn(Optional.of(interview));
        var actual = interviewService.findById(1);
        assertThat(actual, is(Optional.of(interview)));
    }

    @Test
    public void whenFindByIdIsNotCorrect() {
        when(interviewRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        var actual = interviewService.findById(1);
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void whenUpdateIsCorrect() {
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);
        var actual = interviewService.save(interview);
        assertThat(actual, is(Optional.of(interview)));
    }

    @Test
    public void whenDeleteIsCorrect() {
        when(interviewRepository.findById(any(Integer.class))).thenReturn(Optional.of(interview));
        var actual = interviewService.delete(interview);
        assertThat(actual, is(true));
    }

    @Test
    public void whenDeleteIsNotCorrect() {
        when(interviewRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        var actual = interviewService.delete(interview);
        assertThat(actual, is(false));
    }
}