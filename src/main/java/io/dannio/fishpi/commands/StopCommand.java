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
 * This commands stops the conversation with the bot.<br/>
 * Bot won't respond to user until he sends a start command
 *
 */
@Slf4j
@Component
public class StopCommand extends BotCommand {


    public StopCommand() {
        super("stop", "With this command you can stop the Bot");
    }


    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        absSender.execute(SendMessage.builder()
                .chatId(chat.getId().toString())
                .text("Good bye" + user.getUserName() +".\n Hope to see you soon!")
                .parseMode(ParseMode.HTML).build());
    }
}