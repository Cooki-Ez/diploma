package pjatk.diploma.s22673.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pjatk.diploma.s22673.models.Department;
import pjatk.diploma.s22673.models.Employee;
import pjatk.diploma.s22673.models.LeaveRequest;
import pjatk.diploma.s22673.models.LeaveRequestStatus;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    List<LeaveRequest> findByStatus(LeaveRequestStatus status);

    List<LeaveRequest> findByEmployee(Employee employee);

    List<LeaveRequest> findByEmployeeDepartment(Department department);

    List<LeaveRequest> findByStatusInOrderByEndDateDesc(List<LeaveRequestStatus> statuses);

    List<LeaveRequest> findByEmployeeAndStatusInOrderByEndDateDesc(Employee employee, List<LeaveRequestStatus> statuses);

    List<LeaveRequest> findByEmployeeDepartmentAndStatusInOrderByEndDateDesc(Department department, List<LeaveRequestStatus> statuses);
}
