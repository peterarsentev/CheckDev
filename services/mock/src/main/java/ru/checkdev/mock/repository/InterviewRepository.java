package ru.checkdev.mock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.checkdev.mock.domain.Interview;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Integer> {

    Optional<Interview> findById(int id);

    List<Interview> findByTypeInterview(int type);

    Page<Interview> findByTopicId(int topicId, Pageable pageable);
}
