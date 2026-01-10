package dk.tommer.workday;

import dk.tommer.workday.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.
                csrf(csrf -> csrf.disable())  // For development only, enable in production
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/welcome", "/register", "/login", "/create-admin", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            logger.info("=== UserDetailsService: Attempting to load user with email: {} ===", username);
            return userRepository.findByEmail(username)
                    .map(user -> {
                        String role = user.getRole() != null ? user.getRole().name() : "USER";
                        logger.info("User found: {} with role: {}", user.getEmail(), role);
                        logger.info("User password hash (first 20 chars): {}", 
                                user.getPassword() != null && user.getPassword().length() > 20 
                                        ? user.getPassword().substring(0, 20) + "..." 
                                        : user.getPassword());
                        
                        // Convert role to the format Spring Security expects (ROLE_ prefix)
                        String authority = "ROLE_" + role;
                        logger.info("Granting authority: {}", authority);
                        
                        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                                .password(user.getPassword())
                                .authorities(authority)
                                .build();
                        
                        logger.info("UserDetails created successfully for: {}", username);
                        return userDetails;
                    })
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", username);
                        return new UsernameNotFoundException("User not found with email: " + username);
                    });
        };
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, 
                                                HttpServletResponse response, 
                                                Authentication authentication) throws IOException, ServletException {
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                
                boolean isAdmin = authorities.stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                
                if (isAdmin) {
                    logger.info("Admin user logged in, redirecting to admin dashboard");
                    response.sendRedirect("/admin/dashboard");
                } else {
                    logger.info("Regular user logged in, redirecting to user dashboard");
                    response.sendRedirect("/user/dashboard");
                }
            }
        };
    }

}
