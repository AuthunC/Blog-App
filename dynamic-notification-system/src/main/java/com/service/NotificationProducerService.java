package com.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducerService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public void sendNotification(String topic, Notification notification) throws JsonProcessingException {
        String notificationJson = objectMapper.writeValueAsString(notification);
        kafkaTemplate.send(topic, notificationJson);
    }
    
    // Basic code to publish to kafka topic
    public void publish(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
