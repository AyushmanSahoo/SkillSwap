package com.skillswap.notificationservice.controller;

import com.skillswap.notificationservice.dto.NotificationRequest;
import com.skillswap.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    /**
     * Endpoint for other services (like ExchangeService) to send an email.
     */
    @PostMapping("/email")
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest request) {
        // This is asynchronous. We don't wait for the email to be sent.
        // We just queue it up and return "OK" to the calling service.
        emailService.sendEmail(request);
        return ResponseEntity.ok().build();
    }
}