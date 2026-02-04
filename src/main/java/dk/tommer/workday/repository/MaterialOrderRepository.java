package dk.tommer.workday.repository;

import dk.tommer.workday.entity.MaterialOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

import java.util.List;

@Repository
public interface MaterialOrderRepository extends JpaRepository<MaterialOrder, Long> {
    List<MaterialOrder> findTop3ByUser_IdOrderByCreatedAtDesc(Long userId);
    List<MaterialOrder> findByStatusOrderByCreatedAtDesc(dk.tommer.workday.entity.MaterialStatus status);
    List<MaterialOrder> findAllByOrderByCreatedAtDesc();
    List<MaterialOrder> findByProjectId(Long projectId);
    List<MaterialOrder> findByStatusAndCreatedAtBetween(dk.tommer.workday.entity.MaterialStatus status, LocalDateTime start, LocalDateTime end);
}
