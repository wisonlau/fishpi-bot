package io.dannio.fishpi.commands;

import io.dannio.fishpi.service.CommandService;
import io.github.danniod.fish4j.entites.FishPiUser;
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
public class LinkCommand extends BotCommand {

    private final CommandService service;


    public LinkCommand(CommandService service) {
        super("link", "link to your fishpi account.");
        this.service = service;
    }


    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        String reply;
        if (arguments.length == 0) {
            reply = "send '/link apiKey' to bing your fishpi account";
        } else {
            final FishPiUser fishPiUser = service.linkFishAccount(user, arguments[0]);
            if (fishPiUser == null) {
                reply = "apiKey was Incorrect or Expired";
            } else {
                // 🍺 cheers!
                reply = "\uD83C\uDF7A cheers!\nHello "
                        + (fishPiUser.getUserNickname() != null ? fishPiUser.getUserNickname() : fishPiUser.getUserName())
                        + ". Welcome to join [Chatroom](https://t.me/fishpi_cr).\n and you can send me /help to get manual";
            }
        }

        absSender.execute(SendMessage.builder()
                .chatId(chat.getId().toString())
                .text(reply)
                .parseMode(ParseMode.MARKDOWN).build());
    }
}
