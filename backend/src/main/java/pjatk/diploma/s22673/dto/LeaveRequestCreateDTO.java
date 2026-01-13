package pjatk.diploma.s22673.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class LeaveRequestCreateDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean usePoints;

    @Size(max = 255)
    private String employeeComment;
}
