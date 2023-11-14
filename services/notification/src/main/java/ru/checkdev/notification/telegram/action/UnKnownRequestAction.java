package ru.checkdev.notification.telegram.action;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class UnKnownRequestAction implements Action {
    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        String sl = System.lineSeparator();
        var out = new StringBuilder();
        String text = "Команда не поддерживается! Список доступных команд:" + sl + "/start";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}
