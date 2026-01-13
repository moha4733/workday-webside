package dk.tommer.workday.repository;

import dk.tommer.workday.entity.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkTypeRepository extends JpaRepository<WorkType, Long> {
    List<WorkType> findAllByOrderByNameAsc();
    boolean existsByName(String name);
}

