package io.dannio.fishpi.config;

import io.dannio.fishpi.CustomWebhook;
import io.dannio.fishpi.bot.FishpiBot;
import io.dannio.fishpi.properties.BotProperties;
import io.dannio.fishpi.service.BotService;
import io.dannio.fishpi.service.ChatroomService;
import io.github.danniod.fish4j.client.WebSocketClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageId;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.WebhookBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultWebhook;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Configuration
public class JunitTestConfig {


    @Bean
    @Primary
    @SneakyThrows
    public TelegramBotsApi telegramBotsApi(CustomWebhook webhook) {
        return new TelegramBotsApi(DefaultBotSession.class, webhook) {
            @Override
            public void registerBot(WebhookBot bot, SetWebhook setWebhook) {
                log.info("cancel register bot in junit test");
            }
        };
    }


    @Bean
    @Primary
    public FishpiBot fishpiBot(SetWebhook setWebhook, BotService service, ChatroomService chatroomService, BotProperties properties) {
        return new FishpiBot(setWebhook, service, chatroomService, properties) {
            @Override
            public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
                assertNotNull(method);
                log.info("pass execute method in test case");
                final Message message = new Message();
                message.setMessageId(-1);
                return (T) message;
            }
        };
    }


    @Bean
    @Primary
    public WebSocket webSocket(OkHttpClient client, WebSocketClient webSocketClient) {
        Request request = new Request.Builder()
                .url("https://localhost:8080/chat-room-channel")
                .build();
        return client.newWebSocket(request, webSocketClient);
    }

}
