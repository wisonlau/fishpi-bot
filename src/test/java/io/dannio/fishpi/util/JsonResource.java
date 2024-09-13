package io.dannio.fishpi.util;

import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

public class JsonResource {

    @SneakyThrows
    public static byte[] fromResource(String resource) {
        return ByteStreams.toByteArray(new ClassPathResource(resource).getInputStream());
    }

}
