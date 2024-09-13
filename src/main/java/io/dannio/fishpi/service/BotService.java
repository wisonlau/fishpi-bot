package io.dannio.fishpi.service;

import io.dannio.fishpi.bot.FishpiBot;
import io.dannio.fishpi.commands.registry.BotCommandRegistry;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
@AllArgsConstructor
@Service
public class BotService {

    private final BotCommandRegistry registry;

    private final ChatroomService chatroom;

    @SneakyThrows
    public void receive(FishpiBot bot, Update update) {

        if (update.hasCallbackQuery()) {
            chatroom.openRedPacket(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.isSuperGroupMessage()) {
                chatroom.messageToFishPi(message);
            } else if (message.isCommand()) {
                if (!registry.executeCommand(bot, message)) {
                    //we have received a not registered command, handle it as invalid
                    answerMessage(bot, message.getChatId(), "Unknown command. Say what?");
                }
            } else {
                answerMessage(bot, message.getChatId(), "hello");
            }
        }
    }


    @SneakyThrows
    private void answerMessage(AbsSender absSender, Long chatId, String message) {
        absSender.execute(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(message)
                .build());
    }
}
