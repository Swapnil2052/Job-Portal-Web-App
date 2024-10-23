package com.personal.JobPortal.repository;

import com.personal.JobPortal.entity.JobPostActivity;
import com.personal.JobPortal.entity.JobSeekerProfile;
import com.personal.JobPortal.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave,Integer> {
     List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);
     List<JobSeekerSave> findByJob(JobPostActivity job);
}
