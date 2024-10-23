package com.personal.JobPortal.controller;

import com.personal.JobPortal.entity.JobSeekerProfile;
import com.personal.JobPortal.entity.Skills;
import com.personal.JobPortal.entity.Users;
import com.personal.JobPortal.repository.UsersRepository;
import com.personal.JobPortal.services.JobSeekerProfileService;
import com.personal.JobPortal.util.FileDownloadUtil;
import com.personal.JobPortal.util.FileUploadUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {
    private final JobSeekerProfileService jobSeekerProfileService;
    private final UsersRepository usersRepository;

    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UsersRepository usersRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
    }
    @GetMapping("/")
    public String JobSeekerProfile(Model model){
        JobSeekerProfile jobSeekerProfile=new JobSeekerProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Skills> skills=new ArrayList<>();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Users user = usersRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            if(seekerProfile.isPresent()){
                jobSeekerProfile=seekerProfile.get();
                if(jobSeekerProfile.getSkills().isEmpty()){
                    skills.add(new Skills());
                    jobSeekerProfile.setSkills(skills);
                }
            }
            model.addAttribute("skills", skills);
            model.addAttribute("profile", jobSeekerProfile);
        }
        return "job-seeker-profile";
    }

    @PostMapping("/addNew")
    public String addNew(JobSeekerProfile jobSeekerProfile, @RequestParam("image")MultipartFile multipartFile, @RequestParam("pdf") MultipartFile pdf, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Users user = usersRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
            jobSeekerProfile.setUserId(user);
            jobSeekerProfile.setUserAccountId(user.getUserId());
        }
        List<Skills>skillsList=new ArrayList<>();
        model.addAttribute("skills", skillsList);
        model.addAttribute("profile", jobSeekerProfile);
        for(Skills skills: jobSeekerProfile.getSkills()){
           skills.setJobSeekerProfile(jobSeekerProfile);
        }
        String  imageName="";
        String resumeName="";
        if(!multipartFile.getOriginalFilename().equals("")){
            imageName= StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
           jobSeekerProfile.setProfilePhoto(imageName);
        }
        if(!pdf.getOriginalFilename().equals("")){
            resumeName= StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename()));
            jobSeekerProfile.setResume(resumeName);
        }
        JobSeekerProfile seekerProfile=jobSeekerProfileService.addNew(jobSeekerProfile);
        String uploadDir="photos/candidate/"+jobSeekerProfile.getUserAccountId();
        try{
            FileUploadUtil.saveFile(uploadDir,imageName,multipartFile);
            FileUploadUtil.saveFile(uploadDir,resumeName,pdf);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String candidateProfile(@PathVariable("id")int id, Model model){
        Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(id);
        model.addAttribute("profile",seekerProfile.get());
        return "job-seeker-profile";
    }

    @GetMapping("/downloadResume")
    public ResponseEntity<?> downloadResume(@RequestParam(value="fileName")String fileName,
                                           @RequestParam(value = "userID")String userId){

        FileDownloadUtil fileDownloadUtil=new FileDownloadUtil();
        Resource resource=null;

        try{
            resource= fileDownloadUtil.getFileAsResource("photos/candidate/"+userId,fileName);
        }catch(IOException io){
            return ResponseEntity.badRequest().build();
        }

        if(resource==null)return new ResponseEntity<>("FileNotFound", HttpStatus.NOT_FOUND);

        String contentType="application/octet-stream";
        String headerValue="attachment; filename=\"" +resource.getFilename() +"\"";

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,headerValue)
                .body(resource);
    }
}
