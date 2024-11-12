package com.firebase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class FireBaseNotificationController {

    @Autowired
    private FireBaseNotificationService notificationService;

    @PostMapping("/send")
    public String sendNotification(@RequestParam String title, 
                                   @RequestParam String body, 
                                   @RequestParam String targetToken) {
        notificationService.sendNotification(title, body, targetToken);
        return "Notification sent";
    }
}

