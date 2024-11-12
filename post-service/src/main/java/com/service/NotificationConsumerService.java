package com.service;

import com.controller.NotificationSseController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private NotificationSseController notificationSseController;
    
    public NotificationConsumerService(NotificationSseController notificationSseController) {
    	this.notificationSseController=notificationSseController;
	}
    
    @KafkaListener(topics = "blog-notifications", groupId = "blog-group")
    public void listenToNotifications(String message) {
        try {
            Notification notification = objectMapper.readValue(message, Notification.class);
            // Handle the notification (display, store, etc.)
            System.out.println("Received notification: " + notification.getTitle());
            System.out.println("Received Content: " + notification.getContent());
            
            notificationSseController.addNotification(notification);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}