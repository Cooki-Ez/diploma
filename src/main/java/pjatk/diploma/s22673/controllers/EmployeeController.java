package pjatk.diploma.s22673.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pjatk.diploma.s22673.dto.EmployeeDTO;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.services.EmployeeService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final ModelMapper modelMapper;

    @Autowired
    public EmployeeController(EmployeeService employeeService, ModelMapper modelMapper) {
        this.employeeService = employeeService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<EmployeeDTO> getEmployees() {
        List<Employee> employees = employeeService.findAll();
        return employees.stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable("id") int id) {
        Employee employee = employeeService.findOne(id);
        return ResponseEntity.ok(modelMapper.map(employee, EmployeeDTO.class));
    }

    //@GetMapping("/new")
    //public String newEmployee() {}

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody EmployeeDTO employeeDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        employeeService.save(employee);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody EmployeeDTO employeeDTO, @PathVariable("id") int id, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        employeeService.save(employee, id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        employeeService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}

