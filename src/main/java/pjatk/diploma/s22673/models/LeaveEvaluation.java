package pjatk.diploma.s22673.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Leave_Evaluation")
public class LeaveEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "Date of decision should be specified")
    private Timestamp dateOfDecision;

    @Column(name = "comment")
    @Size(max = 255, message = "Comment should be less than 255 characters")
    private String comment;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @OneToMany(mappedBy = "leaveEvaluation")
    @JsonIgnore
    private List<LeaveRequest>  leaveRequests;

    public LeaveEvaluation(Timestamp dateOfDecision, String comment) {
        this.dateOfDecision = dateOfDecision;
        this.comment = comment;
    }
}
