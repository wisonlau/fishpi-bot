package io.dannio.fishpi.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

import static io.dannio.fishpi.util.FileUtils.mkdirIfNotExists;

@Component
@ConfigurationProperties("app.data")
public @Data
class DataProperties {

    private String telegram;

    private String fishpi;


    @PostConstruct
    public void initialize() {
        mkdirIfNotExists(new File(this.telegram));
        mkdirIfNotExists(new File(this.fishpi));
    }

}
