package dk.tommer.workday.repository;

import dk.tommer.workday.entity.WorkLog;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.entity.WorkLogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    @Query("select coalesce(sum(w.hours),0) from WorkLog w where w.user.id = :userId and w.date = :date")
    Double sumHoursByUserIdAndDate(Long userId, LocalDate date);
    
    @Query("select coalesce(sum(w.hours),0) from WorkLog w where w.user.id = :userId and w.date between :startDate and :endDate")
    Double sumHoursByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<WorkLog> findByUserOrderByDateDesc(User user);
    
    List<WorkLog> findByStatus(WorkLogStatus status);
    
    List<WorkLog> findByUser_IdAndStatusAndDateBetweenOrderByDateAsc(Long userId, WorkLogStatus status, LocalDate startDate, LocalDate endDate);
    
    List<WorkLog> findByStatusAndDateBetweenOrderByDateAsc(WorkLogStatus status, LocalDate startDate, LocalDate endDate);
}
