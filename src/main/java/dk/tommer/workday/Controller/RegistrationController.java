package dk.tommer.workday.Controller;

import dk.tommer.workday.Entity.Role;
import dk.tommer.workday.Entity.User;
import dk.tommer.workday.Repo.UserRepository;
import dk.tommer.workday.Service.UserService;
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
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        // Her kalder vi servicen i stedet for at gøre alt arbejdet i controlleren
        userService.createUser(user.getName(), user.getEmail(), user.getPassword(), Role.USER);
        return "redirect:/login?success";
    }
}