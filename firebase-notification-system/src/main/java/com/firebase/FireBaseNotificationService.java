package com.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FireBaseNotificationService {

    public void sendNotification(String title, String body, String targetToken) {
        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
            System.out.println("Notification sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send notification");
        }
    }
}
