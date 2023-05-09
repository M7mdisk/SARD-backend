package com.example.demo.controller;

import com.example.demo.dto.CreateReleaseDTO;
import com.example.demo.model.Release;
import com.example.demo.model.ReleaseFile;
import com.example.demo.model.User;
import com.example.demo.service.FileService;
import com.example.demo.service.ProjectService;
import com.example.demo.service.ReleaseService;
import com.example.demo.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("release")
public class ReleaseController {

    @Autowired
    private ReleaseService releaseService;

    @Autowired
    private ProjectService projectService;


    @GetMapping("/")
    @Secured("ROLE_ADMIN")
    public List<Release> listAll() {
        return releaseService.listReleases();
    }
    @GetMapping("/{projectId}")
    public List<Release> list(@PathVariable("projectId") String projectId) {
        return releaseService.listReleasesForProject(projectId);
    }

    @PostMapping("/")
    @Secured({"ROLE_DEV","ROLE_ADMIN"})
    public ResponseEntity create(@RequestParam("release") String releasestr, @RequestParam(value = "file1") MultipartFile file1,@RequestParam(value = "file2", required = false) MultipartFile file2) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CreateReleaseDTO release = mapper.readValue(releasestr, CreateReleaseDTO.class);
        Release r = new Release();
        Utils.copyNonEmptyProperties(release,r);
        r.setProject(projectService.getProjectById(release.getProjectId()));

        ArrayList files = new ArrayList<ReleaseFile>();
        if (file1 == null || file1.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }
        files.add(releaseService.uploadReleaseFile(file1,r));
        if(file2 != null && !file2.isEmpty()){
            files.add(releaseService.uploadReleaseFile(file2,r));
        }

        if (r.getPlatform() == Release.Platform.IOS && files.size() !=2)
            return new ResponseEntity<>("2 files are needed for ios", HttpStatus.BAD_REQUEST);

        r.setReleaseFilesList(files);

        try {
            Release rr = releaseService.createRelease(r);
            return new ResponseEntity<Release>(rr, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.out.println(e);
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    // Update operation
    @PutMapping("/{id}")
    @Secured({"ROLE_DEV","ROLE_ADMIN"})
    public Release update(@RequestBody Release r,
                           @PathVariable("id") String releaseId) {
        return releaseService.updateRelease(
                r,releaseId);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_DEV","ROLE_ADMIN"})
    public String delete(@PathVariable("id")
                                       String releaseId) {
        try {
            releaseService.deleteRelease(releaseId);
            return "Project deleted Successfully";
        } catch (RuntimeException e) {
            return "Project not found";

        }
    }



}
