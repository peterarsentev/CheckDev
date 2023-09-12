package ru.checkdev.notification.telegram.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.Person;

/**
 * 3. Мидл
 * Класс реализует методы get и post для отправки сообщений через WebClient
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@Service
@Slf4j
@AllArgsConstructor
public class TgAuthCallWebClint {
    private WebClient webClient;

    /**
     * Метод get
     *
     * @param url URL http
     * @return Mono<Person>
     */
    public Mono<Person> doGet(String url) {
        Mono<Person> personMono = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Person.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
        personMono.subscribe();
        return personMono;
    }

    /**
     * Метод POST
     *
     * @param url    URL http
     * @param person Body Person.class
     * @return Mono<Person>
     */
    public Mono<Person> doPost(String url, Person person) {
        Mono<Person> personMono = webClient
                .post()
                .uri(url)
                .body(Mono.just(person), Person.class)
                .retrieve()
                .bodyToMono(Person.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
        personMono.subscribe();
        return personMono;
    }
}
