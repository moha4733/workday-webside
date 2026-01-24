package dk.tommer.workday.config;

import dk.tommer.workday.repository.UserRepository;
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
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity(prePostEnabled = true)
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
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/welcome", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/svend/**").hasRole("SVEND")
                        .requestMatchers("/api/svend/**", "/api/dashboard/svend").hasRole("SVEND")
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
                        // Appen kører kun med ADMIN + SVEND. Hvis der ligger gamle USER/null roller i DB,
                        // behandles de som SVEND for at undgå 500/403 efter login.
                        String role = user.getRole() != null ? user.getRole().name() : "SVEND";
                        if ("USER".equals(role)) role = "SVEND";
                        logger.info("User found: {} with role: {}", user.getEmail(), role);
                        // REMOVED PASSWORD LOGGING FOR SECURITY
                        
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
                boolean isSvend = authorities.stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_SVEND"));
                boolean isUser = authorities.stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
                
                if (isAdmin) {
                    logger.info("Admin user logged in, redirecting to admin dashboard");
                    response.sendRedirect("/admin/dashboard");
                } else {
                    // Default: SVEND dashboard
                    logger.info("User logged in, redirecting to svend dashboard page");
                    response.sendRedirect("/svend/dashboard");
                }
            }
        };
    }

}
