package ru.job4j.site.domain;

/**
 * CheckDev пробное собеседование
 * StatusInterviews enum класс описывает статусы интервью.
 * В модели Interviews поле int status характеризует статус интервью.
 * Для корректной его обработки используется следующие принятые понятия.
 * 0 - Неизвестен IS_UNKNOWN
 * 1 - Новая IS_NEW
 * 2 - В процессе IN_PROGRESS.
 * 3 - Завершено IS_COMPLETED.
 * 4 - Отменено IS_CANCELED.
 *
 * @author Dmitry Stepanov
 * @version 08.10.2023 00:59
 */
public enum StatusInterview {
    IS_UNKNOWN(0, "Неизвестен"),

    IS_NEW(1, "Новая"),

    IN_PROGRESS(2, "В процессе"),

    IS_COMPLETED(3, "Завершено"),

    IS_CANCELED(4, "Отменено");

    private final int id;
    private final String info;

    StatusInterview(int id, String info) {
        this.id = id;
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }
}
