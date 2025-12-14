package pjatk.diploma.s22673.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pjatk.diploma.s22673.dto.DepartmentDTO;
import pjatk.diploma.s22673.models.Department;
import pjatk.diploma.s22673.services.DepartmentService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final ModelMapper modelMapper;

    @Autowired
    public DepartmentController(DepartmentService departmentService, ModelMapper modelMapper) {
        this.departmentService = departmentService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAll() {
        List<Department> departments = departmentService.findAll();
        List<DepartmentDTO> departmentDTOs = departments.stream()
                .map(department -> modelMapper.map(department, DepartmentDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(departmentDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getById(@PathVariable("id") int id) {
        Department department = departmentService.findById(id);
        return ResponseEntity.ok(modelMapper.map(department, DepartmentDTO.class));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody DepartmentDTO departmentDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        Department department = modelMapper.map(departmentDTO, Department.class);
        Department savedDepartment = departmentService.save(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(savedDepartment, DepartmentDTO.class));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @Valid @RequestBody DepartmentDTO departmentDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        Department department = modelMapper.map(departmentDTO, Department.class);
        Department updatedDepartment = departmentService.save(department, id);
        return ResponseEntity.ok(modelMapper.map(updatedDepartment, DepartmentDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        departmentService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
