package dk.tommer.workday.controller;

import dk.tommer.workday.dto.UserRegistrationDTO;
import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder,UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }


    @GetMapping("/welcome")
    public String showWelcomePage() {
        return "welcome";
    }



    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegistrationDTO userDTO) {
        // Her kalder vi servicen i stedet for at gøre alt arbejdet i controlleren
        // Nye brugere er SVEND (der er kun én ADMIN, som allerede findes)
        userService.createUser(userDTO.getName(), userDTO.getEmail(), userDTO.getPassword(), Role.SVEND);
        return "redirect:/login?success";
    }
}