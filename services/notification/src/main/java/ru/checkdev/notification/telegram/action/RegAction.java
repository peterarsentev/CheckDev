package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

/**
 * 3. Мидл
 * Класс реализует пункт меню регистрации нового пользователя в телеграм бот
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@AllArgsConstructor
@Slf4j
public class RegAction implements Action {
    private static final String ERROR_OBJECT = "error";
    private final TgConfig tgConfig = new TgConfig();
    private final TgAuthCallWebClint authCallWebClint;
    private final String urlSiteAuth;
    private final String urlAuthRegistration = "/registration";

    @Override
    public BotApiMethod handle(Update update) {
        var msg = update.getMessage();
        var chatId = msg.getChatId().toString();
        var text = "Введите email для регистрации:";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod callback(Update update) {
        var msg = update.getMessage();
        var chatId = msg.getChatId().toString();
        var email = msg.getText();
        var text = "";
        var sl = System.lineSeparator();
        if (tgConfig.isEmail(email)) {
            var password = tgConfig.getPassword();
            var person = new PersonDTO(email, password, true);
            var result = authCallWebClint.doPost(urlAuthRegistration, person).block();
            var mapObject = tgConfig.getObjectToMap(result);
            if (!mapObject.containsKey(ERROR_OBJECT)) {
                text = "Вы зарегистрированы: " + sl
                       + "Логин: " + email + sl
                       + "Пароль: " + password + sl
                       + urlSiteAuth;
            } else {
                text = "Ошибка регистрации: " + mapObject.get(ERROR_OBJECT);
            }
        } else {
            text = "Email: " + email + " не корректный." + sl
                   + "попробуйте снова." + sl
                   + "/new";
        }
        return new SendMessage(chatId, text);
    }
}
