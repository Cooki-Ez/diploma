package pjatk.diploma.s22673.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

@Service
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, DepartmentService departmentService) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
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
        Department department = employee.getDepartment();

        if (department == null || departmentService.findById(department.getId()) == null || departmentService.findByName(department.getName()) == null)
            throw new DepartmentCannotBeEmptyWhenCreatingAnEmployee("Department must be assigned to that employee");
        employeeRepository.save(employee);
    }

    @Transactional
    public void save(Employee employee, int id) {
        employee.setId(id);
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
}
