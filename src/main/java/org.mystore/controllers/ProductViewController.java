package org.mystore.controllers;

import org.mystore.dtos.ProductDTO;
import org.mystore.dtos.UserDTO;
import org.mystore.models.User;
import org.mystore.services.IAuthService;
import org.mystore.services.ProductService;
import org.mystore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/products/view")  // Base path for all product views
public class ProductViewController {
    @Autowired
    private IAuthService authService;

    private final ProductService productService;

    public ProductViewController(ProductService productService) {
        this.productService = productService;
    }

    // Show all products page
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public String showAllProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products"; // templates/products/list.html
    }

    // Show create product page

    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @GetMapping("/create")
    public String showCreateProductForm(@CookieValue("token") String token, Model model) {

        // Step 1: Extract user info (id + email) from token
        UserDTO user = authService.getUserFromToken(token);
        if (user == null) {
            // token might be invalid or expired
            return "redirect:/login";
        }

        // Step 2: Create a ProductDTO and set the sellerId
        ProductDTO productDTO = new ProductDTO();
        productDTO.setSellerId(user.getId());

        // Step 3: Add to model
        model.addAttribute("product", productDTO);

        // ✅ Add dashboardUrl to model so Thymeleaf can use it
        String dashboardUrl = user.getRoles().contains("ADMIN") ? "/admin/dashboard" : "/seller/dashboard";
        model.addAttribute("dashboardUrl", dashboardUrl);

        return "create-product"; // templates/products/create.html
    }

    // Show edit product page
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "edit-product"; // templates/products/edit.html
    }

    // Show product details
    @GetMapping("/{id}")
    public String showProductDetails(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "product_detail"; // templates/products/details.html
    }
}
