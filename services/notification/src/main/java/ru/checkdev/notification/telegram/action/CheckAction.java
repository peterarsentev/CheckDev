package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.domain.Profile;
import ru.checkdev.notification.service.ChatIdService;
import ru.checkdev.notification.service.InnerMessageService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import ru.checkdev.notification.telegram.service.TgCall;

import java.sql.Timestamp;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class CheckAction implements Action {
    private static final String URL_AUTH_CURRENT = "/person/currentForTg/";
    private final TgCall tgCall;;
    private final ChatIdService chatIdService;
    private final InnerMessageService messageService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatIdString = message.getChatId().toString();
        var text = "";
        var out = new StringBuilder();
        String sl = System.lineSeparator();
        Optional<ChatId> chatIdOptional = chatIdService.findById(Integer.parseInt(chatIdString));
        if (chatIdOptional.isEmpty()) {
            text = "Данный аккаунт Telegram на сайте не зарегистрирован";
            return new SendMessage(chatIdString, text);
        } else {
            try {
                ChatId chatId = chatIdOptional.get();
                Profile profile = tgCall
                        .doGet(URL_AUTH_CURRENT + chatId.getEmail()).block();
                out.append("Имя:")
                        .append(sl)
                        .append(profile.getUsername())
                        .append(sl)
                        .append("Email:")
                        .append(sl)
                        .append(profile.getEmail())
                        .append(sl);
                InnerMessage innerMessage = new InnerMessage();
                innerMessage.setUserId(profile.getId());
                innerMessage.setText(out.toString());
                innerMessage.setCreated(new Timestamp(System.currentTimeMillis()));
                messageService.saveMessage(innerMessage);
                return new SendMessage(chatIdString, out.toString());
            } catch (Exception e) {
                log.error("WebClient doPost error: {}", e.getMessage());
                text = "Сервис не доступен попробуйте позже";
                return new SendMessage(chatIdString, text);
            }
        }
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}