package pjatk.diploma.s22673.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pjatk.diploma.s22673.dto.EmployeeDTO;
import pjatk.diploma.s22673.dto.ProjectDTO;
import pjatk.diploma.s22673.exceptions.DepartmentCannotBeEmptyWhenCreatingAnEmployee;
import org.modelmapper.ModelMapper;
import pjatk.diploma.s22673.exceptions.EmployeeDoesNotExistException;
import pjatk.diploma.s22673.models.Department;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.models.Project;
import pjatk.diploma.s22673.repositories.EmployeeRepository;
import pjatk.diploma.s22673.security.PersonDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;
    private final ProjectService projectService;
    private final ModelMapper modelMapper;

    // Password pattern: at least one digit, one lowercase, one uppercase, one special character, no whitespace, 8-30 chars
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=\\S+$).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, DepartmentService departmentService, PasswordEncoder passwordEncoder,
                           ProjectService projectService, ModelMapper modelMapper, EmployeeDetailsService employeeDetailsService) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.passwordEncoder = passwordEncoder;
        this.projectService = projectService;
        this.modelMapper = modelMapper;
    }

    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() < 8 || password.length() > 30) {
            throw new IllegalArgumentException("Password must be between 8 and 30 characters");
        }
        if (!pattern.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace");
        }
    }

    public Employee getCurrentLoggedInEmployee() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


    }

    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Transactional
    public void save(Employee employee) {
        Employee currentLoggedInEmployee = getCurrentLoggedInEmployee();
        Department department = currentLoggedInEmployee.getDepartment();

        employee.setDepartment(department);

        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            validatePassword(employee.getPassword());
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        employeeRepository.save(employee);
    }

    @Transactional
    public void saveWithDetails(Employee employee, EmployeeDTO employeeDTO) {
        if (employeeDTO.getDepartment() != null && employeeDTO.getDepartment().getId() != 0) {
            Department department = departmentService.findById(employeeDTO.getDepartment().getId());
            employee.setDepartment(department);
        }

        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            validatePassword(employee.getPassword());
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        employeeRepository.save(employee);

        List<Project> savedProjects = new ArrayList<>();
        if (employeeDTO.getProjects() != null && !employeeDTO.getProjects().isEmpty()) {
            for (ProjectDTO projectDTO : employeeDTO.getProjects()) {
                Project project = convertToProject(projectDTO);
                List<Employee> employees = new ArrayList<>();
                employees.add(employee);
                project.setEmployees(employees);
                Project savedProject = projectService.save(project);
                savedProjects.add(savedProject);
            }
            employee.setProjects(savedProjects);
        }
    }

    @Transactional
    public void save(Employee employee, int id) {
        employee.setId(id);

        // Encrypt password before saving if it's provided
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        employeeRepository.save(employee);
    }

    @Transactional
    public void delete(int id) {
        employeeRepository.deleteById(id);
    }

    public Employee findOne(int id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null)
            throw new EmployeeDoesNotExistException("Employee with id " + id + " does not exist");
        return employee;
    }

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    void evaluatePoints() {
        List<Employee> employees = findAll();
        for (Employee employee : employees) {
            employee.setPoints(employee.getPoints() + 1);
            employeeRepository.save(employee);
        }
    }

    @Transactional
    public void addPoints(int employeeId, int points) {
        Employee employee = findOne(employeeId);
        employee.setPoints(employee.getPoints() + points);
        employeeRepository.save(employee);
    }

    private Project convertToProject(ProjectDTO projectDTO) {
        return modelMapper.map(projectDTO, Project.class);
    }
}
