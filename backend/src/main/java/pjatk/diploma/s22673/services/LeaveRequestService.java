package pjatk.diploma.s22673.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pjatk.diploma.s22673.dto.LeaveRequestCreateDTO;
import pjatk.diploma.s22673.exceptions.LeaveRequestDoesNotExistException;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.models.EmployeeRole;
import pjatk.diploma.s22673.models.LeaveRequest;
import pjatk.diploma.s22673.models.LeaveRequestStatus;
import pjatk.diploma.s22673.repositories.LeaveRequestRepository;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeService employeeService;

    private static final List<LeaveRequestStatus> RESOLVED_STATUSES = Arrays.asList(
            LeaveRequestStatus.APPROVED,
            LeaveRequestStatus.CANCELLED,
            LeaveRequestStatus.DECLINED,
            LeaveRequestStatus.DECLINED_S
    );

    @Autowired
    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository, EmployeeService employeeService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeService = employeeService;
    }

    public List<LeaveRequest> findAll() {
        Employee currentEmployee = employeeService.getCurrentLoggedInEmployee();

        if (currentEmployee.getRoles().contains(EmployeeRole.ADMIN)) {
            return leaveRequestRepository.findAll();
        } else if (currentEmployee.getRoles().contains(EmployeeRole.MANAGER)) {
            return leaveRequestRepository.findByEmployeeDepartment(currentEmployee.getDepartment());
        } else {
            return leaveRequestRepository.findByEmployee(currentEmployee);
        }
    }

    public LeaveRequest findOne(int id){
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id).orElse(null);
        if (leaveRequest == null)
            throw new LeaveRequestDoesNotExistException("LeaveRequest with id " + id + " does not exist");
        return leaveRequest;
    }

    @Transactional
    public LeaveRequest save(LeaveRequest leaveRequest) {
        Employee currentEmployee = employeeService.getCurrentLoggedInEmployee();
        leaveRequest.setEmployee(currentEmployee);
        if (leaveRequest.getStatus() == null) {
            leaveRequest.setStatus(LeaveRequestStatus.PENDING);
        }
        leaveRequest.setLeaveEvaluation(null);
        return leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public LeaveRequest save(LeaveRequest leaveRequest, int id) {
        leaveRequest.setId(id);
        return leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public LeaveRequest saveForEmployee(LeaveRequest leaveRequest, int employeeId) {
        Employee employee = employeeService.findOne(employeeId);
        leaveRequest.setEmployee(employee);
        if (leaveRequest.getStatus() == null) {
            leaveRequest.setStatus(LeaveRequestStatus.PENDING);
        }
        leaveRequest.setLeaveEvaluation(null);
        return leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public List<LeaveRequest> saveAll(List<LeaveRequest> leaveRequests) {
        for (LeaveRequest leaveRequest : leaveRequests) {
            Employee employee = employeeService.findOne(leaveRequest.getEmployee().getId());
            leaveRequest.setEmployee(employee);
            if (leaveRequest.getStatus() == null) {
                leaveRequest.setStatus(LeaveRequestStatus.PENDING);
            }
            leaveRequest.setLeaveEvaluation(null);
            leaveRequestRepository.save(leaveRequest);
        }
        return leaveRequests;
    }

    @Transactional
    public void delete(int id) {
        leaveRequestRepository.deleteById(id);
    }

    public List<LeaveRequest> findByStatus(LeaveRequestStatus status) {
        return leaveRequestRepository.findByStatus(status);
    }

    public List<LeaveRequest> findResolvedRequests() {
        Employee currentEmployee = employeeService.getCurrentLoggedInEmployee();

        if (currentEmployee.getRoles().contains(EmployeeRole.ADMIN)) {
            return leaveRequestRepository.findByStatusInOrderByEndDateDesc(RESOLVED_STATUSES);
        } else if (currentEmployee.getRoles().contains(EmployeeRole.MANAGER)) {
            return leaveRequestRepository.findByEmployeeDepartmentAndStatusInOrderByEndDateDesc(
                    currentEmployee.getDepartment(), RESOLVED_STATUSES);
        } else {
            return leaveRequestRepository.findByEmployeeAndStatusInOrderByEndDateDesc(
                    currentEmployee, RESOLVED_STATUSES);
        }
    }
}
