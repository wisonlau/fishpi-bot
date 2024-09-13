package io.dannio.fishpi.commands;

import io.dannio.fishpi.service.CommandService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * This commands link the account of fishpi site with the bot
 */
@Slf4j
@Component
public class LivenessCommand extends BotCommand {

    private final CommandService service;


    public LivenessCommand(CommandService service) {
        super("liveness", "Get liveness present in fishpi.");
        this.service = service;
    }


    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        final Double liveness = service.getLiveness(user);

        absSender.execute(SendMessage.builder()
                .chatId(chat.getId().toString())
                .text(liveness + "%")
                .parseMode(ParseMode.MARKDOWN).build());
    }
}
