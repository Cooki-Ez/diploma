package pjatk.diploma.s22673.services;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pjatk.diploma.s22673.models.Department;
import pjatk.diploma.s22673.repositories.DepartmentRepository;

import java.util.List;

@Service
@Transactional(readOnly=true)
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department findById(int id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Transactional
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Transactional
    public Department save(Department department, int id) {
        department.setId(id);
        return departmentRepository.save(department);
    }

    public Department findByName(String name) {
        return departmentRepository.findByName(name);
    }

    @Transactional
    public void delete(int id) {
        departmentRepository.deleteById(id);
    }
}
