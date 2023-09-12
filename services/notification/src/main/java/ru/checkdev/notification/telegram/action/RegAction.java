package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.checkdev.notification.domain.Person;
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
public class RegAction implements Action {
    private final TgConfig tgConfig = new TgConfig();
    private TgAuthCallWebClint authCallWebClint;
    private String urlSiteAuth;
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
            var person = new Person(email, email, password, true);
            authCallWebClint.doPost(urlAuthRegistration, person);
            text = "Вы зарегистрированы: " + sl
                   + "Логин: " + email + sl
                   + "Пароль: " + password + sl
                   + urlSiteAuth;
        } else {
            text = "Email: " + email + " не корректный." + sl
                   + "попробуйте снова." + sl
                   + "/new";
        }
        // userRepository.save(new User(email));
        return new SendMessage(chatId, text);
    }
}
