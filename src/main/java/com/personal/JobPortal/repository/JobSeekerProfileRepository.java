package com.personal.JobPortal.repository;

import com.personal.JobPortal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSeekerProfileRepository extends JpaRepository <JobSeekerProfile,Integer> {
}
