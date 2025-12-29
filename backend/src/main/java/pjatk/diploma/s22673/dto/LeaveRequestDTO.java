package pjatk.diploma.s22673.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pjatk.diploma.s22673.models.LeaveRequestStatus;

import java.sql.Timestamp;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LeaveRequestStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveRequestStatus status) {
        this.status = status;
    }

    public boolean isUsePoints() {
        return usePoints;
    }

    public void setUsePoints(boolean usePoints) {
        this.usePoints = usePoints;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public int getLeaveEvaluationId() {
        return leaveEvaluationId;
    }

    public void setLeaveEvaluationId(int leaveEvaluationId) {
        this.leaveEvaluationId = leaveEvaluationId;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
    }
}