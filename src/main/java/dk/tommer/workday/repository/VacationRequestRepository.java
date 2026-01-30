package dk.tommer.workday.repository;

import dk.tommer.workday.entity.User;
import dk.tommer.workday.entity.VacationRequest;
import dk.tommer.workday.entity.VacationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long> {
    List<VacationRequest> findByUserOrderByStartDateDesc(User user);
    List<VacationRequest> findByUser_IdOrderByStartDateDesc(Long userId);
    List<VacationRequest> findAllByOrderByCreatedAtDesc();
    List<VacationRequest> findByStatusOrderByCreatedAtDesc(VacationStatus status);
    List<VacationRequest> findByUser_IdAndStatusOrderByStartDateDesc(Long userId, VacationStatus status);
}
