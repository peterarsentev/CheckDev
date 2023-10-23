package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.site.domain.StatusWisher;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.UserInfoDTO;
import ru.job4j.site.dto.WisherDto;
import ru.job4j.site.dto.WisherDetailDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class InterviewService {
    private static final String URL_MOCK = "http://localhost:9912/interview/";

    private final String key;
    private final ProfilesService profilesService;

    public InterviewService(@Value("${server.auth.access.key}") String key, ProfilesService profilesService) {
        this.key = key;
        this.profilesService = profilesService;
    }

    public InterviewDTO create(String token, InterviewDTO interviewDTO) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(URL_MOCK).post(
                token,
                mapper.writeValueAsString(interviewDTO)
        );
        return mapper.readValue(out, InterviewDTO.class);
    }

    public InterviewDTO getById(String token, int id) throws JsonProcessingException {
        var text = new RestAuthCall(String.format("%s%d", URL_MOCK, id))
                .get(token);
        return new ObjectMapper().readValue(text, new TypeReference<>() {
        });
    }

    public void update(String token, InterviewDTO interviewDTO) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        new RestAuthCall(URL_MOCK).update(
                token,
                mapper.writeValueAsString(interviewDTO));
    }

    /**
     * Метод проверяет являться пользователь автором собеседования.
     *
     * @param userInfoDTO  UserInfoDto
     * @param interviewDTO InterviewDTO
     * @return boolean userId == submitterId
     */
    public boolean isAuthor(UserInfoDTO userInfoDTO, InterviewDTO interviewDTO) {
        return userInfoDTO.getId() == interviewDTO.getSubmitterId();
    }

    /**
     * Метод формирует детальную информацию по всем кандидатам в собеседовании
     *
     * @param wishers List<WisherDto>
     * @return List<WisherDetail>
     */
    public List<WisherDetailDTO> getAllWisherDetail(List<WisherDto> wishers) {
        List<WisherDetailDTO> wishersDetail = new ArrayList<>();
        var statusesWisher = StatusWisher.values();
        for (WisherDto wisherDto : wishers) {
            var person = profilesService.getProfileById(wisherDto.getUserId(), key);
            if (person.isPresent()) {
                var wisherUser = new WisherDetailDTO(wisherDto.getId(),
                        wisherDto.getInterviewId(),
                        wisherDto.getUserId(),
                        person.get().getUsername(),
                        wisherDto.getContactBy(),
                        wisherDto.isApprove(),
                        wisherDto.getStatus(),
                        statusesWisher[wisherDto.getStatus()].getInfo());
                wishersDetail.add(wisherUser);
            }
        }
        return wishersDetail;
    }
}
