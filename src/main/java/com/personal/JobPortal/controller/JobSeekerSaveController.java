package com.personal.JobPortal.controller;

import com.personal.JobPortal.entity.*;
import com.personal.JobPortal.services.JobPostActivityService;
import com.personal.JobPortal.services.JobSeekerProfileService;
import com.personal.JobPortal.services.JobSeekerSaveService;
import com.personal.JobPortal.services.UsersService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerSaveController {
    private final UsersService usersService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;

    public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService,
                                   JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            Users users = usersService.getUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("notFound"));
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(users.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
            if (seekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerSave.setUserId(seekerProfile.get());
                jobSeekerSave.setJob(jobPostActivity);
            } else throw new RuntimeException("UserNotFound");
            jobSeekerSaveService.addNew(jobSeekerSave);
        }
        return "redirect:/dashboard";
    }

    @GetMapping("saved-jobs")
    public String savedJob(Model model){
        List<JobPostActivity>jobPost=new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile();

        List<JobSeekerSave>jobSeekerSaveList=jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);
        for(JobSeekerSave jobSeekerSave:jobSeekerSaveList){
            jobPost.add(jobSeekerSave.getJob());
        }
        model.addAttribute("jobPost",jobPost);
        model.addAttribute("user",currentUserProfile);
        return "saved-jobs";
    }
}
