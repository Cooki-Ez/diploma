package pjatk.diploma.s22673.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pjatk.diploma.s22673.models.LeaveEvaluation;

@Repository
public interface LeaveEvaluationRepository extends JpaRepository<LeaveEvaluation, Integer> {
}
