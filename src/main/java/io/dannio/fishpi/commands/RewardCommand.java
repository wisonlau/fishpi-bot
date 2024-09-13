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
 *
 */
@Slf4j
@Component
public class RewardCommand extends BotCommand {

    private final CommandService service;


    public RewardCommand(CommandService service) {
        super("reward", "Collect reward of liveness yesterday in fishpi.");
        this.service = service;
    }


    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        String answer;
        final int reward = service.collectReward(user);

        if (reward == -1) {
            answer = "Already collected! Recollect tomorrow";
        } else {
            // 🪙
            answer = "\uD83E\uDE99" + reward;
        }

        absSender.execute(SendMessage.builder()
                .chatId(chat.getId().toString())
                .text(answer)
                .parseMode(ParseMode.MARKDOWN).build());
    }
}
