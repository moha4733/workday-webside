package dk.tommer.workday.repository;

import dk.tommer.workday.entity.User;
import dk.tommer.workday.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailWithDetails(String email);
    
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
    boolean existsByEmailQuery(String email);
    
    @Query("SELECT u FROM User u WHERE u.role = :role ORDER BY u.name ASC")
    List<User> findByRoleOrderByRole(Role role);
}
