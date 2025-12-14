package pjatk.diploma.s22673.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pjatk.diploma.s22673.dto.LeaveRequestDTO;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.models.LeaveRequest;
import pjatk.diploma.s22673.services.EmployeeService;
import pjatk.diploma.s22673.services.LeaveRequestService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/leaves")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;
    private final EmployeeService employeeService;
    private final ModelMapper modelMapper;

    @Autowired
    public LeaveRequestController(LeaveRequestService leaveRequestService, EmployeeService employeeService, ModelMapper modelMapper) {
        this.leaveRequestService = leaveRequestService;
        this.employeeService = employeeService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<LeaveRequestDTO>> findAll() {
        List<LeaveRequest> leaveRequests = leaveRequestService.findAll();
        List<LeaveRequestDTO> leaveRequestDTOs = leaveRequests.stream()
                .map(request -> modelMapper.map(request, LeaveRequestDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaveRequestDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> findById(@PathVariable("id") int id) {
        LeaveRequest leaveRequest = leaveRequestService.findOne(id);
        return ResponseEntity.ok(modelMapper.map(leaveRequest, LeaveRequestDTO.class));
    }

    @PostMapping
    public ResponseEntity<LeaveRequestDTO> create(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        // Convert DTO to entity
        LeaveRequest leaveRequest = modelMapper.map(leaveRequestDTO, LeaveRequest.class);
        
        // Set the current logged-in employee as the requester
        Employee currentEmployee = employeeService.getCurrentLoggedInEmployee();
        leaveRequest.setEmployee(currentEmployee);
        
        // Save and convert back to DTO
        LeaveRequest savedRequest = leaveRequestService.save(leaveRequest);
        return ResponseEntity.ok(modelMapper.map(savedRequest, LeaveRequestDTO.class));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> update(@PathVariable("id") int id, @RequestBody LeaveRequestDTO leaveRequestDTO) {
        LeaveRequest leaveRequest = modelMapper.map(leaveRequestDTO, LeaveRequest.class);
        LeaveRequest updatedRequest = leaveRequestService.save(leaveRequest, id);
        return ResponseEntity.ok(modelMapper.map(updatedRequest, LeaveRequestDTO.class));
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
                .map(request -> modelMapper.map(request, LeaveRequestDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resolvedRequestDTOs);
    }

}
