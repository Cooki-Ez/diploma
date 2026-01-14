package pjatk.diploma.s22673.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Leave_Request")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "start_date")
    @NotNull(message = "Please specify start date")
    private LocalDateTime  startDate;

    @Column(name = "end_date")
    @NotNull(message = "Please specify end date")
    private LocalDateTime endDate;

    @Column(name = "comment")
    @Size(max = 255, message = "Comment should be less than 255 characters")
    private String comment;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LeaveRequestStatus status;

    @Column(name = "use_points")
    private boolean usePoints = true;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    @JsonIgnore
    private Employee manager;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "leave_evaluation_id", referencedColumnName = "id")
    private LeaveEvaluation leaveEvaluation;

}
 

