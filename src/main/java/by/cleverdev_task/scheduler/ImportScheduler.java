package by.cleverdev_task.scheduler;

import by.cleverdev_task.service.impl.ImportServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task component that periodically runs the import job
 * to transfer notes from the old system into the new system.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ImportScheduler {

    private final ImportServiceImpl importServiceImpl;

    @Scheduled(cron = "${import.schedule}")
    public void scheduledImport() {
        log.info("Launching scheduled import...");
        importServiceImpl.importAllNotes();
        log.info("Import completed");
    }
}
