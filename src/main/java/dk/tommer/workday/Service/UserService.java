package dk.tommer.workday.Service;

import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Vigtigt
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(String name, String email, String password, Role role){
        logger.info("Creating new user with email: {} and role: {}", email, role);
        
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            logger.warn("User with email {} already exists. Updating instead.", email);
            User existingUser = userRepository.findByEmail(email).get();
            existingUser.setName(name);
            existingUser.setPassword(passwordEncoder.encode(password));
            existingUser.setRole(role);
            return userRepository.save(existingUser);
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        
        User savedUser = userRepository.save(user);
        logger.info("Created user with ID: {}", savedUser.getId());
        return savedUser;
    }
}