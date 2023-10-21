package ru.checkdev.mock.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.mock.MockSrv;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.repository.InterviewRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = MockSrv.class)
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
            .additional("test_additional")
            .contactBy("test_contact_by")
            .approximateDate("test_approximate_date")
            .createDate(new Timestamp(System.currentTimeMillis()))
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
        List<Interview> interviews = IntStream.range(0, 5).mapToObj(i -> {
            var interview = new Interview();
            interview.setId(i);
            interview.setTypeInterview(1);
            interview.setSubmitterId(1);
            interview.setTitle(String.format("Interview_%d", i));
            interview.setAdditional("Some text");
            interview.setContactBy("Some contact");
            interview.setApproximateDate("30.02.2024");
            interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
            return interview;
        }).toList();
        var page = new PageImpl<>(interviews);
        when(interviewRepository.findAll(PageRequest.of(0, 5)))
                .thenReturn(page);
        var actual = interviewService.findPaging(0, 5);
        assertThat(actual, is(page));
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

    @Test
    public void whenGetAllWithTopicIdIsNull() {
        Interview interviewWithTopicId = interview;
        interviewWithTopicId.setTopicId(1);
        when(interviewRepository.findAll()).thenReturn(List.of(interviewWithTopicId));
        var actual = interviewService.findAll();
        assertThat(actual, is(List.of(interviewWithTopicId)));
    }

    @Test
    public void whenFindByTypeAllWithTopicIdIsNull() {
        Interview interviewWithTopicId = interview;
        interviewWithTopicId.setTopicId(1);
        interviewWithTopicId.setTypeInterview(1);
        when(interviewRepository.findByTypeInterview(1)).thenReturn(List.of(interviewWithTopicId));
        var actual = interviewService.findByType(1);
        assertThat(actual, is(List.of(interviewWithTopicId)));
    }
}