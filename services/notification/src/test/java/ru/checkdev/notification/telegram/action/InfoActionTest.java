package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

class InfoActionTest {
    @Test
    void handle() {
        Chat chat = new Chat(1L, "type");
        Message message = new Message();
        message.setChat(chat);
        List<String> actions = List.of(
                "/start",
                "/new  зарегистрировать нового пользователя");
        InfoAction infoAction = new InfoAction(actions);
        BotApiMethod<Message> botApiMethod = infoAction.handle(message);
        SendMessage sendMessage = (SendMessage) botApiMethod;
        String sl = System.lineSeparator();
        String text = "Выберите действие:" +sl
                + "/start" + sl
                + "/new  зарегистрировать нового пользователя" +sl;
        Assertions.assertEquals(text, sendMessage.getText());
    }
}