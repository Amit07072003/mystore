package org.mystore.controllers;

import org.mystore.dtos.CategoryDTO;
import org.mystore.dtos.OrderDTO;
import org.mystore.dtos.UserDTO;
import org.mystore.services.CategoryService;
import org.mystore.services.OrderService;
import org.mystore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final CategoryService categoryService;
    private final OrderService orderService;

    private final UserService userService;


    public AdminController(OrderService orderService, UserService userService, CategoryService categoryService) {
        this.orderService = orderService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    // Example: Admin dashboard
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/admin_dashboard";
    }


    // Get all users (ADMIN)

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        System.out.println("getAllUsers:");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Update user roles (ADMIN)
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserRoles(@PathVariable Long id, @RequestBody List<String> roles) {
        return ResponseEntity.ok(userService.updateUserRoles(id, roles));
    }

    // ✅ Get all orders for admin
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders(); // fetch all orders from service
        System.out.println("Admin fetching all orders, count: " + orders.size());
        return ResponseEntity.ok(orders);
    }

    // 👉 Load Create Category Form Page
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateCategoryPage(Model model) {
        model.addAttribute("category", new CategoryDTO());
        return "create-category"; // Name of Thymeleaf file
    }
    // ✅ Load Edit Page

    @GetMapping("/category/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editCategoryPage(@PathVariable Long id, Model model) {
        CategoryDTO category = categoryService.getCategotyById(id);
        model.addAttribute("category", category);
        return "edit-category";  // edit-category.html
    }



//    // Example: Manage categories
//    @PostMapping("/categories")
//    public String addCategory() {
//        return "Category added";
//    }
//
//    @PutMapping("/categories/{id}")
//    public String updateCategory(@PathVariable Long id) {
//        return "Category updated: " + id;
//    }
//
//    @DeleteMapping("/categories/{id}")
//    public String deleteCategory(@PathVariable Long id) {
//        return "Category deleted: " + id;
//    }

    // Add more admin-specific endpoints here
}
