package com.example.demo.controller;

import com.example.demo.dto.CreateProjectDTO;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import com.example.demo.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;


    @GetMapping("/")
    @ResponseBody
    public List<Project> list(HttpServletRequest request) {

        if (request.isUserInRole("ROLE_ADMIN"))
            return projectService.listProjects();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return projectService.listProjectsforUser(user);


    }

    @PostMapping("/")
    @Secured("ROLE_ADMIN")
    public ResponseEntity create(@RequestBody CreateProjectDTO p) {
        Project u = new Project();
        BeanUtils.copyProperties(p, u, new String[]{"id"});
        List<User> participants = new ArrayList<>();
        for(String id: p.getParticipantsIds()){
            participants.add(userService.getUserById(id));
        }
        u.setParticipants(participants);
        // TODO: Better error handling this is horrible.
        try {
            Project project = projectService.createProject(u);
            return new ResponseEntity<Project>(project, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    // Update operation
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public Project update(@RequestBody CreateProjectDTO proj,
                           @PathVariable("id") String projectId) {
        Project p = projectService.getProjectById(projectId);
        Utils.copyNonEmptyProperties(proj,p,new String[]{"id"});

        List<User> participants = new ArrayList<>();
        for(String id: proj.getParticipantsIds()){
            participants.add(userService.getUserById(id));
        }
        p.setParticipants(participants);
        return projectService.updateProject(
                p, projectId);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public String delete(@PathVariable("id")
                                       String projectId) {
        try {
            projectService.deleteProject(projectId);
            return "Project deleted Successfully";
        } catch (RuntimeException e) {
            return "Project not found";

        }
    }

}
