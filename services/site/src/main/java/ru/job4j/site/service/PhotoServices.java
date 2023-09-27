package ru.job4j.site.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * CheckDev пробное собеседование
 * PhotoService загрузки изображения из сервиса Auth
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 27.09.2023
 */
@Service
@AllArgsConstructor
@Slf4j
public class PhotoServices {
    private final WebClientAuthCall webClientAuthCall;
    private static final String URL_IMG = "/img";

    /**
     * Загрузка изображения по ID
     *
     * @param id IMG ID
     * @return ByteArrayResource
     */
    public String getPhotoById(String id) {
        var responseEntityMono = webClientAuthCall
                .doGetPhoto(URL_IMG, id)
                .block();

        if (responseEntityMono == null) {
            return "";
        }
        var body = responseEntityMono.getBody();
        if (body == null) {
            return "";
        }
        var array = body.getByteArray();
        return Base64.getEncoder().encodeToString(array);
    }
}
