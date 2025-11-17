package com.example.authservice.serializer;

import com.example.authservice.eventProducer.UserInfoEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class UserInfoSerializer implements Serializer<UserInfoEvent> {
    @Override
    public byte[] serialize(String topic, UserInfoEvent data) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(data).getBytes();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  retVal;
    }
}
