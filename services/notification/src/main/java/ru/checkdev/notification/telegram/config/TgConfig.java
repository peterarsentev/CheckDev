package ru.checkdev.notification.telegram.config;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 3. Мидл
 * Класс дополнительных функций телеграм бота, проверка почты, генерация пароля.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
public class TgConfig {
    private final String prefix = "tg/";
    private final int passSize = 8;

    /**
     * Метод проверяет входящую строку на соответствие формату email
     *
     * @param email String
     * @return boolean
     */
    public boolean isEmail(String email) {
        Pattern pattern = Pattern.compile("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * метод генерирует пароль для пользователя
     *
     * @return String
     */
    public String getPassword() {
        String password = prefix + UUID.randomUUID();
        return password.substring(0, passSize);
    }
}
