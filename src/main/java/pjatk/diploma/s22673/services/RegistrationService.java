package pjatk.diploma.s22673.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.repositories.EmployeeRepository;

@Service
@Transactional(readOnly = true)
public class RegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public RegistrationService(PasswordEncoder passwordEncoder, EmployeeRepository employeeRepository) {
        this.passwordEncoder = passwordEncoder;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public void register(Employee employee) {
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRepository.save(employee);
    }
}
