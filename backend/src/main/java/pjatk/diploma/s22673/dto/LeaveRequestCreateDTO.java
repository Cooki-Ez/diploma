package pjatk.diploma.s22673.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
public class LeaveRequestCreateDTO {
    @NotNull(message = "Please specify start date")
    private LocalDateTime startDate;

    @NotNull(message = "Please specify end date")
    private LocalDateTime endDate;

    @Size(max = 255, message = "Comment should be less than 255 characters")
    private String comment;

    private boolean usePoints = true;

    private Integer employeeId;
}
