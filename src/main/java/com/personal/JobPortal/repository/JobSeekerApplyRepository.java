package com.personal.JobPortal.repository;

import com.personal.JobPortal.entity.JobPostActivity;
import com.personal.JobPortal.entity.JobSeekerApply;
import com.personal.JobPortal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply,Integer> {
    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);
    List<JobSeekerApply> findByJob(JobPostActivity job);
}
