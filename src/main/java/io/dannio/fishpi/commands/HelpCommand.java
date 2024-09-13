package io.dannio.fishpi.commands;

import io.dannio.fishpi.commands.registry.BotCommandRegistry;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.stream.Collectors;

/**
 * This command helps the user to find the command they need
 *
 */
@Slf4j
@Component
public class HelpCommand extends BotCommand {

    public static final String COMMAND_IDENTIFIER = "help";

    @Setter
    private BotCommandRegistry commandRegistry;


    public HelpCommand() {
        super(COMMAND_IDENTIFIER, "Get all the commands this bot provides");
    }


    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        String header = "<b>Help</b>\nThese are the registered commands for this Bot:\n\n";

        final String text = commandRegistry.getRegisteredCommands().stream()
                .filter(this::filter)
                .sorted(this::sort)
                .map(Object::toString)
                .collect(Collectors.joining("\n\n"));

        absSender.execute(SendMessage.builder()
                .chatId(chat.getId().toString())
                .text(header + text)
                .parseMode(ParseMode.HTML).build());
    }


    private boolean filter(IBotCommand iBotCommand) {
        return !(iBotCommand instanceof StartCommand)
                && !(iBotCommand instanceof StopCommand);
    }


    private int sort(IBotCommand iBotCommand, IBotCommand iBotCommand1) {
        if (iBotCommand1 != null && HelpCommand.COMMAND_IDENTIFIER.equals(iBotCommand1.getCommandIdentifier())) {
            return -1;
        }
        return 0;
    }
}
