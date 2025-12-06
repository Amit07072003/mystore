package org.mystore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductPageController {
    @GetMapping("/product-detail")
    public String productDetailPage() {
        // Returns the Thymeleaf template from src/main/resources/templates/product-detail.html
        return "product_detail";
    }
}
