package pjatk.diploma.s22673.dto;

import lombok.Getter;
import lombok.Setter;
import pjatk.diploma.s22673.models.LeaveRequestStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
public class LeaveRequestDTO {
    private int id;
    private LocalDateTime creationDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String comment;
    private String managerName;
    private String managerSurname;
    private LeaveRequestStatus status;
    private EmployeeDTO employee;
    private EmployeeDTO evaluatedBy;
    private String evaluationComment;
}