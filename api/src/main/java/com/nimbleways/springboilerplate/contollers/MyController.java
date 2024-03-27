package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class MyController {
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("{orderId}/processOrder")
    @ResponseStatus(HttpStatus.OK)
    public ProcessOrderResponse processOrder(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        System.out.println(order);

        Set<Product> products = order.getItems();
        for (Product product : products) {
            processProduct(product);
        }

        return new ProcessOrderResponse(order.getId());
    }

    private void processProduct(Product product) {
        switch (product.getType()) {
            case ProductType.NORMAL:
                productService.handleNormalProduct(product);
                break;
            case ProductType.SEASONAL:
                productService.handleSeasonalProduct(product);
                break;
            case ProductType.EXPIRABLE:
                productService.handleExpirableProduct(product);
                break;
            case ProductType.FLASHSALE:
                productService.handleFlashSaleProduct(product);
                break;
            default:
                break;
        }
    }
}
