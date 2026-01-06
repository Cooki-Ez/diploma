package pjatk.diploma.s22673.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pjatk.diploma.s22673.models.LeaveRequestStatus;

@Setter
@Getter
@jakarta.validation.constraints.NotNull
public class LeaveRequestUpdateDTO {
    @jakarta.validation.constraints.NotNull(message = "Status is required")
    private LeaveRequestStatus status;

    @Size(max = 255, message = "Comment should be less than 255 characters")
    private String comment;

}
