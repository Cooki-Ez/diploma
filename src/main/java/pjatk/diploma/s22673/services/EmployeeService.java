package pjatk.diploma.s22673.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pjatk.diploma.s22673.exceptions.DepartmentCannotBeEmptyWhenCreatingAnEmployee;
import pjatk.diploma.s22673.exceptions.EmployeeDoesNotExistException;
import pjatk.diploma.s22673.models.Department;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.repositories.EmployeeRepository;
import pjatk.diploma.s22673.security.PersonDetails;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;

    // Password pattern: at least one digit, one lowercase, one uppercase, one special character, no whitespace, 8-30 chars
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, DepartmentService departmentService, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.passwordEncoder = passwordEncoder;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.getEmployee();
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
}
