package io.dannio.fishpi.bot;

import io.dannio.fishpi.properties.BotProperties;
import io.dannio.fishpi.service.BotService;
import io.dannio.fishpi.service.ChatroomService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import javax.annotation.PostConstruct;

import static io.dannio.fishpi.util.JsonUtils.toJson;

@Slf4j
@Component
public class FishpiBot extends SpringWebhookBot {

    private final BotService service;

    private final ChatroomService chatroomService;

    private final BotProperties properties;


    public FishpiBot(SetWebhook setWebhook, BotService service, ChatroomService chatroomService, BotProperties properties) {
        super(setWebhook);
        this.service = service;
        this.properties = properties;
        this.chatroomService = chatroomService;
    }

    @PostConstruct
    @SneakyThrows
    public void initialize() {
        this.chatroomService.setAbsSender(this);
        this.chatroomService.setChatroomGroupId(this.properties.getSupergroupName() == null
                ? this.properties.getSupergroupId()
                : this.execute(GetChat.builder()
                .chatId("@" + this.properties.getSupergroupName())
                .build()).getId().toString());
    }


    @Override
    @SneakyThrows
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.trace("Webhook received update[{}]", toJson(update));

        try {
            service.receive(this, update);
        } catch (Exception e) {
            log.warn("An error occurred when receive a update", e);
            if (update.hasMessage()) {
                return SendMessage.builder()
                        .chatId(update.getMessage().getChat().getId().toString())
                        // 😵
                        .text("\uD83D\uDE35 出错了: " + e.getMessage())
                        .build();
            }
        }
        return null;
    }


    public String getFileUrl(String filePath) {
        return File.getFileUrl(this.getBotToken(), filePath);
    }

    @Override
    public String getBotPath() {
        return properties.getPath();
    }

    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }

}
