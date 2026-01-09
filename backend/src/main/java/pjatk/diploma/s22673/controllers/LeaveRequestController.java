package pjatk.diploma.s22673.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pjatk.diploma.s22673.dto.DepartmentDTO;
import pjatk.diploma.s22673.dto.EmployeeDTO;
import pjatk.diploma.s22673.dto.LeaveRequestCreateDTO;
import pjatk.diploma.s22673.dto.LeaveRequestDTO;
import pjatk.diploma.s22673.dto.LeaveRequestUpdateDTO;
import pjatk.diploma.s22673.dto.ProjectDTO;
import pjatk.diploma.s22673.models.Department;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.models.LeaveRequest;
import pjatk.diploma.s22673.models.LeaveRequestStatus;
import pjatk.diploma.s22673.models.Project;
import pjatk.diploma.s22673.services.EmployeeService;
import pjatk.diploma.s22673.services.LeaveEvaluationService;
import pjatk.diploma.s22673.services.LeaveRequestService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/leaves")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;
    private final EmployeeService employeeService;
    private final LeaveEvaluationService leaveEvaluationService;

    @Autowired
    public LeaveRequestController(LeaveRequestService leaveRequestService, EmployeeService employeeService, LeaveEvaluationService leaveEvaluationService) {
        this.leaveRequestService = leaveRequestService;
        this.employeeService = employeeService;
        this.leaveEvaluationService = leaveEvaluationService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<LeaveRequestDTO>> findAll() {
        List<LeaveRequest> leaveRequests = leaveRequestService.findAll();
        List<LeaveRequestDTO> leaveRequestDTOs = leaveRequests.stream()
                .map(this::buildLeaveRequestDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaveRequestDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> findById(@PathVariable("id") int id) {
        LeaveRequest leaveRequest = leaveRequestService.findOne(id);
        return ResponseEntity.ok(buildLeaveRequestDTO(leaveRequest));
    }

    @PostMapping
    public ResponseEntity<LeaveRequestDTO> create(@RequestBody LeaveRequestCreateDTO createDTO) {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStartDate(createDTO.getStartDate());
        leaveRequest.setEndDate(createDTO.getEndDate());
        leaveRequest.setComment(createDTO.getComment());
        leaveRequest.setUsePoints(createDTO.isUsePoints());

        LeaveRequest savedRequest = leaveRequestService.save(leaveRequest);
        return ResponseEntity.ok(buildLeaveRequestDTO(savedRequest));
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<LeaveRequestDTO> update(@PathVariable("id") int id, @RequestBody LeaveRequestUpdateDTO updateDTO) {
        LeaveRequest leaveRequest = leaveRequestService.findOne(id);

        if (updateDTO.getStartDate() != null)
            leaveRequest.setStartDate(updateDTO.getStartDate().atStartOfDay());

        if (updateDTO.getEndDate() != null)
            leaveRequest.setEndDate(updateDTO.getEndDate().atStartOfDay());


        if (updateDTO.getUsePoints() != null)
            leaveRequest.setUsePoints(updateDTO.getUsePoints());

        if (updateDTO.getComment() != null)
            leaveRequest.setComment(updateDTO.getComment());

        if (updateDTO.getStatus() != null)
            leaveRequest.setStatus(updateDTO.getStatus());

        if (updateDTO.getStatus() == LeaveRequestStatus.APPROVED || updateDTO.getStatus() == LeaveRequestStatus.DECLINED) {
            leaveEvaluationService.evaluateRequest(leaveRequest, updateDTO.getComment());
        } else {
            leaveRequestService.save(leaveRequest, id);
        }

        LeaveRequest updatedRequest = leaveRequestService.findOne(id);
        return ResponseEntity.ok(buildLeaveRequestDTO(updatedRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("id") int id) {
        leaveRequestService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/resolved")
    public ResponseEntity<List<LeaveRequestDTO>> getResolvedRequests() {
        List<LeaveRequest> resolvedRequests = leaveRequestService.findResolvedRequests();
        List<LeaveRequestDTO> resolvedRequestDTOs = resolvedRequests.stream()
                .map(this::buildLeaveRequestDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resolvedRequestDTOs);
    }

    private LeaveRequestDTO buildLeaveRequestDTO(LeaveRequest leaveRequest) {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setId(leaveRequest.getId());
        dto.setCreationDate(leaveRequest.getCreationDate());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setComment(leaveRequest.getComment());
        dto.setStatus(leaveRequest.getStatus());

        if (leaveRequest.getEmployee() != null) {
            EmployeeDTO employeeDTO = new EmployeeDTO();
            employeeDTO.setId(leaveRequest.getEmployee().getId());
            employeeDTO.setName(leaveRequest.getEmployee().getName());
            employeeDTO.setSurname(leaveRequest.getEmployee().getSurname());
            employeeDTO.setEmail(leaveRequest.getEmployee().getEmail());
            employeeDTO.setPoints(leaveRequest.getEmployee().getPoints());

            if (leaveRequest.getEmployee().getDepartment() != null) {
                DepartmentDTO departmentDTO = new DepartmentDTO();
                departmentDTO.setId(leaveRequest.getEmployee().getDepartment().getId());
                departmentDTO.setName(leaveRequest.getEmployee().getDepartment().getName());
                departmentDTO.setLocation(leaveRequest.getEmployee().getDepartment().getLocation());
                employeeDTO.setDepartment(departmentDTO);
            }

            if (leaveRequest.getEmployee().getProjects() != null && !leaveRequest.getEmployee().getProjects().isEmpty()) {
                employeeDTO.setProjects(leaveRequest.getEmployee().getProjects().stream()
                        .map(this::convertProjectToDTO)
                        .collect(Collectors.toList()));
            }

            dto.setEmployee(employeeDTO);
        }

        if (leaveRequest.getLeaveEvaluation() != null) {
            dto.setEvaluationComment(leaveRequest.getLeaveEvaluation().getComment());

            if (leaveRequest.getLeaveEvaluation().getEmployee() != null) {
                EmployeeDTO evaluatorDTO = getEmployeeDTO(leaveRequest);

                dto.setEvaluatedBy(evaluatorDTO);
            }
        }

        return dto;
    }

    private EmployeeDTO getEmployeeDTO(LeaveRequest leaveRequest) {
        EmployeeDTO evaluatorDTO = new EmployeeDTO();
        evaluatorDTO.setId(leaveRequest.getLeaveEvaluation().getEmployee().getId());
        evaluatorDTO.setName(leaveRequest.getLeaveEvaluation().getEmployee().getName());
        evaluatorDTO.setSurname(leaveRequest.getLeaveEvaluation().getEmployee().getSurname());

        if (leaveRequest.getLeaveEvaluation().getEmployee().getDepartment() != null) {
            DepartmentDTO departmentDTO = new DepartmentDTO();
            departmentDTO.setId(leaveRequest.getLeaveEvaluation().getEmployee().getDepartment().getId());
            departmentDTO.setName(leaveRequest.getLeaveEvaluation().getEmployee().getDepartment().getName());
            departmentDTO.setLocation(leaveRequest.getLeaveEvaluation().getEmployee().getDepartment().getLocation());
            evaluatorDTO.setDepartment(departmentDTO);
        }
        return evaluatorDTO;
    }

    private ProjectDTO convertProjectToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setImportance(project.getImportance());
        return dto;
    }
}

