package pjatk.diploma.s22673.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pjatk.diploma.s22673.models.Department;
import pjatk.diploma.s22673.services.DepartmentService;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<Department>> getAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getById(@PathVariable("id") int id) {
        return ResponseEntity.ok(departmentService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Department> update(@PathVariable("id") int id, @RequestBody Department department) {
        return ResponseEntity.ok(departmentService.save(department, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        departmentService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
