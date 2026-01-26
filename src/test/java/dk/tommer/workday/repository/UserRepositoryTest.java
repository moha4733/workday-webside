package dk.tommer.workday.repository;

import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.SVEND);
        testUser.setWorkHours(37.5);
        
        userRepository.save(testUser);
    }

    @Test
    void findByEmail_existingEmail_returnsUser() {
        Optional<User> found = userRepository.findByEmail("test@example.com");
        
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getRole()).isEqualTo(Role.SVEND);
    }

    @Test
    void findByEmail_nonExistingEmail_returnsEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        assertThat(found).isEmpty();
    }

    @Test
    void findByRole_existingRole_returnsUsers() {
        // Create another user with same role
        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("password");
        anotherUser.setRole(Role.SVEND);
        userRepository.save(anotherUser);
        
        List<User> svends = userRepository.findByRole(Role.SVEND);
        
        assertThat(svends).hasSize(2);
        assertThat(svends).extracting(User::getName).contains("Test User", "Another User");
    }

    @Test
    void findByRole_adminRole_returnsAdmins() {
        // Create admin user
        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        
        assertThat(admins).hasSize(1);
        assertThat(admins.get(0).getName()).isEqualTo("Admin User");
        assertThat(admins.get(0).getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void existsByEmail_existingEmail_returnsTrue() {
        boolean exists = userRepository.existsByEmail("test@example.com");
        
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_nonExistingEmail_returnsFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        
        assertThat(exists).isFalse();
    }

    @Test
    void save_validUser_savesUser() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");
        newUser.setPassword("newpassword");
        newUser.setRole(Role.SVEND);
        
        User saved = userRepository.save(newUser);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New User");
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
        assertThat(saved.getRole()).isEqualTo(Role.SVEND);
    }

    @Test
    void deleteById_existingUser_deletesUser() {
        long userId = testUser.getId();
        
        userRepository.deleteById(userId);
        
        Optional<User> deleted = userRepository.findById(userId);
        assertThat(deleted).isEmpty();
    }

    @Test
    void findAll_returnsAllUsers() {
        // Create another user
        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("password");
        anotherUser.setRole(Role.SVEND);
        userRepository.save(anotherUser);
        
        List<User> users = userRepository.findAll();
        
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail).contains("test@example.com", "another@example.com");
    }
}
