package ru.checkdev.notification.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.dto.InnerMessageDTO;

import java.util.List;

public interface InnerMessageRepository extends CrudRepository<InnerMessage, Integer> {
    List<InnerMessage> findByUserIdAndReadFalse(int id);

    @Query("""
              SELECT new ru.checkdev.notification.dto.InnerMessageDTO(m.id, m.userId, m.text, m.created, m.interviewId)
              FROM cd_message m
              WHERE m.read = false AND m.userId = :id and m.interviewId > 0
              """)
    List<InnerMessageDTO> findMessageDTOByUserIdAndReadFalse(@Param("id") int userId);
}
