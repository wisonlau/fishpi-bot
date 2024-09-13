package io.dannio.fishpi.commands;

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
 * This commands starts the conversation with the bot
 *
 */
@Slf4j
@Component
public class StartCommand extends BotCommand {


    public StartCommand() {
        super("start", "With this command you can start the Bot");
    }


    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        absSender.execute(SendMessage.builder()
                .chatId(chat.getId().toString())
                .text("hello world")
                .parseMode(ParseMode.HTML).build());
    }
}