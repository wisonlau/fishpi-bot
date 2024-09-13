package io.dannio.fishpi.bot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FishpiBotTest {

    @Test
    void onWebhookUpdateReceived() {

    }


    @TestConfiguration
    static class TestBotConfig {

        @Bean
        DefaultBotOptions options() {
            final DefaultBotOptions options = new DefaultBotOptions();
            options.setBaseUrl("https://req-forword.herokuapp.com/bot");
            return options;
        }
    }
}