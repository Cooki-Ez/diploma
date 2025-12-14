package pjatk.diploma.s22673.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.repositories.EmployeeRepository;
import pjatk.diploma.s22673.security.PersonDetails;

import java.util.Optional;

@Service
public class EmployeeDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Employee> optionalPerson = employeeRepository.findByEmail(username);
        if (optionalPerson.isEmpty())
            throw new UsernameNotFoundException("User not found");
        return new PersonDetails(optionalPerson.get());
    }
}