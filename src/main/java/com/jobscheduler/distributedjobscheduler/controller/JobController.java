package com.jobscheduler.distributedjobscheduler.controller;
import java.util.List;
import java.util.*;
import com.jobscheduler.distributedjobscheduler.dto.CreateJobRequest;
import com.jobscheduler.distributedjobscheduler.model.Job;
import com.jobscheduler.distributedjobscheduler.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.jobscheduler.distributedjobscheduler.enums.JobStatus;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Job createJob(
            @Valid @RequestBody CreateJobRequest request) {
        return jobService.createJob(request);
    }
    @GetMapping
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }
    @GetMapping("/due")
    public List<Job> getDueJobs() {
        return jobService.findDueJobs();
    }


    @GetMapping("/{id}")
    public Job getJobById(@PathVariable UUID id) {
        return jobService.getJobById(id);
    }

    @GetMapping("/paged")
    public Page<Job> getJobsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) JobStatus status
    ) {
        return jobService.getJobsPaged(page, size, status);
    }

}
