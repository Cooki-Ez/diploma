package pjatk.diploma.s22673.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public class EmployeeDTO {
    @NotNull(message = "Name cannot be empty")
    @NotEmpty(message = "Name cannot be empty")
    @Size(max = 30, message = "Name cannot be longer than 30 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Name must contain only letters")
    private String name;

    @NotNull(message = "Surname cannot be empty")
    @NotEmpty(message = "Surname cannot be empty")
    @Size(max = 30, message = "Surname cannot be longer than 30 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Surname must contain only letters")
    private String surname;

    @Email(message = "Please enter a valid email address", regexp = "^[A-Za-z0-9]+@[A-Za-z0-9]+.[A-Za-z0-9]+$")
    @NotNull(message = "Email can not be empty")
    @NotEmpty(message = "Email can not be empty")
    @Size(max = 100, message = "Email should be less than 100 characters")
    private String Email;

    @NotNull
    private String password;

    @NotNull
    @Pattern(
    regexp = "^(0[1-9]|[12][0-9]|3[01])([./])(0[1-9]|1[0-2])\\2(\\d{4})$",
    message = "Date of birth must be in format dd.MM.yyyy or dd/MM/yyyy"
    )
    private LocalDate dateOfBirth;

    private int points;

    private List<ProjectDTO> projects;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
    }
}
