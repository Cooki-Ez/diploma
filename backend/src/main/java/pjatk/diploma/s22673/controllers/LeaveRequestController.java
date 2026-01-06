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

    @GetMapping("/all")
    public ResponseEntity<List<LeaveRequestDTO>> findAll() {
        List<LeaveRequest> leaveRequests = leaveRequestService.findAll();
        List<LeaveRequestDTO> leaveRequestDTOs = leaveRequests.stream()
                .map(this::convertToLeaveRequestDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaveRequestDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> findById(@PathVariable("id") int id) {
        LeaveRequest leaveRequest = leaveRequestService.findOne(id);
        return ResponseEntity.ok(convertToLeaveRequestDTO(leaveRequest));
    }

    @PostMapping
    public ResponseEntity<LeaveRequestDTO> create(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        LeaveRequest leaveRequest = convertToLeaveRequest(leaveRequestDTO);
        
        
        LeaveRequest savedRequest = leaveRequestService.save(leaveRequest);
        return ResponseEntity.ok(convertToLeaveRequestDTO(savedRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> update(@PathVariable("id") int id, @RequestBody LeaveRequestDTO leaveRequestDTO) {
        LeaveRequest leaveRequest = convertToLeaveRequest(leaveRequestDTO);
        LeaveRequest updatedRequest = leaveRequestService.save(leaveRequest, id);
        return ResponseEntity.ok(convertToLeaveRequestDTO(updatedRequest));
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
                .map(this::convertToLeaveRequestDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resolvedRequestDTOs);
    }

    // Model mapper helper methods
    private LeaveRequestDTO convertToLeaveRequestDTO(LeaveRequest leaveRequest) {
        return modelMapper.map(leaveRequest, LeaveRequestDTO.class);
    }

    private LeaveRequest convertToLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        return modelMapper.map(leaveRequestDTO, LeaveRequest.class);
    }
}

