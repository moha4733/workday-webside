package dk.tommer.workday.repository;

import dk.tommer.workday.entity.TravelAllowance;
import dk.tommer.workday.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelAllowanceRepository extends JpaRepository<TravelAllowance, Long> {
    List<TravelAllowance> findByUserOrderByDateDesc(User user);
    List<TravelAllowance> findByUser_IdOrderByDateDesc(Long userId);
}
