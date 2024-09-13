package io.dannio.fishpi.config;

import io.dannio.fishpi.CustomWebhook;
import io.dannio.fishpi.properties.BotProperties;
import io.dannio.fishpi.properties.WebhookProperties;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultWebhook;

@Slf4j
@AllArgsConstructor
@Configuration
public class BotConfig {

    private final WebhookProperties webhookProperties;

    private final BotProperties botProperties;


    @Bean
    public CommandRegistry commandRegistry() {
        return new CommandRegistry(true, botProperties::getPath);
    }


    @Bean
    public SetWebhook setWebhook() {
        return SetWebhook.builder().url(webhookProperties.getUrl()).build();
    }


    @Bean
    @ConditionalOnMissingBean
    @SneakyThrows
    public TelegramBotsApi telegramBotsApi(CustomWebhook customWebhook) {
        return new TelegramBotsApi(DefaultBotSession.class, customWebhook);
    }
}
