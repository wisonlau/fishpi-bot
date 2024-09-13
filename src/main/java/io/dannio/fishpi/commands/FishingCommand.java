package io.dannio.fishpi.commands;

import io.dannio.fishpi.service.CommandService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * This commands check the ApiKey is available.
 */
@Slf4j
@Component
public class FishingCommand extends BotCommand {

    private final CommandService service;

    public FishingCommand(CommandService service) {
        super("fishing", "Check the ApiKey which linked fishpi account is availed or not.");
        this.service = service;
    }


    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        String answer = service.pingFishpi(user)
                // 🎣
                ? "\uD83C\uDFA3"
                : "use /link command to update apiKey";

        absSender.execute(SendMessage.builder()
                .chatId(chat.getId().toString())
                .text(answer)
                .build());
    }
}
