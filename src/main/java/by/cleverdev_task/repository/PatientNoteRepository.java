package by.cleverdev_task.repository;

import by.cleverdev_task.entity.PatientNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing PatientNote entities.
 */
@Repository
public interface PatientNoteRepository extends JpaRepository<PatientNote, Long> {

    Optional<PatientNote> findByGuid(String guid);
}
