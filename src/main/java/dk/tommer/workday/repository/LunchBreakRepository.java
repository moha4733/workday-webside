package dk.tommer.workday.repository;

import dk.tommer.workday.entity.LunchBreak;
import dk.tommer.workday.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LunchBreakRepository extends JpaRepository<LunchBreak, Long> {
    List<LunchBreak> findByUserOrderByDateDesc(User user);
    List<LunchBreak> findByUser_IdOrderByDateDesc(Long userId);
    Optional<LunchBreak> findByUser_IdAndDate(Long userId, LocalDate date);
}
