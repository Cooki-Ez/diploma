package pjatk.diploma.s22673.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthenticationDTO {

    @Email(message = "Please enter a valid email address", regexp = "^[A-Za-z0-9]+@[A-Za-z0-9]+.[A-Za-z0-9]+$")
    @NotNull(message = "Email can not be empty")
    @NotEmpty(message = "Email can not be empty")
    @Size(max = 100, message = "Email should be less than 100 characters")
    private String username;

    @NotEmpty
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
