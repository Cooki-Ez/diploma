package pjatk.diploma.s22673.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pjatk.diploma.s22673.models.LeaveRequestStatus;

import java.sql.Timestamp;

@Setter
@Getter
public class LeaveRequestDTO {
    private int id;

    @NotNull(message = "Please specify start date")
    private Timestamp startDate;

    @NotNull(message = "Please specify end date")
    private Timestamp endDate;

    @Size(max = 255, message = "Comment should be less than 255 characters")
    private String comment;

    private LeaveRequestStatus status;
    private boolean usePoints = true; // Default to true for backward compatibility
    private int employeeId;
    private int managerId;
    private int leaveEvaluationId;
    private EmployeeDTO employee;

}