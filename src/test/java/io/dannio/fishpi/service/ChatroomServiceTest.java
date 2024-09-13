package io.dannio.fishpi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dannio.fishpi.bot.FishpiBot;
import io.dannio.fishpi.config.BotConfig;
import io.dannio.fishpi.config.FishApiConfig;
import io.github.danniod.fish4j.entites.ChatroomMessage;
import io.github.danniod.fish4j.entites.chatroom.ChatMessage;
import io.github.danniod.fish4j.entites.chatroom.RedPacketMessage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static io.dannio.fishpi.util.JsonResource.fromResource;

@ActiveProfiles("dev")
@SpringBootTest
class ChatroomServiceTest {

    @Autowired
    private ChatroomService service;


    @Test
    @SneakyThrows
    void testMessage() {
        final ChatroomMessage message = new ObjectMapper().readValue(fromResource("fishpi/message.json"), ChatMessage.class);
        service.messageToTelegram(message);
    }


    @Test
    @SneakyThrows
    void testRedPacket() {
        final ChatroomMessage message = new ObjectMapper().readValue(fromResource("fishpi/redPacket.json"), RedPacketMessage.class);
        service.messageToTelegram(message);
    }
}
