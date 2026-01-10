package dk.tommer.workday.Controller;

import dk.tommer.workday.Entity.Role;
import dk.tommer.workday.Entity.User;
import dk.tommer.workday.Repo.UserRepository;
import dk.tommer.workday.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@org.springframework.stereotype.Controller
public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/create-admin")
    @ResponseBody
    public String createAdminUser() {
        String email = "admin@workday.dk";
        String password = "admin123";
        
        try {
            // Delete existing admin user if it exists
            userRepository.findByEmail(email).ifPresent(user -> {
                userRepository.delete(user);
                logger.info("Deleted existing admin user with email: {}", email);
            });
            
            // Create new admin user
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            
            User savedUser = userRepository.save(admin);
            logger.info("Created new admin user with ID: {} and email: {}", savedUser.getId(), email);
            
            return "<h2>Admin User Created Successfully</h2>" +
                   "<p>Email: <strong>" + email + "</strong></p>" +
                   "<p>Password: <strong>" + password + "</strong></p>" +
                   "<p><a href=\"/login\">Go to Login Page</a></p>";
                
        } catch (Exception e) {
            logger.error("Error creating admin user: {}", e.getMessage(), e);
            return "<h2>Error Creating Admin User</h2>" +
                   "<p>Error: " + e.getMessage() + "</p>" +
                   "<p><a href=\"/\">Go to Home</a></p>";
        }
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/")
    public String home(){
        return "welcome";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(){
        return "admin-dashboard";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(){
        return "user-dashboard";
    }
}
