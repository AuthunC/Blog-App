package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.model.Notification;
import com.service.NotificationProducerService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationProducerService notificationProducerService;

    @PostMapping("/{topic}")
    public String sendNotification(@PathVariable String topic, @RequestBody Notification notification) {
        try {
            notificationProducerService.sendNotification(topic, notification);
            return "Notification sent successfully!";
        } catch (Exception e) {
            return "Error sending notification: " + e.getMessage();
        }
    }
}
