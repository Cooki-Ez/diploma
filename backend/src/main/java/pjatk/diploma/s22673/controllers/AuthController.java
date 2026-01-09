package pjatk.diploma.s22673.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pjatk.diploma.s22673.dto.AuthenticationDTO;
import pjatk.diploma.s22673.dto.EmployeeDTO;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.models.EmployeeRole;
import pjatk.diploma.s22673.security.JWTUtil;
import pjatk.diploma.s22673.services.EmployeeService;
import pjatk.diploma.s22673.services.RegistrationService;
import pjatk.diploma.s22673.util.PersonValidator;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final RegistrationService registrationsService;
    private final PersonValidator personValidator;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final EmployeeService employeeService;

    @Autowired
    public AuthController(RegistrationService registrationsService, PersonValidator personValidator, JWTUtil jwtUtil,
                        ModelMapper modelMapper, AuthenticationManager authenticationManager, EmployeeService employeeService) {
        this.registrationsService = registrationsService;
        this.personValidator = personValidator;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.employeeService = employeeService;
    }

    @PostMapping("/login")
    public Map <String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(), authenticationDTO.getPassword());

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            return Map.of("message", "Invalid credentials");
        }

        String token = jwtUtil.generateToken(authenticationDTO.getUsername());
        return Map.of("jwt-token", token);
    }

    @PostMapping("/logout")
    public Map<String, String> performLogout() {
        return Map.of("message", "Logged out successfully");
    }

    @PostMapping("/registration")
    public Map<String, String> performRegistration(@RequestBody @Valid EmployeeDTO employeeDTO, BindingResult bindingResult) {
        Employee employee = convertToEmployee(employeeDTO);
        personValidator.validate(employee, bindingResult);

        if (bindingResult.hasErrors()) {
            return Map.of("message", Objects.requireNonNull(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()));
        }

        employeeService.saveWithDetails(employee, employeeDTO);

        String token = jwtUtil.generateToken(employee.getEmail());
        return Collections.singletonMap("jwt-token", token);
    }


    public Employee convertToEmployee(EmployeeDTO employeeDTO) {
        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        if (employeeDTO.getRoles() == null || employeeDTO.getRoles().isEmpty()) {
            employee.setRoles(EnumSet.noneOf(EmployeeRole.class));
        } else {
            employee.setRoles(EnumSet.copyOf(employeeDTO.getRoles()));
        }
        return employee;
    }

    public EmployeeDTO convertToEmployeeDTO(Employee employee) {
        return modelMapper.map(employee, EmployeeDTO.class);
    }
}
