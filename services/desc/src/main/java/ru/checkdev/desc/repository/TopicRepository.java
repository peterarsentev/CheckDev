package ru.checkdev.desc.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.desc.domain.Topic;

import java.util.List;

public interface TopicRepository extends CrudRepository<Topic, Integer> {
    List<Topic> findTopicsByCategoryId(Integer id);

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Modifying
    @Query("UPDATE cd_topic t SET total = total + 1 WHERE t.id = :id")
    void incrementTotal(int id);
}
