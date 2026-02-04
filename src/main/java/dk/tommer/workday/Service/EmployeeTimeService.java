package dk.tommer.workday.Service;

import dk.tommer.workday.entity.*;
import dk.tommer.workday.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeTimeService {

    @Autowired
    private SickLeaveRepository sickLeaveRepository;

    @Autowired
    private VacationRequestRepository vacationRequestRepository;

    @Autowired
    private TravelAllowanceRepository travelAllowanceRepository;

    @Autowired
    private UserRepository userRepository;

    // Sick Leave methods
    @Transactional
    public SickLeave createSickLeave(Long userId, LocalDate startDate, LocalDate endDate, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        SickLeave sickLeave = new SickLeave(user, startDate, endDate, comment);
        return sickLeaveRepository.save(sickLeave);
    }

    public List<SickLeave> getSickLeavesByUser(Long userId) {
        return sickLeaveRepository.findByUser_IdOrderByStartDateDesc(userId);
    }

    public List<SickLeave> getAllSickLeavesForEmployees() {
        // Get all sick leaves for users with SVEND or LÆRLING role
        return sickLeaveRepository.findAll().stream()
                .filter(sickLeave -> {
                    Role role = sickLeave.getUser().getRole();
                    return role == Role.SVEND || role == Role.LÆRLING;
                })
                .sorted((a, b) -> b.getStartDate().compareTo(a.getStartDate()))
                .toList();
    }

    @Transactional
    public void deleteSickLeave(Long id, Long userId) {
        SickLeave sickLeave = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sick leave not found"));
        
        if (!sickLeave.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this sick leave");
        }
        
        sickLeaveRepository.delete(sickLeave);
    }

    // Vacation Request methods
    @Transactional
    public VacationRequest createVacationRequest(Long userId, LocalDate startDate, LocalDate endDate, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        VacationRequest request = new VacationRequest(user, startDate, endDate, comment);
        return vacationRequestRepository.save(request);
    }

    public List<VacationRequest> getVacationRequestsByUser(Long userId) {
        return vacationRequestRepository.findByUser_IdOrderByStartDateDesc(userId);
    }

    public List<VacationRequest> getAllVacationRequests() {
        return vacationRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<VacationRequest> getVacationRequestsByStatus(VacationStatus status) {
        return vacationRequestRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional
    public VacationRequest updateVacationRequestStatus(Long id, VacationStatus status, String adminComment) {
        VacationRequest request = vacationRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacation request not found"));
        
        request.setStatus(status);
        request.setAdminComment(adminComment);
        request.setUpdatedAt(java.time.LocalDateTime.now());
        return vacationRequestRepository.save(request);
    }

    @Transactional
    public void deleteVacationRequest(Long id, Long userId) {
        VacationRequest request = vacationRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacation request not found"));
        
        if (!request.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this vacation request");
        }
        
        vacationRequestRepository.delete(request);
    }

    // Travel Allowance methods
    @Transactional
    public TravelAllowance createTravelAllowance(Long userId, LocalDate date, Double kilometers, Double amount, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TravelAllowance allowance = new TravelAllowance(user, date, kilometers, amount, comment);
        return travelAllowanceRepository.save(allowance);
    }

    public List<TravelAllowance> getTravelAllowancesByUser(Long userId) {
        return travelAllowanceRepository.findByUser_IdOrderByDateDesc(userId);
    }

    @Transactional
    public void deleteTravelAllowance(Long id, Long userId) {
        TravelAllowance allowance = travelAllowanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Travel allowance not found"));
        
        if (!allowance.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this travel allowance");
        }
        
        travelAllowanceRepository.delete(allowance);
    }
}
