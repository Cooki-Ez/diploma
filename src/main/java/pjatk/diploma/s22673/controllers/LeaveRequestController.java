package pjatk.diploma.s22673.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pjatk.diploma.s22673.models.LeaveRequest;
import pjatk.diploma.s22673.services.LeaveRequestService;

import java.util.List;

@RestController
@RequestMapping("/leaves")
public class LeaveRequestController {
    private final LeaveRequestService  leaveRequestService;

    @Autowired
    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> findAll() {
        return ResponseEntity.ok(leaveRequestService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> findById(@PathVariable("id") int id) {
        return ResponseEntity.ok(leaveRequestService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<LeaveRequest> create(@RequestBody LeaveRequest leaveRequest) {
        return ResponseEntity.ok(leaveRequestService.save(leaveRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LeaveRequest> update(@PathVariable("id") int id, @RequestBody LeaveRequest leaveRequest) {
        return ResponseEntity.ok(leaveRequestService.save(leaveRequest, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("id") int id) {
        leaveRequestService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


}
