package io.dannio.fishpi.config;

import io.dannio.fishpi.service.ChatroomService;
import io.github.danniod.fish4j.api.FishApi;
import io.github.danniod.fish4j.api.FishApiImpl;
import io.github.danniod.fish4j.client.WebSocketClient;
import io.github.danniod.fish4j.entites.ChatroomMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.dannio.fishpi.util.JsonUtils.toJson;

@Slf4j
@Configuration
public class FishApiConfig {

    private static final int[] RECONNECT_DELAYS = {10000, 30000, 60000};
    private static int reconnectTimes = 0;
    private static WebSocketClient webSocketClient;
    private static final ScheduledExecutorService EXECUTOR;

    static {
        EXECUTOR = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("ws-heartbeat-%d").daemon(true).build());
    }
    @Bean
    public FishApi fishApi(Retrofit retrofit) {
        return new FishApiImpl(retrofit);
    }


    @Bean
    public Retrofit retrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl("https://fishpi.cn/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }


    @Bean
    public WebSocket webSocket(OkHttpClient client, WebSocketClient webSocketClient) {
        Request request = new Request.Builder()
                .url("https://fishpi.cn/chat-room-channel")
                .build();
        return client.newWebSocket(request, webSocketClient);
    }


    @Bean
    public WebSocketClient webSocketClient(ChatroomService service) {

        webSocketClient = new WebSocketClient((webSocket, response) -> {
            EXECUTOR.scheduleAtFixedRate(() -> {
                webSocket.send("-hb-");
                log.debug("Chatroom websocket heartbeat");
            }, 0, 3, TimeUnit.MINUTES);
            reconnectTimes = 0;
        }, (webSocket, i, s) -> {
            EXECUTOR.shutdown();
            webSocket.close(i, s);
        }, (webSocket, throwable, response) -> {
            log.warn("websocket broken. onFailure", throwable);
            EXECUTOR.shutdown();
            webSocket.close(4999, throwable.getMessage());
            reconnect(service);
        }, (webSocket, message) -> messageToTelegramCaught(service, message));
        return webSocketClient;
    }

    private void messageToTelegramCaught(ChatroomService service, ChatroomMessage message) {
        try {
            log.debug("receive fishpi chatroom message[{}]", toJson(message));
            service.messageToTelegram(message);
        } catch (Exception e) {
            log.warn("drop bad message", e);
        }
    }


    @SneakyThrows
    private void reconnect(ChatroomService service) {
        if (reconnectTimes >= RECONNECT_DELAYS.length) {
            return;
        }
        int currentTimes = reconnectTimes++;
        Thread.sleep(RECONNECT_DELAYS[currentTimes]);
        log.debug("chatroom socket reconnect... try[{}]", currentTimes);
        webSocketClient(service);
    }

    public void reconnectChatroom(ChatroomService service) {
        reconnectTimes = 0;
        webSocketClient(service);
    }

    @Bean
    public OkHttpClient customUaClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(chain.request()
                        .newBuilder()
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36")
                        .build())
                ).build();
    }


}
