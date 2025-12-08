package pjatk.diploma.s22673.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pjatk.diploma.s22673.exceptions.LeaveEvaluationDoesNotExistException;
import pjatk.diploma.s22673.models.*;
import pjatk.diploma.s22673.models.LeaveEvaluation;
import pjatk.diploma.s22673.repositories.LeaveEvaluationRepository;
import pjatk.diploma.s22673.repositories.LeaveRequestRepository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LeaveEvaluationService {
    private final LeaveEvaluationRepository leaveEvaluationRepository;
    private final LeaveRequestService leaveRequestService;
    private final EmployeeService employeeService;

    @Autowired
    public LeaveEvaluationService(LeaveEvaluationRepository leaveEvaluationRepository, LeaveRequestRepository leaveRequestRepository, LeaveRequestRepository leaveRequestRepository1, LeaveRequestService leaveRequestService, EmployeeService employeeService) {
        this.leaveEvaluationRepository = leaveEvaluationRepository;
        this.leaveRequestService = leaveRequestService;
        this.employeeService = employeeService;
    }

    // TODO
    public void evaluateRequest(LeaveRequest leaveRequest, String comment) {
        leaveRequest.setStatus(leaveRequest.getStatus());
        LeaveEvaluation leaveEvaluation = new LeaveEvaluation();
        leaveEvaluation.setComment(comment);
        leaveEvaluation.setDateOfDecision(Timestamp.valueOf(String.valueOf(LocalDate.now())));
    }
    
    public List<LeaveEvaluation> findAll() {
        return leaveEvaluationRepository.findAll();
    }
    
    public LeaveEvaluation findOne(int id) {
        LeaveEvaluation LeaveEvaluation = leaveEvaluationRepository.findById(id).orElse(null);
        if (LeaveEvaluation == null)
            throw new LeaveEvaluationDoesNotExistException("LeaveEvaluation with id " + id + " does not exist");
        return LeaveEvaluation;
    }

    @Transactional
    public void save(LeaveEvaluation leaveEvaluation) {
        leaveEvaluationRepository.save(leaveEvaluation);
    }

    @Transactional
    public void save(LeaveEvaluation leaveEvaluation, int id){
        leaveEvaluation.setId(id);
        leaveEvaluationRepository.save(leaveEvaluation);
    }

    @Transactional
    public void delete(int id) {
        leaveEvaluationRepository.deleteById(id);
    }

    @Scheduled(cron = "0 0 10 * * *")
    void evaluateRequests() {
        List<LeaveRequest> leaveRequests = leaveRequestService.findByStatus(LeaveRequestStatus.PENDING);
        for (LeaveRequest leaveRequest : leaveRequests) {
            int daysRequested = calculateDays(leaveRequest);
            Employee employee = leaveRequest.getEmployee();
            if(employee.getPoints() < daysRequested)
                leaveRequest.setStatus(LeaveRequestStatus.DECLINED_S);
            else {
                leaveRequest.setStatus(LeaveRequestStatus.APPROVED);
                employee.setPoints(employee.getPoints() - daysRequested);
                employeeService.save(employee, employee.getId());
            }
            leaveRequestService.save(leaveRequest, leaveRequest.getId());

        }

    }

    private int calculateDays(LeaveRequest leaveRequest) {
        LocalDate start = leaveRequest.getStartDate().toLocalDateTime().toLocalDate();
        LocalDate end = leaveRequest.getEndDate().toLocalDateTime().toLocalDate();

        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }
}
