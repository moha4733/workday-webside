package dk.tommer.workday.repository;

import dk.tommer.workday.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    @Query("select coalesce(sum(w.hours),0) from WorkLog w where w.user.id = :userId and w.date = :date")
    Double sumHoursByUserIdAndDate(Long userId, LocalDate date);
}
