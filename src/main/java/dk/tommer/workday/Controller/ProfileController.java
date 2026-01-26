package dk.tommer.workday.controller;

import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/profiles/";

    @GetMapping
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        model.addAttribute("user", user);
        return "profile-settings";
    }

    @PostMapping("/photo")
    public String uploadPhoto(@RequestParam("photo") MultipartFile file, 
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vælg venligst et billede");
            return "redirect:/profile";
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update user profile
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElseThrow();
            
            // Delete old photo if exists
            if (user.getProfilePhotoPath() != null) {
                try {
                    Path oldPhoto = Paths.get(UPLOAD_DIR + user.getProfilePhotoPath());
                    Files.deleteIfExists(oldPhoto);
                } catch (IOException e) {
                    // Ignore if old photo doesn't exist
                }
            }
            
            user.setProfilePhotoPath(filename);
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success", "Profilbillede opdateret!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Kunne ikke uploade billede: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}
