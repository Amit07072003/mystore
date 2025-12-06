package org.mystore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Aboutcontroller {

    @GetMapping("/about")
    public String aboutPage() {
        // Return the name of the Thymeleaf template (about.html)
        return "about";
    }
}
