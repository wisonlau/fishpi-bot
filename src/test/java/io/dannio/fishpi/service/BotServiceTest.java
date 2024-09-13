package io.dannio.fishpi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dannio.fishpi.bot.FishpiBot;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Update;

import static io.dannio.fishpi.util.JsonResource.fromResource;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest
class BotServiceTest {

    @Autowired
    private BotService service;

    @Autowired
    private FishpiBot bot;


    @SneakyThrows
    @Test
    public void testReplyMarkup() {
        final Update update = new ObjectMapper().readValue(fromResource("telegram/replyMarkup.json"), Update.class);
        service.receive(bot, update);
    }


    @SneakyThrows
    @Test
    public void testOpenRedPacket() {
        final Update update = new ObjectMapper().readValue(fromResource("telegram/openRedPacket.json"), Update.class);
        service.receive(bot, update);
    }


    @SneakyThrows
    @Test
    public void testReply() {
        final Update update = new ObjectMapper().readValue(fromResource("telegram/reply.json"), Update.class);
        service.receive(bot, update);
    }
}
