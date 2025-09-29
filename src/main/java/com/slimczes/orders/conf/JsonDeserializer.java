package com.slimczes.orders.conf;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Deserializer;

public class JsonDeserializer<T> implements Deserializer<T> {
    private ObjectMapper objectMapper;
    private Class<T> targetType;

    public JsonDeserializer() {
    }

    public JsonDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure(Map<String, ?> configs, boolean isKey) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        if (targetType == null) {
            String typeConfig = (String) configs.get("value.type");
            if (typeConfig != null) {
                try {
                    targetType = (Class<T>) Class.forName(typeConfig);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Cannot find class: " + typeConfig, e);
                }
            }
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null || targetType == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, targetType);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing object", e);
        }
    }
}

