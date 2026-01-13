package pjatk.diploma.s22673.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pjatk.diploma.s22673.models.LeaveRequestStatus;

import java.time.LocalDate;

@Setter
@Getter
public class LeaveRequestUpdateDTO {
    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull(message = "Status is required")
    private LeaveRequestStatus status;

    private Boolean usePoints;

    @Size(max = 255)
    private String employeeComment;

    @Size(max = 255)
    private String evaluationComment;
}
