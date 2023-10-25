package ru.job4j.site.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.job4j.site.dto.FeedbackDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * FeedbackServiceWebClient
 * Класс реализует получение и обработку сущности Feedback из сервиса mock.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 25.10.2023
 */
@Service
@Slf4j
public class FeedbackServiceWebClient implements FeedbackService {
    private final String urlMock;
    private WebClient webClientFeedback;
    private static final String URL_FEEDBACK = "/feedback/";

    public FeedbackServiceWebClient(@Value("${service.mock}") String urlMock) {
        this.urlMock = urlMock;
        this.webClientFeedback = WebClient.create(this.urlMock);
    }

    /**
     * Метод сохраняет отзыв о собеседовании FeedbackDTO
     *
     * @param token       Authorization token
     * @param feedbackDTO FeedbackDTO
     * @return boolean true/false
     */
    @Override
    public boolean save(String token, FeedbackDTO feedbackDTO) {
        var responseEntityMono = this.webClientFeedback
                .post()
                .uri(URL_FEEDBACK)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(feedbackDTO)
                .retrieve()
                .toEntity(FeedbackDTO.class)
                .doOnError(err -> log.error("API MOCK not found: {}", err.getMessage()))
                .blockOptional();
        return responseEntityMono
                .map(re -> re.getStatusCode().is2xxSuccessful())
                .orElse(false);
    }

    /**
     * Метод возвращает все Отзывы по Interview ID
     *
     * @param interviewId ID
     * @return List<FeedbackDTO>
     */
    @Override
    public List<FeedbackDTO> findByInterviewId(int interviewId) {
        Optional<ResponseEntity<List<FeedbackDTO>>> listResponseEntity = this.webClientFeedback
                .get()
                .uri(URL_FEEDBACK + interviewId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(FeedbackDTO.class)
                .doOnError(err -> log.error("API MOCK not found: {}", err.getMessage()))
                .blockOptional();
        return listResponseEntity
                .map(HttpEntity::getBody)
                .orElse(new ArrayList<>());
    }

    public void setWebClientFeedback(WebClient webClient) {
        this.webClientFeedback = webClient;
    }
}
