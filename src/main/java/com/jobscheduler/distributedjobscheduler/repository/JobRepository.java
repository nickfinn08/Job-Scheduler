package com.jobscheduler.distributedjobscheduler.repository;

import com.jobscheduler.distributedjobscheduler.enums.JobStatus;
import com.jobscheduler.distributedjobscheduler.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findByStatusAndScheduledAtLessThanEqual(
            JobStatus status,
            LocalDateTime time
    );
    @Modifying
    @Transactional
    @Query("""
UPDATE Job j
SET j.status = com.jobscheduler.distributedjobscheduler.enums.JobStatus.RUNNING,
    j.lockedBy = :workerId,
    j.lockedAt = CURRENT_TIMESTAMP
WHERE j.id = :jobId
  AND j.status = com.jobscheduler.distributedjobscheduler.enums.JobStatus.SCHEDULED
""")
    int claimJob(UUID jobId, String workerId);
    Page<Job> findByStatus(JobStatus status, Pageable pageable);
    List<Job> findByStatusAndLockedAtBefore(
            JobStatus status,
            LocalDateTime time
    );

}
