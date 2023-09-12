package ru.checkdev.notification.telegram.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Создание Bean WebClient для отправки асинхронных сообщений в сервис auth
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@Configuration
public class WebClientAuthConfig {
    @Value("${server.auth}")
    private String urlAuth;

    @Bean
    public WebClient getWebClientAuth() {
        return WebClient.create(urlAuth);
    }
}
