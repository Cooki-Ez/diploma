package pjatk.diploma.s22673.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pjatk.diploma.s22673.exceptions.LeaveRequestDoesNotExistException;
import pjatk.diploma.s22673.models.LeaveRequest;
import pjatk.diploma.s22673.models.LeaveRequestStatus;
import pjatk.diploma.s22673.repositories.LeaveRequestRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;

    @Autowired
    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    public List<LeaveRequest> findAll() {
        return leaveRequestRepository.findAll();
    }

    public LeaveRequest findOne(int id){
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id).orElse(null);
        if (leaveRequest == null)
            throw new LeaveRequestDoesNotExistException("LeaveRequest with id " + id + " does not exist");
        return leaveRequest;
    }

    @Transactional
    public LeaveRequest save(LeaveRequest leaveRequest) {
        return leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public LeaveRequest save(LeaveRequest leaveRequest, int id) {
        leaveRequest.setId(id);
        return leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public void delete(int id) {
        leaveRequestRepository.deleteById(id);
    }

    public List<LeaveRequest> findByStatus(LeaveRequestStatus status) {
        return leaveRequestRepository.findByStatus(status);
    }
}
