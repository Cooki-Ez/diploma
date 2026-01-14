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
import pjatk.diploma.s22673.util.DateUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
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

    @Transactional
    public void evaluateRequest(LeaveRequest leaveRequest, String comment) {
        LeaveEvaluation leaveEvaluation = new LeaveEvaluation();
        leaveEvaluation.setComment(comment);
        leaveEvaluation.setDateOfDecision(new Timestamp(System.currentTimeMillis()));

        Employee currentEmployee = employeeService.getCurrentLoggedInEmployee();
        leaveEvaluation.setEmployee(currentEmployee);

        LeaveEvaluation savedEvaluation = leaveEvaluationRepository.save(leaveEvaluation);

        leaveRequest.setLeaveEvaluation(savedEvaluation);
        leaveRequest.setManager(currentEmployee);
        leaveRequestService.save(leaveRequest, leaveRequest.getId());
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

    //@Scheduled(cron = "0 */2 * * * *")
    @Scheduled(fixedRate = 90_000)
    @Transactional
    void evaluateRequests() {

        List<LeaveRequest> leaveRequests = leaveRequestService.findByStatus(LeaveRequestStatus.PENDING);

        for (LeaveRequest leaveRequest : leaveRequests) {

            Employee employee = leaveRequest.getEmployee();

            if (!leaveRequest.isUsePoints()) {
                leaveRequest.setStatus(LeaveRequestStatus.MANUAL);
                createSystemEvaluation(leaveRequest);
                leaveRequestService.save(leaveRequest, leaveRequest.getId());
                continue;
            }

            LeaveRequestStatus status = evaluateProjectConstraints(employee, leaveRequest);

            leaveRequest.setStatus(status);

            createSystemEvaluation(leaveRequest);

            leaveRequestService.save(leaveRequest, leaveRequest.getId());
        }
    }

    @Transactional
    public void createSystemEvaluation(LeaveRequest leaveRequest) {
        LeaveEvaluation leaveEvaluation = new LeaveEvaluation();
        leaveEvaluation.setDateOfDecision(new Timestamp(System.currentTimeMillis()));

        leaveEvaluation.setComment(leaveRequest.getStatus().getSystemComment());

        // Get System employee
        Employee systemEmployee = employeeService.getSystemEmployee();
        leaveEvaluation.setEmployee(systemEmployee);

        LeaveEvaluation savedEvaluation = leaveEvaluationRepository.save(leaveEvaluation);
        leaveRequest.setLeaveEvaluation(savedEvaluation);
        leaveRequest.setManager(systemEmployee);
    }



/**
     * Evaluates project constraints to determine if leave request requires manual review.
     * Returns MANUAL status if project is CRUCIAL, deadline is within 2 weeks,
     * or if project is IMPORTANT and 70%+ employees already have overlapping leave.
     */    
    private LeaveRequestStatus evaluateProjectConstraints(Employee employee, LeaveRequest leaveRequest) {
        List<Project> projects = employee.getProjects();
        if (projects == null || projects.isEmpty()) {
            return LeaveRequestStatus.APPROVED_S;
        }

        LocalDate leaveStart = leaveRequest.getStartDate().toLocalDate();
        LocalDate leaveEnd = leaveRequest.getEndDate().toLocalDate();
        LocalDate twoWeeksFromNow = LocalDate.now().plusWeeks(2);

        for (Project project : projects) {
            if (project.getEndDate() == null) {
                continue;
            }

            LocalDate projectDeadline = project.getEndDate().toLocalDateTime().toLocalDate();
            boolean deadlineWithinTwoWeeks = !projectDeadline.isAfter(twoWeeksFromNow);

            if (project.getImportance() == Importance.CRUCIAL) {
                return LeaveRequestStatus.MANUAL;
            }

            if (deadlineWithinTwoWeeks) {
                return LeaveRequestStatus.MANUAL;
            }

            if (project.getImportance() == Importance.IMPORTANT) {
                if (!checkEmployeeAvailability(project, leaveStart, leaveEnd)) {
                    return LeaveRequestStatus.MANUAL;
                }
            }
        }

        return LeaveRequestStatus.APPROVED_S;
    }

   /**
     * Checks if at least 70% of project employees are available (not on leave) during the given dates.
     * Returns true if at least 70% are available, false otherwise.
     */
    private boolean checkEmployeeAvailability(Project project, LocalDate leaveStart, LocalDate leaveEnd) {
        List<Employee> projectEmployees = project.getEmployees();
        if (projectEmployees == null || projectEmployees.isEmpty()) 
            return true;

        int totalEmployees = projectEmployees.size();
        int employeesOnLeave = 0;

        for (Employee emp : projectEmployees) {
            List<LeaveRequest> empLeaveRequests = emp.getLeaveRequests();
            if (empLeaveRequests == null) 
                continue;

            for (LeaveRequest lr : empLeaveRequests) {
                if (lr.getStatus() == LeaveRequestStatus.APPROVED || lr.getStatus() == LeaveRequestStatus.APPROVED_S) {
                    LocalDate lrStart = lr.getStartDate().toLocalDate();
                    LocalDate lrEnd = lr.getEndDate().toLocalDate();

                    if (datesOverlap(leaveStart, leaveEnd, lrStart, lrEnd)) {
                        employeesOnLeave++;
                        break;
                    }
                }
            }
        }

        double percentageOnLeave = (double) employeesOnLeave / totalEmployees * 100;
        return percentageOnLeave < 70;
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

}
