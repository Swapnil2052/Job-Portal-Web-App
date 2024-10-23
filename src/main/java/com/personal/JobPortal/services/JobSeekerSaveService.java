package com.personal.JobPortal.services;

import com.personal.JobPortal.entity.JobPostActivity;
import com.personal.JobPortal.entity.JobSeekerProfile;
import com.personal.JobPortal.entity.JobSeekerSave;
import com.personal.JobPortal.repository.JobSeekerSaveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerSaveService {
    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    public JobSeekerSaveService(JobSeekerSaveRepository jobSeekerSaveRepository) {
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }

    public List<JobSeekerSave> getCandidatesJob(JobSeekerProfile userAccountId){
        return jobSeekerSaveRepository.findByUserId(userAccountId);
    }
    public List<JobSeekerSave>getJobCandidates(JobPostActivity job){
        return  jobSeekerSaveRepository.findByJob(job);
    }

    public void addNew(JobSeekerSave jobSeekerSave) {
        jobSeekerSaveRepository.save((jobSeekerSave));
    }
}
