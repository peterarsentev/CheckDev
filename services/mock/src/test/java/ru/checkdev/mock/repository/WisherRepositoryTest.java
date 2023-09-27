package ru.checkdev.mock.repository;

import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.mock.domain.Wisher;
import javax.persistence.EntityManager;
import static org.junit.Assert.*;
import java.util.Collections;
import java.util.Optional;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

@DataJpaTest()
@RunWith(SpringRunner.class)
class WisherRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private WisherRepository wisherRepository;

    @Before
    public void clearTable() {
        entityManager.createQuery("delete from wisher").executeUpdate();
    }

    @Test
    public void injectedComponentAreNotNull() {
        assertNotNull(entityManager);
        assertNotNull(wisherRepository);
    }

    @Test
    public void whenFindInterviewByIdThenReturnEmpty() {
        Optional<Wisher> wisher = wisherRepository.findById(-1);
        assertThat(wisher, is(Optional.empty()));
    }

    @Test
    public void whenFindAllInterview() {
        var listWisher = wisherRepository.findAll();
        assertThat(listWisher, is(Collections.emptyList()));
    }
}