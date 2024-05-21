package com.example.demo.Controller;


import com.example.demo.DTOs.RegistrationDto;
import com.example.demo.Model.User;
import com.example.demo.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
public class AuthController {
    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        RegistrationDto user = new RegistrationDto();
        model.addAttribute("user", user);
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("file") MultipartFile file,
                               @Valid @ModelAttribute("user") RegistrationDto userDto,
                               BindingResult bindingResult, Model model) {
        User existingUserByEmail = userService.findByEmail(userDto.getEmail());
        if (existingUserByEmail != null) {
            bindingResult.rejectValue("email", "error.user", "There is already an account registered with this email.");
        }
        if (!userDto.isPasswordMatch()) {
            bindingResult.rejectValue("rePassword", "error.user", "Passwords don't match");
        }
        User existingUserByUsername = userService.findByUsername(userDto.getUsername());
        if (existingUserByUsername != null) {
            bindingResult.rejectValue("username", "error.user", "There is already a user registered with this username.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDto);
            return "auth/register";
        }

        try {
            // Resmi kaydetme işlemi
            String fileName = saveImage(file);
            userDto.setProfilePicture(fileName);
        } catch (IOException e) {
            bindingResult.rejectValue("file", "error.user", "Could not save image file: " + e.getMessage());
            model.addAttribute("user", userDto);
            return "auth/register";
        }

        // DTO'dan Model'e Dönüştürme
        userService.saveUser(userDto);

        return "redirect:/?success";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "auth/login";
    }
    @GetMapping("/access-denied")
    public String accessDenied(){
        return "auth/access-denied";
    }
    private String saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("src/main/resources/static/user-photos/");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Could not save image file: " + fileName, e);
        }
        return fileName;
    }

}