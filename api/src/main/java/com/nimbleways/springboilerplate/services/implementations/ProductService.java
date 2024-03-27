package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationService notificationService;


    public void handleNormalProduct(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                notifyDelay(leadTime, product);
            }
        }
    }

    public void handleSeasonalProduct(Product product) {
        if (LocalDate.now().isAfter(product.getSeasonStartDate()) && LocalDate.now().isBefore(product.getSeasonEndDate())
                && product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            handleSeasonalProductAvailability(product);
        }
    }

    public void handleExpirableProduct(Product product) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            handleExpiredProduct(product);
        }
    }

    public void handleFlashSaleProduct(Product product) {
        if (LocalDateTime.now().isAfter(product.getFlashSaleStartDate()) && LocalDateTime.now().isBefore(product.getFlashSaleEndDate())
                && product.getAvailable() > 0 && product.getAvailable() <= product.getMaxFlashSaleQuantity()) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            handleFlashSaleEnded(product);
        }
    }


    private void handleSeasonalProductAvailability(Product product) {
        if (LocalDate.now().plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
        } else if (product.getSeasonStartDate().isAfter(LocalDate.now())) {
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
        } else {
            notifyDelay(product.getLeadTime(), product);
        }
    }

    private void handleExpiredProduct(Product product) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);
        }
    }

    private void handleFlashSaleEnded(Product product) {
        notificationService.sendFlashSaleEndedNotification(product.getName());
        product.setAvailable(0);
        productRepository.save(product);
    }


    private void notifyDelay(int leadTime, Product product) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }

}