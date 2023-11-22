package ru.checkdev.mock.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.dto.InterviewDTO;
import ru.checkdev.mock.enums.StatusInterview;
import ru.checkdev.mock.mapper.InterviewMapper;
import ru.checkdev.mock.repository.InterviewRepository;
import ru.checkdev.mock.repository.WisherRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final WisherRepository wisherRepository;

    private static final Logger LOG = LoggerFactory.getLogger(InterviewService.class.getName());

    public Optional<InterviewDTO> save(InterviewDTO interviewDTO) {
        Optional<InterviewDTO> rsl = Optional.empty();
        var interview = InterviewMapper.getInterview(interviewDTO);
        interview.setCreateDate(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)));
        try {
            var saveInterview = interviewRepository.save(interview);
            rsl = Optional.of(InterviewMapper.getInterviewDTO(saveInterview));
        } catch (DataIntegrityViolationException e) {
            LOG.error("Error!", e);
        }
        return rsl;
    }

    public List<InterviewDTO> findAll() {
        return interviewRepository.findAll().stream()
                .peek(interview -> {
                    if (interview.getTopicId() == null) {
                        interview.setTopicId(1);
                    }
                })
                .map(InterviewMapper::getInterviewDTO)
                .collect(Collectors.toList());
    }

    public List<InterviewDTO> findLast() {
        var status = StatusInterview.IS_NEW;
        return interviewRepository.findAllByStatusOrderByCreateDateDesc(status)
                .stream()
                .map(InterviewMapper::getInterviewDTO)
                .toList();
    }

    public Page<InterviewDTO> findPaging(int page, int size) {
        return interviewRepository.findAll(
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findPagingByUserIdRelated(int page, int size, int userId) {
        Page<Interview> interviews = wisherRepository.findInterviewByUserIdApproved(userId, Pageable.unpaged());
        List<Integer> interviewIds = interviews.stream().map(Interview::getId).toList();
        var status = StatusInterview.IS_NEW;
        return interviewRepository.findAllByUserIdRelated(userId, status, interviewIds,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Optional<InterviewDTO> findById(Integer id) {
        var interview = interviewRepository.findById(id);
        if (interview.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(InterviewMapper.getInterviewDTO(interview.get()));
    }

    public List<InterviewDTO> findByMode(int mode) {
        return interviewRepository.findByMode(mode).stream()
                .peek(interview -> {
                    if (interview.getTopicId() == null) {
                        interview.setTopicId(1);
                    }
                }).map(InterviewMapper::getInterviewDTO)
                .toList();
    }

    public Page<InterviewDTO> findByTopicId(int topicId, int page, int size) {
        return interviewRepository.findByTopicId(topicId, PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findByTopicsIds(List<Integer> topicsIds, int page, int size) {
        return interviewRepository.findByTopicIdIn(topicsIds, PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public boolean update(InterviewDTO interviewDTO) {
        try {
            this.save(interviewDTO);
        } catch (Exception e) {
            log.error("Update interview error:{}", e);
            return false;
        }
        return true;
    }

    public void delete(int interviewId) {
        interviewRepository.deleteById(interviewId);
    }

    /**
     * Метод обновляет статус собеседования.
     *
     * @param interviewDTO InterviewDTO.
     * @return boolean true / false
     */
    public boolean updateStatus(InterviewDTO interviewDTO) {
        var newStatus = InterviewMapper.getStatusInterviewById(interviewDTO.getStatusId());
        try {
            interviewRepository.updateStatus(interviewDTO.getId(), newStatus);
            return true;
        } catch (Exception e) {
            log.error("Update status error {}", e.getMessage());
            return false;
        }
    }

    public Page<InterviewDTO> findBySubmitterId(int submitterId, int page, int size) {
        return interviewRepository
                .findBySubmitterId(submitterId, PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findBySubmitterIdNot(int submitterId, int page, int size) {
        return interviewRepository
                .findBySubmitterIdNot(submitterId, PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findByTopicIdAndSubmitterId(int topicId, int submitterId,
                                                          int page, int size) {
        return interviewRepository.findByTopicIdAndSubmitterId(topicId, submitterId,
                        PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findByTopicIdAndSubmitterIdNot(int topicId, int submitterId,
                                                             int page, int size) {
        return interviewRepository.findByTopicIdAndSubmitterIdNot(topicId, submitterId,
                        PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findByTopicsListIdAndSubmitterId(Collection<Integer> topicsIds,
                                                               int submitterId,
                                                               int page, int size) {
        return interviewRepository.findByTopicIdInAndSubmitterId(topicsIds, submitterId,
                        PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findByTopicsListIdAndSubmitterIdNot(
            Collection<Integer> topicsIds,
            int submitterId,
            int page, int size) {
        return interviewRepository.findByTopicIdInAndSubmitterIdNot(topicsIds, submitterId,
                        PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findByUserIdAsWisher(int userId, int page, int size) {
        return wisherRepository.findInterviewByUserId(userId, PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findByUserIdAsNotWisher(int userId, int page, int size) {
        return interviewRepository.findInterviewByUserIdNot(userId, PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findByUserIdAsWisherByTopic(int userId, int topicId,
                                                          int page, int size) {
        return wisherRepository
                .findInterviewByUserIdAndByTopicId(userId, topicId, PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findByUserIdAsNotWisherByTopic(int userId, int topicId,
                                                             int page, int size) {
        return interviewRepository
                .findInterviewByUserIdNotAndByTopicId(userId, topicId,
                        PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findByUserIdAsWisherByTopicList(int userId,
                                                              Collection<Integer> topicsIds,
                                                              int page, int size) {
        return wisherRepository
                .findInterviewByUserIdAndByTopicIdIn(userId, topicsIds,
                        PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findByUserIdAsNotWisherByTopicList(int userId,
                                                                 Collection<Integer> topicsIds,
                                                                 int page, int size) {
        return interviewRepository
                .findInterviewByUserIdNotAndByTopicIdIn(userId, topicsIds,
                        PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }

    /**
     * Метод возвращает все Interview на которые пользователь должен оставить отзыв
     *
     * @param userId ID User
     * @return List<Interview>
     */
    public List<InterviewDTO> findAllIdByNoFeedback(int userId) {
        return interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(userId)
                .stream()
                .map(InterviewMapper::getInterviewDTO)
                .toList();
    }

    /**
     * Метод возвращает все Interview со статусом новые
     *
     * @return List<Interview>
     */
    public List<InterviewDTO> findNewInterview() {
        var status = StatusInterview.IS_NEW;
        return interviewRepository.findAllByStatus(status)
                .stream()
                .map(InterviewMapper::getInterviewDTO)
                .toList();
    }
}
