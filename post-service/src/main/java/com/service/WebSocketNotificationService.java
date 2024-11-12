package com.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate template;

    public WebSocketNotificationService(SimpMessagingTemplate template) {
        this.template = template;
    }

    @KafkaListener(topics = "blog-notifications", groupId = "blog-group-1")
    public void listen(String message) {
        template.convertAndSend("/topic/notification", message);
    }
}
