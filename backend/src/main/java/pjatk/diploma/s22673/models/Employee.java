package pjatk.diploma.s22673.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pjatk.diploma.s22673.util.RoleConverter;

import java.time.LocalDate;
import java.time.Period;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    @NotNull(message = "Name cannot be empty")
    @NotEmpty(message = "Name cannot be empty")
    @Size(max = 30, message = "Name cannot be longer than 30 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Name must contain only letters")
    private String name;

    @Column(name = "surname")
    @NotNull(message = "Surname cannot be empty")
    @NotEmpty(message = "Surname cannot be empty")
    @Size(max = 30, message = "Surname cannot be longer than 30 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Surname must contain only letters")
    private String surname;

    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "Please enter a valid email address")
    @NotNull(message = "Email can not be empty")
    @NotEmpty(message = "Email can not be empty")
    @Size(max = 100, message = "Email should be less than 100 characters")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "date_of_birth")
    @NotNull
    private LocalDate dateOfBirth;

    @Column(name = "salary")
    @DecimalMin(value = "500", message = "Salary cannot be lower than 500PLN")
    private Double salary;

    @Column(name = "points")
    @DecimalMin(value = "0")
    private int points;

    @Convert(converter = RoleConverter.class)
    @Column(name = "roles")
    private EnumSet<EmployeeRole> roles;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

    @OneToMany(mappedBy = "employee")
    private List<LeaveRequest> leaveRequests;

    @OneToMany(mappedBy = "manager")
    private List<LeaveRequest> approvedLeaveRequests;

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<LeaveEvaluation>  leaveEvaluations;

    @ManyToMany(mappedBy = "employees", fetch =  FetchType.EAGER)
    @JsonManagedReference
    private List<Project> projects;

    public Employee(String name, String surname, double salary, EnumSet<EmployeeRole> roles) {
        this.name = name;
        this.surname = surname;
        this.salary = salary;
        this.roles = roles;
    }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}




