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

//    // SSE endpoint to stream notifications to the client
//    @GetMapping(value = "/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter streamNotifications() {
//        SseEmitter emitter = new SseEmitter();
//        new Thread(() -> {
//            try {
//                while (true) {
//                    // Take the notification from the queue and send it to the SSE client
//                    Notification notification = notificationQueue.take();
//                    emitter.send(notification, MediaType.APPLICATION_JSON);
//                }
//            } catch (IOException | InterruptedException e) {
//                emitter.completeWithError(e);
//            }
//        }).start();
//        return emitter;
//    }
    
    @GetMapping(value = "/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Set a long timeout
        new Thread(() -> {
            try {
                while (true) {
                    Notification notification = notificationQueue.take();
                    try {
                        emitter.send(notification, MediaType.APPLICATION_JSON);
                    } catch (IOException e) {
                        emitter.completeWithError(e); // Complete with error if send fails
                        break; // Exit loop to prevent further sends on a closed emitter
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
