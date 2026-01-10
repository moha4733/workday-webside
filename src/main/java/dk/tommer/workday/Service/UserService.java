package dk.tommer.workday.Service;

import dk.tommer.workday.Entity.Role;
import dk.tommer.workday.Entity.User;
import dk.tommer.workday.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Vigtigt
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(String name, String email, String password, Role role){
        User user = new User();
        user.setName(name);
        user.setEmail(email);


        user.setPassword(passwordEncoder.encode(password));

        user.setRole(role);
        return userRepository.save(user);
    }
}