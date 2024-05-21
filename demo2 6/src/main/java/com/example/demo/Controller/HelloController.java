package com.example.demo.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HelloController {

    @GetMapping("/")
    public String showHomePage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            if (authentication.getAuthorities().stream().anyMatch(a -> "ROLE_Admin".equals(a.getAuthority()))) {
                return "redirect:/index";
            } else {
                return "redirect:/index";
            }
        } else {
            return "redirect:/login";
        }

    }
    @GetMapping("/index")
    public String indexPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the authenticated user's username
        model.addAttribute("username", username); // Add the username to the model

        return "index";
    }
}
