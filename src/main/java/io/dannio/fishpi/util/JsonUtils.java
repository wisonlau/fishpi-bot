package io.dannio.fishpi.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonUtils {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.setSerializationInclusion(Include.NON_NULL);
    }


    @SneakyThrows
    public static String toJson(Object object) {
        return MAPPER.writeValueAsString(object);
    }


    @SneakyThrows
    public static  <T> T toType(String json, Class<T> type) {
        return MAPPER.readValue(json, type);
    }

}
