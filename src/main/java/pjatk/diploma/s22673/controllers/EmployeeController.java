package pjatk.diploma.s22673.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.services.EmployeeService;

import java.util.List;

@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getEmployees() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable("id") int id) {
        return ResponseEntity.ok(employeeService.findOne(id));
    }

    //@GetMapping("/new")
    //public String newEmployee() {}

    @PostMapping
    public ResponseEntity<HttpStatus> save(@RequestBody Employee employee) {
        employeeService.save(employee);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@RequestBody Employee employee, @PathVariable("id") int id) {
        employeeService.save(employee, id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        employeeService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}

