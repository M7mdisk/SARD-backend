package com.example.demo.controller;

import com.example.demo.dto.CreateReleaseDTO;
import com.example.demo.model.Release;
import com.example.demo.model.User;
import com.example.demo.service.ProjectService;
import com.example.demo.service.ReleaseService;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    @Secured("ROLE_DEV")
    public ResponseEntity create(@RequestBody CreateReleaseDTO release) {
        Release r = new Release();
        Utils.copyNonEmptyProperties(release,r);
        r.setProject(projectService.getProjectById(release.getProjectId()));
        try {
            Release rr = releaseService.createRelease(r);

            return new ResponseEntity<Release>(rr, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    // Update operation
    @PutMapping("/{id}")
    @Secured("ROLE_DEV")
    public Release update(@RequestBody Release r,
                           @PathVariable("id") String releaseId) {
        return releaseService.updateRelease(
                r,releaseId);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_DEV")
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
