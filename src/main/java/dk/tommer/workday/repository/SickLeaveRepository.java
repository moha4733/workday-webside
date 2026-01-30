package dk.tommer.workday.repository;

import dk.tommer.workday.entity.SickLeave;
import dk.tommer.workday.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {
    List<SickLeave> findByUserOrderByStartDateDesc(User user);
    List<SickLeave> findByUser_IdOrderByStartDateDesc(Long userId);
}
