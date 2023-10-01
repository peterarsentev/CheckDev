package ru.checkdev.desc.repository;

import org.springframework.data.repository.CrudRepository;
import ru.checkdev.desc.domain.Topic;

import java.util.List;

public interface TopicRepository extends CrudRepository<Topic, Integer> {
    List<Topic> findTopicsByCategoryId(Integer id);
}
