package dk.tommer.workday.repository;

import dk.tommer.workday.entity.DayPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {
    Optional<DayPlan> findFirstByUser_IdAndDateOrderByDateAsc(Long userId, LocalDate date);
    List<DayPlan> findTop5ByUser_IdAndDateGreaterThanEqualOrderByDateAsc(Long userId, LocalDate date);
}
