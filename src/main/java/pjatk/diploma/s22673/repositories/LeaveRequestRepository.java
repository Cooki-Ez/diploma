package pjatk.diploma.s22673.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pjatk.diploma.s22673.models.LeaveRequest;
import pjatk.diploma.s22673.models.LeaveRequestStatus;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    List<LeaveRequest> findByStatus(LeaveRequestStatus status);
}
