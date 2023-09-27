package ru.checkdev.mock.repository;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.mock.domain.Interview;
import javax.persistence.EntityManager;
import static org.junit.Assert.*;
import java.util.Collections;
import java.util.Optional;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

@DataJpaTest()
@RunWith(SpringRunner.class)
class InterviewRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private InterviewRepository interviewRepository;

    @Before
    public void clearTable() {
        entityManager.createQuery("delete from interview").executeUpdate();
    }

    @Test
    public void injectedComponentAreNotNull() {
        assertNotNull(entityManager);
        assertNotNull(interviewRepository);
    }

    @Test
    public void whenFindInterviewByIdThenReturnEmpty() {
        Optional<Interview> interview = interviewRepository.findById(-1);
        assertThat(interview, is(Optional.empty()));
    }

    @Test
    public void whenFindAllInterview() {
        var listInterview = interviewRepository.findAll();
        assertThat(listInterview, is(Collections.emptyList()));
    }
}