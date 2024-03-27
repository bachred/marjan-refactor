package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

// WARN: Should not be changed during the exercise
@Service
public class NotificationService {

    public void sendDelayNotification(int leadTime, String productName) {
    }

    public void sendOutOfStockNotification(String productName) {
    }

    public void sendExpirationNotification(String productName, LocalDate expiryDate) {
    }

    public void sendFlashSaleEndedNotification(String productName) {

        System.out.println("Notification : La vente flash pour le produit " + productName + " est terminée.");
    }
}