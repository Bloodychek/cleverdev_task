package by.cleverdev_task.repository;

import by.cleverdev_task.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing and managing PatientProfile entities.
 */
@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {

    @Query("SELECT p FROM PatientProfile p WHERE p.oldClientGuid LIKE %:clientGuid%")
    List<PatientProfile> findByOldClientGuid(@Param("clientGuid") String clientGuid);
}
