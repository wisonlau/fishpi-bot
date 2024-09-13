package io.dannio.fishpi.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("bot")
public @Data class BotProperties {

    private String username;

    private String token;

    private String path;

    private String supergroupId;

    private String supergroupName;

}
