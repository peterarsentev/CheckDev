package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.NtfSrv;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.service.ChatIdService;
import ru.checkdev.notification.service.InnerMessageService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import ru.checkdev.notification.telegram.service.TgCall;

@TestPropertySource(locations="classpath:application.properties")
@SpringBootTest(classes = NtfSrv.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class RegActionTest {

    @Autowired
    private ChatIdService chatIdService;

    @Autowired
    private InnerMessageService messageService;

    @Autowired
    private TgCall tgCall;;

    @Test
    void whenHandleThenOk() {
        Chat chat = new Chat(1L, "type");
        Message message = new Message();
        message.setChat(chat);
        RegAction regAction = new RegAction(tgCall, chatIdService, messageService, "www");
        BotApiMethod<Message> botApiMethod = regAction.handle(message);
        SendMessage sendMessage = (SendMessage) botApiMethod;
        String text = "Введите email для регистрации:";
        Assertions.assertEquals(text, sendMessage.getText());
        chatIdService.delete(1);
    }

    @Test
    void whenNotUniqueChatId() {
        Chat chat = new Chat(1L, "type");
        Message message = new Message();
        message.setChat(chat);
        chatIdService.save(new ChatId(Integer.parseInt(message.getChatId().toString()), 10, "mail"));
        RegAction regAction = new RegAction(tgCall, chatIdService, messageService, "www");
        regAction.handle(message);
        BotApiMethod<Message> botApiMethod = regAction.handle(message);
        SendMessage sendMessage = (SendMessage) botApiMethod;
        String text = "Данный аккаунт Telegram уже зарегистрирован на сайте";
        Assertions.assertEquals(text, sendMessage.getText());
        chatIdService.delete(1);
    }

    @Test
    void whenNotCorrectEmail() {
        Chat chat = new Chat(1L, "type");
        Message message = new Message();
        message.setChat(chat);
        message.setText("емайл без собачки и точки");
        RegAction regAction = new RegAction(tgCall, chatIdService, messageService, "www");
        BotApiMethod<Message> botApiMethod = regAction.callback(message);
        SendMessage sendMessage = (SendMessage) botApiMethod;
        String sl = System.lineSeparator();
        String text = "Email: емайл без собачки и точки не корректный." + sl
                + "попробуйте снова." + sl
                + "/new";
        Assertions.assertEquals(text, sendMessage.getText());
        chatIdService.delete(1);
    }

    @Test
    void whenNotConnection() {
        Chat chat = new Chat(1L, "type");
        Message message = new Message();
        message.setChat(chat);
        message.setText("a@a.ru");
        RegAction regAction = new RegAction(tgCall, chatIdService, messageService, "");
        BotApiMethod<Message> botApiMethod = regAction.callback(message);
        SendMessage sendMessage = (SendMessage) botApiMethod;
        String sl = System.lineSeparator();
        String text = "Сервис не доступен попробуйте позже" + sl
                + "/start";
        Assertions.assertEquals(text, sendMessage.getText());
    }

    @Disabled
    @Test
    void whenCallBackThenOk() {
        Chat chat = new Chat(1L, "type");
        Message message = new Message();
        message.setChat(chat);
        message.setText("mail@mail.ru");
        RegAction regAction = new RegAction(tgCall, chatIdService, messageService, "www");
        BotApiMethod<Message> botApiMethod = regAction.callback(message);
        SendMessage sendMessage = (SendMessage) botApiMethod;
        String text = "Вы зарегистрированы: \n"
                + "Имя: mail\n"
                + "Email: mail@mail.ru\n"
                + "Пароль : " + "password" + "\n"
                + "urlSiteAuth";
        Assertions.assertEquals(text, sendMessage.getText());
        chatIdService.delete(1);
    }
}