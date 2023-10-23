package ru.checkdev.mock.repository;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.mock.domain.Interview;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.hamcrest.core.Is.is;

@DataJpaTest()
@RunWith(SpringRunner.class)
class InterviewRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private InterviewRepository interviewRepository;

    @Test
    public void injectedComponentAreNotNull() {
        Assertions.assertNotNull(entityManager);
        Assertions.assertNotNull(interviewRepository);
    }

    @Test
    public void whenFindInterviewByIdThenReturnEmpty() {
        Optional<Interview> interview = interviewRepository.findById(-1);
        MatcherAssert.assertThat(interview, is(Optional.empty()));
    }

    @Test
    public void whenInterviewFindByType() {
        var interview = new Interview();
        interview.setTypeInterview(1);
        interview.setSubmitterId(1);
        interview.setTitle("title");
        interview.setAdditional("additional");
        interview.setContactBy("contact");
        interview.setApproximateDate("30.02.2070");
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        interview.setTopicId(1);
        entityManager.createQuery("delete from interview").executeUpdate();
        entityManager.persist(interview);
        var interviews = interviewRepository.findByTypeInterview(1);
        Assertions.assertTrue(interviews.size() > 0);
        Assertions.assertEquals(interviews.get(0), interview);
    }

    @Test
    public void whenInterviewNotFoundByType() {
        entityManager.createQuery("delete from interview").executeUpdate();
        var interviews = interviewRepository.findByTypeInterview(1);
        Assertions.assertEquals(0, interviews.size());
    }

    @Test
    public void whenFindAllInterview() {
        entityManager.createQuery("delete from interview").executeUpdate();
        var listInterview = interviewRepository.findAll();
        MatcherAssert.assertThat(listInterview, is(Collections.emptyList()));
    }

    @Test
    public void whenInterviewFindByTopicId() {
        var interview = new Interview();
        interview.setTypeInterview(1);
        interview.setSubmitterId(1);
        interview.setTitle("title");
        interview.setAdditional("additional");
        interview.setContactBy("contact");
        interview.setApproximateDate("30.02.2070");
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        interview.setTopicId(1);
        entityManager.createQuery("delete from interview").executeUpdate();
        entityManager.persist(interview);
        var interviews =
                interviewRepository.findByTopicId(1, PageRequest.of(0, 5));
        Assertions.assertTrue(interviews.toList().size() > 0);
        Assertions.assertEquals(interviews.toList().get(0), interview);
    }

    @Test
    public void whenGetInterviewsBy3onPageAndFindByTopicId() {
        entityManager.createQuery("delete from interview").executeUpdate();
        var interviewsList = IntStream
                .range(0, 8).mapToObj(i -> {
                    var interview = new Interview();
                    interview.setTypeInterview(1);
                    interview.setSubmitterId(1);
                    interview.setTitle(String.format("Interview_%d", i));
                    interview.setAdditional(String.format("Some text_%d", i));
                    interview.setContactBy("Some contact");
                    interview.setApproximateDate("30.02.2024");
                    interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
                    interview.setTopicId(1);
                    entityManager.persist(interview);
                    return  interview;
                }).toList();
        var firstPage =
                interviewRepository.findByTopicId(1, PageRequest.of(0, 3));
        var secondPage =
                interviewRepository.findByTopicId(1, PageRequest.of(1, 3));
        var thirdPage =
                interviewRepository.findByTopicId(1, PageRequest.of(2, 3));
        var firstPageList = interviewsList.subList(0, 3);
        var secondPageList = interviewsList.subList(3, 6);
        var thirdPageList = interviewsList.subList(6, 8);
        Assertions.assertEquals(firstPage.toList().size(), 3);
        Assertions.assertEquals(firstPage.toList(), firstPageList);
        Assertions.assertEquals(secondPage.toList().size(), 3);
        Assertions.assertEquals(secondPage.toList(), secondPageList);
        Assertions.assertEquals(thirdPage.toList().size(), 2);
        Assertions.assertEquals(thirdPage.toList(), thirdPageList);
    }

    @Test
    public void whenInterviewNotFoundByTopicId() {
        entityManager.createQuery("delete from interview").executeUpdate();
        var interviews =
                interviewRepository.findByTopicId(1, PageRequest.of(1, 5));
        Assertions.assertEquals(0, interviews.toList().size());
    }
}