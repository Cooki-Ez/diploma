package pjatk.diploma.s22673.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pjatk.diploma.s22673.models.LeaveRequestStatus;

import java.time.LocalDate;

@Setter
@Getter
@jakarta.validation.constraints.NotNull
public class LeaveRequestUpdateDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull(message = "Status is required")
    private LeaveRequestStatus status;
    private Boolean usePoints;
    @Size(max = 255, message = "Comment should be less than 255 characters")
    private String comment;

}
