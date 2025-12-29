package pjatk.diploma.s22673.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.services.EmployeeService;

@Component
public class PersonValidator implements Validator {
    private final EmployeeService employeeService;

    @Autowired
    public PersonValidator(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Employee.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Employee employee = (Employee) target;
        if(employeeService.findByEmail(employee.getEmail()).isPresent())
            errors.rejectValue("email", "", "This email address is already in database");
    }
}
