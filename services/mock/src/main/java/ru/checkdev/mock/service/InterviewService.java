package ru.checkdev.mock.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.repository.InterviewRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;

    private static final Logger LOG = LoggerFactory.getLogger(InterviewService.class.getName());

    public Optional<Interview> save(Interview interview) {
        Optional<Interview> rsl = Optional.empty();
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        try {
            rsl = Optional.of(interviewRepository.save(interview));
        } catch (DataIntegrityViolationException e) {
            LOG.error("Error!", e);
        }
        return rsl;
    }

    public List<Interview> findAll() {
        return interviewRepository.findAll().stream()
                .peek(interview -> {
                    if (interview.getTopicId() == null) {
                        interview.setTopicId(1);
                    }
                }).collect(Collectors.toList());
    }
  
    public Page<Interview> findPaging(int page, int size) {
        return interviewRepository.findAll(PageRequest.of(page, size));
    }

    public Optional<Interview> findById(Integer id) {
        return interviewRepository.findById(id);
    }

    public List<Interview> findByType(int type) {
        return interviewRepository.findByTypeInterview(type).stream()
                .peek(interview -> {
                    if (interview.getTopicId() == null) {
                        interview.setTopicId(1);
                    }
                })
                .collect(Collectors.toList());
    }

    public Page<Interview> findByTopicId(int topicId, int page, int size) {
        return interviewRepository.findByTopicId(topicId, PageRequest.of(page, size));
    }

    public boolean update(Interview interview) {
        interviewRepository.save(interview);
        return true;
    }

    public boolean delete(Interview interview) {
        if (findById(interview.getId()).isPresent()) {
            interviewRepository.delete(interview);
            return true;
        }
        return false;
    }
}
