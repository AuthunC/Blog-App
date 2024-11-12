package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.model.Notification;
import com.service.NotificationConsumerService;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationSseController {

    private final BlockingQueue<Notification> notificationQueue = new LinkedBlockingQueue<>();

    @Autowired
    private NotificationConsumerService notificationConsumerService;
    
    @GetMapping(value = "/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        new Thread(() -> {
            try {
                while (true) {
                    Notification notification = notificationQueue.take();
                    try {
                        emitter.send(notification, MediaType.APPLICATION_JSON);
                    } catch (IOException e) {
                        emitter.completeWithError(e); 
                        break; 
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                emitter.completeWithError(e);
            }
        }).start();
        
        emitter.onCompletion(() -> {
            System.out.println("Emitter completed");
        });

        emitter.onTimeout(() -> {
            System.out.println("Emitter timed out");
            emitter.complete();
        });

        return emitter;
    }


    // Add received Kafka notifications to the queue
    public void addNotification(Notification notification) {
        notificationQueue.offer(notification);
    }
}
