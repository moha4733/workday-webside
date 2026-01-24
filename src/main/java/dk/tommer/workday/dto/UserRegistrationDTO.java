package dk.tommer.workday.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDTO {

    @NotBlank(message = "Navn er påkrævet")
    private String name;

    @NotBlank(message = "Email er påkrævet")
    @Email(message = "Ugyldig email format")
    private String email;

    @NotBlank(message = "Adgangskode er påkrævet")
    @Size(min = 6, message = "Adgangskode skal være mindst 6 tegn")
    private String password;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
