package pjatk.diploma.s22673.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import pjatk.diploma.s22673.models.EmployeeRole;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class EmployeeDTO {
    private int id;

    @Setter
    @NotNull(message = "Name cannot be empty")
    @NotEmpty(message = "Name cannot be empty")
    @Size(max = 30, message = "Name cannot be longer than 30 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Name must contain only letters")
    private String name;

    @Setter
    @NotNull(message = "Surname cannot be empty")
    @NotEmpty(message = "Surname cannot be empty")
    @Size(max = 30, message = "Surname cannot be longer than 30 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Surname must contain only letters")
    private String surname;

    @Email(message = "Please enter a valid email address")
    @NotNull(message = "Email can not be empty")
    @NotEmpty(message = "Email can not be empty")
    @Size(max = 100, message = "Email should be less than 100 characters")
    private String email;

    @Setter
    @NotNull
    private String password;

    @NotNull
    private LocalDate dateOfBirth;

    private int points;

    private Set<EmployeeRole> roles;

    private DepartmentDTO department;

    private List<ProjectDTO> projects;
}
